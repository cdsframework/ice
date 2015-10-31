package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_CCS_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
    private static final String SAMPLE_CCS0001 = "src/test/resources/samples/hedis-ccs/SampleCCS0001.xml"
	private static final String SAMPLE_CCS0002 = "src/test/resources/samples/hedis-ccs/SampleCCS0002.xml"
	private static final String SAMPLE_CCS0003 = "src/test/resources/samples/hedis-ccs/SampleCCS0003.xml"
	private static final String SAMPLE_CCS0004 = "src/test/resources/samples/hedis-ccs/SampleCCS0004.xml"
	private static final String SAMPLE_CCS0005 = "src/test/resources/samples/hedis-ccs/SampleCCS0005.xml"
	private static final String SAMPLE_CCS0006 = "src/test/resources/samples/hedis-ccs/SampleCCS0006.xml"
	private static final String SAMPLE_CCS0007 = "src/test/resources/samples/hedis-ccs/SampleCCS0007.xml"
	private static final String SAMPLE_CCS0008 = "src/test/resources/samples/hedis-ccs/SampleCCS0008.xml"
	private static final String SAMPLE_CCS0009 = "src/test/resources/samples/hedis-ccs/SampleCCS0009.xml"
	private static final String SAMPLE_CCS0010 = "src/test/resources/samples/hedis-ccs/SampleCCS0010.xml"
	@Unroll
    def "test HEDIS CCS Cervical Cancer Screening"() {
        when:
        def input = new File(vmr).text
        def params = [
            kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CCS', version: '2014.0.0'],
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
		SAMPLE_CCS0001 | [C2750: '', C2751: '', C31:'', C544:'', C545:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_CCS0002 | [C2750: '', C2756: '', C31:'',C514:'',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_CCS0003 | [C2750: '', C2833: '', C31:'',C514:'',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_CCS0004 | [C2750: '', C31:'',C54:'',C545:'',numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CCS0005 | [C2750: '', C31:'',C54:'',C545:'',numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CCS0006 | [C2750: '', C2751: '', C31:'', C544:'', C545:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_CCS0007 | [C2750: '', C2751: '', C31:'', C544:'', C545:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_CCS0008 | [C2750: '', C2756: '', C31:'',C514:'',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_CCS0009 | [C31:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_CCS0010 | [C31:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		}
}