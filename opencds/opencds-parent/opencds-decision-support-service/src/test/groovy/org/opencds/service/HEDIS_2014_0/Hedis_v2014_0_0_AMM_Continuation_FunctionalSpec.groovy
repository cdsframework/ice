package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_AMM_Continuation_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_AMM_Continuation0001 = "src/test/resources/samples/hedis-amm/SampleAMM0001.xml"
	private static final String SAMPLE_AMM_Continuation0002 = "src/test/resources/samples/hedis-amm/SampleAMM0002.xml"
	private static final String SAMPLE_AMM_Continuation0003 = "src/test/resources/samples/hedis-amm/SampleAMM0003.xml"
	private static final String SAMPLE_AMM_Continuation0004 = "src/test/resources/samples/hedis-amm/SampleAMM0004.xml"
	private static final String SAMPLE_AMM_Continuation0005 = "src/test/resources/samples/hedis-amm/SampleAMM0005.xml"
	private static final String SAMPLE_AMM_Continuation0006 = "src/test/resources/samples/hedis-amm/SampleAMM0006.xml"
	private static final String SAMPLE_AMM_Continuation0007 = "src/test/resources/samples/hedis-amm/SampleAMM0007.xml"
	private static final String SAMPLE_AMM_Continuation0008 = "src/test/resources/samples/hedis-amm/SampleAMM0008.xml"
	private static final String SAMPLE_AMM_Continuation0009 = "src/test/resources/samples/hedis-amm/SampleAMM0009.xml"
	private static final String SAMPLE_AMM_Continuation0010 = "src/test/resources/samples/hedis-amm/SampleAMM0010.xml"

	@Unroll
	def "test HEDIS AMM_Continuation"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_AMM_Continuation', version: '2014.0.0'],
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
		SAMPLE_AMM_Continuation0001 | [C2888:'', C3170:'',  C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'100', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_AMM_Continuation0002 | [C2888:'', C3170:'',  C54:  '', C545: '', DaysSuppliedAcute:'98', DaysSuppliedContinuation:'98',  numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_AMM_Continuation0003 | [C2888:'', C3170:'', C54:  '', C545: '',DaysSuppliedAcute:'80', DaysSuppliedContinuation:'80', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_AMM_Continuation0004 | [denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_AMM_Continuation0005 | [C2888:'', C3170:'',  C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'100', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_AMM_Continuation0006 | [C2888:'', C3170:'', C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'100', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_AMM_Continuation0007 | [C2888:'', C3170:'',  C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'100', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_AMM_Continuation0008 | [C2888:'', C3170:'',  C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'100', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_AMM_Continuation0009 | [C2888:'', C3170:'', C544: '', C545: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_AMM_Continuation0010 | [C2888:'', C3170:'', C539: '', C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'180',numMet: '', wrapper : ''] | '1' | '1'
		}
}