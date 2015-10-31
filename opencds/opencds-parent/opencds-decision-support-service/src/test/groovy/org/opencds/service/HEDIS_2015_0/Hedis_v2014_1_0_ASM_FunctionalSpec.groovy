package org.opencds.service.HEDIS_2015_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_ASM_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2613: [num: -1, denom: -1]]
	
    private static final String ASM0001 = "src/test/resources/samples/hedis-asm/SampleASM0001.xml" //Num Met
	private static final Map ASSERTIONS_ASM0001 = [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C539:'', C54: '', C545: '']
    private static final Map MEASURES_ASM0001  = [C2613: [num: 1, denom: 1]]

	
    private static final String ASM0002 = "src/test/resources/samples/hedis-asm/SampleASM0002.xml" //Num Met
	private static final Map ASSERTIONS_ASM0002 = [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C539:'', C54: '', C545: '']
	private static final Map MEASURES_ASM0002  = [C2613: [num: 1, denom: 1]]
	
    private static final String ASM0003 = "src/test/resources/samples/hedis-asm/SampleASM0003.xml" //Num Met
	private static final Map ASSERTIONS_ASM0003 = [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C539:'', C54: '', C545: '', NonAcuteEncountersYr2:'4']
	private static final Map MEASURES_ASM0003  = [C2613: [num: 1, denom: 1]]

	
    private static final String ASM0004 = "src/test/resources/samples/hedis-asm/SampleASM0004.xml" //Num Met
	private static final Map ASSERTIONS_ASM0004 = [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C539:'', C54: '', C545: '', NonAcuteEncountersYr1:'4']
	private static final Map MEASURES_ASM0004  = [C2613: [num: 1, denom: 1]]
	
	private static final String ASM0005 = "src/test/resources/samples/hedis-asm/SampleASM0005.xml" //Num Met
	private static final Map ASSERTIONS_ASM0005 = [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C3271: '', C3272: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_ASM0005  = [C2613: [num: 1, denom: 1]]

	
	private static final String ASM0006 = "src/test/resources/samples/hedis-asm/SampleASM0006.xml" //Num Met
	private static final Map ASSERTIONS_ASM0006 = [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C3271: '', C3272: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_ASM0006  = [C2613: [num: 1, denom: 1]]
	
	private static final String ASM0007 = "src/test/resources/samples/hedis-asm/SampleASM0007.xml" //Num Met
	private static final Map ASSERTIONS_ASM0007 = [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C3271: '', C3272: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_ASM0007  = [C2613: [num: 1, denom: 1]]

	
	private static final String ASM0008 = "src/test/resources/samples/hedis-asm/SampleASM0008.xml" //Num Met
	private static final Map ASSERTIONS_ASM0008 = [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C3271: '', C3272: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_ASM0008  = [C2613: [num: 1, denom: 1]]
/*
	private static final String ASM0009 = "src/test/resources/samples/hedis-asm/SampleASM0009.xml" //Denom Not Met
	private static final Map ASSERTIONS_ASM0009 = [:]
	private static final Map MEASURES_ASM0009 = [C2613: [num: 0, denom: 0]]
*/
	
	private static final String ASM0010 = "src/test/resources/samples/hedis-asm/SampleASM0010.xml" //Num Met
	private static final Map ASSERTIONS_ASM0010 = [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C3271: '', C3272: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_ASM0010  = [C2613: [num: 1, denom: 1]]
	
/*
Concepts used:
INPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C1938 -> Emphysema"
"	C2361 -> Injection"
"	C2511 -> HEDIS 2014"
"	C2770 -> Patient Age GE 5 and LT 12 Years"
"	C2771 -> Patient Age GE 12 and LT 19 Years"
"	C2772 -> Patient Age GE 19 and LT 51 Years"
"	C2773 -> Patient Age GE 51 and LT 65 Years"
"	C2787 -> HEDIS-Other Emphysema"
"	C2788 -> Bronchitis"
"	C2789 -> Chronic Respiratory Conditions due to Fumes or Vapors"
"	C2790 -> Cystic Fibrosis"
"	C284 -> Respiratory Failure"
"	C2845 -> HEDIS-ASM Table C Asthma Medications"
"	C2846 -> HEDIS-ASM Table D Asthma Controller Medications"
"	C2964 -> HEDIS-Outpatient"
"	C2968 -> HEDIS-ED"
"	C2971 -> HEDIS-Acute Inpatient"
"	C3022 -> HEDIS-Observation"
"	C3067 -> HEDIS-Acute Respiratory Failure"
"	C3071 -> HEDIS-Asthma"
"	C3089 -> HEDIS-Chronic Respiratory Conditions Due To Fumes/Vapors"
"	C3094 -> HEDIS-COPD"
"	C3095 -> HEDIS-Cystic Fibrosis"
"	C3111 -> HEDIS-Emphysema"
"	C3139 -> HEDIS-Obstructive Chronic Bronchitis"
"	C3256 -> HEDIS-ASM Table C Subset - Non-Leukotriene Modifiers"
"	C3265 -> Named Dates Inserted"
"	C3268 -> Patient Age GE 5 and LT 65 Years"
"	C3269 -> Asthma Criterion Met"
"	C3270 -> Asthma Criterion Met"
"	C3271 -> Asthma Criterion Met"
"	C3272 -> Asthma Criterion Met"
"	C36 -> OpenCDS"
"	C405 -> Part of"
"	C416 -> Primary"
"	C42 -> Chronic Obstructive Pulmonary Disease (COPD)"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
"	C597 -> Oral"
"	C598 -> Inhalation"
OUTPUT
"	1 -> year(s)"
"	C1938 -> Emphysema"
"	C2613 -> QM HEDIS-ASM Asthma Pharmacotherapy"
"	C2770 -> Patient Age GE 5 and LT 12 Years"
"	C2771 -> Patient Age GE 12 and LT 19 Years"
"	C2772 -> Patient Age GE 19 and LT 51 Years"
"	C2773 -> Patient Age GE 51 and LT 65 Years"
"	C2788 -> Bronchitis"
"	C2789 -> Chronic Respiratory Conditions due to Fumes or Vapors"
"	C2790 -> Cystic Fibrosis"
"	C284 -> Respiratory Failure"
"	C3265 -> Named Dates Inserted"
"	C3268 -> Patient Age GE 5 and LT 65 Years"
"	C3269 -> Asthma Criterion Met"
"	C3270 -> Asthma Criterion Met"
"	C3271 -> Asthma Criterion Met"
"	C3272 -> Asthma Criterion Met"
"	C3360 -> QM HEDIS-ASM Asthma Pharmacotherapy (Age 05-11)"
"	C3361 -> QM HEDIS-ASM Asthma Pharmacotherapy (Age 12-18)"
"	C3362 -> QM HEDIS-ASM Asthma Pharmacotherapy (Age 19-50)"
"	C3363 -> QM HEDIS-ASM Asthma Pharmacotherapy (Age 51-64)"
"	C42 -> Chronic Obstructive Pulmonary Disease (COPD)"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"

*/
	
	@Unroll
	def "test HEDIS ASM v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_ASM', version: '2014.1.0'],
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
		ASM0001 | ASSERTIONS_ASM0001| MEASURES_ASM0001
		ASM0002 | ASSERTIONS_ASM0002| MEASURES_ASM0002
		ASM0003 | ASSERTIONS_ASM0003| MEASURES_ASM0003
		ASM0004 | ASSERTIONS_ASM0004| MEASURES_ASM0004
		ASM0005 | ASSERTIONS_ASM0005| MEASURES_ASM0005
		ASM0006 | ASSERTIONS_ASM0006| MEASURES_ASM0006
		ASM0007 | ASSERTIONS_ASM0007| MEASURES_ASM0007
		ASM0008 | ASSERTIONS_ASM0008| MEASURES_ASM0008
//		ASM0009 | ASSERTIONS_ASM0009| MEASURES_ASM0009
		ASM0010 | ASSERTIONS_ASM0010| MEASURES_ASM0010

	}
}
