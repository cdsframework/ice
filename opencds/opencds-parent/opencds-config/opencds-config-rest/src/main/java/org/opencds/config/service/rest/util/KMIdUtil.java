/*
 * Copyright 2015-2020 OpenCDS.org
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

package org.opencds.config.service.rest.util;

import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.service.KnowledgeModuleService;

public class KMIdUtil {
    
    public static boolean found(KnowledgeModuleService kmService, KMId kmId) {
        return find(kmService, kmId) != null;
    }

    public static KnowledgeModule find(KnowledgeModuleService kmService, KMId kmId) {
        return kmService.find(kmId);
    }

}
