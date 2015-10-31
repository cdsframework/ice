package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_CMC_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C3384: [num: -1, denom: -1]]
	
    private static final String CMC0001 = "src/test/resources/samples/hedis-cmc/CMC0001.xml" 
	//Screening, denom met, acute inpatient encounter : CPT=99223 discharged alive (UUHC= 01) from 1-2 years ago EncDx acute MI ICD9CM: 410.01 and 18 years old, female
	private static final Map ASSERTIONS_CMC0001 = [C2606: '', C341: '', C54: '']
    private static final Map MEASURES_CMC0001 = [C2616: [num: 0, denom: 1], C3200: [num: 0, denom: 1]]

	
    private static final String CMC0002 = "src/test/resources/samples/hedis-cmc/CMC0002.xml" 
	//Screening, denom met, acute inpatient encounter : CPT=99223 discharged alive (UUHC= 01) from 1-2 years ago EncDx acute MI ICD9CM: 410.01 and 18 years old, female
	private static final Map ASSERTIONS_CMC0002 = [C2606: '', C341: '', C54: '']
    private static final Map MEASURES_CMC0002 = [C2616: [num: 0, denom: 1], C3200: [num: 0, denom: 1]]
	
    private static final String CMC0003 = "src/test/resources/samples/hedis-cmc/CMC0003.xml" 
	//Screening, denom met, Denom check:   Acute Inpatient (UBREV:0100)  discharged alive (UUHC= 03)  EncDx CABG(CPT: 33510) from 1-2 years ago (denomMet), 18 years old, female
	private static final Map ASSERTIONS_CMC0003 = [C2606: '', C2541: '', C54: '']
    private static final Map MEASURES_CMC0003 = [C2616: [num: 0, denom: 1], C3200: [num: 0, denom: 1]]

	
    private static final String CMC0004 = "src/test/resources/samples/hedis-cmc/CMC0004.xml" 
	//Screening, denom met, acute inpatient(CPT: 99223) discharged alive (UUHC= 03) HEDIS-CABG(HCPCS: S2205) from 1-2 years ago
	private static final Map ASSERTIONS_CMC0004 = [C2606: '', C2541: '', C54: '']
    private static final Map MEASURES_CMC0004 = [C2616: [num: 0, denom: 1], C3200: [num: 0, denom: 1]]
	
    private static final String CMC0005 = "src/test/resources/samples/hedis-cmc/CMC0005.xml" 
	//Screening, denom met, Denom check:  Acute Inpatient (UBREV:0100)  discharged alive (UUHC= 03) with CABG procedures(ICD9PCS) from 1-2 years ago  18 years old, female
	private static final Map ASSERTIONS_CMC0005 = [C2606: '', C2541: '', C54: '']
    private static final Map MEASURES_CMC0005 = [C2616: [num: 0, denom: 1], C3200: [num: 0, denom: 1]]

	
    private static final String CMC0006 = "src/test/resources/samples/hedis-cmc/CMC0006.xml" 
	//Screening, denom met, Outpatient visit (UBREV) alive with PCI procedures(CPT) from 1-2 years ago  18 years old, female
	private static final Map ASSERTIONS_CMC0006 = [C2606: '', C2542: '', C54: '']
    private static final Map MEASURES_CMC0006 = [C2616: [num: 0, denom: 1], C3200: [num: 0, denom: 1]]
	
    private static final String CMC0007 = "src/test/resources/samples/hedis-cmc/CMC0007.xml" 
	//Screening, denom met, Outpatient visit (UBREV) alive with PCI procedures(PCI (HCPCS: G0290)) from 1-2 years ago  18 years old, female
	private static final Map ASSERTIONS_CMC0007 = [C2606: '', C2542: '', C54: '']
    private static final Map MEASURES_CMC0007 = [C2616: [num: 0, denom: 1], C3200: [num: 0, denom: 1]]

	
    private static final String CMC0008 = "src/test/resources/samples/hedis-cmc/CMC0008.xml" 
	//Screening, denom met, Outpatient visit (UBREV) alive with PCI procedures(ICD9PCS: 00.66)) from 1-2 years ago  18 years old, female
	private static final Map ASSERTIONS_CMC0008 = [C2606: '', C2542: '', C54: '']
    private static final Map MEASURES_CMC0008 = [C2616: [num: 0, denom: 1], C3200: [num: 0, denom: 1]]
/*	
    private static final String CMC0009 = "src/test/resources/samples/hedis-cmc/CMC0009.xml" 
	//Screening, denom not met, too young
	private static final Map ASSERTIONS_CMC0009 = [:]
    private static final Map MEASURES_CMC0009 = [C2616: [num: 0, denom: 0], C3200: [num: 0, denom: 0]]
	
    private static final String CMC0010 = "src/test/resources/samples/hedis-cmc/CMC0010.xml" 
	//Screening, denom not met, too old
	private static final Map ASSERTIONS_CMC0010 = [:]
    private static final Map MEASURES_CMC0010 = [C2616: [num: 0, denom: 0], C3200: [num: 0, denom: 0]]
*/
	
    private static final String CMC0011 = "src/test/resources/samples/hedis-cmc/CMC0011.xml" 
	//Screening, denom not met, died - not discharged alive
	private static final Map ASSERTIONS_CMC0011 = [C2606: '']
    private static final Map MEASURES_CMC0011 = [C2616: [num: 0, denom: 0], C3200: [num: 0, denom: 0]]
	
	private static final String CMC0012 = "src/test/resources/samples/hedis-cmc/CMC0012.xml" 
	//Screening, denom met, acute inpatient encounter : CPT=99223 discharged alive (UUHC= 01) from 1-2 years ago EncDx IVD ICD9CM: 411 and a second similar encounter in past year. 18 years old, female
	private static final Map ASSERTIONS_CMC0012 = [C2606: '', C3196: '',C3197: '', C54: '']
    private static final Map MEASURES_CMC0012 = [C2616: [num: 0, denom: 1], C3200: [num: 0, denom: 1]]
	
    private static final String CMC0013 = "src/test/resources/samples/hedis-cmc/CMC0013.xml" 
	//Screening, denom not met, vascular decease
	private static final Map ASSERTIONS_CMC0013 = [C2606: '', C3196: '']
    private static final Map MEASURES_CMC0013 = [C2616: [num: 0, denom: 0], C3200: [num: 0, denom: 0]]

	
   private static final String CMC0014 = "src/test/resources/samples/hedis-cmc/CMC0014.xml" 
   //Screening, numerator met, lab test one in the past year
	private static final Map ASSERTIONS_CMC0014 = [C2606: '', C341: '', C54: '']
    private static final Map MEASURES_CMC0014 = [C2616: [num: 1, denom: 1], C3200: [num: 0, denom: 1]]
 	
    private static final String CMC0015 = "src/test/resources/samples/hedis-cmc/CMC0015.xml" 
   //Screening, numerator met, lab test one in the past year
	private static final Map ASSERTIONS_CMC0015 = [C2606: '', C341: '', C54: '']
    private static final Map MEASURES_CMC0015 = [C2616: [num: 1, denom: 1], C3200: [num: 0, denom: 1]]

	
    private static final String CMC0016 = "src/test/resources/samples/hedis-cmc/CMC0016.xml" 
	//Screening, num not met, lab test too long ago
	private static final Map ASSERTIONS_CMC0016 = [C2606: '', C341: '', C54: '']
    private static final Map MEASURES_CMC0016 = [C2616: [num: 0, denom: 1], C3200: [num: 0, denom: 1]]
	
    private static final String CMC0017 = "src/test/resources/samples/hedis-cmc/CMC0017.xml" 
	//Screening, num met, lab test one in the past year of 90 mg/dL
	private static final Map ASSERTIONS_CMC0017 = [C2606: '', C341: '', C54: '']
    private static final Map MEASURES_CMC0017 = [C2616: [num: 1, denom: 1], C3200: [num: 0, denom: 1]]

	
    private static final String CMC0018 = "src/test/resources/samples/hedis-cmc/CMC0018.xml" 
	//Control, num met, HEDIS-LDL-C Level Less Than 100
	private static final Map ASSERTIONS_CMC0018 = [C2606: '', C341: '', C54: '',C2736:'']
    private static final Map MEASURES_CMC0018 = [C2616: [num: 1, denom: 1], C3200: [num: 1, denom: 1]]
	
    private static final String CMC0019 = "src/test/resources/samples/hedis-cmc/CMC0019.xml" 
	//Control, num met, HEDIS-LDL-C Level Less Than 100
	private static final Map ASSERTIONS_CMC0019 = [C2606: '', C341: '', C54: '',C2736:'']
    private static final Map MEASURES_CMC0019 = [C2616: [num: 1, denom: 1], C3200: [num: 1, denom: 1]]
	
    private static final String CMC0020 = "src/test/resources/samples/hedis-cmc/CMC0020.xml" 
	//Control, num not met, Include 2 values, latest value >100 (NumNotMet)
	private static final Map ASSERTIONS_CMC0020 = [C2606: '', C341: '', C54: '']
    private static final Map MEASURES_CMC0020 = [C2616: [num: 1, denom: 1], C3200: [num: 0, denom: 1]]

	
    private static final String CMC0021 = "src/test/resources/samples/hedis-cmc/CMC0021.xml" 
	//Control, num not met, 2 lab tests using the CPT codes.  the LDL-C Tests was second so it should fail.
	private static final Map ASSERTIONS_CMC0021 = [C2606: '', C341: '', C54: '']
    private static final Map MEASURES_CMC0021 = [C2616: [num: 1, denom: 1], C3200: [num: 0, denom: 1]]
	
	private static final String CMC0022 = "src/test/resources/samples/hedis-cmc/CMC0022.xml" 
	//Control, num met, lab test one in the past year of 90 mg/dL
	private static final Map ASSERTIONS_CMC0022 = [C2606: '', C341: '', C54: '']
    private static final Map MEASURES_CMC0022 = [C2616: [num: 1, denom: 1], C3200: [num: 1, denom: 1]]
	

	
	

/*
Concepts used:
Concepts used:
INPUT
"	1 -> year(s)"
"	C2511 -> HEDIS 2014"
"	C2541 -> CABG - Coronary Artery Bypass Graft"
"	C2542 -> Coronary Intervention"
"	C2606 -> Patient Age GE 18 and LT 76 Years"
"	C2736 -> LDL-C Control LT 100 mg per dL"
"	C2964 -> HEDIS-Outpatient"
"	C2971 -> HEDIS-Acute Inpatient"
"	C3000 -> HEDIS-LDL-C Level Less Than 100"
"	C3001 -> HEDIS-LDL-C Tests"
"	C3028 -> HEDIS-PCI"
"	C3068 -> HEDIS-AMI"
"	C3081 -> HEDIS-CABG"
"	C3126 -> HEDIS-IVD"
"	C3196 -> Vascular Disease"
"	C3197 -> Vascular Disease"
"	C3198 -> Alive"
"	C3378 -> LDL-C Screening"
"	C341 -> Myocardial Infarction"
"	C36 -> OpenCDS"
"	C405 -> Part of"
"	C489 -> Discharge Disposition"
"	C54 -> Denominator Criteria Met"
OUTPUT
"	C2541 -> CABG - Coronary Artery Bypass Graft"
"	C2542 -> Coronary Intervention"
"	C2606 -> Patient Age GE 18 and LT 76 Years"
"	C2616 -> QM HEDIS-CMC Screening Cholesterol Mgt for Cardio"
"	C2736 -> LDL-C Control LT 100 mg per dL"
"	C3196 -> Vascular Disease"
"	C3197 -> Vascular Disease"
"	C3200 -> QM HEDIS-CMC LT100 Control Cholesterol Mgt for Cardio"
"	C3378 -> LDL-C Screening"
"	C3384 -> QM HEDIS-CMC Cholesterol Mgt for Cardio"
"	C341 -> Myocardial Infarction"
"	C529 -> Rejected for Missing or Bad Data"
"	C54 -> Denominator Criteria Met"
"	C569 -> Missing Data for Date of Birth"

*/
	
	@Unroll
	def "test HEDIS CMC v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CMC', version: '2014.1.0'],
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
		CMC0001 | ASSERTIONS_CMC0001| MEASURES_CMC0001
		CMC0002 | ASSERTIONS_CMC0002| MEASURES_CMC0002
		CMC0003 | ASSERTIONS_CMC0003| MEASURES_CMC0003
		CMC0004 | ASSERTIONS_CMC0004| MEASURES_CMC0004
		CMC0005 | ASSERTIONS_CMC0005| MEASURES_CMC0005
		CMC0006 | ASSERTIONS_CMC0006| MEASURES_CMC0006
		CMC0007 | ASSERTIONS_CMC0007| MEASURES_CMC0007
		CMC0008 | ASSERTIONS_CMC0008| MEASURES_CMC0008
/*		CMC0009 | ASSERTIONS_CMC0009| MEASURES_CMC0009
		CMC0010 | ASSERTIONS_CMC0010| MEASURES_CMC0010 */
		CMC0011 | ASSERTIONS_CMC0011| MEASURES_CMC0011
		CMC0012 | ASSERTIONS_CMC0012| MEASURES_CMC0012
		CMC0013 | ASSERTIONS_CMC0013| MEASURES_CMC0013
		CMC0014 | ASSERTIONS_CMC0014| MEASURES_CMC0014
		CMC0015 | ASSERTIONS_CMC0015| MEASURES_CMC0015
		CMC0016 | ASSERTIONS_CMC0016| MEASURES_CMC0016
		CMC0017 | ASSERTIONS_CMC0017| MEASURES_CMC0017
		CMC0018 | ASSERTIONS_CMC0018| MEASURES_CMC0018
		CMC0019 | ASSERTIONS_CMC0019| MEASURES_CMC0019
		CMC0020 | ASSERTIONS_CMC0020| MEASURES_CMC0020
		CMC0021 | ASSERTIONS_CMC0021| MEASURES_CMC0021
		CMC0022 | ASSERTIONS_CMC0022| MEASURES_CMC0022



	}
}
