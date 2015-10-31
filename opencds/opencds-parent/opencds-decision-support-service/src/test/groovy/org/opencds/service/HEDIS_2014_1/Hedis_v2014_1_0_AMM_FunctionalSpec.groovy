package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_AMM_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C3385: [num: -1, denom: -1]]
	
    private static final String AMM0001 = "src/test/resources/samples/hedis-amm/SampleAMM0001.xml" 
	//Num Met: AMM Stand Alone Visits Value Set (HCPCS: G0155), Major Depression, 100 days of AMM-C drugs
	private static final Map ASSERTIONS_AMM0001 = [C2888:'', C3170:'',  C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'100']
    private static final Map MEASURES_AMM0001 = [C2584: [num: 1, denom: 1], C3331: [num: 0, denom: 1]]

	
    private static final String AMM0002 = "src/test/resources/samples/hedis-amm/SampleAMM0002.xml" 
	//Num Met: ED visit (CPT), Major Depression, 98 days of AMM-C drugs
	private static final Map ASSERTIONS_AMM0002 = [C2888:'', C3170:'',  C54:  '', C545: '', DaysSuppliedAcute:'98', DaysSuppliedContinuation:'98']
    private static final Map MEASURES_AMM0002 = [C2584: [num: 1, denom: 1], C3331: [num: 0, denom: 1]]
	
    private static final String AMM0003 = "src/test/resources/samples/hedis-amm/SampleAMM0003.xml" 
	//Num Not Met: ED visit (CPT), Major Depression, 80 days of AMM-C drugs
	private static final Map ASSERTIONS_AMM0003 = [C2888:'', C3170:'', C54:  '', C545: '',DaysSuppliedAcute:'80', DaysSuppliedContinuation:'80']
    private static final Map MEASURES_AMM0003 = [C2584: [num: 0, denom: 1], C3331: [num: 0, denom: 1]]

/*	
    private static final String AMM0004 = "src/test/resources/samples/hedis-amm/SampleAMM0004.xml" 
	//Denom Not Met
	private static final Map ASSERTIONS_AMM0004 = [:]
    private static final Map MEASURES_AMM0004 = [C2584: [num: 0, denom: 1], C3331: [num: 0, denom: 1]]
*/	
    private static final String AMM0005 = "src/test/resources/samples/hedis-amm/SampleAMM0005.xml" 
	//Num Met: AMMStandAloneEncs (CPT), Major Depression, 100 days of AMM-C drugs, AMM Stand Alone Visits Value Set (CPT: 90804)
	private static final Map ASSERTIONS_AMM0005 = [C2888:'', C3170:'',  C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'100']
    private static final Map MEASURES_AMM0005 = [C2584: [num: 1, denom: 1], C3331: [num: 0, denom: 1]]

	
    private static final String AMM0006 = "src/test/resources/samples/hedis-amm/SampleAMM0006.xml" 
	//Num Met: AMM Visits Value Set (CPT: 90791), Major Depression, 100 days of AMM-C drugs, POS - Observation Result
	private static final Map ASSERTIONS_AMM0006 = [C2888:'', C3170:'', C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'100']
    private static final Map MEASURES_AMM0006 = [C2584: [num: 1, denom: 1], C3331: [num: 0, denom: 1]]
	
    private static final String AMM0007 = "src/test/resources/samples/hedis-amm/SampleAMM0007.xml" 
	//Num Met: Acute Inpatient (CPT: 99223), Major Depression, 100 days of AMM-C drug
	private static final Map ASSERTIONS_AMM0007 = [C2888:'', C3170:'', C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'100']
    private static final Map MEASURES_AMM0007 = [C2584: [num: 1, denom: 1], C3331: [num: 0, denom: 1]]

	
    private static final String AMM0008 = "src/test/resources/samples/hedis-amm/SampleAMM0008.xml" 
	//Num Met: Nonacute Inpatient (CPT: 99335), Major Depression, 100 days of AMM-C drugs, AMM Stand Alone Visits Value Set (CPT: 90804)
	private static final Map ASSERTIONS_AMM0008 = [C2888:'', C3170:'', C54:  '', C545: '', DaysSuppliedAcute:'100', DaysSuppliedContinuation:'100']
    private static final Map MEASURES_AMM0008 = [C2584: [num: 1, denom: 1], C3331: [num: 0, denom: 1]]
	
    private static final String AMM0009 = "src/test/resources/samples/hedis-amm/SampleAMM0009.xml" 
	//Denom Exclusions Met: AMMStandAloneEncs (CPT), Major Depression, 100 days of AMM-C drugs, Positive Medication History
	private static final Map ASSERTIONS_AMM0009 = [C3170:'']
    private static final Map MEASURES_AMM0009 = [C2584: [num: 0, denom: 0], C3331: [num: 0, denom: 0]]
	
    private static final String AMM0010 = "src/test/resources/samples/hedis-amm/SampleAMM0010.xml" 
	//Num Met: AMM Stand Alone Visits Value Set (HCPCS: G0155), Major Depression, 100 days of AMM-C drugs
	private static final Map ASSERTIONS_AMM0010 = [C2888:'', C3170:'', C539: '', C54:  '', C545: '']
    private static final Map MEASURES_AMM0010 = [C2584: [num: 1, denom: 1], C3331: [num: 1, denom: 1]]


	

	
	

/*
Concepts used:
INPUT
"	1 -> year(s)"
"	2 -> month(s)"
"	5 -> day(s)"
"	C2511 -> HEDIS 2014"
"	C2828 -> Healthcare Facility or Place of Service (POS)"
"	C2843 -> HEDIS-AMM Table C Antidepressant Medications"
"	C2888 -> Depression"
"	C2930 -> HEDIS-AMM POS"
"	C2931 -> HEDIS-AMM Stand Alone Visits"
"	C2932 -> HEDIS-AMM Visits"
"	C2968 -> HEDIS-ED"
"	C2971 -> HEDIS-Acute Inpatient"
"	C3020 -> HEDIS-Nonacute Inpatient"
"	C3130 -> HEDIS-Major Depression"
"	C3170 -> Patient Age GE 18 Years"
"	C36 -> OpenCDS"
"	C405 -> Part of"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	2 -> month(s)"
"	5 -> day(s)"
"	C2584 -> QM HEDIS-AMM_ACUTE_Antidepressant Med Mgt."
"	C2888 -> Depression"
"	C2931 -> HEDIS-AMM Stand Alone Visits"
"	C2932 -> HEDIS-AMM Visits"
"	C2968 -> HEDIS-ED"
"	C2971 -> HEDIS-Acute Inpatient"
"	C3020 -> HEDIS-Nonacute Inpatient"
"	C3170 -> Patient Age GE 18 Years"
"	C3265 -> Named Dates Inserted"
"	C3331 -> QM HEDIS-AMM_CONTINUATION_Antidepressant Med Mgt."
"	C3385 -> QM HEDIS-AMM Antidepressant Med Mgt
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"
*/
	
	@Unroll
	def "test HEDIS AMM v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_AMM', version: '2014.1.0'],
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
		AMM0001 | ASSERTIONS_AMM0001| MEASURES_AMM0001
		AMM0002 | ASSERTIONS_AMM0002| MEASURES_AMM0002
		AMM0003 | ASSERTIONS_AMM0003| MEASURES_AMM0003
/*		AMM0004 | ASSERTIONS_AMM0004| MEASURES_AMM0004 */
		AMM0005 | ASSERTIONS_AMM0005| MEASURES_AMM0005
		AMM0006 | ASSERTIONS_AMM0006| MEASURES_AMM0006
		AMM0007 | ASSERTIONS_AMM0007| MEASURES_AMM0007
		AMM0008 | ASSERTIONS_AMM0008| MEASURES_AMM0008
		AMM0009 | ASSERTIONS_AMM0009| MEASURES_AMM0009
		AMM0010 | ASSERTIONS_AMM0010| MEASURES_AMM0010




	}
}
