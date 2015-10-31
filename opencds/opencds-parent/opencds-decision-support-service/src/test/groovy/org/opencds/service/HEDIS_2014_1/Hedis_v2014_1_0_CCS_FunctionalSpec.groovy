package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_CCS_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2598: [num: -1, denom: -1]]
	
    private static final String CCS0001 = "src/test/resources/samples/hedis-ccs/SampleCCS0001.xml" //Denom Not Met
	private static final Map ASSERTIONS_CCS0001 = [C2750: '', C2751: '', C31:'', C544:'', C545:'']
    private static final Map MEASURES_CCS0001  = [C2598: [num: 0, denom: 0]]

	
    private static final String CCS0002 = "src/test/resources/samples/hedis-ccs/SampleCCS0002.xml" //Num Met
	private static final Map ASSERTIONS_CCS0002 = [C2750: '', C2756: '', C31:'',C514:'',C539:'',C54:'',C545:'']
	private static final Map MEASURES_CCS0002  = [C2598: [num: 1, denom: 1]]
	
    private static final String CCS0003 = "src/test/resources/samples/hedis-ccs/SampleCCS0003.xml" //Num Met
	private static final Map ASSERTIONS_CCS0003 = [C2750: '', C2833: '', C31:'',C514:'',C539:'',C54:'',C545:'']
	private static final Map MEASURES_CCS0003  = [C2598: [num: 1, denom: 1]]

	
    private static final String CCS0004 = "src/test/resources/samples/hedis-ccs/SampleCCS0004.xml" //Denom Met
	private static final Map ASSERTIONS_CCS0004 = [C2750: '', C31:'',C54:'',C545:'']
	private static final Map MEASURES_CCS0004  = [C2598: [num: 0, denom: 1]]
	
	private static final String CCS0005 = "src/test/resources/samples/hedis-ccs/SampleCCS0005.xml" //Denom Met
	private static final Map ASSERTIONS_CCS0005 = [C2750: '', C31:'',C54:'',C545:'']
	private static final Map MEASURES_CCS0005  = [C2598: [num: 0, denom: 1]]

	
	private static final String CCS0006 = "src/test/resources/samples/hedis-ccs/SampleCCS0006.xml" //Denom Not Met
	private static final Map ASSERTIONS_CCS0006 = [C2750: '', C2751: '', C31:'', C544:'', C545:'']
	private static final Map MEASURES_CCS0006  = [C2598: [num: 0, denom: 0]]
	
	private static final String CCS0007 = "src/test/resources/samples/hedis-ccs/SampleCCS0007.xml" //Denom Not Met
	private static final Map ASSERTIONS_CCS0007 = [C2750: '', C2751: '', C31:'', C544:'', C545:'']
	private static final Map MEASURES_CCS0007  = [C2598: [num: 0, denom: 0]]

	
	private static final String CCS0008 = "src/test/resources/samples/hedis-ccs/SampleCCS0008.xml" //Num Met
	private static final Map ASSERTIONS_CCS0008 = [C2750: '', C2756: '', C31:'',C514:'',C539:'',C54:'',C545:'']
	private static final Map MEASURES_CCS0008  = [C2598: [num: 1, denom: 1]]

	private static final String CCS0009 = "src/test/resources/samples/hedis-ccs/SampleCCS0009.xml" //Denom Not Met
	private static final Map ASSERTIONS_CCS0009 = [C31:'']
	private static final Map MEASURES_CCS0009 = [C2598: [num: 0, denom: 0]]

	
	private static final String CCS0010 = "src/test/resources/samples/hedis-ccs/SampleCCS0010.xml" //Denom Not Met
	private static final Map ASSERTIONS_CCS0010 = [C31:'']
	private static final Map MEASURES_CCS0010  = [C2598: [num: 0, denom: 0]]
	


/*
Concepts used:
INPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C2511 -> HEDIS 2014"
"	C2750 -> Patient Age GE 24 and LT 65 Years"
"	C2992 -> HEDIS-HPV Tests"
"	C2993 -> HEDIS-Hysterectomy"
"	C3084 -> HEDIS-Cervical Cytology"
"	C31 -> Female"
"	C36 -> OpenCDS"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	C2598 -> QM HEDIS-CCS Cervical Cancer Screening"
"	C2750 -> Patient Age GE 24 and LT 65 Years"
"	C2751 -> Hysterectomy Total"
"	C2756 -> Cervical Cytology"
"	C2833 -> Cervical Cancer Screening by Cervical Cytology with HPV testing"
"	C31 -> Female"
"	C514 -> Cervical Cancer Screening"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"

*/
	
	@Unroll
	def "test HEDIS CCS v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CCS', version: '2014.1.0'],
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
		CCS0001 | ASSERTIONS_CCS0001| MEASURES_CCS0001
		CCS0002 | ASSERTIONS_CCS0002| MEASURES_CCS0002
		CCS0003 | ASSERTIONS_CCS0003| MEASURES_CCS0003
		CCS0004 | ASSERTIONS_CCS0004| MEASURES_CCS0004
		CCS0005 | ASSERTIONS_CCS0005| MEASURES_CCS0005
		CCS0006 | ASSERTIONS_CCS0006| MEASURES_CCS0006
		CCS0007 | ASSERTIONS_CCS0007| MEASURES_CCS0007
		CCS0008 | ASSERTIONS_CCS0008| MEASURES_CCS0008
		CCS0009 | ASSERTIONS_CCS0009| MEASURES_CCS0009
		CCS0010 | ASSERTIONS_CCS0010| MEASURES_CCS0010



	}
}
