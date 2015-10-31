package org.opencds.terminology.apelon;

import spock.lang.Specification

public class XmlRequestParserSpec extends Specification {

    private static final String REQUEST = '<?xml version="1.0" encoding="UTF-8"?><ApelonDtsServiceRequest><queryType>findConceptByName</queryType><code>Concept Determination Method</code></ApelonDtsServiceRequest>'

    XmlRequestParser parser

    def setup() {
        parser = new XmlRequestParser()
    }

    def "test getParameters on request"() {
        given:
        def inputStream = new ByteArrayInputStream(REQUEST.bytes)

        when:
        def map = parser.getParameters(inputStream)

        then:
        map == [code: 'Concept Determination Method',
            queryType: 'findConceptByName']
    }
    
}
