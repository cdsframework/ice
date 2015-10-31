package org.opencds.service.HEDIS_2015_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_AMR_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2615: [num: -1, denom: -1]]
	
    private static final String AMR0001 = "src/test/resources/samples/hedis-amr/SampleAMR0001.xml" //Age 19-50, asthma by ED visit two years ago, by ED visit one year ago, 1 controller, 1 reliever, numMet
	private static final Map ASSERTIONS_AMR0001 = [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C539:'',C54:'',C545:'', Controllers:'2',Relievers:'1']
    private static final Map MEASURES_AMR0001 = [C2615: [num: 1, denom: 1], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0], 
		C3376: [num: 1, denom: 1], C3377: [num: 0, denom: 0]]

	
    private static final String AMR0002 = "src/test/resources/samples/hedis-amr/SampleAMR0002.xml" //Age 19-50
	private static final Map ASSERTIONS_AMR0002 = [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C539:'',C54:'',C545:'',Controllers:'4',Relievers:'3']
	private static final Map MEASURES_AMR0002 = [C2615: [num: 1, denom: 1], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0], 
		C3376: [num: 1, denom: 1], C3377: [num: 0, denom: 0]]
	
    private static final String AMR0003 = "src/test/resources/samples/hedis-amr/SampleAMR0003.xml" //Age 19-50
	private static final Map ASSERTIONS_AMR0003 = [C2772:'',C3265:'',C3268:'',C3269:'']
	private static final Map MEASURES_AMR0003 = [C2615: [num: 0, denom: 0], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0], 
		C3376: [num: 0, denom: 0], C3377: [num: 0, denom: 0]]

	
    private static final String AMR0004 = "src/test/resources/samples/hedis-amr/SampleAMR0004.xml" //Age 19-50
	private static final Map ASSERTIONS_AMR0004 = [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C3272:'',C539:'',C54:'',C545:'',Controllers:'5',Relievers:'4']
	private static final Map MEASURES_AMR0004 = [C2615: [num: 1, denom: 1], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0], 
		C3376: [num: 1, denom: 1], C3377: [num: 0, denom: 0]]
	
	private static final String AMR0005 = "src/test/resources/samples/hedis-amr/SampleAMR0005.xml" //Age 19-50
	private static final Map ASSERTIONS_AMR0005 = [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C3271:'',C3272:'',Controllers:'5',Relievers:'0']
	private static final Map MEASURES_AMR0005 = [C2615: [num: 1, denom: 1], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0], 
		C3376: [num: 1, denom: 1], C3377: [num: 0, denom: 0]]

	
	private static final String AMR0006 = "src/test/resources/samples/hedis-amr/SampleAMR0006.xml" //denom not met, Age 19-50
	private static final Map ASSERTIONS_AMR0006 = [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C3271:'',C3272:'',C539:'',C54:'',C545:'',Controllers:'5',Relievers:'1']
	private static final Map MEASURES_AMR0006 = [C2615: [num: 1, denom: 1], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0], 
		C3376: [num: 1, denom: 1], C3377: [num: 0, denom: 0]]
	
	private static final String AMR0007 = "src/test/resources/samples/hedis-amr/SampleAMR0007.xml" //Age 19-50
	private static final Map ASSERTIONS_AMR0007 = [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C3271:'',C3272:'',C539:'',C54:'',C545:'',Controllers:'6',Relievers:'1']
	private static final Map MEASURES_AMR0007 = [C2615: [num: 1, denom: 1], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0], 
		C3376: [num: 1, denom: 1], C3377: [num: 0, denom: 0]]

	
	private static final String AMR0008 = "src/test/resources/samples/hedis-amr/SampleAMR0008.xml" //Age 19-50
	private static final Map ASSERTIONS_AMR0008 = [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C3271:'',C3272:'',C539:'',C54:'',C545:'']
	private static final Map MEASURES_AMR0008 = [C2615: [num: 1, denom: 1], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0], 
		C3376: [num: 1, denom: 1], C3377: [num: 0, denom: 0]]
/*
	private static final String AMR0009 = "src/test/resources/samples/hedis-amr/SampleAMR0009.xml" //denom not met
	private static final Map ASSERTIONS_AMR0009 = [:]
	private static final Map MEASURES_AMR0009 = [C2615: [num: 0, denom: 0], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0], 
		C3376: [num: 0, denom: 0], C3377: [num: 0, denom: 0]]
*/
	
	private static final String AMR0010 = "src/test/resources/samples/hedis-amr/SampleAMR0010.xml" //Age 19-50
	private static final Map ASSERTIONS_AMR0010 = [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C3271:'',C3272:'',C539:'',C54:'',C545:'',Controllers:'7',Relievers:'1']
	private static final Map MEASURES_AMR0010 = [C2615: [num: 1, denom: 1], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0], 
		C3376: [num: 1, denom: 1], C3377: [num: 0, denom: 0]]
	
	private static final String AMR0011 = "src/test/resources/samples/hedis-amr/SampleAMR0011.xml" //Age 19-50
	private static final Map ASSERTIONS_AMR0011 = [C1938:'',C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C544:'',C545:'']
	private static final Map MEASURES_AMR0011 = [C2615: [num: 0, denom: 0], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0],
		C3376: [num: 0, denom: 0], C3377: [num: 0, denom: 0]]
	
	private static final String AMR0012 = "src/test/resources/samples/hedis-amr/SampleAMR0012.xml" //Age 19-50
	private static final Map ASSERTIONS_AMR0012 = [C1938:'',C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C544:'',C545:'']
	private static final Map MEASURES_AMR0012 = [C2615: [num: 0, denom: 0], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0],
		C3376: [num: 0, denom: 0], C3377: [num: 0, denom: 0]]
	
	private static final String AMR0013 = "src/test/resources/samples/hedis-amr/SampleAMR0013.xml" //Age 19-50
	private static final Map ASSERTIONS_AMR0013 = [C2789:'',C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C544:'',C545:'']
	private static final Map MEASURES_AMR0013 = [C2615: [num: 0, denom: 0], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0],
		C3376: [num: 0, denom: 0], C3377: [num: 0, denom: 0]]
	
	private static final String AMR0014 = "src/test/resources/samples/hedis-amr/SampleAMR0014.xml" //Age 19-50
	private static final Map ASSERTIONS_AMR0014 = [C42:'',C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C544:'',C545:'']
	private static final Map MEASURES_AMR0014 = [C2615: [num: 0, denom: 0], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0],
		C3376: [num: 0, denom: 0], C3377: [num: 0, denom: 0]]
	
	private static final String AMR0015 = "src/test/resources/samples/hedis-amr/SampleAMR0015.xml" //Age 19-50
	private static final Map ASSERTIONS_AMR0015 = [C2788:'',C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C544:'',C545:'']
	private static final Map MEASURES_AMR0015 = [C2615: [num: 0, denom: 0], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0],
		C3376: [num: 0, denom: 0], C3377: [num: 0, denom: 0]]
	
	private static final String AMR0016 = "src/test/resources/samples/hedis-amr/SampleAMR0016.xml" //Age 05-11, asthma by ED visit two years ago, by ED visit one year ago, 1 controller, 1 reliever, numMet
	private static final Map ASSERTIONS_AMR0016 = [C2770:'',C3265:'',C3268:'',C3269:'',C3270:'',C539:'',C54:'',C545:'', Controllers:'2',Relievers:'1']
	private static final Map MEASURES_AMR0016 = [C2615: [num: 1, denom: 1], C3374: [num: 1, denom: 1], C3375: [num: 0, denom: 0],
		C3376: [num: 0, denom: 0], C3377: [num: 0, denom: 0]]
	
	private static final String AMR0017 = "src/test/resources/samples/hedis-amr/SampleAMR0017.xml" //Age 12-18, asthma by ED visit two years ago, by ED visit one year ago, 1 controller, 1 reliever, numMet
	private static final Map ASSERTIONS_AMR0017 = [C2771:'',C3265:'',C3268:'',C3269:'',C3270:'',C539:'',C54:'',C545:'', Controllers:'2',Relievers:'1']
	private static final Map MEASURES_AMR0017 = [C2615: [num: 1, denom: 1], C3374: [num: 0, denom: 0], C3375: [num: 1, denom: 1],
		C3376: [num: 0, denom: 0], C3377: [num: 0, denom: 0]]
	
	private static final String AMR0018 = "src/test/resources/samples/hedis-amr/SampleAMR0018.xml" //Age 51-64, asthma by ED visit two years ago, by ED visit one year ago, 1 controller, 1 reliever, numMet
	private static final Map ASSERTIONS_AMR0018 = [C2773:'',C3265:'',C3268:'',C3269:'',C3270:'',C539:'',C54:'',C545:'', Controllers:'2',Relievers:'1']
	private static final Map MEASURES_AMR0018 = [C2615: [num: 1, denom: 1], C3374: [num: 0, denom: 0], C3375: [num: 0, denom: 0],
		C3376: [num: 0, denom: 0], C3377: [num: 1, denom: 1]]

/*
Concepts used:
INPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C1938 -> Emphysema"
"	C2361 -> Injection"
"	C2511 -> HEDIS 2014"
"	C2787 -> HEDIS-Other Emphysema"
"	C2788 -> Bronchitis"
"	C2789 -> Chronic Respiratory Conditions due to Fumes or Vapors"
"	C2790 -> Cystic Fibrosis"
"	C284 -> Respiratory Failure"
"	C2844 -> HEDIS-AMR Table A Asthma Controller and Reliever Medications"
"	C2845 -> HEDIS-ASM Table C Asthma Medications"
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
"	C3280 -> HEDIS-AMR Table A Asthma Controller Medications"
"	C3281 -> HEDIS-AMR Table A Asthma Reliever Medications"
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
"	C2615 -> QM HEDIS-AMR Asthma Medication Ratio"
"	C2770 -> Patient Age GE 5 and LT 12 Years"
"	C2771 -> Patient Age GE 12 and LT 19 Years"
"	C2772 -> Patient Age GE 19 and LT 51 Years"
"	C2773 -> Patient Age GE 51 and LT 65 Years"
"	C2788 -> Bronchitis"
"	C2789 -> Chronic Respiratory Conditions due to Fumes or Vapors"
"	C2790 -> Cystic Fibrosis"
"	C284 -> Respiratory Failure"
"	C2844 -> HEDIS-AMR Table A Asthma Controller and Reliever Medications"
"	C3265 -> Named Dates Inserted"
"	C3268 -> Patient Age GE 5 and LT 65 Years"
"	C3269 -> Asthma Criterion Met"
"	C3270 -> Asthma Criterion Met"
"	C3271 -> Asthma Criterion Met"
"	C3272 -> Asthma Criterion Met"
"	C3374 -> QM HEDIS-AMR Asthma Medication Ratio (Age 05-11)"
"	C3375 -> QM HEDIS-AMR Asthma Medication Ratio (Age 12-18)"
"	C3376 -> QM HEDIS-AMR Asthma Medication Ratio (Age 19-50)"
"	C3377 -> QM HEDIS-AMR Asthma Medication Ratio (Age 51-64)"
"	C42 -> Chronic Obstructive Pulmonary Disease (COPD)"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"

*/
	
	@Unroll
	def "test HEDIS AMR v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_AMR', version: '2014.1.0'],
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
		AMR0001 | ASSERTIONS_AMR0001| MEASURES_AMR0001
		AMR0002 | ASSERTIONS_AMR0002| MEASURES_AMR0002
		AMR0003 | ASSERTIONS_AMR0003| MEASURES_AMR0003
		AMR0004 | ASSERTIONS_AMR0004| MEASURES_AMR0004
		AMR0005 | ASSERTIONS_AMR0005| MEASURES_AMR0005
		AMR0006 | ASSERTIONS_AMR0006| MEASURES_AMR0006
		AMR0007 | ASSERTIONS_AMR0007| MEASURES_AMR0007
		AMR0008 | ASSERTIONS_AMR0008| MEASURES_AMR0008
//		AMR0009 | ASSERTIONS_AMR0009| MEASURES_AMR0009
		AMR0010 | ASSERTIONS_AMR0010| MEASURES_AMR0010
		AMR0011 | ASSERTIONS_AMR0011| MEASURES_AMR0011
		AMR0012 | ASSERTIONS_AMR0012| MEASURES_AMR0012
		AMR0013 | ASSERTIONS_AMR0013| MEASURES_AMR0013
		AMR0014 | ASSERTIONS_AMR0014| MEASURES_AMR0014
		AMR0015 | ASSERTIONS_AMR0015| MEASURES_AMR0015
		AMR0016 | ASSERTIONS_AMR0016| MEASURES_AMR0016
		AMR0017 | ASSERTIONS_AMR0017| MEASURES_AMR0017
		AMR0018 | ASSERTIONS_AMR0018| MEASURES_AMR0018
	}
}
