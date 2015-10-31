package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_ABA_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2591: [num: -1, denom: -1]]
	
    private static final String ABA0001 = "src/test/resources/samples/hedis-aba/SampleABA0001.xml" //Num Met
	private static final Map ASSERTIONS_ABA0001 = [C2754:'', C44: '', C544:'', C545: '',C844:'']
    private static final Map MEASURES_ABA0001  = [C2591: [num: 0, denom: 0]]

	
    private static final String ABA0002 = "src/test/resources/samples/hedis-aba/SampleABA0002.xml" //Num Met
	private static final Map ASSERTIONS_ABA0002 = [C2754:'',C2757:'', C44: '', C539:'', C54: '',C545:'']
	private static final Map MEASURES_ABA0002  = [C2591: [num: 1, denom: 1]]
/*	
    private static final String ABA0003 = "src/test/resources/samples/hedis-aba/SampleABA0003.xml" //Num Met
	private static final Map ASSERTIONS_ABA0003 = [:]
	private static final Map MEASURES_ABA0003  = [C2591: [num: 0, denom: 0]]

	
    private static final String ABA0004 = "src/test/resources/samples/hedis-aba/SampleABA0004.xml" //Denom MEt
	private static final Map ASSERTIONS_ABA0004 = [:]
	private static final Map MEASURES_ABA0004  = [C2591: [num: 0, denom: 0]]
*/	
	private static final String ABA0005 = "src/test/resources/samples/hedis-aba/SampleABA0005.xml" //Num Met
	private static final Map ASSERTIONS_ABA0005 = [C2754: '']
	private static final Map MEASURES_ABA0005  = [C2591: [num: 0, denom: 0]]

	
	private static final String ABA0006 = "src/test/resources/samples/hedis-aba/SampleABA0006.xml" //BMI Percentile
	private static final Map ASSERTIONS_ABA0006 = [C2754:'', C44: '', C54:'', C545: '']
	private static final Map MEASURES_ABA0006  = [C2591: [num: 0, denom: 1]]
	
	private static final String ABA0007 = "src/test/resources/samples/hedis-aba/SampleABA0007.xml" //Num Met
	private static final Map ASSERTIONS_ABA0007 = [C2754:'',C2757:'', C44: '', C539:'', C54: '',C545:'']
	private static final Map MEASURES_ABA0007  = [C2591: [num: 1, denom: 1]]

	
	private static final String ABA0008 = "src/test/resources/samples/hedis-aba/SampleABA0008.xml" //Num Met
	private static final Map ASSERTIONS_ABA0008 = [C2754:'',C2757:'', C44: '', C539:'', C54: '',C545:'']
	private static final Map MEASURES_ABA0008  = [C2591: [num: 1, denom: 1]]

	private static final String ABA0009 = "src/test/resources/samples/hedis-aba/SampleABA0009.xml" //Num Met
	private static final Map ASSERTIONS_ABA0009 = [C2754:'',C2757:'', C44: '', C539:'', C54: '',C545:'']
	private static final Map MEASURES_ABA0009 = [C2591: [num: 1, denom: 1]]

/*
Concepts used:
INPUT
"	1 -> year(s)"
"	C2511 -> HEDIS 2014"
"	C2754 -> Patient Age GE 18 and LT 75 Years"
"	C2964 -> HEDIS-Outpatient"
"	C3078 -> HEDIS-BMI"
"	C3079 -> HEDIS-BMI Percentile"
"	C3143 -> HEDIS-Pregnancy"
"	C36 -> OpenCDS"
"	C44 -> Outpatient"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	C2591 -> QM HEDIS-ABA Adult BMI Assessment"
"	C2754 -> Patient Age GE 18 and LT 75 Years"
"	C2757 -> Body Mass Index (BMI)"
"	C44 -> Outpatient"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"
"	C844 -> Pregnancy"

*/
	
	@Unroll
	def "test HEDIS ABA v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_ABA', version: '2014.1.0'],
			specifiedTime: '2012-02-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
		println responsePayload

		then:
		def data = new XmlSlurper().parseText(responsePayload)
		def results = VMRUtil.getResults(data, '\\|')
//		assertions.size() == results.assertions.size()
		results.assertions.each {entry ->
			System.err.println "${entry.key} -> ${entry.value}"
		}
		if (!assertions) {
			assert assertions == results.assertions
		} else {
		assertions.each {entry ->
			assert results.assertions.containsKey(entry.key);
			if (entry?.value) {
				assert results.assertions.get(entry.key) == entry.value
			}
		}
		}
//        measures.size() == results.measures.size()
        measures.each {entry ->
            assert results.measures.containsKey(entry.key)
            assert results.measures.get(entry.key).num == entry.value.num
            assert results.measures.get(entry.key).denom == entry.value.denom			
        }
//		results.measures.each {entry ->
//			System.err.println "${entry.key} -> ${entry.value.num} ${entry.value.denom}"
//		}
//		results.assertions.each {entry ->
//			System.err.println "${entry.key} -> ${entry.value}"
//		}

		where:
		vmr | assertions | measures
		EMPTY0001 | ASSERTIONS_EMPTY0001| MEASURES_EMPTY0001 
		ABA0001 | ASSERTIONS_ABA0001| MEASURES_ABA0001
		ABA0002 | ASSERTIONS_ABA0002| MEASURES_ABA0002
/*		ABA0003 | ASSERTIONS_ABA0003| MEASURES_ABA0003
		ABA0004 | ASSERTIONS_ABA0004| MEASURES_ABA0004  */
		ABA0005 | ASSERTIONS_ABA0005| MEASURES_ABA0005
		ABA0006 | ASSERTIONS_ABA0006| MEASURES_ABA0006
		ABA0007 | ASSERTIONS_ABA0007| MEASURES_ABA0007
		ABA0008 | ASSERTIONS_ABA0008| MEASURES_ABA0008
		ABA0009 | ASSERTIONS_ABA0009| MEASURES_ABA0009

	}
}
