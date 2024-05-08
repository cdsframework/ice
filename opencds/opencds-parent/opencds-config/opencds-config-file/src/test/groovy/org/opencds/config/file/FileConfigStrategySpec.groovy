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

package org.opencds.config.file;

import java.nio.file.Path
import java.nio.file.Paths

import org.opencds.config.api.ConfigData
import org.opencds.config.service.CacheServiceImpl;

import spock.lang.Specification

class FileConfigStrategySpec extends Specification {

    def "test getKnowledgeRepository"() {
//        given:
        def fileConfig = new FileConfigStrategy()
        Path path = Paths.get("src/test/resources/resources_v1.3")
        ConfigData configData = new ConfigData(configType: 'SIMPLE_FILE', configPath: path.toString())
        
//        when:
        def kr = fileConfig.getKnowledgeRepository(configData, new CacheServiceImpl())
        
//        then:
        kr != null
        kr.conceptDeterminationMethodService != null
        kr.conceptDeterminationMethodService.getAll().size() == 3
        kr.conceptService != null
        kr.executionEngineService != null
        kr.executionEngineService.getAll().size() == 3
        kr.knowledgeModuleService != null
        kr.knowledgeModuleService.getAll().size() == 24
        kr.knowledgePackageService != null
        kr.semanticSignifierService != null
        kr.semanticSignifierService.getAll().size() == 1
        kr.supportingDataPackageService != null
        kr.supportingDataService != null
        kr.supportingDataService.getAll().size() == 1
    }

}
