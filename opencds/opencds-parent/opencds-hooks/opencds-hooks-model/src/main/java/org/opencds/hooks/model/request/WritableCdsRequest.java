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

import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.config.api.model.FhirVersion;
import org.opencds.hooks.model.Extension;
import org.opencds.hooks.model.context.HookContext;
import org.opencds.hooks.model.context.WritableHookContext;
import org.opencds.hooks.model.request.prefetch.PrefetchResult;
import org.opencds.hooks.model.util.GsonUtil;
import org.opencds.hooks.model.util.TypeUtil;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class WritableCdsRequest implements CdsRequest {
    private static final Log log = LogFactory.getLog(WritableCdsRequest.class);
    private String hook;
    private String hookInstance;
    private URL fhirServer;
    private FhirAuthorization fhirAuthorization;
    private String user;
    // possibly a list of FHIR resources
    private HookContext context;
    private Map<String, Object> prefetch = new ConcurrentHashMap<>();
    private Extension extension;
    /**
     * Non-standard field.
     */
    private FhirVersion fhirVersion;

    @Override
    public String getHook() {
        return hook;
    }

    @Override
    public void setHook(String hook) {
        this.hook = hook;
    }

    @Override
    public String getHookInstance() {
        return hookInstance;
    }

    @Override
    public void setHookInstance(String hookInstance) {
        this.hookInstance = hookInstance;
    }

    @Override
    public URL getFhirServer() {
        return fhirServer;
    }

    @Override
    public void setFhirServer(URL fhirServer) {
        this.fhirServer = fhirServer;
    }

    @Override
    public FhirAuthorization getFhirAuthorization() {
        return fhirAuthorization;
    }

    @Override
    public void setFhirAuthorization(FhirAuthorization fhirAuthorization) {
        this.fhirAuthorization = fhirAuthorization;
    }

    @Override
    public HookContext getContext() {
        return context;
    }

    @Override
    public void setContext(HookContext context) {
        this.context = context;
    }

    @Override
    public Set<String> getPrefetchKeys() {
        return prefetch.keySet();
    }

    @Override
    public <O, R> PrefetchResult<O, R> getPrefetchResource(String key,
            Function<Function<Class<?>, Object>, PrefetchResult<O, R>> f) {
        return f.apply(c -> TypeUtil.cast(c, prefetch.get(key)));
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
        if (value == null) {
            log.warn("PrefetchHelper Resource value is null; will not be added, but will be retrievable as null.");
        } else {
            prefetch.put(key, value);
        }
    }

    @Override
    public Extension getExtension() {
        return extension;
    }

    @Override
    public void setExtension(Extension extension) {
        this.extension = extension;
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
        GsonUtil.addObject(json, CONTEXT, jsonSerializationContext.serialize(context, WritableHookContext.class));

        if (prefetch != null && !prefetch.isEmpty()) {
            JsonObject pf = new JsonObject();
            prefetch.forEach((key, value) -> pf.add(key, jsonSerializationContext.serialize(value, value.getClass())));
            GsonUtil.addObject(json, PREFETCH, pf);
        }

        GsonUtil.addObject(json, EXTENSION, jsonSerializationContext.serialize(extension, Extension.class));
        return json;
    }

}
