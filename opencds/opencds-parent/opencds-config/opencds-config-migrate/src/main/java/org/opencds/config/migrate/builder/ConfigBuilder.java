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

import org.opencds.common.cache.OpencdsCache;
import org.opencds.config.migrate.OpencdsBaseConfig;

public class ConfigBuilder {

    private final ExecutionEnginesConfigBuilder executionEnginesConfigBuilder;
    private final SemanticSignifiersConfigBuilder semanticSignifiersMetaConfigBuilder;
    private final KnowledgeModulesConfigBuilder knowledgeModulesConfigBuilder;
    private final CodeSystemsConfigBuilder codeSystemsConfigBuilder;
    private final SupportedConceptsBuilder supportedConceptsBuilder;
    private final CDMConfigBuilder cdmConfigBuilder;

    public ConfigBuilder(ExecutionEnginesConfigBuilder executionEnginesConfigBuilder,
            SemanticSignifiersConfigBuilder semanticSignifiersMetaConfigBuilder,
            KnowledgeModulesConfigBuilder knowledgeModulesConfigBuilder,
            CodeSystemsConfigBuilder codeSystemsConfigBuilder, SupportedConceptsBuilder supportedConceptsBuilder,
            CDMConfigBuilder cdmConfigBuilder) {
        this.executionEnginesConfigBuilder = executionEnginesConfigBuilder;
        this.semanticSignifiersMetaConfigBuilder = semanticSignifiersMetaConfigBuilder;
        this.knowledgeModulesConfigBuilder = knowledgeModulesConfigBuilder;
        this.codeSystemsConfigBuilder = codeSystemsConfigBuilder;
        this.supportedConceptsBuilder = supportedConceptsBuilder;
        this.cdmConfigBuilder = cdmConfigBuilder;
    }

    public void build(OpencdsBaseConfig config, OpencdsCache cache) {
        if (executionEnginesConfigBuilder != null) {
            executionEnginesConfigBuilder.loadExecutionEngines(config, cache);
        }
        if (semanticSignifiersMetaConfigBuilder != null) {
            semanticSignifiersMetaConfigBuilder.loadSemanticSignifiers(config, cache);
        }
        if (knowledgeModulesConfigBuilder != null) {
            knowledgeModulesConfigBuilder.loadKnowledgeModules(config, cache);
        }
        if (codeSystemsConfigBuilder != null) {
            codeSystemsConfigBuilder.loadCodeSystems(config, cache);
        }
        if (supportedConceptsBuilder != null) {
            supportedConceptsBuilder.loadSupportedConcepts(cache);
        }
        if (cdmConfigBuilder != null) {
            cdmConfigBuilder.loadCDMs(cache);
        }
    }

}
