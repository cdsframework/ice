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

package org.opencds.hooks.model.stu3.util;

import com.google.gson.GsonBuilder;
import org.hl7.fhir.dstu3.model.Resource;
import org.opencds.hooks.model.json.CdsHooksJsonUtil;
import org.opencds.hooks.model.response.Action;
import org.opencds.hooks.model.stu3.support.ActionJsonSupport;
import org.opencds.hooks.model.stu3.support.ResourceJsonSupport;
import org.opencds.hooks.model.util.ClassUtil;

public class Stu3JsonUtil extends CdsHooksJsonUtil {

    public Stu3JsonUtil() {
        super(new GsonBuilder());

        // CdsResponse
        getGsonBuilder().registerTypeAdapter(Action.class, new ActionJsonSupport());

        // FHIR
        ClassUtil.getResourceClassesAssignableFrom("org.hl7.fhir.dstu3.model", Resource.class)
                .forEach(cls -> getGsonBuilder().registerTypeAdapter(cls, new ResourceJsonSupport()));
        getGsonBuilder().registerTypeAdapter(Resource.class, new ResourceJsonSupport());
        buildGson();
    }

}