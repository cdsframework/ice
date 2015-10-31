package org.opencds.service;

import org.opencds.service.util.OpencdsClient
import org.springframework.ws.soap.client.SoapFaultClientException

import spock.lang.Specification

class Op13FunctionalSpec extends Specification {
    
    private final String INVALID_HIERARCHY_DUP_IDS = "src/test/resources/samples/op-13/op-13-invalid-hierarchy-dup-ids.xml"
    private final String VALID_HIERARCHY = "src/test/resources/samples/op-13/op-13-fixed-hierarchy.xml"
    
    def "test op-13 fix to potential stackoverflow"() {
        given:
        def input = new File(INVALID_HIERARCHY_DUP_IDS).text
        def params = [
            kmEvaluationRequest:[scopingEntityId: 'org.opencds', businessId: 'bounce', version: '1.5.5'],
            containingEntityId:[scopingEntityId: 'edu.utah.bmi', businessId: 'bounceData', version: '1.0.0']
            ]
        
        when:
        def responsePayload = OpencdsClient.sendEvaluateMessage(params, input)
        
        then:
        def ex = thrown(SoapFaultClientException)
        ex.message.startsWith("RuntimeException in CdsInputFactListsBuilder: root and extension of source and target ID of ClinicalStatementRelationship may not be the same")
    }

    def "test op-13 with valid hierarchy"() {
        given:
        def input = new File(VALID_HIERARCHY).text
        def params = [
            kmEvaluationRequest:[scopingEntityId: 'org.opencds', businessId: 'bounce', version: '1.5.5'],
            containingEntityId:[scopingEntityId: 'edu.utah.bmi', businessId: 'bounceData', version: '1.0.0']
            ]
        
        when:
        def responsePayload = OpencdsClient.sendEvaluateMessage(params, input)
        
        then:
        notThrown(SoapFaultClientException)
    }
        
}