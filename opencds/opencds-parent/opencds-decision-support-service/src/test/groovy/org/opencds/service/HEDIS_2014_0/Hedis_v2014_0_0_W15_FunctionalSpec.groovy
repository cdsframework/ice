package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_W15_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-w15/Empty0001.xml"
    private static final String SAMPLE_W150001 = "src/test/resources/samples/hedis-w15/W150001.xml"
	private static final String SAMPLE_W150002 = "src/test/resources/samples/hedis-w15/W150002.xml"
	private static final String SAMPLE_W150003 = "src/test/resources/samples/hedis-w15/W150003.xml"
	private static final String SAMPLE_W150004 = "src/test/resources/samples/hedis-w15/W150004.xml"
	private static final String SAMPLE_W150005 = "src/test/resources/samples/hedis-w15/W150005.xml"
	private static final String SAMPLE_W150006 = "src/test/resources/samples/hedis-w15/W150006.xml"
	private static final String SAMPLE_W150007 = "src/test/resources/samples/hedis-w15/W150007.xml"
	private static final String SAMPLE_W150008 = "src/test/resources/samples/hedis-w15/W150008.xml"
	private static final String SAMPLE_W150009 = "src/test/resources/samples/hedis-w15/W150009.xml"
	private static final String SAMPLE_W150010 = "src/test/resources/samples/hedis-w15/W150010.xml"
	@Unroll
    def "test HEDIS W15 Well Child 15 Months"() {
        when:
        def input = new File(vmr).text
        def params = [
            kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_W15', version: '2014.0.0'],
            specifiedTime: '2012-02-01'
        ]
        def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)

       then:
        def data = new XmlSlurper().parseText(responsePayload)
        def results = VMRUtil.getResults(data, '\\|')
        assertions.size() == results.assertions.size()
        assertions.each {entry ->
            assert results.assertions.containsKey(entry.key)
            if (entry.value) {
                assert results.assertions.get(entry.key) == entry.value
            }
        }
        numerator == results.measure.numerator
        denominator == results.measure.denominator
        
        where:
        vmr | assertions | denominator | numerator
		SAMPLE_ALL0001 | [C529: '', C569: '', reject: '', wrapper : ''] | '0' | '0'
		SAMPLE_W150001 | [C2683: '', C2690: '', C539:'', C54: '', C545: '', WCVCount: '6', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_W150002 | [C2683: '', C2689: '', C539:'', C54: '', C545: '', WCVCount: '5', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_W150003 | [C2683: '', C2688: '', C539:'', C54: '', C545: '', WCVCount: '4', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_W150004 | [denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_W150005 | [denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_W150006 | [denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_W150007 | [C2683: '', C2684: '', C539:'', C54: '', C545: '', WCVCount: '0', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_W150008 | [C2683: '', C2687: '', C539:'', C54: '', C545: '', WCVCount: '3', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_W150009 | [C2683: '', C2686: '', C539:'', C54: '', C545: '', WCVCount: '2', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_W150010 | [C2683: '', C2685: '', C539:'', C54: '', C545: '', WCVCount: '1', numMet: '', wrapper : ''] | '1' | '1'
		}

}