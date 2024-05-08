package org.opencds.service

import org.opencds.service.util.OpencdsClient
import org.xmlunit.matchers.CompareMatcher
import spock.lang.Specification

class BounceFunctionalSpec extends Specification {
    
    private static final String TEST_VMR = 'src/test/resources/samples/bounce/vmr.xml'
    private static final String TEST_VMR_OUTPUT = 'src/test/resources/samples/bounce/cds-output.xml'

    def "test"() {
        given:
        def input = new File(TEST_VMR).text
        def output = new File(TEST_VMR_OUTPUT).text
        def params = [
            kmEvaluationRequest:[scopingEntityId: 'org.opencds', businessId: 'bounce', version: '7'],
            ]
        
        when:
        def responsePayload = OpencdsClient.sendEvaluateMessage(params, input, false)

        then:
        notThrown(Exception)
        CompareMatcher.isSimilarTo(output)
                .ignoreWhitespace()
                .ignoreComments()
                .normalizeWhitespace()
                .ignoreElementContentWhitespace()
                .matches(responsePayload)
    }

}
