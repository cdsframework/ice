package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_W34_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
    private static final String SAMPLE_W340001 = "src/test/resources/samples/hedis-w34/SampleW340001.xml"
	private static final String SAMPLE_W340002 = "src/test/resources/samples/hedis-w34/SampleW340002.xml"
	private static final String SAMPLE_W340003 = "src/test/resources/samples/hedis-w34/SampleW340003.xml"
	private static final String SAMPLE_W340004 = "src/test/resources/samples/hedis-w34/SampleW340004.xml"
	private static final String SAMPLE_W340005 = "src/test/resources/samples/hedis-w34/SampleW340005.xml"
	private static final String SAMPLE_W340006 = "src/test/resources/samples/hedis-w34/SampleW340006.xml"
	private static final String SAMPLE_W340007 = "src/test/resources/samples/hedis-w34/SampleW340007.xml"
	private static final String SAMPLE_W340008 = "src/test/resources/samples/hedis-w34/SampleW340008.xml"
	private static final String SAMPLE_W340009 = "src/test/resources/samples/hedis-w34/SampleW340009.xml"
    private static final String SAMPLE_W340010 = "src/test/resources/samples/hedis-w34/SampleW340010.xml"
    private static final String SAMPLE_W340011 = "src/test/resources/samples/hedis-w34/SampleW340011.xml"
        
	@Unroll
    def "test HEDIS W34 Well Child 3-6 years"() {
        when:
        def input = new File(vmr).text
        def params = [
            kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_W34', version: '2014.0.0'],
            specifiedTime: '2012-02-01'
        ]
        def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)

       then:
        def data = new XmlSlurper().parseText(responsePayload)
        def results = VMRUtil.getResults(data)
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
		SAMPLE_W340001 | [C2680:'', C2706: '', C539:'', C54: '', C545: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_W340002 | [C2680:'', C2706: '', C539:'', C54: '', C545: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_W340003 | [C2680:'', C2706: '', C539:'', C54: '', C545: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_W340004 | [C2680:'', C2706: '', C539:'', C54: '', C545: '', numMet: '', wrapper : ''] | '1' | '1'
        SAMPLE_W340011 | [C2680:'', C2706: '', C539:'', C54: '', C545: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_W340005 | [C2706:'',C54:'',C545:'',numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_W340006 | [denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_W340007 | [denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_W340008 | [C2706:'',C54:'',C545:'',numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_W340009 | [C2706:'',C54:'',C545:'',numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_W340010 | [C2706:'',C54:'',C545:'',numNotMet: '', wrapper : ''] | '1' | '0'
		}

}