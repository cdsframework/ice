package org.opencds.config.store.dao.je;

import org.opencds.config.api.model.ConceptDeterminationMethod
import org.opencds.config.store.je.OpenCDSConfigStore
import org.opencds.config.store.model.je.CDMIdJe

import spock.lang.Specification

class CdmJeDaoSpec extends Specification {
    static File path
    static OpenCDSConfigStore configStore
    
    static ConceptDeterminationMethodJeDao dao

    def setupSpec() {
        path = File.createTempDir()
        println path.getAbsolutePath()
        configStore = new OpenCDSConfigStore(path)
        def files = path.listFiles()
        files.each {file ->
            println "file/size: ${file.getAbsolutePath()} ${file.length()}"
        }
        dao = new ConceptDeterminationMethodJeDao(configStore)
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

    def "test save then retrieve to ensure the same values, but different object, then delete and find with no results"() {
        given:
        ConceptDeterminationMethod cdm = DaoHelper.createCDM()

        when:
        dao.persist(cdm)
        
        and:
        def persistedCdm = dao.find(CDMIdJe.create(cdm.getCDMId()))
        
        and:
        dao.delete(persistedCdm)
        
        and:
        def found = dao.find(CDMIdJe.create(cdm.getCDMId()))
        
        then:
        !persistedCdm.is(cdm)
        persistedCdm.cdmId.code == cdm.cdmId.code
        persistedCdm.cdmId.codeSystem == cdm.cdmId.codeSystem
        persistedCdm.cdmId.version == cdm.cdmId.version
        dao.delete(persistedCdm)
        !found
    }
    
    def "test multiple saves with more complex object trees, and validate"() {
        given:
        def cdm1 = DaoHelper.createCDM(4,5)
        def cdm2 = DaoHelper.createCDM(10,3)
        
        when:
        dao.persist(cdm1)
        dao.persist(cdm2)
        def pcdm1 = dao.find(CDMIdJe.create(cdm1.CDMId))
        def pcdm2 = dao.find(CDMIdJe.create(cdm2.CDMId))
        def allCdms = dao.getAllCE()

        then:
        !pcdm1.is(cdm1)
        pcdm1.cdmId.code == cdm1.cdmId.code
        pcdm1.cdmId.codeSystem == cdm1.cdmId.codeSystem
        pcdm1.cdmId.version == cdm1.cdmId.version
        !pcdm2.is(cdm2)
        pcdm2.cdmId.code == cdm2.cdmId.code
        pcdm2.cdmId.codeSystem == cdm2.cdmId.codeSystem
        pcdm2.cdmId.version == cdm2.cdmId.version
        def pcm1 = pcdm1.conceptMappings
        def pcm2 = pcdm2.conceptMappings
        def cm1 = cdm1.conceptMappings
        def cm2 = cdm2.conceptMappings
        pcm1.size() == 4
        pcm2.size() == 10
        allCdms
        allCdms.size() == 2
    }
}
