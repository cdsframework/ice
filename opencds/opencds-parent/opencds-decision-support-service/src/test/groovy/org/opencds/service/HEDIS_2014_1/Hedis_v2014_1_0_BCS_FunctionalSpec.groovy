package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_BCS_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2597: [num: -1, denom: -1]]
	
    private static final String BCS0001 = "src/test/resources/samples/hedis-bcs/SampleBCS0001.xml" //Denom Not Met
	private static final Map ASSERTIONS_BCS0001 = [C2761:'', C31: '', C46:'', C47: '',C544:'',C545:'']
    private static final Map MEASURES_BCS0001  = [C2597: [num: 0, denom: 0]]

	
    private static final String BCS0002 = "src/test/resources/samples/hedis-bcs/SampleBCS0002.xml" //Denom Not Met
	private static final Map ASSERTIONS_BCS0002 = [C2761:'', C31: '', C47:'', C544: '',C545:'']
	private static final Map MEASURES_BCS0002  = [C2597: [num: 0, denom: 0]]
	
    private static final String BCS0003 = "src/test/resources/samples/hedis-bcs/SampleBCS0003.xml" //Num Met
	private static final Map ASSERTIONS_BCS0003 = [C2761:'', C2762: '', C31:'', C539: '',C54:'',C545:'']
	private static final Map MEASURES_BCS0003  = [C2597: [num: 1, denom: 1]]

	
    private static final String BCS0004 = "src/test/resources/samples/hedis-bcs/SampleBCS0004.xml" //Denom Not Met
	private static final Map ASSERTIONS_BCS0004 = [C2761:'', C31: '', C47:'', C544: '',C545:'']
	private static final Map MEASURES_BCS0004  = [C2597: [num: 0, denom: 0]]
	
	private static final String BCS0005 = "src/test/resources/samples/hedis-bcs/SampleBCS0005.xml" //Denom Not Met
	private static final Map ASSERTIONS_BCS0005 = [C31: '']
	private static final Map MEASURES_BCS0005  = [C2597: [num: 0, denom: 0]]

	
	private static final String BCS0006 = "src/test/resources/samples/hedis-bcs/SampleBCS0006.xml" //Denom Not Met
	private static final Map ASSERTIONS_BCS0006 = [C31: '']
	private static final Map MEASURES_BCS0006  = [C2597: [num: 0, denom: 0]]
	
	private static final String BCS0007 = "src/test/resources/samples/hedis-bcs/SampleBCS0007.xml" //Denom Not Met
	private static final Map ASSERTIONS_BCS0007 = [C31: '']
	private static final Map MEASURES_BCS0007  = [C2597: [num: 0, denom: 0]]

	
	private static final String BCS0008 = "src/test/resources/samples/hedis-bcs/SampleBCS0008.xml" //Denom Not Met
	private static final Map ASSERTIONS_BCS0008 = [:]
	private static final Map MEASURES_BCS0008  = [C2597: [num: 0, denom: 0]]

	private static final String BCS0009 = "src/test/resources/samples/hedis-bcs/SampleBCS0009.xml" //Num Met
	private static final Map ASSERTIONS_BCS0009 = [C2761:'', C2762: '', C31:'', C539: '',C54:'',C545:'']
	private static final Map MEASURES_BCS0009 = [C2597: [num: 1, denom: 1]]

	
	private static final String BCS0010 = "src/test/resources/samples/hedis-bcs/SampleBCS0010.xml" //Num Met
	private static final Map ASSERTIONS_BCS0010 = [C2761:'', C2762: '', C31:'', C539: '',C54:'',C545:'']
	private static final Map MEASURES_BCS0010  = [C2597: [num: 1, denom: 1]]
	
	private static final String BCS0011 = "src/test/resources/samples/hedis-bcs/SampleBCS0011.xml" //Num Met
	private static final Map ASSERTIONS_BCS0011 = [C2761:'', C2762: '', C31:'', C539: '',C54:'',C545:'']
	private static final Map MEASURES_BCS0011  = [C2597: [num: 1, denom: 1]]
	
	private static final String BCS0012 = "src/test/resources/samples/hedis-bcs/SampleBCS0012.xml" //Num Met
	private static final Map ASSERTIONS_BCS0012 = [C2761:'', C2762: '', C31:'', C539: '',C54:'',C545:'']
	private static final Map MEASURES_BCS0012  = [C2597: [num: 1, denom: 1]]
	

	


/*
Concepts used:
INPUT
"	1 -> year(s)"
"	2 -> month(s)"
"	5 -> day(s)"
"	C2511 -> HEDIS 2014"
"	C2761 -> Patient Age GE 50 and LT 75 Years"
"	C3003 -> HEDIS-Left Modifier"
"	C3009 -> HEDIS-Mammography"
"	C3040 -> HEDIS-Right Modifier"
"	C3073 -> HEDIS-Bilateral Mastectomy"
"	C3074 -> HEDIS-Bilateral Modifier"
"	C3091 -> HEDIS-Colonoscopy"
"	C31 -> Female"
"	C3150 -> HEDIS-Unilateral Mastectomy"
"	C36 -> OpenCDS"
"	C413 -> Data from EHR or Clinician"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	C2514 -> Colonoscopy"
"	C2597 -> QM HEDIS-BCS Breast Cancer Screening"
"	C2761 -> Patient Age GE 50 and LT 75 Years"
"	C2762 -> Mammography"
"	C31 -> Female"
"	C46 -> Mastectomy"
"	C47 -> Mastectomy Unilateral"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"

*/
	
	@Unroll
	def "test HEDIS BCS v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_BCS', version: '2014.1.0'],
			specifiedTime: '2012-02-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
		println responsePayload

		then:
		def data = new XmlSlurper().parseText(responsePayload)
		def results = VMRUtil.getResults(data, '\\|')
//		assertions.size() == results.assertions.size()
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
		BCS0001 | ASSERTIONS_BCS0001| MEASURES_BCS0001
		BCS0002 | ASSERTIONS_BCS0002| MEASURES_BCS0002
		BCS0003 | ASSERTIONS_BCS0003| MEASURES_BCS0003
		BCS0004 | ASSERTIONS_BCS0004| MEASURES_BCS0004
		BCS0005 | ASSERTIONS_BCS0005| MEASURES_BCS0005
		BCS0006 | ASSERTIONS_BCS0006| MEASURES_BCS0006
		BCS0007 | ASSERTIONS_BCS0007| MEASURES_BCS0007
//		BCS0008 | ASSERTIONS_BCS0008| MEASURES_BCS0008
		BCS0009 | ASSERTIONS_BCS0009| MEASURES_BCS0009
		BCS0010 | ASSERTIONS_BCS0010| MEASURES_BCS0010
		BCS0011 | ASSERTIONS_BCS0011| MEASURES_BCS0011
		BCS0012 | ASSERTIONS_BCS0012| MEASURES_BCS0012


	}
}
