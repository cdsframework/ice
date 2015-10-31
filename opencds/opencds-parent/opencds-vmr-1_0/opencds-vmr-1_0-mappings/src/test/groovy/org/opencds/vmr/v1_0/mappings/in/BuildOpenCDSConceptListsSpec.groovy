package org.opencds.vmr.v1_0.mappings.in

import org.opencds.config.api.KnowledgeRepositoryService
import org.opencds.config.api.service.ConceptDeterminationMethodService
import org.opencds.config.api.service.ConceptService
import org.opencds.config.api.service.ExecutionEngineService
import org.opencds.config.api.service.KnowledgeModuleService
import org.opencds.config.api.service.KnowledgePackageService
import org.opencds.config.api.service.PluginDataCacheService
import org.opencds.config.api.service.PluginPackageService
import org.opencds.config.api.service.SemanticSignifierService
import org.opencds.config.api.service.SupportingDataPackageService
import org.opencds.config.api.service.SupportingDataService
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
        SemanticSignifierService semanticSignifierService = Mock()
        SupportingDataService supportingDataService = Mock()
        SupportingDataPackageService supportingDataPackageService = Mock()
        PluginDataCacheService pluginDataCacheService = Mock()
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
                pluginDataCacheService)
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
