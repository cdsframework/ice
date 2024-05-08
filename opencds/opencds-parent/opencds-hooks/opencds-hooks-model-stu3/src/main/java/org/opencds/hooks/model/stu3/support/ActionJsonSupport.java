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

package org.opencds.hooks.model.stu3.support;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.hl7.fhir.dstu3.model.Resource;
import org.opencds.hooks.model.response.Action;
import org.opencds.hooks.model.response.ActionType;
import org.opencds.hooks.model.util.GsonUtil;

import java.lang.reflect.Type;

public class ActionJsonSupport implements JsonDeserializer<Action>, JsonSerializer<Action> {

    private static final String DESC = "description";
    private static final String TYPE = "type";
    private static final String RESOURCE = "resource";

    @Override
    public Action deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement.isJsonObject()) {
            JsonObject json = jsonElement.getAsJsonObject();
            String description = json.get(DESC) != null ?
                    json.getAsJsonPrimitive(DESC).getAsString() :
                    null;
            return Action.create(
                    context.deserialize(json.get(TYPE), ActionType.class),
                    description,
                    context.deserialize(json.get(RESOURCE), Resource.class)
            );
        }
        return null;
    }

    @Override
    public JsonElement serialize(Action action, Type typeOfSrc, JsonSerializationContext context) {
        if (action == null) {
            return null;
        }
        JsonObject json = new JsonObject();
        GsonUtil.addObject(json, TYPE, context.serialize(action.getType(), ActionType.class));
        GsonUtil.addString(json, DESC, action.getDescription());
        GsonUtil.addObject(json, RESOURCE, context.serialize(action.getResource(), Resource.class));
        return json;
    }
}
