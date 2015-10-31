package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_NCS_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
    private static final String SAMPLE_NCS0001 = "src/test/resources/samples/hedis-ncs/SampleNCS0001.xml"
	private static final String SAMPLE_NCS0002 = "src/test/resources/samples/hedis-ncs/SampleNCS0002.xml"
	private static final String SAMPLE_NCS0003 = "src/test/resources/samples/hedis-ncs/SampleNCS0003.xml"
	private static final String SAMPLE_NCS0004 = "src/test/resources/samples/hedis-ncs/SampleNCS0004.xml"
	private static final String SAMPLE_NCS0005 = "src/test/resources/samples/hedis-ncs/SampleNCS0005.xml"
	private static final String SAMPLE_NCS0006 = "src/test/resources/samples/hedis-ncs/SampleNCS0006.xml"
	private static final String SAMPLE_NCS0007 = "src/test/resources/samples/hedis-ncs/SampleNCS0007.xml"
	private static final String SAMPLE_NCS0008 = "src/test/resources/samples/hedis-ncs/SampleNCS0008.xml"
	private static final String SAMPLE_NCS0009 = "src/test/resources/samples/hedis-ncs/SampleNCS0009.xml"
	private static final String SAMPLE_NCS0010 = "src/test/resources/samples/hedis-ncs/SampleNCS0010.xml"
	private static final String SAMPLE_NCS0011 = "src/test/resources/samples/hedis-ncs/SampleNCS0011.xml"
	private static final String SAMPLE_NCS0012 = "src/test/resources/samples/hedis-ncs/SampleNCS0012.xml"
	private static final String SAMPLE_NCS0013 = "src/test/resources/samples/hedis-ncs/SampleNCS0013.xml"
	@Unroll
    def "test HEDIS NCS"() {
        when:
        def input = new File(vmr).text
        def params = [
            kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_NCS', version: '2014.0.0'],
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
		SAMPLE_NCS0001 | [C1671: '', C2766: '', C2767: '', C2769: '',C31:'', C544:'', C545:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_NCS0002 | [C2766: '', C2767: '', C31:'', C544:'', C545:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_NCS0003 | [C2756: '', C2766: '', C31:'',C514:'',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_NCS0004 | [C2756: '', C2766: '', C31:'',C514:'',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_NCS0005 | [C2756: '', C2766: '', C31:'',C514:'',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_NCS0006 | [C1671: '', C2766: '', C31:'', C544:'', C545:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_NCS0007 | [C2760: '', C2766: '', C31:'',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_NCS0008 | [C2760: '', C2766: '', C31:'',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_NCS0009 | [C2766: '', C2769: '', C31:'', C544:'', C545:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_NCS0010 | [C31:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_NCS0011 | [C31:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_NCS0012 | [C2766:'', C31:'',  C54:'',C545:'', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_NCS0013 | [C2766:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		}
}