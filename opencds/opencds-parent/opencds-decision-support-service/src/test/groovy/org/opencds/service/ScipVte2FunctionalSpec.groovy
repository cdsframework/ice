package org.opencds.service;

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.IgnoreTextAndAttributeValuesDifferenceListener
import org.custommonkey.xmlunit.examples.RecursiveElementNameAndTextQualifier
import org.opencds.service.util.OpencdsClient
import org.springframework.ws.soap.client.SoapFaultClientException

import spock.lang.Specification

public class ScipVte2FunctionalSpec extends Specification {

    private final String EXPECTED_RESULT_PATH = "src/test/resources/samples/SCIP_VTE_2-RESULT.xml"
    private final String INPUT_PATH = "src/test/resources/samples/SCIP_VTE_2.xml"
    
    def "test SCIP_VTE to opencds"() {
        given:
        def input = new File(INPUT_PATH).text
        def expectedResult = new File(EXPECTED_RESULT_PATH).text
        def params = [
            kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'SCIP_VTE_2', version: '2014.0.0'],
            containingEntityId:[scopingEntityId: 'edu.utah', businessId: 'SCIP_VTE_2', version: '2014.0.0']
        ]
            
        when:
        def responsePayload = OpencdsClient.sendEvaluateMessage(params, input)
		println responsePayload
		
        then:
        def data = new XmlSlurper().parseText(responsePayload)
        
        data.vmrOutput.templateId.@root.text() == "2.16.840.1.113883.3.1829.11.1.2.1"
        data.vmrOutput.patient.clinicalStatements.observationResults.observationResult[2].relatedClinicalStatement[0].observationResult.id.@root.text() == '2.16.840.1.113883.3.795.5.1'
        data.vmrOutput.patient.clinicalStatements.observationResults.observationResult[2].relatedClinicalStatement[0].observationResult.id.@extension.text() == 'denominator'
        data.vmrOutput.patient.clinicalStatements.observationResults.observationResult[2].relatedClinicalStatement[0].observationResult.observationFocus.@codeSystem.text() == '2.16.840.1.113883.3.795.12.1.1'
        data.vmrOutput.patient.clinicalStatements.observationResults.observationResult[2].relatedClinicalStatement[0].observationResult.observationFocus.@code.text() == 'C529'
        data.vmrOutput.patient.clinicalStatements.observationResults.observationResult[2].relatedClinicalStatement[1].observationResult.id.@root.text() == '2.16.840.1.113883.3.795.5.1'
        data.vmrOutput.patient.clinicalStatements.observationResults.observationResult[2].relatedClinicalStatement[1].observationResult.id.@extension.text() == 'numerator'
        data.vmrOutput.patient.clinicalStatements.observationResults.observationResult[2].relatedClinicalStatement[1].observationResult.observationFocus.@codeSystem.text() == '2.16.840.1.113883.3.795.12.1.1'
        data.vmrOutput.patient.clinicalStatements.observationResults.observationResult[2].relatedClinicalStatement[1].observationResult.observationFocus.@code.text() == 'C529'
        
//        and:"xml differencing with xmlunit"
//        Diff diff = new Diff(expectedResult, responsePayload)
//        diff.overrideDifferenceListener(new IgnoreTextAndAttributeValuesDifferenceListener())
//        diff.overrideElementQualifier(new RecursiveElementNameAndTextQualifier())
//        diff.similar()
    }
    
}
