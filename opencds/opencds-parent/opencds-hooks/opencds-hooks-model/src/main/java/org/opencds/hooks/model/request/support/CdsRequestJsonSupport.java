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

package org.opencds.hooks.model.request.support;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ca.uhn.fhir.rest.annotation.Read;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.hooks.model.Extension;
import org.opencds.hooks.model.context.HookContext;
import org.opencds.hooks.model.context.ReadOnlyHookContext;
import org.opencds.hooks.model.context.support.HookContextJsonSupport;
import org.opencds.hooks.model.request.CdsRequest;
import org.opencds.hooks.model.request.FhirAuthorization;
import org.opencds.hooks.model.request.ReadOnlyCdsRequest;
import org.opencds.hooks.model.util.GsonUtil;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Custom implementation (which is normally not needed for such a class); the
 * custom part is that the 'hook' is copied from the parent CdsRequest<Resource> object to
 * the child 'context' in order to propertly parse and create an instance of
 * {@link ReadOnlyHookContext} in {@link HookContextJsonSupport}.
 * 
 * @author phillip
 *
 */
public class CdsRequestJsonSupport implements JsonDeserializer<CdsRequest>, JsonSerializer<CdsRequest> {
	private static final Log log = LogFactory.getLog(CdsRequestJsonSupport.class);

	private static final String HOOK = "hook";
	private static final String HOOK_INSTANCE = "hookInstance";
	private static final String FHIR_SERVER = "fhirServer";
	private static final String FHIR_AUTH = "fhirAuthorization";
	private static final String USER = "user";
	private static final String CONTEXT = "context";
	private static final String PREFETCH = "prefetch";
	private static final String EXTENSION = "extension";
	private static final String CDS_REQUEST = "CdsRequest";
	private static final String HTTPS = "https";
	private static final char DOT = '.';

	@Override
	public CdsRequest deserialize(JsonElement jsonElem, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		CdsRequest req = null;
		if (jsonElem.isJsonObject()) {
			JsonObject json = jsonElem.getAsJsonObject();
			URL fhirServer = GsonUtil.getUrl(json, FHIR_SERVER);
			if (fhirServer != null && StringUtils.compareIgnoreCase(HTTPS, fhirServer.getProtocol()) != 0) {
				log.warn("Insecure " + FHIR_SERVER + " URL: " + fhirServer);
			}
			FhirAuthorization fhirAuthorization = getFhirAuthorization(json, context);
			if (fhirAuthorization != null && fhirServer == null) {
				throw new OpenCDSRuntimeException(
						CDS_REQUEST + DOT + FHIR_SERVER +
						" is required when " + CDS_REQUEST + DOT + FHIR_AUTH +
						" is present");
			}
			req = ReadOnlyCdsRequest.create(
			        GsonUtil.getStringRequired(json, CDS_REQUEST, HOOK),
			        GsonUtil.getStringRequired(json, CDS_REQUEST, HOOK_INSTANCE),
			        fhirServer,
			        fhirAuthorization,
			        GsonUtil.getString(json, USER),
			        getHookContext(json, context),
			        getPrefetch(json, context),
			        getExtension(json, context),
			        context);
		}
		return req;
	}

    @Override
	public JsonElement serialize(CdsRequest req, Type typeOfSrc, JsonSerializationContext context) {
		return Optional.ofNullable(req)
				.filter(r -> CdsRequest.class.isAssignableFrom(r.getClass()))
				.map(r -> r.toJsonObject(context))
				.orElse(null);
	}

	private Map<String, JsonElement> getPrefetch(JsonObject json, JsonDeserializationContext context) {
		return Collections.unmodifiableMap(json.entrySet().stream().filter(e -> PREFETCH.equalsIgnoreCase(e.getKey()))
				.flatMap(e -> e.getValue().getAsJsonObject().entrySet().stream())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	private Extension getExtension(JsonObject json, JsonDeserializationContext context) {
		return Optional.ofNullable(json.get(EXTENSION))
				.<Extension>map(ext -> context.deserialize(ext, Extension.class))
				.orElse(null);
	}

	private FhirAuthorization getFhirAuthorization(JsonObject json, JsonDeserializationContext context) {
		JsonElement fhirAuth = json.get(FHIR_AUTH);
		if (fhirAuth == null) {
			return null;
		}
		return Optional.of(fhirAuth)
				.<FhirAuthorization>map(fa -> context.deserialize(fa, FhirAuthorization.class))
				.filter(FhirAuthorization::allFieldsValid)
				.orElseThrow(() -> new OpenCDSRuntimeException("fhirAuthorization is incomplete"));
	}

	private HookContext getHookContext(JsonObject json, JsonDeserializationContext context) {
		return Optional.ofNullable(json.get(CONTEXT))
				.<HookContext>map(ctx -> context.deserialize(ctx, ReadOnlyHookContext.class))
				.orElseThrow(() -> new OpenCDSRuntimeException("CdsRequest.context is required"));
	}

}
