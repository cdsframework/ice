package org.opencds.service.HEDIS_2015_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_AAP_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2644: [num: -1, denom: -1]]
	
    private static final String AAP0001 = "src/test/resources/samples/hedis-aap/SampleAAP0001.xml" //Num Met
	private static final Map ASSERTIONS_AAP0001 = [C2746: '', C2857: '', C2858: '', C2859: '',C539:'',C54:'',C545:'']
    private static final Map MEASURES_AAP0001  = [C2644: [num: 1, denom: 1]]

	
    private static final String AAP0002 = "src/test/resources/samples/hedis-aap/SampleAAP0002.xml" //Num Met
	private static final Map ASSERTIONS_AAP0002 = [C2746: '', C2857: '', C2859: '',C539:'',C54:'',C545:'']
	private static final Map MEASURES_AAP0002  = [C2644: [num: 1, denom: 1]]
	
    private static final String AAP0003 = "src/test/resources/samples/hedis-aap/SampleAAP0003.xml" //Num Met
	private static final Map ASSERTIONS_AAP0003 = [C2746: '', C2857: '', C2859: '',C539:'',C54:'',C545:'']
	private static final Map MEASURES_AAP0003  = [C2644: [num: 1, denom: 1]]

	
    private static final String AAP0004 = "src/test/resources/samples/hedis-aap/SampleAAP0004.xml" //Denom MEt
	private static final Map ASSERTIONS_AAP0004 = [C2746: '', C2857: '', C2859: '',C539:'',C54:'',C545:'']
	private static final Map MEASURES_AAP0004  = [C2644: [num: 1, denom: 1]]
	
	private static final String AAP0005 = "src/test/resources/samples/hedis-aap/SampleAAP0005.xml" //Num Met
	private static final Map ASSERTIONS_AAP0005 = [C2746: '', C2857: '', C2859: '',C539:'',C54:'',C545:'']
	private static final Map MEASURES_AAP0005  = [C2644: [num: 1, denom: 1]]

	
	private static final String AAP0006 = "src/test/resources/samples/hedis-aap/SampleAAP0006.xml" //Num Met
	private static final Map ASSERTIONS_AAP0006 = [C2746: '', C2857: '', C2859: '',C539:'',C54:'',C545:'']
	private static final Map MEASURES_AAP0006  = [C2644: [num: 1, denom: 1]]
	
	private static final String AAP0007 = "src/test/resources/samples/hedis-aap/SampleAAP0007.xml" //Num Met
	private static final Map ASSERTIONS_AAP0007 = [C2746: '', C2858: '', C2859: '',C539:'',C54:'',C545:'']
	private static final Map MEASURES_AAP0007  = [C2644: [num: 1, denom: 1]]

	
	private static final String AAP0008 = "src/test/resources/samples/hedis-aap/SampleAAP0008.xml" //Num Met
	private static final Map ASSERTIONS_AAP0008 = [C2746: '', C2858: '', C2859: '',C539:'',C54:'',C545:'']
	private static final Map MEASURES_AAP0008  = [C2644: [num: 1, denom: 1]]

	private static final String AAP0009 = "src/test/resources/samples/hedis-aap/SampleAAP0009.xml" //Num Met
	private static final Map ASSERTIONS_AAP0009 = [C2746: '', C2858: '', C2859: '',C539:'',C54:'',C545:'']
	private static final Map MEASURES_AAP0009 = [C2644: [num: 1, denom: 1]]

/*	
	private static final String AAP0010 = "src/test/resources/samples/hedis-aap/SampleAAP0010.xml" //Num Met
	private static final Map ASSERTIONS_AAP0010 = [:]
	private static final Map MEASURES_AAP0010  = [C2644: [num: 1, denom: 1]]
*/	
	private static final String AAP0011 = "src/test/resources/samples/hedis-aap/SampleAAP0011.xml" //Num Met
	private static final Map ASSERTIONS_AAP0011 = [C2747: '', C2857: '', C2858: '',C2859: '',C539:'',C54:'',C545:'']
	private static final Map MEASURES_AAP0011  = [C2644: [num: 1, denom: 1]]
	
	private static final String AAP0012 = "src/test/resources/samples/hedis-aap/SampleAAP0012.xml" //Num Met
	private static final Map ASSERTIONS_AAP0012 = [C2746:'', C2859:'',  C54:'',C545:'']
	private static final Map MEASURES_AAP0012  = [C2644: [num: 0, denom: 1]]
	
	private static final String AAP0013 = "src/test/resources/samples/hedis-aap/SampleAAP0013.xml" //Num Met
	private static final Map ASSERTIONS_AAP0013 = [C2608: '', C2857: '', C2858: '', C2859: '',C539:'',C54:'',C545:'']
	private static final Map MEASURES_AAP0013  = [C2644: [num: 1, denom: 1]]

/*
Concepts used:
INPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C2511 -> HEDIS 2014"
"	C2842 -> HEDIS-AAP Table D Antibiotic Medications"
"	C2964 -> HEDIS-Outpatient"
"	C2968 -> HEDIS-ED"
"	C2971 -> HEDIS-Acute Inpatient"
"	C3020 -> HEDIS-Nonacute Inpatient"
"	C3022 -> HEDIS-Observation"
"	C3065 -> HEDIS-Acute Bronchitis"
"	C3092 -> HEDIS-Comorbid Conditions"
"	C3093 -> HEDIS-Competing Diagnosis"
"	C3094 -> HEDIS-COPD"
"	C3095 -> HEDIS-Cystic Fibrosis"
"	C3111 -> HEDIS-Emphysema"
"	C3122 -> HEDIS-HIV"
"	C3131 -> HEDIS-Malignant Neoplasms"
"	C3141 -> HEDIS-Pharyngitis"
"	C3199 -> Patient Age GE 18 LT 65 Years"
"	C36 -> OpenCDS"
"	C405 -> Part of"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	5 -> day(s)"
"	C1527 -> Antibiotic"
"	C2644 -> QM HEDIS-AAP Avoid AntiBx in Adults Acute Bronc."
"	C3199 -> Patient Age GE 18 LT 65 Years"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"


*/
	
	@Unroll
	def "test HEDIS AAP v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_AAP', version: '2014.1.0'],
			specifiedTime: '2012-01-01'
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
		AAP0001 | ASSERTIONS_AAP0001| MEASURES_AAP0001
		AAP0002 | ASSERTIONS_AAP0002| MEASURES_AAP0002
		AAP0003 | ASSERTIONS_AAP0003| MEASURES_AAP0003
		AAP0004 | ASSERTIONS_AAP0004| MEASURES_AAP0004
		AAP0005 | ASSERTIONS_AAP0005| MEASURES_AAP0005
		AAP0006 | ASSERTIONS_AAP0006| MEASURES_AAP0006
		AAP0007 | ASSERTIONS_AAP0007| MEASURES_AAP0007
		AAP0008 | ASSERTIONS_AAP0008| MEASURES_AAP0008
		AAP0009 | ASSERTIONS_AAP0009| MEASURES_AAP0009
/*		AAP0010 | ASSERTIONS_AAP0010| MEASURES_AAP0010*/
		AAP0011 | ASSERTIONS_AAP0011| MEASURES_AAP0011
		AAP0012 | ASSERTIONS_AAP0012| MEASURES_AAP0012
		AAP0013 | ASSERTIONS_AAP0013| MEASURES_AAP0013

	}
}
