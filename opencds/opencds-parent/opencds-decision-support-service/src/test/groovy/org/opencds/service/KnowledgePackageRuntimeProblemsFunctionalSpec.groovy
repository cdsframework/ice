package org.opencds.service;

import org.opencds.service.util.OpencdsClient
import org.springframework.ws.soap.client.SoapFaultClientException

import spock.lang.Specification

import org.springframework.ws.soap.client.SoapFaultClientException

import spock.lang.Specification

class KnowledgePackageRuntimeProblemsFunctionalSpec extends Specification {
    
    private final String VALID_HIERARCHY = "src/test/resources/samples/op-13/op-13-fixed-hierarchy.xml"
    
    def "attempt to load a module that doesn't have a package"() {
        given:
        def input = new File(VALID_HIERARCHY).text
        def params = [
            kmEvaluationRequest:[scopingEntityId: 'does-not-exist', businessId: 'dummy', version: '0.0.1'],
            ]
        
        when:
        def responsePayload = OpencdsClient.sendEvaluateMessage(params, input)
        
        then:
        def ex = thrown(SoapFaultClientException)
        ex.message == 'org.omg.dss.DSSRuntimeExceptionFault: OpenCDS call to Drools.execute failed with error: org.opencds.common.exceptions.OpenCDSRuntimeException: KnowledgeModule package cannot be found (possibly due to misconfiguration?); packageId= nada.pkg, packageType= PKG'
    }

}