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
import java.util.function.Function;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.opencds.config.api.model.FhirVersion;
import org.opencds.hooks.model.context.HookContext;
import org.opencds.hooks.model.fhir.FhirClient;
import org.opencds.hooks.model.request.prefetch.PrefetchResult;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.opencds.hooks.model.Extension;

public interface CdsRequest {
    String HOOK = "hook";
    String HOOK_INSTANCE = "hookInstance";
    String FHIR_SERVER = "fhirServer";
    String FHIR_AUTH = "fhirAuthorization";
    String CONTEXT = "context";
    String PREFETCH = "prefetch";
    String EXTENSION = "extension";

	String getHook();

	void setHook(String hook);

	String getHookInstance();

	void setHookInstance(String hookInstance);

	URL getFhirServer();

	void setFhirServer(URL fhirServer);

	FhirAuthorization getFhirAuthorization();

    default IGenericClient getFhirClient(FhirContext context) {
        return new FhirClient(context).get(getFhirServer(), getFhirAuthorization());
    }

    default IGenericClient getFhirClient(FhirContext context, int socketTimeout) {
        return new FhirClient(context).get(getFhirServer(), getFhirAuthorization(), socketTimeout);
    }

	void setFhirAuthorization(FhirAuthorization fhirAuthorization);

	HookContext getContext();

	void setContext(HookContext context);

	Set<String> getPrefetchKeys();

    <O, R> PrefetchResult<O, R> getPrefetchResource(String key,
            Function<Function<Class<?>, Object>, PrefetchResult<O, R>> f);

	<O, R> Map<String, PrefetchResult<O, R>> getPrefetches(Function<Function<Class<?>, Object>, PrefetchResult<O, R>> f);

	<R> void addPrefetchResource(String key, R value);

	Extension getExtension();

	void setExtension(Extension extension);

	FhirVersionEnum getFhirVersionEnum();

	void setFhirVersion(FhirVersion fhirVersion);

    JsonElement toJsonObject(JsonSerializationContext context);

}
