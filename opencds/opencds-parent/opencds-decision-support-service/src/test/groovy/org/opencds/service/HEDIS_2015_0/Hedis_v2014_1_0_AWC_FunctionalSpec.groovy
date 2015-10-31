package org.opencds.service.HEDIS_2015_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_AWC_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2626: [num: -1, denom: -1]]
	
    private static final String AWC0001 = "src/test/resources/samples/hedis-awc/SampleAWC0001.xml" //Num Met
	private static final Map ASSERTIONS_AWC0001 = [C2679: '', C2680: '', C539: '', C54: '', C545: '']
    private static final Map MEASURES_AWC0001  = [C2626: [num: 1, denom: 1]]

	
    private static final String AWC0002 = "src/test/resources/samples/hedis-awc/SampleAWC0002.xml" //Num Met
	private static final Map ASSERTIONS_AWC0002 = [C2679: '', C2680: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_AWC0002  = [C2626: [num: 1, denom: 1]]
	
    private static final String AWC0003 = "src/test/resources/samples/hedis-awc/SampleAWC0003.xml" //Num Not Met
	private static final Map ASSERTIONS_AWC0003 = [C2679: '', C54: '', C545: '']
	private static final Map MEASURES_AWC0003  = [C2626: [num: 0, denom: 1]]

	
    private static final String AWC0004 = "src/test/resources/samples/hedis-awc/SampleAWC0004.xml" //Denom Not Met
	private static final Map ASSERTIONS_AWC0004 = [:]
	private static final Map MEASURES_AWC0004  = [C2626: [num: 0, denom: 0]]
	
	private static final String AWC0005 = "src/test/resources/samples/hedis-awc/SampleAWC0005.xml" //Denom Not Met
	private static final Map ASSERTIONS_AWC0005 = [:]
	private static final Map MEASURES_AWC0005  = [C2626: [num: 0, denom: 0]]

	
	private static final String AWC0006 = "src/test/resources/samples/hedis-awc/SampleAWC0006.xml" //Num Not Met
	private static final Map ASSERTIONS_AWC0006 = [C2679: '', C54: '', C545: '']
	private static final Map MEASURES_AWC0006  = [C2626: [num: 0, denom: 1]]
	
	private static final String AWC0007 = "src/test/resources/samples/hedis-awc/SampleAWC0007.xml" //Num Not Met
	private static final Map ASSERTIONS_AWC0007 = [C2679: '', C54: '', C545: '']
	private static final Map MEASURES_AWC0007  = [C2626: [num: 0, denom: 1]]

	
	private static final String AWC0008 = "src/test/resources/samples/hedis-awc/SampleAWC0008.xml" //Num Not Met
	private static final Map ASSERTIONS_AWC0008 = [C2679: '', C54: '', C545: '']
	private static final Map MEASURES_AWC0008  = [C2626: [num: 0, denom: 1]]

	private static final String AWC0009 = "src/test/resources/samples/hedis-awc/SampleAWC0009.xml" //Num Not Met
	private static final Map ASSERTIONS_AWC0009 = [C2679: '', C54: '', C545: '']
	private static final Map MEASURES_AWC0009 = [C2626: [num: 0, denom: 1]]


/*
Concepts used:
INPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C2511 -> HEDIS 2014"
"	C2557 -> Performer"
"	C2679 -> Patient Age GE 12 and LT 22 Years"
"	C2704 -> Provider Primary Care (PCP)"
"	C2705 -> Provider Obstetrics and Gynecology  (OB/GYN)"
"	C3062 -> HEDIS-Well-Care"
"	C36 -> OpenCDS"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	C2626 -> QM HEDIS-AWC Adolescent Well-Care Visits"
"	C2679 -> Patient Age GE 12 and LT 22 Years"
"	C2680 -> Comprehensive Well-Care Visit"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"
*/
	
	@Unroll
	def "test HEDIS AWC v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_AWC', version: '2014.1.0'],
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
		AWC0001 | ASSERTIONS_AWC0001| MEASURES_AWC0001
		AWC0002 | ASSERTIONS_AWC0002| MEASURES_AWC0002
		AWC0003 | ASSERTIONS_AWC0003| MEASURES_AWC0003
//		AWC0004 | ASSERTIONS_AWC0004| MEASURES_AWC0004
//		AWC0005 | ASSERTIONS_AWC0005| MEASURES_AWC0005
		AWC0006 | ASSERTIONS_AWC0006| MEASURES_AWC0006
		AWC0007 | ASSERTIONS_AWC0007| MEASURES_AWC0007
		AWC0008 | ASSERTIONS_AWC0008| MEASURES_AWC0008
		AWC0009 | ASSERTIONS_AWC0009| MEASURES_AWC0009


	}
}
