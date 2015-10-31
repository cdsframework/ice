package org.opencds.config.client.rest

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder

import spock.lang.Specification

class RestAuthFunctionalSpec extends Specification {
    private static final String URI = "http://localhost:18080/opencds-decision-support-service/config/v1"
    
    Client c
    RestClient restClient
    ConceptDeterminationMethodRestClient client
    
    def "wrong username"() {
        given:
        c = ClientBuilder.newClient()
        restClient = new RestClient(c.target(URI), 'some-other-user', 'bad pass')
        client = new ConceptDeterminationMethodRestClient(restClient)
        
        when:
        def result = client.getConceptDeterminationMethods(String.class)
        
        then:
        def e = thrown(NotAuthorizedException)
        e.message == 'HTTP 401 Unauthorized'
    }
    
    def "correct username, wrong password"() {
        given:
        c = ClientBuilder.newClient()
        restClient = new RestClient(c.target(URI), 'username', 'bad pass')
        client = new ConceptDeterminationMethodRestClient(restClient)
        
        when:
        def result = client.getConceptDeterminationMethods(String.class)
        
        then:
        def e = thrown(NotAuthorizedException)
        e.message == 'HTTP 401 Unauthorized'
    }
    
}
