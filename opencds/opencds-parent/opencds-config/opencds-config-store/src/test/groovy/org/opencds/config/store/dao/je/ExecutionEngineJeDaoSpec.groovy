package org.opencds.config.store.dao.je;

import org.opencds.config.api.model.ExecutionEngine
import org.opencds.config.api.model.impl.SSIdImpl
import org.opencds.config.store.je.OpenCDSConfigStore

import spock.lang.Specification

class ExecutionEngineJeDaoSpec extends Specification {
    static File path
    static OpenCDSConfigStore configStore

    static ExecutionEngineJeDao dao

    def setupSpec() {
        path = File.createTempDir()
        println path.getAbsolutePath()
        configStore = new OpenCDSConfigStore(path)
        def files = path.listFiles()
        files.each {file -> println "file/size: ${file.getAbsolutePath()} ${file.length()}" }
        dao = new ExecutionEngineJeDao(configStore)
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
        ExecutionEngine ee = DaoHelper.createExecutionEngine()

        when:
        dao.persist(ee)

        and:
        def persistedEE = dao.find(ee.getIdentifier())

        and:
        dao.delete(persistedEE)

        and:
        def found = dao.find(ee.getIdentifier())

        then:
        persistedEE != null
        !ee.is(persistedEE)
        !found
        ee.identifier == persistedEE.identifier
        ee.description == persistedEE.description
        ee.supportedOperations[0] == persistedEE.supportedOperations[0]
        ee.timestamp == persistedEE.timestamp
        ee.userId == persistedEE.userId
    }
}
