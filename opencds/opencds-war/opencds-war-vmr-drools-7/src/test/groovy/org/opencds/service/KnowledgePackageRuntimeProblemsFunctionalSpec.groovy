package org.opencds.service;

import org.opencds.service.util.OpencdsClient
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
        def responsePayload = OpencdsClient.sendEvaluateMessage(params, input, false)

        then:
        def ex = thrown(SoapFaultClientException)
        ex.soapFault.faultDetail.result.node.firstChild.nodeName.endsWith 'EvaluationException'
        ex.soapFault.faultStringOrReason == 'org.opencds.common.exceptions.OpenCDSConfigurationException: Exception when attempting to load knowledge package: nada.pkg'
    }

}
