package org.opencds.config.client.rest;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder

import spock.lang.Specification

class ConceptDeterminationMethodRestClientFunctionalSpec extends Specification {
    private static final String URI = "http://localhost:18080/opencds-decision-support-service/config/v1"
    private static final String CDM = "src/test/resources/conceptDeterminationMethods/cdm.xml"
    
    RestClient restClient
    ConceptDeterminationMethodRestClient client
    
    def setup() {
        Client c = ClientBuilder.newClient()
        restClient = new RestClient(c.target(URI), 'username', 'my1pass1')
        client = new ConceptDeterminationMethodRestClient(restClient)
    }

    def "get cdms"() {
        when:
        def result = client.getConceptDeterminationMethods(InputStream.class)
        println result.text.size()

        then:
        result != null
    }
    
    def "get unknown cdm"() {
        given:
        def cdmId = "2.16.840.1.113883.3.795.12.1.1^C00^1.0"
        
        when:
        def cdm = client.getConceptDeterminationMethod(cdmId, String.class)
        
        then:
        thrown(NotFoundException)
    }

    def "get known cdm"() {
        given:
        def cdmId = "2.16.840.1.113883.3.795.12.1.1^C74^1.0"
        
        when:
        def cdm = client.getConceptDeterminationMethod(cdmId, String.class)
        
        then:
        cdm != null
    }

    def "post cdms"() {
        given:
        String input = new File(CDM).text
        
        when:
        client.putConceptDeterminationMethods(input)
        
        then:
        def e = thrown(InternalServerErrorException)
        println e.message
    }
}
