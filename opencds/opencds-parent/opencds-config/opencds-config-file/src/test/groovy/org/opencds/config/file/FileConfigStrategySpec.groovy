package org.opencds.config.file;

import java.nio.file.Paths

import org.opencds.config.api.ConfigData
import org.opencds.config.service.CacheServiceImpl

import spock.lang.Specification

class FileConfigStrategySpec extends Specification {

    def "test getKnowledgeRepository"() {
        given:
        def fileConfig = new FileConfigStrategy()
        ConfigData configData = new ConfigData()
        configData.configPath = Paths.get("src/test/resources/resources_v1.3")
        configData.configType = 'SIMPLE_FILE'
        
        when:
        def kr = fileConfig.getKnowledgeRepository(configData, new CacheServiceImpl())
        
        then:
        kr != null
        kr.conceptDeterminationMethodService != null
        kr.conceptDeterminationMethodService.getAll().size() == 1
        kr.conceptService != null
        kr.executionEngineService != null
        kr.executionEngineService.getAll().size() == 5
        kr.knowledgeModuleService != null
        kr.knowledgeModuleService.getAll().size() == 3
        kr.knowledgePackageService != null
        kr.semanticSignifierService != null
        kr.semanticSignifierService.getAll().size() == 1
        kr.supportingDataPackageService != null
        kr.supportingDataService != null
        kr.supportingDataService.getAll().size() == 0
    }

}
