/*
 * Copyright 2013-2020 OpenCDS.org
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

package org.opencds.vmr.v1_0.mappings.in

import org.opencds.config.api.KnowledgeRepositoryService
import org.opencds.config.api.cache.CacheService
import org.opencds.config.api.service.ConceptDeterminationMethodService
import org.opencds.config.api.service.ConceptService
import org.opencds.config.api.service.ExecutionEngineService
import org.opencds.config.api.service.KnowledgeModuleService
import org.opencds.config.api.service.KnowledgePackageService
import org.opencds.config.api.service.PluginPackageService
import org.opencds.config.api.service.SemanticSignifierService
import org.opencds.config.api.service.SupportingDataPackageService
import org.opencds.config.api.service.SupportingDataService
import org.opencds.plugin.PluginDataCache
import org.opencds.vmr.v1_0.internal.concepts.ProcedureTargetBodySiteConcept
import org.opencds.vmr.v1_0.internal.concepts.ProcedureTargetBodySiteLateralityConcept
import org.opencds.vmr.v1_0.internal.concepts.UndeliveredProcedureReasonConcept

import spock.lang.Specification


class BuildOpenCDSConceptListsSpec extends Specification {

    BuildOpenCDSConceptLists builder

    def conceptService
    def knowledgeRepository

    def setup() {
        ConceptDeterminationMethodService conceptDeterminationMethodService = Mock()
        ConceptService conceptService = Mock()
        ExecutionEngineService executionEngineService = Mock()
        KnowledgeModuleService knowledgeModuleService = Mock()
        KnowledgePackageService knowledgePackageService = Mock()
        PluginPackageService pluginPackageService = Mock()
        PluginDataCache pluginDataCache = Mock()
        SemanticSignifierService semanticSignifierService = Mock()
        SupportingDataService supportingDataService = Mock()
        SupportingDataPackageService supportingDataPackageService = Mock()
        CacheService cacheService = Mock()
        knowledgeRepository = new KnowledgeRepositoryService(
                conceptDeterminationMethodService,
                conceptService,
                executionEngineService,
                knowledgeModuleService,
                knowledgePackageService,
                pluginPackageService,
                semanticSignifierService,
                supportingDataService,
                supportingDataPackageService,
                cacheService,
                pluginDataCache)
        builder = new BuildOpenCDSConceptLists()
    }

    def "empty result map when calling buildConceptLists with no input facts nor concepts"() {
        given:
        def factLists = new FactLists()
        def allFactLists = [:]

        when:
        builder.buildConceptLists(conceptService, factLists, allFactLists)

        then:
        allFactLists == [:]
    }

    def "test for UndeliveredProcedureReasonConcept"() {
        given:
        def factLists = new FactLists()
        def concept = new UndeliveredProcedureReasonConcept(id: '1', conceptTargetId: '2', openCdsConceptCode: '3', determinationMethodCode: '4')
        def allFactLists = ['UndeliveredProcedureReasonConcept':[concept]]

        when:
        builder.buildConceptLists(conceptService, factLists, allFactLists)

        then:
        allFactLists.size() == 1
        def data = allFactLists.UndeliveredProcedureReasonConcept
        data != null
        data instanceof List
        data.size() == 1
        data.contains concept
    }

    def "test for ProcedureTargetBodySiteConcept"() {
        given:
        def factLists = new FactLists()
        def concept = new ProcedureTargetBodySiteConcept(id: '1', conceptTargetId: '2', openCdsConceptCode: '3', determinationMethodCode: '4')
        def allFactLists = ['ProcedureTargetBodySiteConcept' : [concept]]

        when:
        builder.buildConceptLists(conceptService, factLists, allFactLists)

        then:
        allFactLists.size() == 1
        def data = allFactLists.ProcedureTargetBodySiteConcept
        data != null
        data.size() == 1
        data.contains concept
    }

    def "test for ProcedureTargetBodySiteLateralityConcept"() {
        given:
        def factLists = new FactLists()
        def concept = new ProcedureTargetBodySiteLateralityConcept(id: '1', conceptTargetId: '2', openCdsConceptCode: '3', determinationMethodCode: '4')
        def allFactLists = ["ProcedureTargetBodySiteLateralityConcept" : [concept]]

        when:
        builder.buildConceptLists(conceptService, factLists, allFactLists)

        then:
        allFactLists.size() == 1
        def data = allFactLists.ProcedureTargetBodySiteLateralityConcept
        data != null
        data.size() == 1
        data.contains concept
    }
}
