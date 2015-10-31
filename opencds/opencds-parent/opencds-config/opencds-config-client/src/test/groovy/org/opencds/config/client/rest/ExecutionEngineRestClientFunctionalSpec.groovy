package org.opencds.config.client.rest;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder

import spock.lang.Specification

class ExecutionEngineRestClientFunctionalSpec extends Specification {
    private static final String URI = "http://localhost:18080/opencds-decision-support-service/config/v1"
    private static final String EE = "src/test/resources/executionEngines.xml"

    RestClient restClient
    ExecutionEngineRestClient eeClient
    
    def setup() {
        Client c = ClientBuilder.newClient()
        restClient = new RestClient(c.target(URI), 'username', 'my1pass1')
        eeClient = new ExecutionEngineRestClient(restClient)
    }
    
    def "get execution engines"() {
        when:
        def ees = eeClient.getExecutionEngines(InputStream.class)
        println ees.text
        
        then:
        ees != null
    }

    def "put and get executionengines"() {
        given:
        def input = new FileInputStream(EE)
        
        when:
        eeClient.putExecutionEngines(input)
        
        then:
        def e = thrown(InternalServerErrorException)
        e.message == 'HTTP/1.1 500 Internal Server Error : Cannot persist to file store through dao API'
    }

    def "get specific executionengine"() {
        given:
        def id = 'org.opencds.client.drools.DroolsDecisionEngine53DSSEvaluationAdapter'
        
        when:
        def ee = eeClient.getExecutionEngine(id, InputStream.class)
        println ee.text
        
        then:
        ee != null
    }    
}
