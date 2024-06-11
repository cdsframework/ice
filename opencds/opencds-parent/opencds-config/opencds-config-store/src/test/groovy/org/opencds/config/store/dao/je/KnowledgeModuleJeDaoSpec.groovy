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

package org.opencds.config.store.dao.je;

import org.opencds.config.api.model.KnowledgeModule
import org.opencds.config.api.model.impl.KMIdImpl
import org.opencds.config.store.je.OpenCDSConfigStore

import spock.lang.Specification

class KnowledgeModuleJeDaoSpec extends Specification {
    static File path
    static OpenCDSConfigStore configStore
    
    static KnowledgeModuleJeDao dao
    
    def setupSpec() {
        path = File.createTempDir()
        println path.getAbsolutePath()
        configStore = new OpenCDSConfigStore(path)
        def files = path.listFiles()
        files.each {file ->
            println "file/size: ${file.getAbsolutePath()} ${file.length()}"
        }
        dao = new KnowledgeModuleJeDao(configStore)
    }
    
    def cleanupSpec() {
        configStore.close()
        def files = path.listFiles()
        files.each {file ->
            println "deleting ${file.getAbsolutePath()} ${file.length()}"
            assert file.delete() == true
        }
        println "deleting ${path.getAbsolutePath()} ${path.length()}"
        assert path.delete() == true
    }

    def "test save, then retrieve to ensure same values, but different object, then delete and find with no results"() {
        given:
        KnowledgeModule km = DaoHelper.createKnowledgeModule()
        
        when:
        dao.persist(km)
        
        and:
        def persistedKM = dao.find(KMIdImpl.create(km.getKMId()))
        
        and:
        dao.delete(persistedKM)
        
        and:
        def found = dao.find(KMIdImpl.create(km.getKMId()))
        
        then:
        persistedKM != null
        !km.is(persistedKM)
        !found
        km.kmId == persistedKM.kmId
        km.status == persistedKM.status
        km.executionEngine == persistedKM.executionEngine
        km.ssId == persistedKM.ssId
        km.primaryCDM == persistedKM.primaryCDM
        km.secondaryCDMs[0].cdmId == persistedKM.secondaryCDMs[0].cdmId
        km.packageType == persistedKM.packageType
        km.packageId == persistedKM.packageId
        km.primaryProcess == persistedKM.primaryProcess
        km.traitIds[0].scopingEntityId == persistedKM.traitIds[0].scopingEntityId
        km.traitIds[0].businessId == persistedKM.traitIds[0].businessId
        km.traitIds[0].version == persistedKM.traitIds[0].version
		km.preProcessPluginIds == persistedKM.preProcessPluginIds
		km.postProcessPluginIds == persistedKM.postProcessPluginIds
        km.timestamp == persistedKM.timestamp
        km.userId == persistedKM.userId
    }
    
    
}
