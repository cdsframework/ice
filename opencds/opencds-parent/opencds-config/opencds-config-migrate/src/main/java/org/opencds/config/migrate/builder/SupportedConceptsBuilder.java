/*
 * Copyright 2014-2020 OpenCDS.org
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

package org.opencds.config.migrate.builder;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.cache.OpencdsCache;
import org.opencds.config.migrate.ConfigResourceType;
import org.opencds.config.migrate.cache.ConfigCacheRegion;
import org.opencds.config.migrate.terminology.OpenCDSConceptTypes;

public class SupportedConceptsBuilder {
    private static final Log log = LogFactory.getLog(SupportedConceptsBuilder.class);

    public void loadSupportedConcepts(OpencdsCache cache) {
        // load codeSystem arrays
        List<String> conceptTypes = OpenCDSConceptTypes.getOpenCdsConceptTypes();// conceptTypesRootEntity.getChildrenWithLabel("ConceptType");
        
        cache.put(ConfigCacheRegion.DATA, ConfigResourceType.SUPPORTED_CONCEPTS, conceptTypes);
        log.trace("OpenCDSConceptTypes: " + conceptTypes);
    }

}
