package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_CMC_Screening_FunctionalSpec extends Specification {
	private static final String CMC_C0002 = "src/test/resources/samples/hedis-cmc_control/CMC_C0002.xml"
	private static final String CMC_C0003 = "src/test/resources/samples/hedis-cmc_control/CMC_C0003.xml"
	private static final String CMC_C0004 = "src/test/resources/samples/hedis-cmc_control/CMC_C0004.xml"
	private static final String CMC_C0005 = "src/test/resources/samples/hedis-cmc_control/CMC_C0005.xml"
	private static final String CMC_C0006 = "src/test/resources/samples/hedis-cmc_control/CMC_C0006.xml"
	private static final String CMC_C0007 = "src/test/resources/samples/hedis-cmc_control/CMC_C0007.xml"
	private static final String CMC_C0008 = "src/test/resources/samples/hedis-cmc_control/CMC_C0008.xml"
	private static final String CMC_C0009 = "src/test/resources/samples/hedis-cmc_control/CMC_C0009.xml"
	private static final String CMC_C0010 = "src/test/resources/samples/hedis-cmc_control/CMC_C0010.xml"
	private static final String CMC_C0011 = "src/test/resources/samples/hedis-cmc_control/CMC_C0011.xml"
	private static final String CMC_C0012 = "src/test/resources/samples/hedis-cmc_control/CMC_C0012.xml"
	private static final String CMC_C0013 = "src/test/resources/samples/hedis-cmc_control/CMC_C0013.xml"
	private static final String CMC_C0014 = "src/test/resources/samples/hedis-cmc_control/CMC_C0014.xml"
	private static final String CMC_C0015 = "src/test/resources/samples/hedis-cmc_control/CMC_C0015.xml"
	private static final String CMC_C0016 = "src/test/resources/samples/hedis-cmc_control/CMC_C0016.xml"
	private static final String CMC_C0017 = "src/test/resources/samples/hedis-cmc_control/CMC_C0017.xml"
    private static final String CMC_C0018 = "src/test/resources/samples/hedis-cmc_control/CMC_C0018.xml"
    private static final String CMC_C0019 = "src/test/resources/samples/hedis-cmc_control/CMC_C0019.xml"
    private static final String CMC_C0020 = "src/test/resources/samples/hedis-cmc_control/CMC_C0020.xml"
    private static final String CMC_C0021 = "src/test/resources/samples/hedis-cmc_control/CMC_C0021.xml"
//    private static final String CMC_C0022 = "H:/KMM ITS/Projects/OpenCDS/Quality Measures/HEDIS/testing/cmc-phi.xml"
	
	@Unroll
	def "test HEDIS CMC screening"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CMC_Screening', version: '2014.0.0'],
			specifiedTime: '2012-01-01'
//			specifiedTime: '2014-01-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
		println responsePayload

		then:
		def data = new XmlSlurper().parseText(responsePayload)
		def results = VMRUtil.getResults(data, '\\|')
		results.assertions.each {entry ->
			System.err.println "${entry.key} -> ${entry.value}"
		}
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
		CMC_C0002 | [C2606: '', C341: '', C54: '', numNotMet: '', wrapper : ''] | '1' | '0'
		CMC_C0003 | [C2606: '', C2541: '', C54: '', numNotMet: '', wrapper : ''] | '1' | '0'
		CMC_C0004 | [C2606: '', C2541: '', C54: '', numNotMet: '', wrapper : ''] | '1' | '0'
		CMC_C0005 | [C2606: '', C2541: '', C54: '', numNotMet: '', wrapper : ''] | '1' | '0'
		CMC_C0006 | [C2606: '', C2542: '', C54: '', numNotMet: '', wrapper : ''] | '1' | '0'
		CMC_C0007 | [C2606: '', C2542: '', C54: '', numNotMet: '', wrapper : ''] | '1' | '0'
		CMC_C0008 | [C2606: '', C2542: '', C54: '', numNotMet: '', wrapper : ''] | '1' | '0'
		CMC_C0009 | [C2541: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		CMC_C0010 | [C2541: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		CMC_C0011 | [C2606: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		CMC_C0012 | [C2606: '', C3196: '',C3197: '', C54: '', numNotMet: '', wrapper : ''] | '1' | '0'
		CMC_C0013 | [C2606: '', C3196: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		CMC_C0014 | [C1678: '', C2606: '', C341: '', C539: '', C54: '', numMet: '', wrapper : ''] | '1' | '1'
		CMC_C0015 | [C1678: '', C2606: '', C341: '', C539: '', C54: '', numMet: '', wrapper : ''] | '1' | '1'
		CMC_C0016 | [C2606: '', C341: '', C54: '', numNotMet: '', wrapper : ''] | '1' | '0'
		CMC_C0017 | [C1678: '', C2606: '', C341: '', C539: '', C54: '', numMet: '', wrapper : ''] | '1' | '1'
		CMC_C0018 | [C1678: '', C2606: '', C341: '', C539: '', C54: '', numMet: '', wrapper : ''] | '1' | '1'
		CMC_C0019 | [C2606: '', C341: '', C54: '', numNotMet: '', wrapper : ''] | '1' | '0'
		CMC_C0020 | [C1678: '', C2606: '', C341: '', C539: '', C54: '', numMet: '', wrapper : ''] | '1' | '1'
		CMC_C0021 | [C1678: '', C2606: '', C341: '', C539: '', C54: '', numMet: '', wrapper : ''] | '1' | '1'
//		CMC_C0022 | [denomNotMet: '', wrapper : ''] | '0' | '0'
		}
}