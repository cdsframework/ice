package org.opencds.service

import org.junit.runner.RunWith
import org.opencds.config.api.ConfigurationService
import org.opencds.config.api.ExecutionEngineAdapter
import org.opencds.config.api.model.ExecutionEngine
import org.opencds.config.api.model.KnowledgeModule
import org.opencds.config.api.model.impl.KMIdImpl
import org.opencds.config.api.service.ExecutionEngineService
import org.opencds.config.api.service.KnowledgeModuleService
import org.opencds.war.cdshooks.test.TestConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import spock.lang.Specification

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class OpenCDSSpringContextSpec extends Specification {

    @Autowired
    ConfigurationService configurationService

    KnowledgeModuleService knowledgeModuleService
    ExecutionEngineService executionEngineService

    def setup() {
        knowledgeModuleService = configurationService.getKnowledgeRepository().getKnowledgeModuleService()
        assert knowledgeModuleService
        executionEngineService = configurationService.getKnowledgeRepository().getExecutionEngineService()
        assert executionEngineService
    }

    def "test me"() {
        when:
        KnowledgeModule km = knowledgeModuleService.find(KMIdImpl.create('org.opencds.hook', 'opencds-test', '1.0'))

        then:
        notThrown(Exception)
        km

        when:
        ExecutionEngine ee = executionEngineService.find(km.getExecutionEngine())

        then:
        notThrown(Exception)
        ee

        when:
        ExecutionEngineAdapter adapter = executionEngineService.getExecutionEngineAdapter(ee)

        then:
        notThrown(Exception)
        adapter
    }
}
