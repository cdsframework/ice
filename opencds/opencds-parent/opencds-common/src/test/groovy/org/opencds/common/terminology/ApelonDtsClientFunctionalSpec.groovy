package org.opencds.common.terminology;

import org.opencds.common.xml.XmlEntity
import org.opencds.common.xml.XmlHttpSender;

import spock.lang.Specification

class ApelonDtsClientFunctionalSpec extends Specification {

	ApelonDtsClient client

	def setup() {
		client = new ApelonDtsClient("http://localhost:8080");
//        client = new ApelonDtsClient("http://tolven.chpc.utah.edu:8084");
	}
        
    def "successfully find concept by name"() {
        when:
        def responseEntity = client.findConceptByName("Concept Determination Method")
        
        then:
        def conceptEntity = responseEntity.firstChild
        conceptEntity.label == "OpenCdsConcept"
    }
    
    def "Empty XmlEntity returned when name not found"() {
        when:
        def notFound = client.findConceptByName("don't find me")
        
        then:
        notFound.children.empty
    }
    
    def "Error returned on null name"() {
        when:
        def nullValue = client.findConceptByName(null);
        def nullValueError = nullValue.getFirstChildWithLabel("Error")
        
        then:
        nullValueError
    }
    
    def "successfully find concept by code"() {
        when:
        def responseEntity = client.findConceptByCode("C35")
        
        then:
        def conceptEntity = responseEntity.firstChild
        conceptEntity.label == "OpenCdsConcept"
    }
    
    def "Empty XmlEntity returned when code not found"() {
        when:
        def notFound = client.findConceptByCode("don't find me")
        
        then:
        notFound.children.empty
    }
    
    def "Error returned on null code"() {
        when:
        def nullValue = client.findConceptByCode(null);
        def nullValueError = nullValue.getFirstChildWithLabel("Error")
        
        then:
        nullValueError
    }
    
    def "find direct children using name and code, should be equal"() {
        when:
        def byName = client.findDirectChildrenOfConcept("Concept Determination Method", false)
        def byCode = client.findDirectChildrenOfConcept("C35", false)
        
        then:
        byName
        byCode
        byName.children
        byCode.children
        byName == byCode
    }
    
    def "find direct children including and excluding root"() {
        when:
        def withRoot = client.findDirectChildrenOfConcept("Concept Determination Method", true)
        def noRoot = client.findDirectChildrenOfConcept("Concept Determination Method", false)
        
        then:
        withRoot
        noRoot
        withRoot.children
        noRoot.children
        withRoot.children.size == noRoot.children.size + 1
    }
    
    def "No children when identifier not found"() {
        when:
        def notFound = client.findDirectChildrenOfConcept("don't find me", false)
        
        then:
        notFound.children.empty
    }
    
    def "Get Error on null identifier"() {
        when:
        def nullValue = client.findDirectChildrenOfConcept(null, false);
        def nullValueError = nullValue.getFirstChildWithLabel("Error")
        
        then:
        nullValueError
    }
    
    def "Find descencdants using name and code, should be equal"() {
        when:
        def byName = client.findDescendantsOfConcept("Concept Determination Method", 10, false)
        def byCode = client.findDescendantsOfConcept("C35", 10, false)
        
        then:
        byName
        byCode
        byName == byCode
    }
    
    def "Find descendants including and excluding root"() {
        when:
        def withRoot = client.findDescendantsOfConcept("Concept Determination Method", 10, true)
        def noRoot = client.findDescendantsOfConcept("Concept Determination Method", 10, false)
        
        then:
        withRoot
        noRoot
        withRoot.children.size == noRoot.children.size + 1
    }
    
    def "No children entities when root identifier not found"() {
        when:
        def notFound = client.findDescendantsOfConcept("don't find me", 10, false)
        
        then:
        notFound.children.empty
    }
    
    def "Error when root identifier is null"() {
        when:
        def nullValue = client.findDescendantsOfConcept(null, 10, false);
        def nullValueError = nullValue.getFirstChildWithLabel("Error")
        
        then:
        nullValueError
    }
    
    def "Error when depth invalid"() {
        when:
        def invalidDepth = client.findDescendantsOfConcept("C35", -1, false);
        def invalidError = invalidDepth.getFirstChildWithLabel("Error")
        
        then:
        invalidError
    }
    
    def "Successful wildcard property value match"() {
        when:
        def allWithProperty = client.findConceptsWithPropertyMatching("Description", "*")
        
        then:
        allWithProperty.children.size > 1
    }
    
    def "One child entity on specific property value match"() {
        when:
        def specificPropertyValue = client.findConceptsWithPropertyMatching("Description", "Agency for Healthcare Research*")
        
        then:
        specificPropertyValue.children.size == 1
    }
    
    def "No children entities when property value not found"() {
        when:
        def badPropertyValue = client.findConceptsWithPropertyMatching("Description", "jkadjasfdjkal")
        
        then:
        badPropertyValue
        badPropertyValue.children.size == 0
    }
    
    def "Error on null property value"() {
        when:
        def noPropertyValue = client.findConceptsWithPropertyMatching("Description", null)
        def noPropertyValueError = noPropertyValue.getFirstChildWithLabel("Error")
        
        then:
        noPropertyValueError
    }
    
    def "Error when property type not found"() {
        when:
        def badPropertyType = client.findConceptsWithPropertyMatching("Don't find me", "*")
        def badPropertyTypeError = badPropertyType.getFirstChildWithLabel("Error")
        
        then:
        badPropertyTypeError
    }
    
    def "Error on null property type"() {
        when:
        def noPropertyType = client.findConceptsWithPropertyMatching(null, "*")
        def noPropertyTypeError = noPropertyType.getFirstChildWithLabel("Error")
        
        then:
        noPropertyTypeError
    }
    
    def "send request get response"() {
        given:
        def openCdsApelonServiceHostUrl = "http://localhost:8080";

        when:
        def requestEntity = new XmlEntity("Request")
        requestEntity.addChild(new XmlEntity("queryType", "findConceptByName", false))    
        requestEntity.addChild(new XmlEntity("name", "Concept Determination Method", false))    
        def responseEntity = client.sendRequestGetResponse(openCdsApelonServiceHostUrl, requestEntity)
        def responseString = responseEntity.getAsConciseXmlString(true, false)   
              
        then:
        responseEntity
        responseString
    }

}
