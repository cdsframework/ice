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

package org.opencds.hooks.model.support;

import java.lang.reflect.Type;

import org.opencds.hooks.model.Extension;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ExtensionJsonSupport implements JsonDeserializer<Extension>, JsonSerializer<Extension> {

	@Override
	public Extension deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return new Extension(json);
	}

	@Override
	public JsonElement serialize(Extension src, Type typeOfSrc, JsonSerializationContext context) {
	    if (src.getValue() instanceof JsonElement) {
	        return (JsonElement) src.getValue();
	    } else {
	        return context.serialize(src.getValue());
	    }
	}

}
