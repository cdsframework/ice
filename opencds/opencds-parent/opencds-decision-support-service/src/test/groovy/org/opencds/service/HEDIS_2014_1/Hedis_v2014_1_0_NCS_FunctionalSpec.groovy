package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_NCS_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2599: [num: -1, denom: -1]]
	
    private static final String NCS0001 = "src/test/resources/samples/hedis-ncs/SampleNCS0001.xml" //Denom Not Met
	private static final Map ASSERTIONS_NCS0001 = [C1671: '', C2766: '', C2767: '', C2769: '',C31:'', C544:'', C545:'']
    private static final Map MEASURES_NCS0001  = [C2599: [num: 0, denom: 0]]

	
    private static final String NCS0002 = "src/test/resources/samples/hedis-ncs/SampleNCS0002.xml" //Num Met
	private static final Map ASSERTIONS_NCS0002 = [C2766: '', C2767: '', C31:'', C544:'', C545:'']
	private static final Map MEASURES_NCS0002  = [C2599: [num: 0, denom: 0]]
	
    private static final String NCS0003 = "src/test/resources/samples/hedis-ncs/SampleNCS0003.xml" //Num Met
	private static final Map ASSERTIONS_NCS0003 = [C2756: '', C2766: '', C31:'',C514:'',C539:'',C54:'',C545:'']
	private static final Map MEASURES_NCS0003  = [C2599: [num: 1, denom: 1]]

	
    private static final String NCS0004 = "src/test/resources/samples/hedis-ncs/SampleNCS0004.xml" //Denom Met
	private static final Map ASSERTIONS_NCS0004 = [C2756: '', C2766: '', C31:'',C514:'',C539:'',C54:'',C545:'']
	private static final Map MEASURES_NCS0004  = [C2599: [num: 1, denom: 1]]
	
	private static final String NCS0005 = "src/test/resources/samples/hedis-ncs/SampleNCS0005.xml" //Denom Met
	private static final Map ASSERTIONS_NCS0005 = [C2756: '', C2766: '', C31:'',C514:'',C539:'',C54:'',C545:'']
	private static final Map MEASURES_NCS0005  = [C2599: [num: 1, denom: 1]]

	
	private static final String NCS0006 = "src/test/resources/samples/hedis-ncs/SampleNCS0006.xml" //Denom Not Met
	private static final Map ASSERTIONS_NCS0006 = [C1671: '', C2766: '', C31:'', C544:'', C545:'']
	private static final Map MEASURES_NCS0006  = [C2599: [num: 0, denom: 0]]
	
	private static final String NCS0007 = "src/test/resources/samples/hedis-ncs/SampleNCS0007.xml" //Denom Not Met
	private static final Map ASSERTIONS_NCS0007 = [C2760: '', C2766: '', C31:'',C539:'',C54:'',C545:'']
	private static final Map MEASURES_NCS0007  = [C2599: [num: 1, denom: 1]]

	
	private static final String NCS0008 = "src/test/resources/samples/hedis-ncs/SampleNCS0008.xml" //Num Met
	private static final Map ASSERTIONS_NCS0008 = [C2760: '', C2766: '', C31:'',C539:'',C54:'',C545:'']
	private static final Map MEASURES_NCS0008  = [C2599: [num: 1, denom: 1]]

	private static final String NCS0009 = "src/test/resources/samples/hedis-ncs/SampleNCS0009.xml" //Denom Not Met
	private static final Map ASSERTIONS_NCS0009 = [C2766: '', C2769: '', C31:'', C544:'', C545:'']
	private static final Map MEASURES_NCS0009 = [C2599: [num: 0, denom: 0]]

	
	private static final String NCS0010 = "src/test/resources/samples/hedis-ncs/SampleNCS0010.xml" //Denom Not Met
	private static final Map ASSERTIONS_NCS0010 = [C31:'']
	private static final Map MEASURES_NCS0010  = [C2599: [num: 0, denom: 0]]
	
	private static final String NCS0011 = "src/test/resources/samples/hedis-ncs/SampleNCS0011.xml" //Denom Not Met
	private static final Map ASSERTIONS_NCS0011 = [C31:'']
	private static final Map MEASURES_NCS0011  = [C2599: [num: 0, denom: 0]]
	
	private static final String NCS0012 = "src/test/resources/samples/hedis-ncs/SampleNCS0012.xml" //Denom Not Met
	private static final Map ASSERTIONS_NCS0012 = [C2766:'', C31:'',  C54:'',C545:'']
	private static final Map MEASURES_NCS0012  = [C2599: [num: 0, denom: 1]]
	
	private static final String NCS0013 = "src/test/resources/samples/hedis-ncs/SampleNCS0013.xml" //Denom Not Met
	private static final Map ASSERTIONS_NCS0013 = [C2766:'']
	private static final Map MEASURES_NCS0013  = [C2599: [num: 0, denom: 0]]

/*
Concepts used:
INPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C2511 -> HEDIS 2014"
"	C2766 -> Patient Age GE 16 and LT 21 Years"
"	C2992 -> HEDIS-HPV Tests"
"	C3083 -> HEDIS-Cervical Cancer"
"	C3084 -> HEDIS-Cervical Cytology"
"	C31 -> Female"
"	C3122 -> HEDIS-HIV"
"	C3124 -> HEDIS-Immunodeficiency"
"	C36 -> OpenCDS"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	C1671 -> Human Immunodeficiency Virus (HIV) Infection"
"	C2599 -> QM HEDIS-NCS Non-Recommended CCS Teens"
"	C2756 -> Cervical Cytology"
"	C2760 -> Human Papilloma Virus (HPV) Test"
"	C2766 -> Patient Age GE 16 and LT 21 Years"
"	C2767 -> Cancer"
"	C2769 -> Immunodeficiency"
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
	def "test HEDIS NCS v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_NCS', version: '2014.1.0'],
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
		NCS0001 | ASSERTIONS_NCS0001| MEASURES_NCS0001
		NCS0002 | ASSERTIONS_NCS0002| MEASURES_NCS0002
		NCS0003 | ASSERTIONS_NCS0003| MEASURES_NCS0003
		NCS0004 | ASSERTIONS_NCS0004| MEASURES_NCS0004
		NCS0005 | ASSERTIONS_NCS0005| MEASURES_NCS0005
		NCS0006 | ASSERTIONS_NCS0006| MEASURES_NCS0006
		NCS0007 | ASSERTIONS_NCS0007| MEASURES_NCS0007
		NCS0008 | ASSERTIONS_NCS0008| MEASURES_NCS0008
		NCS0009 | ASSERTIONS_NCS0009| MEASURES_NCS0009
		NCS0010 | ASSERTIONS_NCS0010| MEASURES_NCS0010
		NCS0011 | ASSERTIONS_NCS0011| MEASURES_NCS0011
		NCS0012 | ASSERTIONS_NCS0012| MEASURES_NCS0012
		NCS0013 | ASSERTIONS_NCS0013| MEASURES_NCS0013
	}
}
