package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_ABA_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_ABA0001 = "src/test/resources/samples/hedis-aba/SampleABA0001.xml"
	private static final String SAMPLE_ABA0002 = "src/test/resources/samples/hedis-aba/SampleABA0002.xml"
	private static final String SAMPLE_ABA0003 = "src/test/resources/samples/hedis-aba/SampleABA0003.xml"
	private static final String SAMPLE_ABA0004 = "src/test/resources/samples/hedis-aba/SampleABA0004.xml"
	private static final String SAMPLE_ABA0005 = "src/test/resources/samples/hedis-aba/SampleABA0005.xml"
	private static final String SAMPLE_ABA0006 = "src/test/resources/samples/hedis-aba/SampleABA0006.xml"
	private static final String SAMPLE_ABA0007 = "src/test/resources/samples/hedis-aba/SampleABA0007.xml"
	private static final String SAMPLE_ABA0008 = "src/test/resources/samples/hedis-aba/SampleABA0008.xml"
	private static final String SAMPLE_ABA0009 = "src/test/resources/samples/hedis-aba/SampleABA0009.xml"
	@Unroll
	def "test HEDIS ABA Adult BMI"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_ABA', version: '2014.0.0'],
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
		SAMPLE_ABA0001 | [C2754:'', C44: '', C544:'', C545: '',C844:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_ABA0002 | [C2754:'',C2757:'', C44: '', C539:'', C54: '',C545:'', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_ABA0003 | [C44: '',denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_ABA0004 | [C44: '',denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_ABA0005 | [C2754: '',denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_ABA0006 | [C2754:'', C44: '', C54:'', C545: '', numNotMet: '', wrapper : ''] | '1' | '0' //BMI Percentile???
		SAMPLE_ABA0007 | [C2754:'',C2757:'', C44: '', C539:'', C54: '',C545:'', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_ABA0008 | [C2754:'',C2757:'', C44: '', C539:'', C54: '',C545:'', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_ABA0009 | [C2754:'',C2757:'', C44: '', C539:'', C54: '',C545:'', numMet: '', wrapper : ''] | '1' | '1'
		}

}