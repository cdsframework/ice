package org.opencds.config.store.dao.je;

import org.opencds.config.api.model.SupportingData
import org.opencds.config.api.model.impl.KMIdImpl
import org.opencds.config.store.je.OpenCDSConfigStore

import spock.lang.Specification

class SupportingDataJeDaoSpec extends Specification {
    static File path
    static OpenCDSConfigStore configStore

    static SupportingDataJeDao dao
    
    def setupSpec() {
        path = File.createTempDir()
        println path.getAbsolutePath()
        configStore = new OpenCDSConfigStore(path)
        def files = path.listFiles()
        files.each {file ->
            println "file/size: ${file.getAbsolutePath()} ${file.length()}"
        }
        dao = new SupportingDataJeDao(configStore)
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
        SupportingData sd = DaoHelper.createSupportingData()
        
        when:
        dao.persist(sd)
        
        and:
        def persistedSD = dao.find(KMIdImpl.create(sd.getKMId()))
        
        and:
        dao.delete(persistedSD[0])
        
        and:
        def found = dao.find(KMIdImpl.create(sd.getKMId()))
        
        then:
        persistedSD != null
        !sd.is(persistedSD)
        !found
        sd.identifier == persistedSD[0].identifier
        sd.kmId == persistedSD[0].kmId
        sd.packageId == persistedSD[0].packageId
        sd.packageType == persistedSD[0].packageType
        sd.timestamp == persistedSD[0].timestamp
        sd.userId == persistedSD[0].userId
    }
    
}
