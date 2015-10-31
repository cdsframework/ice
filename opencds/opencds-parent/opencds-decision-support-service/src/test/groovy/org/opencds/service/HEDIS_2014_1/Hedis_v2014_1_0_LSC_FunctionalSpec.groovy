package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_LSC_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2596: [num: -1, denom: -1]]
	
    private static final String LSC0001 = "src/test/resources/samples/hedis-lsc/LSC0001.xml" //last day eligible
	private static final Map ASSERTIONS_LSC0001 = [C3285: '',C54: '',DOB:'', SecondBirthday:'']
    private static final Map MEASURES_LSC0001  = [C2596: [num: 0, denom: 1]]

	
    private static final String LSC0002 = "src/test/resources/samples/hedis-lsc/LSC0002.xml" // too young
	private static final Map ASSERTIONS_LSC0002 = [DOB:'', SecondBirthday:'']
	private static final Map MEASURES_LSC0002  = [C2596: [num: 0, denom: 0]]
	
    private static final String LSC0003 = "src/test/resources/samples/hedis-lsc/LSC0003.xml" // testing boundary - earliest DOB eligible
	private static final Map ASSERTIONS_LSC0003 = [C3285: '',C54: '',DOB:'', SecondBirthday:'']
	private static final Map MEASURES_LSC0003  = [C2596: [num: 0, denom: 1]]

	
    private static final String LSC0004 = "src/test/resources/samples/hedis-lsc/LSC0004.xml" // too old
	private static final Map ASSERTIONS_LSC0004 = [DOB:'', SecondBirthday:'']
	private static final Map MEASURES_LSC0004  = [C2596: [num: 0, denom: 0]]
	
	private static final String LSC0005 = "src/test/resources/samples/hedis-lsc/LSC0005.xml" //cpt test within timeframe
	private static final Map ASSERTIONS_LSC0005 = [C3285: '', C54: '',DOB:'', SecondBirthday:'', C2701:'', C539:'']
	private static final Map MEASURES_LSC0005  = [C2596: [num: 1, denom: 1]]

	
	private static final String LSC0006 = "src/test/resources/samples/hedis-lsc/LSC0006.xml" //CPT test but day after 2nd birthday
	private static final Map ASSERTIONS_LSC0006 = [C3285: '',C54: '',DOB:'', SecondBirthday:'']
	private static final Map MEASURES_LSC0006  = [C2596: [num: 0, denom: 1]]
	
	private static final String LSC0007 = "src/test/resources/samples/hedis-lsc/LSC0007.xml" //testing the LOINC lab test in correct timeframe
	private static final Map ASSERTIONS_LSC0007 = [C3285: '', C54: '',DOB:'']
	private static final Map MEASURES_LSC0007  = [C2596: [num: 1, denom: 1]]

	
	private static final String LSC0008 = "src/test/resources/samples/hedis-lsc/LSC0008.xml" //LOINC lab but before DOB
	private static final Map ASSERTIONS_LSC0008 = [C3285: '',C54: '',DOB:'', SecondBirthday:'']
	private static final Map MEASURES_LSC0008  = [C2596: [num: 0, denom: 1]]




/*
Concepts used:
INPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C2511 -> HEDIS 2014"
"	C3002 -> HEDIS-Lead Tests"
"	C3285 -> Patient Age EQ 2 Years"
"	C36 -> OpenCDS"
"	C54 -> Denominator Criteria Met"
OUTPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C2596 -> QM HEDIS-LSC Lead Screening in Children"
"	C2701 -> Blood Lead Test"
"	C3285 -> Patient Age EQ 2 Years"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C569 -> Missing Data for Date of Birth"


*/
	
	@Unroll
	def "test HEDIS LSC v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_LSC', version: '2014.1.0'],
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
		LSC0001 | ASSERTIONS_LSC0001| MEASURES_LSC0001
		LSC0002 | ASSERTIONS_LSC0002| MEASURES_LSC0002
		LSC0003 | ASSERTIONS_LSC0003| MEASURES_LSC0003
		LSC0004 | ASSERTIONS_LSC0004| MEASURES_LSC0004
		LSC0005 | ASSERTIONS_LSC0005| MEASURES_LSC0005
		LSC0006 | ASSERTIONS_LSC0006| MEASURES_LSC0006
		LSC0007 | ASSERTIONS_LSC0007| MEASURES_LSC0007
		LSC0008 | ASSERTIONS_LSC0008| MEASURES_LSC0008


	}
}
