package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_CWP_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2604: [num: -1, denom: -1]]
	
    private static final String CWP0001 = "src/test/resources/samples/hedis-cwp/SampleCWP0001.xml" //Num Met
	private static final Map ASSERTIONS_CWP0001 = [C2791:'',C539:'',C54:'',C545:'',IESD:'2011-02-13 11:50:00']
    private static final Map MEASURES_CWP0001  = [C2604: [num: 1, denom: 1]]

	
    private static final String CWP0002 = "src/test/resources/samples/hedis-cwp/SampleCWP0002.xml" //Denom Met
	private static final Map ASSERTIONS_CWP0002 = [C2791:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00']
	private static final Map MEASURES_CWP0002  = [C2604: [num: 0, denom: 1]]
	
    private static final String CWP0003 = "src/test/resources/samples/hedis-cwp/SampleCWP0003.xml" // Num Met
	private static final Map ASSERTIONS_CWP0003 = [C2791:'',C539:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00']
	private static final Map MEASURES_CWP0003  = [C2604: [num: 1, denom: 1]]

	
    private static final String CWP0004 = "src/test/resources/samples/hedis-cwp/SampleCWP0004.xml" //Num Met
	private static final Map ASSERTIONS_CWP0004 = [C2791:'',C539:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00']
	private static final Map MEASURES_CWP0004  = [C2604: [num: 1, denom: 1]]
	
	private static final String CWP0005 = "src/test/resources/samples/hedis-cwp/SampleCWP0005.xml" //Num Met
	private static final Map ASSERTIONS_CWP0005 = [C2791:'',C539:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00']
	private static final Map MEASURES_CWP0005  = [C2604: [num: 1, denom: 1]]

	
	private static final String CWP0006 = "src/test/resources/samples/hedis-cwp/SampleCWP0006.xml" //Denom Met
	private static final Map ASSERTIONS_CWP0006 = [C2791:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00']
	private static final Map MEASURES_CWP0006  = [C2604: [num: 0, denom: 1]]
	
	private static final String CWP0007 = "src/test/resources/samples/hedis-cwp/SampleCWP0007.xml" //Denom Met
	private static final Map ASSERTIONS_CWP0007 = [C2791:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00']
	private static final Map MEASURES_CWP0007  = [C2604: [num: 0, denom: 1]]

	
	private static final String CWP0008 = "src/test/resources/samples/hedis-cwp/SampleCWP0008.xml" //Num Met
	private static final Map ASSERTIONS_CWP0008 = [C2791:'',C539:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00']
	private static final Map MEASURES_CWP0008  = [C2604: [num: 1, denom: 1]]

	private static final String CWP0009 = "src/test/resources/samples/hedis-cwp/SampleCWP0009.xml" //Num Met
	private static final Map ASSERTIONS_CWP0009 = [C2791:'',C539:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00']
	private static final Map MEASURES_CWP0009 = [C2604: [num: 1, denom: 1]]

	
	private static final String CWP0010 = "src/test/resources/samples/hedis-cwp/SampleCWP0010.xml" //Denom Not Met
	private static final Map ASSERTIONS_CWP0010 = [C2791:'',C545:'']
	private static final Map MEASURES_CWP0010  = [C2604: [num: 0, denom: 0]]


/*
Concepts used:
INPUT
"	1 -> year(s)"
"	2 -> month(s)"
"	5 -> day(s)"
"	C2511 -> HEDIS 2014"
"	C2791 -> Patient Age GE 2 and LT 19 Years"
"	C2847 -> HEDIS-CWP Table C Antibiotic Medications"
"	C2964 -> HEDIS-Outpatient"
"	C2968 -> HEDIS-ED"
"	C2971 -> HEDIS-Acute Inpatient"
"	C2981 -> HEDIS-Group A Strep Tests"
"	C3020 -> HEDIS-Nonacute Inpatient"
"	C3022 -> HEDIS-Observation"
"	C3141 -> HEDIS-Pharyngitis"
"	C36 -> OpenCDS"
"	C405 -> Part of"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	5 -> day(s)"
"	C2604 -> QM HEDIS-CWP Test Children with Pharyngitis"
"	C2791 -> Patient Age GE 2 and LT 19 Years"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"

*/
	
	@Unroll
	def "test HEDIS CWP v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CWP', version: '2014.1.0'],
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
		CWP0001 | ASSERTIONS_CWP0001| MEASURES_CWP0001
		CWP0002 | ASSERTIONS_CWP0002| MEASURES_CWP0002
		CWP0003 | ASSERTIONS_CWP0003| MEASURES_CWP0003
		CWP0004 | ASSERTIONS_CWP0004| MEASURES_CWP0004
		CWP0005 | ASSERTIONS_CWP0005| MEASURES_CWP0005
		CWP0006 | ASSERTIONS_CWP0006| MEASURES_CWP0006
		CWP0007 | ASSERTIONS_CWP0007| MEASURES_CWP0007
		CWP0008 | ASSERTIONS_CWP0008| MEASURES_CWP0008
		CWP0009 | ASSERTIONS_CWP0009| MEASURES_CWP0009
		CWP0010 | ASSERTIONS_CWP0010| MEASURES_CWP0010

	}
}
