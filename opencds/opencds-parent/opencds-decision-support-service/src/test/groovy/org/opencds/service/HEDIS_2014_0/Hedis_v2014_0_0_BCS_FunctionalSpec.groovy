package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_BCS_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_BCS0001 = "src/test/resources/samples/hedis-bcs/SampleBCS0001.xml"
	private static final String SAMPLE_BCS0002 = "src/test/resources/samples/hedis-bcs/SampleBCS0002.xml"
	private static final String SAMPLE_BCS0003 = "src/test/resources/samples/hedis-bcs/SampleBCS0003.xml"
	private static final String SAMPLE_BCS0004 = "src/test/resources/samples/hedis-bcs/SampleBCS0004.xml"
	private static final String SAMPLE_BCS0005 = "src/test/resources/samples/hedis-bcs/SampleBCS0005.xml"
	private static final String SAMPLE_BCS0006 = "src/test/resources/samples/hedis-bcs/SampleBCS0006.xml"
	private static final String SAMPLE_BCS0007 = "src/test/resources/samples/hedis-bcs/SampleBCS0007.xml"
	private static final String SAMPLE_BCS0008 = "src/test/resources/samples/hedis-bcs/SampleBCS0008.xml"
	private static final String SAMPLE_BCS0009 = "src/test/resources/samples/hedis-bcs/SampleBCS0009.xml"
	private static final String SAMPLE_BCS0010 = "src/test/resources/samples/hedis-bcs/SampleBCS0010.xml"
	private static final String SAMPLE_BCS0011 = "src/test/resources/samples/hedis-bcs/SampleBCS0011.xml"
	private static final String SAMPLE_BCS0012 = "src/test/resources/samples/hedis-bcs/SampleBCS0012.xml"
	@Unroll
	def "test HEDIS W34 Well Child 3-6 years"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_BCS', version: '2014.0.0'],
			specifiedTime: '2012-02-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)

	   then:
		def data = new XmlSlurper().parseText(responsePayload)
		def results = VMRUtil.getResults(data, ',')
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
		SAMPLE_BCS0001 | [C2761:'', C31: '', C46:'', C47: '',C544:'',C545:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_BCS0002 | [C2761:'', C31: '', C47:'', C544: '',C545:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_BCS0003 | [C2761:'', C2762: '', C31:'', C539: '',C54:'',C545:'', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_BCS0004 | [C2761:'', C31: '', C47:'', C544: '',C545:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_BCS0005 | [C31: '', C47:'', C544: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_BCS0006 | [C31: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_BCS0007 | [C31: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_BCS0008 | [C47:'', C544: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_BCS0009 | [C2761:'', C2762: '', C31:'', C539: '',C54:'',C545:'', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_BCS0010 | [C2761:'', C2762: '', C31:'', C539: '',C54:'',C545:'', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_BCS0011 | [C2761:'', C2762: '', C31:'', C539: '',C54:'',C545:'', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_BCS0012 | [C2761:'', C2762: '', C31:'', C539: '',C54:'',C545:'', numMet: '', wrapper : ''] | '1' | '1'
		}
}