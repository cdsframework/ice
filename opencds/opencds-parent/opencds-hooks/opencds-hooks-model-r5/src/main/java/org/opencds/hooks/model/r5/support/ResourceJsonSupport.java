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

package org.opencds.hooks.model.r5.support;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.hl7.fhir.r5.model.Resource;
import org.opencds.hooks.lib.fhir.FhirContextFactory;

import java.lang.reflect.Type;
import java.util.Optional;

public class ResourceJsonSupport implements JsonDeserializer<Resource>, JsonSerializer<Resource> {

	private static final FhirContext ctx = FhirContextFactory.get(FhirVersionEnum.R5);
	private static final JsonParser jsonParser = new JsonParser();
	private static final String RESOURCE_TYPE = "resourceType";

	@Override
	public Resource deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (json == null || !json.isJsonObject() || json.getAsJsonObject().get(RESOURCE_TYPE) == null) {
			return null;
		}
		String resourceType = json.getAsJsonObject().get(RESOURCE_TYPE).getAsString();
		Resource resource = null;
		try {
			Class<? extends Resource> resourceClass = (Class<? extends Resource>) Class.forName("org.hl7.fhir.r5.model." + resourceType);
			resource = ctx.newJsonParser().parseResource(resourceClass, json.toString());
			if (resource == null) {
				return null;
			}
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
		}
		return resource;
	}

	@Override
	public JsonElement serialize(Resource src, Type typeOfSrc, JsonSerializationContext context) {
		return Optional.ofNullable(src)
				.map(s -> ctx.newJsonParser().encodeResourceToString(s))
				.map(JsonParser::parseString)
				.orElse(null);
	}

}
