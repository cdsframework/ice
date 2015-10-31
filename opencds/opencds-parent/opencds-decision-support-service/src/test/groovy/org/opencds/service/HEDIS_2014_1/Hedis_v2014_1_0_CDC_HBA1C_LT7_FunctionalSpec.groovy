package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_CDC_HBA1C_LT7_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C3206: [num: -1, denom: -1]]
	
    private static final String CDC_HbA1c_LT70001 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop01.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70001 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2608:'',C3265:'',C544:'',C545:'']
    private static final Map MEASURES_CDC_HbA1c_LT70001  = [C3206: [num: 0, denom: 0]]

	
    private static final String CDC_HbA1c_LT70002 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop02.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70002 = [AcuteInpatientEncounters:'1',C2519:'',C2541:'',C2579:'',C2606:'',C3265:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70002  = [C3206: [num: 0, denom: 0]]
	
    private static final String CDC_HbA1c_LT70003 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop03.xml" // Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70003 = [AcuteInpatientEncounters:'1',C2519:'',C2541:'',C2579:'',C2606:'',C3265:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70003  = [C3206: [num: 0, denom: 0]]

	
    private static final String CDC_HbA1c_LT70004 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop04.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70004 = [AcuteInpatientEncounters:'1',C2519:'',C2541:'',C2579:'',C2606:'',C3265:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70004  = [C3206: [num: 0, denom: 0]]
	
	private static final String CDC_HbA1c_LT70005 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop05.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70005 = [AcuteInpatientEncounters:'1',C2519:'',C2542:'',C2579:'',C2606:'',C3265:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70005  = [C3206: [num: 0, denom: 0]]

	
	private static final String CDC_HbA1c_LT70006 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop06.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70006 = [AcuteInpatientEncounters:'1',C2519:'',C2542:'',C2579:'',C2606:'',C3265:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70006  = [C3206: [num: 0, denom: 0]]
	
	private static final String CDC_HbA1c_LT70007 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop07.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70007 = [AcuteInpatientEncounters:'1',C2519:'',C2542:'',C2579:'',C2606:'',C3265:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70007  = [C3206: [num: 0, denom: 0]]

	
	private static final String CDC_HbA1c_LT70008 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop08.xml" //Num Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70008 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3265:'',C54:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70008  = [C3206: [num: 0, denom: 1]]

	private static final String CDC_HbA1c_LT70009 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop09.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70009 = [AcuteInpatientEncounters:'1',C2519:'',C2543:'',C2579:'',C2606:'',C3196:'',C3197:'',C3265:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70009 = [C3206: [num: 0, denom: 0]]

	
	private static final String CDC_HbA1c_LT70010 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop10.xml" //Num Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70010 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3197:'',C3265:'',C54:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70010  = [C3206: [num: 0, denom: 1]]
	
	private static final String CDC_HbA1c_LT70011 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop11.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70011 = [AcuteInpatientEncounters:'1',C2519:'',C2544:'',C2579:'',C2606:'',C3263:'',C3264:'',C3265:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70011  = [C3206: [num: 0, denom: 0]]
	
	private static final String CDC_HbA1c_LT70012 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop12.xml" //Num Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70012 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3264:'',C3265:'',C54:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70012  = [C3206: [num: 0, denom: 1]]
	
	private static final String CDC_HbA1c_LT70013 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop13.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70013 = [AcuteInpatientEncounters:'1',C2519:'',C2545:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70013  = [C3206: [num: 0, denom: 0]]
	
	private static final String CDC_HbA1c_LT70014 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop14.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70014 = [AcuteInpatientEncounters:'1',C2519:'',C2546:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70014 = [C3206: [num: 0, denom: 0]]

	private static final String CDC_HbA1c_LT70015 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop15.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70015 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C340:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70015  = [C3206: [num: 0, denom: 0]]

	
	private static final String CDC_HbA1c_LT70016 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop16.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70016 = [AcuteInpatientEncounters:'1',C1922:'',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70016  = [C3206: [num: 0, denom: 0]]
	
	private static final String CDC_HbA1c_LT70017 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop17.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70017 = [AcuteInpatientEncounters:'1',C2519:'',C2549:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70017  = [C3206: [num: 0, denom: 0]]

	
	private static final String CDC_HbA1c_LT70018 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop18.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70018 = [AcuteInpatientEncounters:'1',C2519:'',C2550:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70018  = [C3206: [num: 0, denom: 0]]

	private static final String CDC_HbA1c_LT70019 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop19.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70019 = [AcuteInpatientEncounters:'1',C2519:'',C2550:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70019 = [C3206: [num: 0, denom: 0]]

	private static final String CDC_HbA1c_LT70020 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop20.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70020 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C339:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70020  = [C3206: [num: 0, denom: 0]]
	
	private static final String CDC_HbA1c_LT70021 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop21.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70021 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C339:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70021  = [C3206: [num: 0, denom: 0]]
	
	private static final String CDC_HbA1c_LT70022 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop22.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70022 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C339:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70022  = [C3206: [num: 0, denom: 0]]
	
	private static final String CDC_HbA1c_LT70023 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop23.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70023 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C339:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70023  = [C3206: [num: 0, denom: 0]]
	
	private static final String CDC_HbA1c_LT70024 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop24.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70024 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C339:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70024 = [C3206: [num: 0, denom: 0]]

	private static final String CDC_HbA1c_LT70025 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop25.xml" //Num Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70025 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3264:'',C3265:'',C54:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70025  = [C3206: [num: 0, denom: 1]]

	
	private static final String CDC_HbA1c_LT70026 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop26.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70026 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C339:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70026  = [C3206: [num: 0, denom: 0]]
	
	private static final String CDC_HbA1c_LT70027 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop27.xml" //Denom Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70027 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C339:'',C544:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70027  = [C3206: [num: 0, denom: 0]]

	
	private static final String CDC_HbA1c_LT70028 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_LT7_001.xml" //Num Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70028 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'']
	private static final Map MEASURES_CDC_HbA1c_LT70028  = [C3206: [num: 0, denom: 1]]

	private static final String CDC_HbA1c_LT70029 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_LT7_002.xml" //Num Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70029 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'']
	private static final Map MEASURES_CDC_HbA1c_LT70029 = [C3206: [num: 0, denom: 1]]
	
	private static final String CDC_HbA1c_LT70030 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_LT7_003.xml" //Num Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70030 = [AcuteInpatientEncounters:'1',C2519:'',C2524:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70030  = [C3206: [num: 1, denom: 1]]

	private static final String CDC_HbA1c_LT70031 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_LT7_004.xml" //Num Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70031 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'']
	private static final Map MEASURES_CDC_HbA1c_LT70031 = [C3206: [num: 0, denom: 1]]
	
	private static final String CDC_HbA1c_LT70032 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_LT7_005.xml" //Num Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70032 = [AcuteInpatientEncounters:'1',C2519:'',C2524:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'']
	private static final Map MEASURES_CDC_HbA1c_LT70032  = [C3206: [num: 1, denom: 1]]

	private static final String CDC_HbA1c_LT70033 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_LT7_006.xml" //Num Not Met
	private static final Map ASSERTIONS_CDC_HbA1c_LT70033 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'']
	private static final Map MEASURES_CDC_HbA1c_LT70033 = [C3206: [num: 0, denom: 1]]

/*
Concepts used:
INPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C239 -> Emergency Department"
"	C2511 -> HEDIS 2014"
"	C2579 -> Diabetes by Claims Data"
"	C2606 -> Patient Age GE 18 and LT 76 Years"
"	C2607 -> Diabetes by Pharmacy Data"
"	C2828 -> Healthcare Facility or Place of Service (POS)"
"	C2849 -> HEDIS-CDC Table A Prescriptions to Identify Members With Diabetes"
"	C2964 -> HEDIS-Outpatient"
"	C2968 -> HEDIS-ED"
"	C2971 -> HEDIS-Acute Inpatient"
"	C2972 -> HEDIS-ESRD"
"	C2984 -> HEDIS-HbA1c Level Less Than 7.0"
"	C2985 -> HEDIS-HbA1c Tests"
"	C3008 -> HEDIS-Lower Extremity Amputation"
"	C3020 -> HEDIS-Nonacute Inpatient"
"	C3022 -> HEDIS-Observation"
"	C3028 -> HEDIS-PCI"
"	C3076 -> HEDIS-Blindness"
"	C3081 -> HEDIS-CABG"
"	C3086 -> HEDIS-CHF"
"	C3090 -> HEDIS-CKD Stage 4"
"	C3098 -> HEDIS-Dementia"
"	C3100 -> HEDIS-Diabetes"
"	C3113 -> HEDIS-ESRD Obsolete"
"	C3126 -> HEDIS-IVD"
"	C3134 -> HEDIS-MI"
"	C3148 -> HEDIS-Thoracic Aortic Aneurysm"
"	C3196 -> Vascular Disease"
"	C3197 -> Vascular Disease"
"	C3198 -> Alive"
"	C3263 -> Thoracic Aortic Aneurysm encounter in measurement year"
"	C3264 -> Thoracic Aortic Aneurysm encounter in prior year"
"	C36 -> OpenCDS"
"	C405 -> Part of"
"	C44 -> Outpatient"
"	C489 -> Discharge Disposition"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C1922 -> Dementia"
"	C2519 -> Denominator Inclusions by Claims Data"
"	C2520 -> Denominator Inclusions by Pharmacy Data"
"	C2524 -> HbA1c Control LT 7.0 Pct"
"	C2541 -> CABG - Coronary Artery Bypass Graft"
"	C2542 -> Coronary Intervention"
"	C2543 -> Vascular Disease"
"	C2544 -> Aortic Aneurysm"
"	C2545 -> Heart Failure"
"	C2546 -> Myocardial Infarction"
"	C2549 -> Blindness"
"	C2550 -> Amputation"
"	C2579 -> Diabetes by Claims Data"
"	C2606 -> Patient Age GE 18 and LT 76 Years"
"	C2607 -> Diabetes by Pharmacy Data"
"	C2608 -> Patient Age GE 65 Years"
"	C2661 -> Comorbid Disease"
"	C3196 -> Vascular Disease"
"	C3197 -> Vascular Disease"
"	C3206 -> QM HEDIS-CDC (HbA1c control LT 7%)"
"	C3263 -> Thoracic Aortic Aneurysm encounter in measurement year"
"	C3264 -> Thoracic Aortic Aneurysm encounter in prior year"
"	C3265 -> Named Dates Inserted"
"	C339 -> Kidney Failure"
"	C340 -> Kidney Disease"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"

*/
	
	@Unroll
	def "test HEDIS CDC_HbA1c_LT7 v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CDC_HBA1C_LT7', version: '2014.1.0'],
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
		CDC_HbA1c_LT70001 | ASSERTIONS_CDC_HbA1c_LT70001| MEASURES_CDC_HbA1c_LT70001
		CDC_HbA1c_LT70002 | ASSERTIONS_CDC_HbA1c_LT70002| MEASURES_CDC_HbA1c_LT70002
		CDC_HbA1c_LT70003 | ASSERTIONS_CDC_HbA1c_LT70003| MEASURES_CDC_HbA1c_LT70003
		CDC_HbA1c_LT70004 | ASSERTIONS_CDC_HbA1c_LT70004| MEASURES_CDC_HbA1c_LT70004
		CDC_HbA1c_LT70005 | ASSERTIONS_CDC_HbA1c_LT70005| MEASURES_CDC_HbA1c_LT70005
		CDC_HbA1c_LT70006 | ASSERTIONS_CDC_HbA1c_LT70006| MEASURES_CDC_HbA1c_LT70006
		CDC_HbA1c_LT70007 | ASSERTIONS_CDC_HbA1c_LT70007| MEASURES_CDC_HbA1c_LT70007
		CDC_HbA1c_LT70008 | ASSERTIONS_CDC_HbA1c_LT70008| MEASURES_CDC_HbA1c_LT70008
		CDC_HbA1c_LT70009 | ASSERTIONS_CDC_HbA1c_LT70009| MEASURES_CDC_HbA1c_LT70009
		CDC_HbA1c_LT70010 | ASSERTIONS_CDC_HbA1c_LT70010| MEASURES_CDC_HbA1c_LT70010
		CDC_HbA1c_LT70011 | ASSERTIONS_CDC_HbA1c_LT70011| MEASURES_CDC_HbA1c_LT70011
		CDC_HbA1c_LT70012 | ASSERTIONS_CDC_HbA1c_LT70012| MEASURES_CDC_HbA1c_LT70012
		CDC_HbA1c_LT70013 | ASSERTIONS_CDC_HbA1c_LT70013| MEASURES_CDC_HbA1c_LT70013
		CDC_HbA1c_LT70014 | ASSERTIONS_CDC_HbA1c_LT70014| MEASURES_CDC_HbA1c_LT70014
		CDC_HbA1c_LT70015 | ASSERTIONS_CDC_HbA1c_LT70015| MEASURES_CDC_HbA1c_LT70015
		CDC_HbA1c_LT70016 | ASSERTIONS_CDC_HbA1c_LT70016| MEASURES_CDC_HbA1c_LT70016
		CDC_HbA1c_LT70017 | ASSERTIONS_CDC_HbA1c_LT70017| MEASURES_CDC_HbA1c_LT70017
		CDC_HbA1c_LT70018 | ASSERTIONS_CDC_HbA1c_LT70018| MEASURES_CDC_HbA1c_LT70018
		CDC_HbA1c_LT70019 | ASSERTIONS_CDC_HbA1c_LT70019| MEASURES_CDC_HbA1c_LT70019
		CDC_HbA1c_LT70020 | ASSERTIONS_CDC_HbA1c_LT70020| MEASURES_CDC_HbA1c_LT70020
		CDC_HbA1c_LT70021 | ASSERTIONS_CDC_HbA1c_LT70021| MEASURES_CDC_HbA1c_LT70021
		CDC_HbA1c_LT70022 | ASSERTIONS_CDC_HbA1c_LT70022| MEASURES_CDC_HbA1c_LT70022
		CDC_HbA1c_LT70023 | ASSERTIONS_CDC_HbA1c_LT70023| MEASURES_CDC_HbA1c_LT70023
		CDC_HbA1c_LT70024 | ASSERTIONS_CDC_HbA1c_LT70024| MEASURES_CDC_HbA1c_LT70024
		CDC_HbA1c_LT70025 | ASSERTIONS_CDC_HbA1c_LT70025| MEASURES_CDC_HbA1c_LT70025
		CDC_HbA1c_LT70026 | ASSERTIONS_CDC_HbA1c_LT70026| MEASURES_CDC_HbA1c_LT70026
		CDC_HbA1c_LT70027 | ASSERTIONS_CDC_HbA1c_LT70027| MEASURES_CDC_HbA1c_LT70027
		CDC_HbA1c_LT70028 | ASSERTIONS_CDC_HbA1c_LT70028| MEASURES_CDC_HbA1c_LT70028
		CDC_HbA1c_LT70029 | ASSERTIONS_CDC_HbA1c_LT70029| MEASURES_CDC_HbA1c_LT70029
		CDC_HbA1c_LT70030 | ASSERTIONS_CDC_HbA1c_LT70030| MEASURES_CDC_HbA1c_LT70030
		CDC_HbA1c_LT70031 | ASSERTIONS_CDC_HbA1c_LT70031| MEASURES_CDC_HbA1c_LT70031
		CDC_HbA1c_LT70032 | ASSERTIONS_CDC_HbA1c_LT70032| MEASURES_CDC_HbA1c_LT70032
		CDC_HbA1c_LT70033 | ASSERTIONS_CDC_HbA1c_LT70033| MEASURES_CDC_HbA1c_LT70033
	}
}
