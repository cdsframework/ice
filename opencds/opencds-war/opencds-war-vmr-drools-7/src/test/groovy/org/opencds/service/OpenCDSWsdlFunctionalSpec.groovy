package org.opencds.service

import org.xmlunit.matchers.CompareMatcher
import spock.lang.Specification

public class OpenCDSWsdlFunctionalSpec extends Specification {

    private final String WS_URL = "http://localhost:38180/opencds-test/evaluate?wsdl"
    private final String WSDL_TXT = "src/test/resources/opencds-v1.1-wsdl-full.xml"
    private final String WSDL_TEST_TXT = "src/test/resources/opencds-war-test-wsdl.xml"

    def "get the wsdl"() {
        given:
        def expectedText = new File(WSDL_TEST_TXT).text
        
        when:
        def wsdl = new URL(WS_URL).text

        then:
        CompareMatcher.isSimilarTo(expectedText)
                .ignoreWhitespace()
                .ignoreComments()
                .normalizeWhitespace()
                .ignoreElementContentWhitespace()
                .matches(wsdl)
    }

    
}
