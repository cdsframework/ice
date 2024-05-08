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

package org.opencds.hooks.model.context;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.hooks.model.util.GsonUtil;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class ReadOnlyHookContext implements HookContext {
    private static final Log log = LogFactory.getLog(ReadOnlyHookContext.class);
    private static final String HOOK = "hook";

    private final String hook;
    private final Map<String, JsonElement> contextParams;
    private final JsonDeserializationContext jsonDeserializationContext;

    private ReadOnlyHookContext(String hook, Map<String, JsonElement> contextParams,
            JsonDeserializationContext jsonDeserializationContext) {
        this.hook = hook;
        this.contextParams = contextParams;
        this.jsonDeserializationContext = jsonDeserializationContext;
    }

    public static ReadOnlyHookContext create(JsonObject jsonObject,
            JsonDeserializationContext jsonDeserializationContext) {
        return new ReadOnlyHookContext(GsonUtil.getString(jsonObject, HOOK), parseJsonElement(jsonObject),
                jsonDeserializationContext);
    }

    private static Map<String, JsonElement> parseJsonElement(JsonObject jsonObject) {
        return Collections.unmodifiableMap(
                jsonObject.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @Override
    public String getHook() {
        return hook;
    }

    /**
     * This class does not support writing.
     * 
     * @param hook
     * @throws UnsupportedOperationException
     */
    @Override
    public void setHook(String hook) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T get(String key, Class<T> valueClass) {
        if (jsonDeserializationContext == null) {
            log.error("Deserializer is null");
            return null;
        }
        if (contextParams == null) {
            log.warn("No context provided");
            return null;
        }
        if (!contextParams.containsKey(key)) {
            log.warn("Context does not contain key: " + key);
            return null;
        }
        try {
            return jsonDeserializationContext.deserialize(contextParams.get(key), valueClass);
        } catch (JsonParseException e) {
            log.error("Unable to parse context: " + e.getMessage(), e);
        }
        return null;
    }

    public Map<String, JsonElement> getContextParams() {
        return contextParams;
    }

    @Override
    public <T> void add(String key, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonObject toJsonObject(JsonSerializationContext context) {
        if (contextParams == null) {
            return null;
        }
        JsonObject json = new JsonObject();
        contextParams.entrySet().forEach(e -> json.add(e.getKey(), e.getValue()));
        return json;
    }

    @Override
    public String toString() {
        return "ReadOnlyHookContext [hook=" + hook + ", contextParams=" + contextParams
                + ", jsonDeserializationContext=" + jsonDeserializationContext + "]";
    }

}
