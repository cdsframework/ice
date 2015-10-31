package org.opencds.service;

import org.custommonkey.xmlunit.XMLUnit

import spock.lang.Specification

public class OpenCDSWsdlFunctionalSpec extends Specification {

    private final String WS_URL = "http://localhost:38080/opencds-decision-support-service/evaluate?wsdl"
    private final String WSDL_TXT = "src/test/resources/opencds-v1.1-wsdl.txt"
    
    def "get the wsdl"() {
        given:
        def expectedText = new File(WSDL_TXT).text
        
        when:
        def wsdl = new URL(WS_URL).text
        
        XMLUnit.setIgnoreWhitespace(true)
        XMLUnit.setIgnoreComments(true)
        XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true)
        XMLUnit.setNormalizeWhitespace(true)        
        def diff = XMLUnit.compareXML(expectedText, wsdl)
        
        then:
        diff.identical()
    }

    
}
