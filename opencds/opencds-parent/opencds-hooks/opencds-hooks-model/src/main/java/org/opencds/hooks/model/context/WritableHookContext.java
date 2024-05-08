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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class WritableHookContext implements HookContext {
    private final Map<String, Object> contextParams;

    private String hook;
    
    public WritableHookContext() {
        contextParams = new ConcurrentHashMap<>();
    }
    
    @Override
    public String getHook() {
        return hook;
    }

    @Override
    public void setHook(String hook) {
        this.hook = hook;
    }
    
    @Override
    public <T> T get(String key, Class<T> valueClass) {
        Object value = contextParams.get(key);
        if (value != null && valueClass.isAssignableFrom(value.getClass())) {
            return valueClass.cast(value);
        }
        return null;
    }
    
    @Override
    public <T> void add(String key, T value) {
        if (value == null) {
            throw new RuntimeException("Context value cannot be null");
        }
        contextParams.put(key, value);
    }

    @Override
    public JsonElement toJsonObject(JsonSerializationContext context) {
        if (contextParams == null || contextParams.isEmpty()) {
            return null;
        }
        JsonObject json = new JsonObject();
        contextParams.entrySet().forEach(e -> json.add(e.getKey(), context.serialize(e.getValue())));
        return json;
    }

}
