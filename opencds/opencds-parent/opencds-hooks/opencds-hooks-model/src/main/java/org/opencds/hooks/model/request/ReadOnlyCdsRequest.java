/*
 * Copyright 2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.hooks.model.request;

import ca.uhn.fhir.context.FhirVersionEnum;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.config.api.model.FhirVersion;
import org.opencds.hooks.model.Extension;
import org.opencds.hooks.model.context.HookContext;
import org.opencds.hooks.model.request.prefetch.PrefetchResult;
import org.opencds.hooks.model.util.GsonUtil;

import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * CdsRequest elements are read-only. Server parameters are writable.
 *
 * @author phillip
 *
 */
public class ReadOnlyCdsRequest implements CdsRequest {
    private static final Log log = LogFactory.getLog(ReadOnlyCdsRequest.class);

    private final String hook;
    private final String hookInstance;
    private final URL fhirServer;
    private final FhirAuthorization fhirAuthorization;
    private final String user;
    // possibly a list of FHIR resources
    private final HookContext context;
    private final Map<String, JsonElement> prefetch;
    private final Extension extension;

    /**
     * Non-standard field.
     */
    private FhirVersion fhirVersion;

    private JsonDeserializationContext jsonDeserializationContext;

    private ReadOnlyCdsRequest(String hook, String hookInstance, URL fhirServer, FhirAuthorization fhirAuthorization,
                               String user, HookContext context, Map<String, JsonElement> prefetch,
                               Extension extension, JsonDeserializationContext jsonDeserializationContext) {
        this.hook = hook;
        this.hookInstance = hookInstance;
        this.fhirServer = fhirServer;
        this.fhirAuthorization = fhirAuthorization;
        this.user = user;
        this.context = context;
        this.prefetch = prefetch;
        this.extension = extension;
        this.jsonDeserializationContext = jsonDeserializationContext;
    }

    public static CdsRequest create(String hook, String hookInstance, URL fhirServer,
            FhirAuthorization fhirAuthorization, String user, HookContext context, Map<String, JsonElement> prefetch,
            Extension extension, JsonDeserializationContext jsonDeserializationContext) {
        return new ReadOnlyCdsRequest(hook, hookInstance, fhirServer, fhirAuthorization, user, context, prefetch,
                extension, jsonDeserializationContext);
    }

    @Override
    public String getHook() {
        return hook;
    }

    @Override
    public void setHook(String hook) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getHookInstance() {
        return hookInstance;
    }

    @Override
    public void setHookInstance(String hookInstance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public URL getFhirServer() {
        return fhirServer;
    }

    @Override
    public void setFhirServer(URL fhirServer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FhirAuthorization getFhirAuthorization() {
        return fhirAuthorization;
    }

    @Override
    public void setFhirAuthorization(FhirAuthorization fhirAuthorization) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HookContext getContext() {
        return context;
    }

    @Override
    public void setContext(HookContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getPrefetchKeys() {
        return prefetch.keySet();
    }

    @Override
    public <O, R> PrefetchResult<O, R> getPrefetchResource(String key,
            Function<Function<Class<?>, Object>, PrefetchResult<O, R>> f) {
        if (jsonDeserializationContext == null) {
            log.error("Deserializer is null");
            return PrefetchResult.nullResult();
        }
        if (prefetch == null) {
            log.warn("No PrefetchHelper Resources provided");
            return PrefetchResult.nullResult();
        }
        if (!prefetch.containsKey(key)) {
            log.warn("No PrefetchHelper Resources associated with key: " + key);
            return PrefetchResult.nullResult();
        }
        return f.apply(createDeserializer(jsonDeserializationContext, prefetch.get(key)));
    }

    private Function<Class<?>, Object> createDeserializer(JsonDeserializationContext jsonDeserializationContext, JsonElement jsonElement) {
        assert Objects.nonNull(jsonDeserializationContext);
        assert Objects.nonNull(jsonElement);
        return resourceType -> {
            try {
                return jsonDeserializationContext.deserialize(jsonElement, resourceType);
            } catch (JsonParseException e) {
                log.warn("Exception while parsing Resource of type: " + resourceType.getCanonicalName(), e);
            }
            return null;
        };
    }

    @Override
    public <O, R> Map<String, PrefetchResult<O, R>> getPrefetches(Function<Function<Class<?>, Object>, PrefetchResult<O, R>> f) {
        return prefetch.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> getPrefetchResource(e.getKey(), f)));
    }

    @Override
    public <T> void addPrefetchResource(String key, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Extension getExtension() {
        return extension;
    }

    @Override
    public void setExtension(Extension extension) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FhirVersionEnum getFhirVersionEnum() {
        return FhirVersionEnum.valueOf(fhirVersion.getHapiFhirVersionEnumValue());
    }

    @Override
    public void setFhirVersion(FhirVersion fhirVersion) {
        this.fhirVersion = fhirVersion;
    }

    @Override
    public String toString() {
        return "CdsRequest [hook=" + hook + ", hookInstance=" + hookInstance + ", fhirServer=" + fhirServer
                + ", fhirAuthorization=" + fhirAuthorization + ", user=" + user + ", context=" + context + ", prefetch="
                + prefetch + ", fhirVersion=" + fhirVersion + "]";
    }

    @Override
    public JsonElement toJsonObject(JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        GsonUtil.addString(json, HOOK, hook);
        GsonUtil.addString(json, HOOK_INSTANCE, hookInstance);
        if (fhirServer != null) {
            GsonUtil.addString(json, FHIR_SERVER, fhirServer.toString());
        }
        GsonUtil.addObject(json, FHIR_AUTH, jsonSerializationContext.serialize(fhirAuthorization, FhirAuthorization.class));
        GsonUtil.addObject(json, CONTEXT, jsonSerializationContext.serialize(context, HookContext.class));
        if (prefetch != null && !prefetch.isEmpty()) {
            JsonObject pf = new JsonObject();
            prefetch.entrySet().forEach(e -> pf.add(e.getKey(), e.getValue()));
            GsonUtil.addObject(json, PREFETCH, pf);
        }
        GsonUtil.addObject(json, EXTENSION, jsonSerializationContext.serialize(extension, Extension.class));
        return json;
    }

}
