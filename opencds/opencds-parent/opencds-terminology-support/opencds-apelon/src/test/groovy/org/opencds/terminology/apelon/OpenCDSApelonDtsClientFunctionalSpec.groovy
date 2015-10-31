package org.opencds.terminology.apelon;

import spock.lang.Specification

class OpenCDSApelonDtsClientFunctionalSpec extends Specification {
    
    OpenCDSApelonDtsClient client

    def setup() {
        client = new OpenCDSApelonDtsClient('opencds.bmi.utah.edu', 16666, 'opencds.guest', 'welcome2opencds', 'OpenCDS')
    }
    
    def "find concept by name"() {
        when:
        def response = client.findConceptsWithProperty('OpenCDS Concept Type', '*')

        then:
        response
        response.size() == 81
    }
}
