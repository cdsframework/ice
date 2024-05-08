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

package org.opencds.hooks.model.context.support;

import java.lang.reflect.Type;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.hooks.model.context.HookContext;
import org.opencds.hooks.model.context.ReadOnlyHookContext;
import org.opencds.hooks.model.request.support.CdsRequestJsonSupport;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * In order to effectively use this class, a 'hook' JsonString needs to be put
 * onto the JsonElement; the code will look for the 'hook' value and process the
 * context accordingly. The 'hook' value is added in
 * {@link CdsRequestJsonSupport}.
 * 
 * @author phillip
 *
 */
public class HookContextJsonSupport implements JsonDeserializer<HookContext>, JsonSerializer<HookContext> {
    static final Log log = LogFactory.getLog(HookContextJsonSupport.class);

    static final String PT_ID = "patientId";
    static final String ENC_ID = "encounterId";
    static final String MEDS = "medications";
    static final String HOOK = "hook";
    static final String ORDERS = "orders";

    public HookContext deserialize(JsonElement jsonElem, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (jsonElem == null) {
            return null;
        }
        HookContext hookCtx = null;
        if (jsonElem.isJsonObject()) {
            JsonObject json = jsonElem.getAsJsonObject();
            hookCtx = ReadOnlyHookContext.create(json, context);
        }
        return hookCtx;
    }

    public JsonElement serialize(HookContext hookCtx, Type typeOfSrc, JsonSerializationContext context) {
        if (hookCtx == null) {
            return null;
        } else {
            HookContext.class.isAssignableFrom(hookCtx.getClass());
        }
        return hookCtx.toJsonObject(context);
    }

}
