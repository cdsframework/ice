package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_W15_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-w15/Empty0001.xml" //missing DOB
    private static final Map MEASURES_EMPTY0001 = [C2624: [num: -1, denom: -1]]
	
    private static final String W150001 = "src/test/resources/samples/hedis-w15/W150001.xml" //6 well care visits
    private static final Map MEASURES_W150001 = [C3349: [num: 0, denom: 1], C3350: [num: 0, denom: 1], C3351: [num: 0, denom: 1], 
		C3352: [num: 0, denom: 1], C3353: [num: 0, denom: 1], C3354: [num: 0, denom: 1], C3355: [num: 1, denom: 1]]

	
    private static final String W150002 = "src/test/resources/samples/hedis-w15/W150002.xml" //5 well care visits
    private static final Map MEASURES_W150002 = [C3349: [num: 0, denom: 1], C3350: [num: 0, denom: 1], C3351: [num: 0, denom: 1], 
		C3352: [num: 0, denom: 1], C3353: [num: 0, denom: 1], C3354: [num: 1, denom: 1], C3355: [num: 0, denom: 1]]
	
    private static final String W150003 = "src/test/resources/samples/hedis-w15/W150003.xml" //4 well care visits
    private static final Map MEASURES_W150003 = [C3349: [num: 0, denom: 1], C3350: [num: 0, denom: 1], C3351: [num: 0, denom: 1], 
		C3352: [num: 0, denom: 1], C3353: [num: 1, denom: 1], C3354: [num: 0, denom: 1], C3355: [num:0, denom: 1]]

	
    private static final String W150004 = "src/test/resources/samples/hedis-w15/W150004.xml" //denom not met
    private static final Map MEASURES_W150004 = [C3349: [num: 0, denom: 0], C3350: [num: 0, denom: 0], C3351: [num: 0, denom: 0], 
		C3352: [num: 0, denom: 0], C3353: [num: 0, denom: 0], C3354: [num: 0, denom: 0], C3355: [num: 0, denom: 0]]
	
	private static final String W150005 = "src/test/resources/samples/hedis-w15/W150005.xml" //denom not met
   private static final Map MEASURES_W150005 = [C3349: [num: 0, denom: 0], C3350: [num: 0, denom: 0], C3351: [num: 0, denom: 0], 
		C3352: [num: 0, denom: 0], C3353: [num: 0, denom: 0], C3354: [num: 0, denom: 0], C3355: [num: 0, denom: 0]]

	
	private static final String W150006 = "src/test/resources/samples/hedis-w15/W150006.xml" //denom not met
   private static final Map MEASURES_W150006 = [C3349: [num: 0, denom: 0], C3350: [num: 0, denom: 0], C3351: [num: 0, denom: 0], 
		C3352: [num: 0, denom: 0], C3353: [num: 0, denom: 0], C3354: [num: 0, denom: 0], C3355: [num: 0, denom: 0]]
	
	private static final String W150007 = "src/test/resources/samples/hedis-w15/W150007.xml" //0 well care visits
	private static final Map MEASURES_W150007 = [C3349: [num: 1, denom: 1], C3350: [num: 0, denom: 1], C3351: [num: 0, denom: 1],
		C3352: [num: 0, denom: 1], C3353: [num: 0, denom: 1], C3354: [num: 0, denom: 1], C3355: [num:0, denom: 1]]

	
	private static final String W150008 = "src/test/resources/samples/hedis-w15/W150008.xml" //3 well care visits
	private static final Map MEASURES_W150008 = [C3349: [num: 0, denom: 1], C3350: [num: 0, denom: 1], C3351: [num: 0, denom: 1],
		C3352: [num: 1, denom: 1], C3353: [num: 0, denom: 1], C3354: [num: 0, denom: 1], C3355: [num: 0, denom: 1]]

	private static final String W150009 = "src/test/resources/samples/hedis-w15/W150009.xml" //2 well care visits
	private static final Map MEASURES_W150009 = [C3349: [num: 0, denom: 1], C3350: [num: 0, denom: 1], C3351: [num: 1, denom: 1],
		C3352: [num: 0, denom: 1], C3353: [num: 0, denom: 1], C3354: [num: 0, denom: 1], C3355: [num:0, denom: 1]]

	
	private static final String W150010 = "src/test/resources/samples/hedis-w15/W150010.xml" //1 well care visits
	private static final Map MEASURES_W150010 = [C3349: [num: 0, denom: 1], C3350: [num: 1, denom: 1], C3351: [num: 0, denom: 1],
		C3352: [num: 0, denom: 1], C3353: [num: 0, denom: 1], C3354: [num: 0, denom: 1], C3355: [num: 0, denom: 1]]

	private static final String W150011 = "src/test/resources/samples/hedis-w15/W150011.xml" //1 well care visits, but with nurse
	private static final Map MEASURES_W150011 = 
	[C3349: [num: 1, denom: 1], C3350: [num: 0, denom: 1], C3351: [num: 0, denom: 1],C3352: [num: 0, denom: 1], C3353: [num: 0, denom: 1], C3354: [num: 0, denom: 1], C3355: [num: 0, denom: 1]]

//	private static final String W150012 = "H:/KMM ITS/Projects/OpenCDS/PHI Data Sets/w15-sample.xml" //7 well care visits, but with nurse
//	private static final Map MEASURES_W150012 = 
//	[C3349: [num: 0, denom: 1], C3350: [num: 0, denom: 1], C3351: [num: 0, denom: 1],C3352: [num: 0, denom: 1], C3353: [num: 0, denom: 1], C3354: [num: 0, denom: 1], C3355: [num: 1, denom: 1]]

/*
INPUT
"	2 -> month(s)"
"	C2511 -> HEDIS 2014"
"	C2683 -> Patient Age GE 15 and LT 27 Months"
"	C3062 -> HEDIS-Well-Care"
"	C36 -> OpenCDS"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	C2624 -> QM HEDIS-W15 Well-Child Visit First 15 Mo."
"	C2683 -> Patient Age GE 15 and LT 27 Months"
"	C2684 -> Well-Child Visit Count 0"
"	C2685 -> Well-Child Visit Count 1"
"	C2686 -> Well-Child Visit Count 2"
"	C2687 -> Well-Child Visit Count 3"
"	C2688 -> Well-Child Visit Count 4"
"	C2689 -> Well-Child Visit Count 5"
"	C2690 -> Well-Child Visit Count 6 or More"
"	C3349 -> QM HEDIS-W15 Well-Child Visit First 15 Mo.(none)"
"	C3350 -> QM HEDIS-W15 Well-Child Visit First 15 Mo.(1)"
"	C3351 -> QM HEDIS-W15 Well-Child Visit First 15 Mo.(2)"
"	C3352 -> QM HEDIS-W15 Well-Child Visit First 15 Mo.(3)"
"	C3353 -> QM HEDIS-W15 Well-Child Visit First 15 Mo.(4)"
"	C3354 -> QM HEDIS-W15 Well-Child Visit First 15 Mo.(5)"
"	C3355 -> QM HEDIS-W15 Well-Child Visit First 15 Mo.(6+)"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"
*/
	
	@Unroll
	def "test HEDIS W15 v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_W15', version: '2014.1.0'],
			specifiedTime: '2012-01-01'
//			specifiedTime: '2014-01-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
		println responsePayload

		then:
		def data = new XmlSlurper().parseText(responsePayload)
		def results = VMRUtil.getResults(data, '\\|')
	/*	assertions.size() == results.assertions.size()
		assertions.each {entry ->
			assert results.assertions.containsKey(entry.key)
			if (entry.value) {
				assert results.assertions.get(entry.key) == entry.value
			}
		}*/
		results.assertions.each {entry ->
			System.err.println "${entry.key} -> ${entry.value}"
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
		vmr /*| assertions*/ | measures
		EMPTY0001 /*| ASSERTIONS_EMPTY0001*/| MEASURES_EMPTY0001 
		W150001 /*| ASSERTIONS_W150001*/| MEASURES_W150001
		W150002 /*| ASSERTIONS_W150002*/| MEASURES_W150002
		W150003 /*| ASSERTIONS_W150003*/| MEASURES_W150003
		W150004 /*| ASSERTIONS_W150003*/| MEASURES_W150004
		W150005 /*| ASSERTIONS_W150003*/| MEASURES_W150005
		W150006 /*| ASSERTIONS_W150003*/| MEASURES_W150006
		W150007 /*| ASSERTIONS_W150003*/| MEASURES_W150007
		W150008 /*| ASSERTIONS_W150003*/| MEASURES_W150008
		W150009 /*| ASSERTIONS_W150003*/| MEASURES_W150009
		W150010 /*| ASSERTIONS_W150003*/| MEASURES_W150010
		W150011 /*| ASSERTIONS_W150003*/| MEASURES_W150011
//		W150012 /*| ASSERTIONS_W150003*/| MEASURES_W150012

		
	}
}
