package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_CIS_RV_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_CIS_RV0001 = "src/test/resources/samples/hedis-cis_rv/SampleCIS_RV0001.xml"
	private static final String SAMPLE_CIS_RV0002 = "src/test/resources/samples/hedis-cis_rv/SampleCIS_RV0002.xml"
	private static final String SAMPLE_CIS_RV0003 = "src/test/resources/samples/hedis-cis_rv/SampleCIS_RV0003.xml"
	private static final String SAMPLE_CIS_RV0004 = "src/test/resources/samples/hedis-cis_rv/SampleCIS_RV0004.xml"
	private static final String SAMPLE_CIS_RV0005 = "src/test/resources/samples/hedis-cis_rv/SampleCIS_RV0005.xml"
	private static final String SAMPLE_CIS_RV0006 = "src/test/resources/samples/hedis-cis_rv/SampleCIS_RV0006.xml"
	private static final String SAMPLE_CIS_RV0007 = "src/test/resources/samples/hedis-cis_rv/SampleCIS_RV0007.xml"
	private static final String SAMPLE_CIS_RV0008 = "src/test/resources/samples/hedis-cis_rv/SampleCIS_RV0008.xml"
	private static final String SAMPLE_CIS_RV0009 = "src/test/resources/samples/hedis-cis_rv/SampleCIS_RV0009.xml"
	private static final String SAMPLE_CIS_RV0010 = "src/test/resources/samples/hedis-cis_rv/SampleCIS_RV0010.xml"
	private static final String SAMPLE_CIS_RV0011 = "src/test/resources/samples/hedis-cis_rv/SampleCIS_RV0011.xml"
	private static final String SAMPLE_CIS_RV0012 = "src/test/resources/samples/hedis-cis_rv/SampleCIS_RV0012.xml"

	@Unroll
	def "test HEDIS CIS_RV"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CIS_RV', version: '2014.0.0'],
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
		SAMPLE_CIS_RV0001 | [C2700: '', C539: '', C54: '', C545: '', C3190: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_CIS_RV0002 | [C2700: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_CIS_RV0003 | [C2700: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_CIS_RV0004 | [C2700: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_CIS_RV0005 | [C2700: '', C539: '', C54: '', C545: '', C3191: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_CIS_RV0006 | [C2700: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_CIS_RV0007 | [C2700: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_CIS_RV0008 | [denomNotMet: '', wrapper: ''] | '0' | '0'
		SAMPLE_CIS_RV0009 | [denomNotMet: '', wrapper: ''] | '0' | '0'
		SAMPLE_CIS_RV0010 | [C2700: '', C539: '', C54: '', C545: '', C860: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_CIS_RV0011 | [C2700: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_CIS_RV0012 | [C2700: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		}
}