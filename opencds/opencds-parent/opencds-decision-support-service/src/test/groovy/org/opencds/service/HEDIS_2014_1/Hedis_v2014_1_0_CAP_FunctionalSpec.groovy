package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_CAP_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2645: [num: -1, denom: -1]]
	
    private static final String CAP0001 = "src/test/resources/samples/hedis-cap/SampleCAP0001.xml" //Num Met
	private static final Map ASSERTIONS_CAP0001 = [C2742:'',C2856:'',C3287:'',C539:'',C54:'',C545:'']
    private static final Map MEASURES_CAP0001  = [C2645: [num: 1, denom: 1], C3368: [num: 0, denom: 0], C3369: [num: 0, denom: 0],
												  C3370: [num: 1, denom: 1], C3371: [num: 0, denom: 0],]

	
    private static final String CAP0002 = "src/test/resources/samples/hedis-cap/SampleCAP0002.xml" //Num Met
	private static final Map ASSERTIONS_CAP0002 = [C2742:'',C2856:'',C3287:'',C539:'',C54:'',C545:'']
	private static final Map MEASURES_CAP0002  = [C2645: [num: 1, denom: 1], C3368: [num: 0, denom: 0], C3369: [num: 0, denom: 0],
												  C3370: [num: 1, denom: 1], C3371: [num: 0, denom: 0],]
	
    private static final String CAP0003 = "src/test/resources/samples/hedis-cap/SampleCAP0003.xml" //Num Not Met
	private static final Map ASSERTIONS_CAP0003 = [C2741:'',C2856:'',C3286:'',C54:'',C545:'']
	private static final Map MEASURES_CAP0003  = [C2645: [num: 0, denom: 1], C3368: [num: 0, denom: 0], C3369: [num: 0, denom: 1],
												  C3370: [num: 0, denom: 0], C3371: [num: 0, denom: 0],]

	
    private static final String CAP0004 = "src/test/resources/samples/hedis-cap/SampleCAP0004.xml" //Num Met
	private static final Map ASSERTIONS_CAP0004 = [C2740:'',C2856:'',C3286:'',C539:'',C54:'',C545:'']
	private static final Map MEASURES_CAP0004  = [C2645: [num: 1, denom: 1], C3368: [num: 1, denom: 1], C3369: [num: 0, denom: 0],
												  C3370: [num: 0, denom: 0], C3371: [num: 0, denom: 0],]
	
	private static final String CAP0005 = "src/test/resources/samples/hedis-cap/SampleCAP0005.xml" //Num Met
	private static final Map ASSERTIONS_CAP0005 = [C2743:'',C2856:'',C3287:'',C539:'',C54:'',C545:'']
	private static final Map MEASURES_CAP0005  = [C2645: [num: 1, denom: 1], C3368: [num: 0, denom: 0], C3369: [num: 0, denom: 0],
												  C3370: [num: 0, denom: 0], C3371: [num: 1, denom: 1],]

	
/*
Concepts used:
INPUT
"	1 -> year(s)"
"	2 -> month(s)"
"	5 -> day(s)"
"	C2511 -> HEDIS 2014"
"	C2557 -> Performer"
"	C2704 -> Provider Primary Care (PCP)"
"	C2856 -> Patient Age GE 12 Months and LT 20 Years"
"	C2969 -> HEDIS-Ambulatory Visits"
"	C3286 -> Patient Age GE 12 Months and LT 7 Years"
"	C3287 -> Patient Age GE 7 and  LT 20 Years"
"	C36 -> OpenCDS"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	C2645 -> QM HEDIS-CAP Youth Access Prim Care"
"	C2740 -> Patient Age GE 12 and LT 25 Months"
"	C2741 -> Patient Age GE 25 and LT 84 Months"
"	C2742 -> Patient Age GE 7 and LT 12 Years"
"	C2743 -> Patient Age GE 12 and LT 20 Years"
"	C2856 -> Patient Age GE 12 Months and LT 20 Years"
"	C3286 -> Patient Age GE 12 Months and LT 7 Years"
"	C3287 -> Patient Age GE 7 and  LT 20 Years"
"	C3368 -> QM HEDIS-CAP Youth Access Prim Care (Age 12mo-24mo)"
"	C3369 -> QM HEDIS-CAP Youth Access Prim Care (Age 25mo-06yr)"
"	C3370 -> QM HEDIS-CAP Youth Access Prim Care (Age 07yr-11yr)"
"	C3371 -> QM HEDIS-CAP Youth Access Prim Care (Age 12yr-19yr)"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"

*/
	
	@Unroll
	def "test HEDIS CAP v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CAP', version: '2014.1.0'],
			specifiedTime: '2012-01-01'
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
		CAP0001 | ASSERTIONS_CAP0001| MEASURES_CAP0001
		CAP0002 | ASSERTIONS_CAP0002| MEASURES_CAP0002
		CAP0003 | ASSERTIONS_CAP0003| MEASURES_CAP0003
		CAP0004 | ASSERTIONS_CAP0004| MEASURES_CAP0004
		CAP0005 | ASSERTIONS_CAP0005| MEASURES_CAP0005
	}
}
