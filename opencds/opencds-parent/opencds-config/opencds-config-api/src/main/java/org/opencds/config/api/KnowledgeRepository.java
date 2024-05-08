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

package org.opencds.config.api;

import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.service.ConceptDeterminationMethodService;
import org.opencds.config.api.service.ConceptService;
import org.opencds.config.api.service.ExecutionEngineService;
import org.opencds.config.api.service.KnowledgeModuleService;
import org.opencds.config.api.service.KnowledgePackageService;
import org.opencds.config.api.service.PluginPackageService;
import org.opencds.config.api.service.SemanticSignifierService;
import org.opencds.config.api.service.SupportingDataPackageService;
import org.opencds.config.api.service.SupportingDataService;
import org.opencds.plugin.PluginDataCache;

public interface KnowledgeRepository {
    ConceptDeterminationMethodService getConceptDeterminationMethodService();
    
    ConceptService getConceptService();
    
    ExecutionEngineService getExecutionEngineService();
    
    KnowledgeModuleService getKnowledgeModuleService();
    
    KnowledgePackageService getKnowledgePackageService();
    
    SemanticSignifierService getSemanticSignifierService();
    
    SupportingDataService getSupportingDataService();

    SupportingDataPackageService getSupportingDataPackageService();

    PluginPackageService getPluginPackageService();
    
    PluginDataCache getPluginDataCache(PluginId pluginId);

}
