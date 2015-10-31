package org.opencds.service;

import org.opencds.service.util.OpencdsClient
import org.springframework.ws.soap.client.SoapFaultClientException

import spock.lang.Specification

public class Op18FunctionalSpec extends Specification {

    private final String EXPECTED_RESULT_PATH = "src/test/resources/samples/op-18/OP18.valid-RESULT.xml"
    private final String INPUT_PATH = "src/test/resources/samples/op-18/OP18.valid.xml"
    private final String VALID_LOW_PATH = "src/test/resources/samples/op-18/OP18.valid.low.xml"
    private final String VALID_LOW_RESULT_PATH = "src/test/resources/samples/op-18/OP18.valid.low-RESULT.xml"
    private final String VALID_HIGH_PATH = "src/test/resources/samples/op-18/OP18.valid.high.xml"
    private final String VALID_HIGH_RESULT_PATH = "src/test/resources/samples/op-18/OP18.valid.high-RESULT.xml"
    private final String INVALID_EET_PATH = "src/test/resources/samples/op-18/OP18.invalid.encountereventtime.xml"
    
    def "test an input to opencds"() {
        given:
        def input = new File(INPUT_PATH).text
        def expectedResult = new File(EXPECTED_RESULT_PATH).text
        def params = [
            kmEvaluationRequest:[scopingEntityId: 'org.opencds', businessId: 'bounce', version: '1.5.5'],
            containingEntityId:[scopingEntityId: 'edu.utah.bmi', businessId: 'bounceData', version: '1.0.0']
            ]
        
        when:
        def responsePayload = OpencdsClient.sendEvaluateMessage(params, input)
        
        then:
        def data = new XmlSlurper().parseText(responsePayload)
        data.vmrOutput.templateId.@root.text() == "2.16.840.1.113883.3.1829.11.1.2.1"
    }
    
    def "test an input to opencds with valid EET.low"() {
        given:
        def input = new File(VALID_LOW_PATH).text
        def expectedResult = new File(VALID_LOW_RESULT_PATH).text
        def params = [
            kmEvaluationRequest:[scopingEntityId: 'org.opencds', businessId: 'bounce', version: '1.5.5'],
            containingEntityId:[scopingEntityId: 'edu.utah.bmi', businessId: 'bounceData', version: '1.0.0']
            ]
        
        when:
        def responsePayload = OpencdsClient.sendEvaluateMessage(params, input)
        
        then:
        def data = new XmlSlurper().parseText(responsePayload)
        data.vmrOutput.templateId.@root.text() == "2.16.840.1.113883.3.1829.11.1.2.1"
    }
    
    def "test an input to opencds with valid EET.high"() {
        given:
        def input = new File(VALID_HIGH_PATH).text
        def expectedResult = new File(VALID_HIGH_RESULT_PATH).text
        def params = [
            kmEvaluationRequest:[scopingEntityId: 'org.opencds', businessId: 'bounce', version: '1.5.5'],
            containingEntityId:[scopingEntityId: 'edu.utah.bmi', businessId: 'bounceData', version: '1.0.0']
            ]
        
        when:
        def responsePayload = OpencdsClient.sendEvaluateMessage(params, input)
        
        then:
        def data = new XmlSlurper().parseText(responsePayload)
        data.vmrOutput.templateId.@root.text() == "2.16.840.1.113883.3.1829.11.1.2.1"
    }
    
    def "test an input to opencds with invalid encountereventtimes"() {
        given:
        def input = new File(INVALID_EET_PATH).text
        def expectedResult = new File(EXPECTED_RESULT_PATH).text
        def params = [
            kmEvaluationRequest:[scopingEntityId: 'org.opencds', businessId: 'bounce', version: '1.5.5'],
            containingEntityId:[scopingEntityId: 'edu.utah.bmi', businessId: 'bounceData', version: '1.0.0']
            ]
        
        when:
        def responsePayload = OpencdsClient.sendEvaluateMessage(params, input)
        
        then:
        def ex = thrown(SoapFaultClientException)
        ex.message.startsWith("InvalidDataException error in CdsInputFactListsBuilder: EncounterEventTime must have non-null low or high value, therefore unable to complete unmarshalling input Semantic Payload")
    }
    
}
