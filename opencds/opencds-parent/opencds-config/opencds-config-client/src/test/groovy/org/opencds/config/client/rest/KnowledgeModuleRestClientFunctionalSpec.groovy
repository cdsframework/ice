package org.opencds.config.client.rest;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder

import spock.lang.Specification

class KnowledgeModuleRestClientFunctionalSpec extends Specification {
    private static final String URI = "http://localhost:18080/opencds-decision-support-service/config/v1"
    private static final String KM = "src/test/resources/knowledgeModules.xml"
    private static final String KP = "src/test/resources/knowledgePackages/org.opencds^bounce^1.5.5.drl"
    
    RestClient restClient
    KnowledgeModuleRestClient client
    
    def setup() {
        Client c = ClientBuilder.newClient()
        restClient = new RestClient(c.target(URI), 'username', 'my1pass1')
        client = new KnowledgeModuleRestClient(restClient)
    }
    
    def "get knowledge modules"() {
        when:
        def result = client.getKnowledgeModules(InputStream.class)
        println result.text
        
        then:
        result != null
    }
    
    def "put and get knowledge modules"() {
        given:
        def km = new FileInputStream(KM)
        
        when:
        client.postKnowledgeModule(km)
        
        and:
        def result = client.getKnowledgeModules(InputStream.class)
        println result.text
        km.close()
        
        then:
        result != null
    }
    
    def "put and get knowledge module and package"() {
        given:
        def km = new FileInputStream(KM)
        def kp = new FileInputStream(KP)
        def kpsize = new File(KP).size()
        def kmid = "org.opencds^bounce^1.5.5"
        
        when:
        client.postKnowledgeModule(km)
        
        and:
        client.putKnowledgePackage(kmid, kp)
        def pkg = client.getKnowledgePackage(kmid, InputStream.class)
        def pkgsize = pkg.getBytes().length
        
        and:
        client.deleteKnowledgePackage(kmid)
        
        then:
        pkg
        kpsize == pkgsize
        def e = thrown(InternalServerErrorException)
        e.message == 'HTTP/1.1 500 Internal Server Error : Operation not supported on this type of FileDao'
    }
}
