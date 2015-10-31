package org.opencds.common.terminology;

import org.opencds.common.xml.XmlEntity
import org.opencds.common.xml.XmlHttpSender;

import spock.lang.Specification

class VMREnumerationsClientFunctionalSpec extends Specification {

	VMREnumerationsClient client

    def setupSpec() {
        System.getProperties().put(VMREnumerationsClient.APELON_SERVICE_URL_PROPERTY_NAME, "http://localhost:18080/opencds-apelon/apelonDtsService")
    }
    
    def cleanupSpec() {
        System.getProperties().remove(VMREnumerationsClient.APELON_SERVICE_URL_PROPERTY_NAME)
    }
    
	def setup() {
		client = new VMREnumerationsClient()
	}
    
    def "successfully get medication Concept"() {
        when:
        def medicationConcept = client.getOpenCdsConcepts("medication")
        
        then:
        medicationConcept
    }
    
    def "successfully get problem Concept"() {
        when:
        def problemConcept = client.getOpenCdsConcepts("problem")
        
        then:
        problemConcept
    }
    
    def "successfully get medicationClass Concept"() {
        when:
        def medicationClassConcept = client.getOpenCdsConcepts("medicationClass")
        
        then:
        medicationClassConcept
    }
    
    def "successfully get calendar units"() {
        when:
        def calendarUnits = client.getOpenCdsConcepts("calendarUnit")
        
        then:
        calendarUnits
    }
    
    def "successfully get assertions"() {
        when:
        def assertions = client.getOpenCdsConcepts("assertion")
        
        then:
        assertions
    }
    
    def "successfully get determination methods"() {
        when:
        def determinationMethods = client.getOpenCdsConcepts("determinationMethod")
        
        then:
        determinationMethods
    }
    
    def "successfully get all concepts"() {
        when:
        def all = client.getOpenCdsConcepts("all")

        then:        
        all
    }
    
    def "return empty list if not found"() {
        when:
        def concepts = client.getOpenCdsConcepts("notFound")
        
        then:
        concepts.empty
    }
    
    def "throw exception when search string is null"() {
        when:
        def nullConcept = client.getOpenCdsConcepts(null)
        
        then:
        nullConcept == null
        thrown(Exception)
    }
    
    def "test getting clinicalStatementRelationships"() {
        when:
        def clinicalStatementRelationship = client.getOpenCdsConcepts("clinicalStatementRelationship")
        
        then:
        clinicalStatementRelationship
    }
        
    def "successfully get code=name strings from response XmlEntity"() {
        given:
        def code = "testCode1"
        def name = "testName1"
        def rootEntity = new XmlEntity("root")
        def conceptEntity = new XmlEntity("OpenCdsConcept")
        conceptEntity.addChild(new XmlEntity("Code_in_Source", code, false))
        conceptEntity.addChild(new XmlEntity("Name", name, false))
        
        when:
        rootEntity.addChild(conceptEntity)
        def codeNameList = client.getConceptCodeNameStringsFromResponse(rootEntity)
        
        then: 
        codeNameList
        codeNameList.get(0) == code + "=" + name;
    }  
    
    def "Return empty list if no concepts"() {
        given:
        def rootEntity = new XmlEntity("root")
        
        when:
        def emptyList= client.getConceptCodeNameStringsFromResponse(rootEntity)
        
        then:
        emptyList != null
        emptyList.empty
    }
    
    def "Exception thrown if concepts found with no Code_in_source or Name labels"() {
        given:
        def rootEntity = new XmlEntity("root")
        def conceptEntity = new XmlEntity("OpenCdsConcept")
        
        when:
        rootEntity.addChild(conceptEntity)
        def nullList = client.getConceptCodeNameStringsFromResponse(rootEntity)
        
        then:
        nullList == null
        thrown(Exception)
    }   

}
