package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_AMM_Acute_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_AMM_Acute0001 = "src/test/resources/samples/hedis-amm/SampleAMM0001.xml"
	private static final String SAMPLE_AMM_Acute0002 = "src/test/resources/samples/hedis-amm/SampleAMM0002.xml"
	private static final String SAMPLE_AMM_Acute0003 = "src/test/resources/samples/hedis-amm/SampleAMM0003.xml"
	private static final String SAMPLE_AMM_Acute0004 = "src/test/resources/samples/hedis-amm/SampleAMM0004.xml"
	private static final String SAMPLE_AMM_Acute0005 = "src/test/resources/samples/hedis-amm/SampleAMM0005.xml"
	private static final String SAMPLE_AMM_Acute0006 = "src/test/resources/samples/hedis-amm/SampleAMM0006.xml"
	private static final String SAMPLE_AMM_Acute0007 = "src/test/resources/samples/hedis-amm/SampleAMM0007.xml"
	private static final String SAMPLE_AMM_Acute0008 = "src/test/resources/samples/hedis-amm/SampleAMM0008.xml"
	private static final String SAMPLE_AMM_Acute0009 = "src/test/resources/samples/hedis-amm/SampleAMM0009.xml"

	@Unroll
	def "test HEDIS AMM_Acute"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_AMM_Acute', version: '2014.0.0'],
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
/*C2931:HEDIS-AMM Stand Alone Visits*/		
		where:
		vmr | assertions | denominator | numerator
		SAMPLE_ALL0001 | [C529: '', C569: '', reject: '', wrapper : ''] | '0' | '0'
		SAMPLE_AMM_Acute0001 | [IPSD:'2011-02-05 00:00:00',C2931:'',C3265:'',C2888:'', C3170:'', C539: '', C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'100',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AMM_Acute0002 | [IPSD:'2011-02-05 00:00:00',C2968:'',C3265:'',C2888:'', C3170:'', C539: '', C54:  '', C545: '', DaysSuppliedAcute:'98', DaysSuppliedContinuation:'98',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AMM_Acute0003 | [IPSD:'2011-02-05 00:00:00',C2968:'',C3265:'',C2888:'', C3170:'', C54:  '', C545: '',DaysSuppliedAcute:'80', DaysSuppliedContinuation:'80', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_AMM_Acute0004 | [denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_AMM_Acute0005 | [IPSD:'2011-02-05 00:00:00',C2931:'',C3265:'',C2888:'', C3170:'', C539: '', C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'100',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AMM_Acute0006 | [IPSD:'2011-02-05 00:00:00',C2932:'',C3265:'',C2888:'', C3170:'', C539: '', C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'100',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AMM_Acute0007 | [IPSD:'2011-02-05 00:00:00',C2971:'',C3265:'',C2888:'', C3170:'', C539: '', C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'100',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AMM_Acute0008 | [IPSD:'2011-02-05 00:00:00',C3020:'',C3265:'',C2888:'', C3170:'', C539: '', C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'100',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AMM_Acute0009 | [IPSD:'2010-08-01 00:00:00',C2931:'',C3265:'',C2888:'', C3170:'', C544: '', C545: '', denomNotMet: '', wrapper : ''] | '0' | '0'

		}
}