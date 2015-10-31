package org.opencds.terminology.apelon;

import spock.lang.Specification

class ApelonDtsClientFunctionalSpec extends Specification {
    
    ApelonDtsClient client
    
    def setup() {
        client = new OpenCDSApelonDtsClient("opencds.bmi.utah.edu", 16666,
            "opencds.guest", "welcome2opencds", "OpenCDS")        
    }
    
    def "find concept by name"() {
        when:
        def concept = client.findConceptByName("Concept Determination Method")
       
        then:
        concept
        concept.code == "C35"        
    }
    
    def "concept not found by name"() {
        when:
        def badConcept = client.findConceptByName("Don't find me")
        
        then:
        badConcept == null
    }
    
    def "Exception thrown on null name"() {
        when:
       def nullConcept = client.findConceptByName(null)
        
        then:
        thrown(ApelonClientException)
        nullConcept == null
    }
    
    def "find concept by code"() {
        when:
        def concept = client.findConceptByCode("C35")
        
        then:
        concept
        concept.name == "Concept Determination Method"        
    }
    
    def "concept not found by code"() {
        when:
        def badConcept = client.findConceptByCode("Don't find me")
        
        then:
        badConcept == null       
    }
    
    def "Exception thrown on null code"() {
        when:
        def nullConcept = client.findConceptByCode(null)
        
        then:
        thrown(ApelonClientException)
        nullConcept == null
    }
    
    def "children concepts found including and excluding parent"() {
        given:
        def parentConcept = client.findConceptByName("Concept Determination Method")
        
        when:
        def children = client.findDirectChildrenOfConcept(parentConcept, false)
        def childrenAndParent = client.findDirectChildrenOfConcept(parentConcept, true)
        
        then:
        children
        childrenAndParent
        childrenAndParent.size() == children.size() + 1
    }
    
    def "empty list returned when no children found"() {
        given:
        def noChildrenConcept = client.findConceptByName("NQF")
        
        when:
        def noChildren = client.findDirectChildrenOfConcept(noChildrenConcept, false)
        
        then:
        noChildren.empty        
    }
    
    def "Exception thrown on null parent"() {
        when:
        def nullConcept = client.findDirectChildrenOfConcept(null, false)
        
        then:
        thrown(ApelonClientException)
        nullConcept == null
    }
    
    def "descendant concepts found including and excluding root concept"() {
        given:
        def rootConcept = client.findConceptByName("Concept Determination Method")
                
        when:
        def directDecendants = client.findDescendantsOfConcept(rootConcept, 1, false)
        def directDecendantsAndRoot = client.findDescendantsOfConcept(rootConcept, 1, true)
        
        then:
        directDecendants
        directDecendantsAndRoot
        directDecendantsAndRoot.size() == directDecendants.size() + 1       
    }
    
    def "more descendant concepts found in deeper levels"() {
        given:
        def rootConcept = client.findConceptByName("OpenCDS Templates (node)")
                
        when:
        def directDecendants = client.findDescendantsOfConcept(rootConcept, 1, false)
        def decendants = client.findDescendantsOfConcept(rootConcept, 2, false)
                
        then:
        directDecendants
        decendants
        decendants.size() > directDecendants.size()        
    }
    
    def "Exception thrown when finding descendants with invalid depth"() {
        given:
        def rootConcept = client.findConceptByName("Concept Determination Method")

        when:
        def invalidDepth = client.findDescendantsOfConcept(rootConcept, -1, false)

        then:
        invalidDepth == null
        thrown(Exception)
    }
    
    def "ApelonClientException thrown when root concept is null"() {
        when:
        def nullConcept = client.findDescendantsOfConcept(null, 2, false)
       
        then:       
        nullConcept == null
        thrown(ApelonClientException)
        
    }
    
    def "successfully find by property"() {
        when:
        def concepts = client.findConceptsWithProperty("Description", "*")
                
        then:
        concepts
    }
    
    def "Find property type but no matching values"() {
        when:
        def notFoundValue = client.findConceptsWithProperty("Description", "don't find me")
                
        then:
        notFoundValue.empty
    }
    
    def "Exception thrown when propertyType not found"() {
        when:
        def notFoundType = client.findConceptsWithProperty("Don't find me", "*")
       
        then:        
        notFoundType == null
        thrown(Exception)
    }
      
    def "successfully find namespace id by name"() {
        when:
        def goodNsId = client.findNamespaceId("OpenCDS")
       
        then:
        goodNsId               
    }
    
    def "Return -1 when namespace id not found by name"() {
        when:
        def badNsId = client.findNamespaceId("Don't find me")
       
        then:
        badNsId == -1
    }
    
    def "Throw exception when namespace name is null"() {
        when:
        def nullParam = client.findNamespaceId(null)
        
        then:
        nullParam == null
        thrown(Exception)
    }
	
	def "test getSecureSocketConnection"() {
		when:
		def conn = client.getSecureSocketConnection()
		
		then:
        conn
		notThrown(Exception)		
	}
    
}
