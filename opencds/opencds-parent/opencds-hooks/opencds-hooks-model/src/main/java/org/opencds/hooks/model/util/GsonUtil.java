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

package org.opencds.hooks.model.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.opencds.common.exceptions.OpenCDSRuntimeException;

public class GsonUtil {
	private static final Log log = LogFactory.getLog(GsonUtil.class);
	private static final char DOT = '.';

	public static void addString(JsonObject json, String key, String value) {
		if (value != null) {
			json.add(key, new JsonPrimitive(value));
		}
	}

	public static void addObject(JsonObject json, String key, JsonElement value) {
		if (value != null) {
			json.add(key, value);
		}
	}

	public static String getString(JsonObject json, String key) {
		if (json.get(key) != null && json.get(key).isJsonPrimitive()) {
			return json.get(key).getAsString();
		}
		return null;
	}

	public static String getStringRequired(JsonObject json, String base, String key) {
		return Optional.ofNullable(getString(json, key))
				.orElseThrow(() -> new OpenCDSRuntimeException(base + DOT + key + " is required"));
	}

	public static URL getUrl(JsonObject json, String value) {
		String urlString = getString(json, value);
		if (urlString == null) {
			return null;
		}
		try {
			return new URL(urlString);
		} catch (MalformedURLException e) {
			log.error("fhirServer value is not a proper URL: " + urlString);
			e.printStackTrace();
		}
		return null;
	}
}
