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

import org.opencds.config.api.model.SemanticSignifier
import org.opencds.config.api.model.impl.SSIdImpl
import org.opencds.config.store.je.OpenCDSConfigStore

import spock.lang.Specification

class SemanticSignifierJeDaoSpec extends Specification {
    static File path
    static OpenCDSConfigStore configStore

    static SemanticSignifierJeDao dao
    
    def setupSpec() {
        path = File.createTempDir()
        println path.getAbsolutePath()
        configStore = new OpenCDSConfigStore(path)
        def files = path.listFiles()
        files.each {file ->
            println "file/size: ${file.getAbsolutePath()} ${file.length()}"
        }
        dao = new SemanticSignifierJeDao(configStore)
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

    def "test save then retrieve to ensure the same values, but different object, then delete, and find with no results"() {
        given:
        SemanticSignifier ss = DaoHelper.createSemanticSignifier()
        
        when:
        dao.persist(ss)
        
        and:
        def persistedSS = dao.find(SSIdImpl.create(ss.getSSId()))
        
        and:
        dao.delete(persistedSS)
        
        and:
        def found = dao.find(SSIdImpl.create(ss.getSSId()))
        
        then:
        persistedSS != null
        !ss.is(persistedSS)
        !found
        ss.ssId == persistedSS.ssId
        ss.name == persistedSS.name
        ss.description == persistedSS.description
        ss.xsdComputableDefinitions[0].url == persistedSS.xsdComputableDefinitions[0].url
        ss.entryPoint == persistedSS.entryPoint
        ss.exitPoint == persistedSS.exitPoint
        ss.timestamp == persistedSS.timestamp
        ss.userId == persistedSS.userId
    }
        
}
