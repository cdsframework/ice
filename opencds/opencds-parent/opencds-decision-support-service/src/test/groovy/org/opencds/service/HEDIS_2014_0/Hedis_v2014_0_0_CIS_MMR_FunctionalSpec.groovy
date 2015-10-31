package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_CIS_MMR_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_CIS_MMR0001 = "src/test/resources/samples/hedis-cis_mmr/SampleCIS_MMR0001.xml"
	private static final String SAMPLE_CIS_MMR0002 = "src/test/resources/samples/hedis-cis_mmr/SampleCIS_MMR0002.xml"
	private static final String SAMPLE_CIS_MMR0003 = "src/test/resources/samples/hedis-cis_mmr/SampleCIS_MMR0003.xml"
	private static final String SAMPLE_CIS_MMR0004 = "src/test/resources/samples/hedis-cis_mmr/SampleCIS_MMR0004.xml"
	private static final String SAMPLE_CIS_MMR0005 = "src/test/resources/samples/hedis-cis_mmr/SampleCIS_MMR0005.xml"
	private static final String SAMPLE_CIS_MMR0006 = "src/test/resources/samples/hedis-cis_mmr/SampleCIS_MMR0006.xml"
	private static final String SAMPLE_CIS_MMR0007 = "src/test/resources/samples/hedis-cis_mmr/SampleCIS_MMR0007.xml"
	private static final String SAMPLE_CIS_MMR0008 = "src/test/resources/samples/hedis-cis_mmr/SampleCIS_MMR0008.xml"
	private static final String SAMPLE_CIS_MMR0009 = "src/test/resources/samples/hedis-cis_mmr/SampleCIS_MMR0009.xml"
	private static final String SAMPLE_CIS_MMR0010 = "src/test/resources/samples/hedis-cis_mmr/SampleCIS_MMR0010.xml"
	private static final String SAMPLE_CIS_MMR0011 = "src/test/resources/samples/hedis-cis_mmr/SampleCIS_MMR0011.xml"


	@Unroll
	def "test HEDIS CIS_MMR"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CIS_MMR', version: '2014.0.0'],
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
		SAMPLE_CIS_MMR0001 | [C2700: '', C539: '', C54: '', C545: '', C858: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_CIS_MMR0002 | [C2700: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_CIS_MMR0003 | [C2700: '', C539: '', C54: '', C545: '', C3193: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_CIS_MMR0004 | [C2700: '', C539: '', C54: '', C545: '', C3193: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_CIS_MMR0005 | [C2700: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_CIS_MMR0006 | [C2700: '', C539: '', C54: '', C545: '', C3194: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_CIS_MMR0007 | [C2700: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_CIS_MMR0008 | [C2700: '', C539: '', C54: '', C545: '', C3194: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_CIS_MMR0009 | [C2700: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_CIS_MMR0010 | [denomNotMet: '', wrapper: ''] | '0' | '0'
		SAMPLE_CIS_MMR0011 | [denomNotMet: '', wrapper: ''] | '0' | '0'
		}
}