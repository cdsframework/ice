package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_CHL_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2601: [num: -1, denom: -1]]
	
    private static final String CHL0001 = "src/test/resources/samples/hedis-chl/SampleCHL0001.xml" //Num Met
	private static final Map ASSERTIONS_CHL0001 = [C1468:'', C1651:'', C1693:'', C2519:'', C2520:'', C2766:'', C2836:'', C31: '', C539:'', C54: '',C545:'',C844:'']
    private static final Map MEASURES_CHL0001  = [C2601: [num: 1, denom: 1], C3372: [num: 1, denom: 1], C3373: [num: 0, denom: 0]]

	
    private static final String CHL0002 = "src/test/resources/samples/hedis-chl/SampleCHL0002.xml" //Num Not Met
	private static final Map ASSERTIONS_CHL0002 = [C1468:'', C2519:'', C2836:'', C2837:'', C31:  '', C54:  '', C545: '']
	private static final Map MEASURES_CHL0002  = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 0], C3373: [num: 0, denom: 1]]
	
    private static final String CHL0003 = "src/test/resources/samples/hedis-chl/SampleCHL0003.xml" // Num Not Met
	private static final Map ASSERTIONS_CHL0003 = [C1468:'', C2520:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '']
	private static final Map MEASURES_CHL0003  = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 1], C3373: [num: 0, denom: 0]]

	
    private static final String CHL0004 = "src/test/resources/samples/hedis-chl/SampleCHL0004.xml" //Num Not Met
	private static final Map ASSERTIONS_CHL0004 = [C1468:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '']
	private static final Map MEASURES_CHL0004  = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 1], C3373: [num: 0, denom: 0]]
	
	private static final String CHL0005 = "src/test/resources/samples/hedis-chl/SampleCHL0005.xml" //Num Not Met
	private static final Map ASSERTIONS_CHL0005 = [C1468:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '']
	private static final Map MEASURES_CHL0005  = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 1], C3373: [num: 0, denom: 0]]

	
	private static final String CHL0006 = "src/test/resources/samples/hedis-chl/SampleCHL0006.xml" //Num Not Met
	private static final Map ASSERTIONS_CHL0006 = [C1468:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '']
	private static final Map MEASURES_CHL0006  = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 1], C3373: [num: 0, denom: 0]]
	
	private static final String CHL0007 = "src/test/resources/samples/hedis-chl/SampleCHL0007.xml" //Num Not Met
	private static final Map ASSERTIONS_CHL0007 = [C1468:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '']
	private static final Map MEASURES_CHL0007  = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 1], C3373: [num: 0, denom: 0]]

	
	private static final String CHL0008 = "src/test/resources/samples/hedis-chl/SampleCHL0008.xml" //Num Not Met
	private static final Map ASSERTIONS_CHL0008 = [C1468:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '']
	private static final Map MEASURES_CHL0008  = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 1], C3373: [num: 0, denom: 0]]

	private static final String CHL0009 = "src/test/resources/samples/hedis-chl/SampleCHL0009.xml" //Num Not Met
	private static final Map ASSERTIONS_CHL0009 = [C1468:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '']
	private static final Map MEASURES_CHL0009 = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 1], C3373: [num: 0, denom: 0]]

	
	private static final String CHL0010 = "src/test/resources/samples/hedis-chl/SampleCHL0010.xml" //Num Not Met
	private static final Map ASSERTIONS_CHL0010 = [C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', C844: '']
	private static final Map MEASURES_CHL0010  = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 1], C3373: [num: 0, denom: 0]]
	
	private static final String CHL0011 = "src/test/resources/samples/hedis-chl/SampleCHL0011.xml" //Denom Not Met
	private static final Map ASSERTIONS_CHL0011 = [C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '']
	private static final Map MEASURES_CHL0011  = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 1], C3373: [num: 0, denom: 0]]
	
	private static final String CHL0012 = "src/test/resources/samples/hedis-chl/SampleCHL0012.xml" //Num Not Met
	private static final Map ASSERTIONS_CHL0012 = [C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '']
	private static final Map MEASURES_CHL0012  = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 1], C3373: [num: 0, denom: 0]]
	
	private static final String CHL0013 = "src/test/resources/samples/hedis-chl/SampleCHL0013.xml" //Denom Met
	private static final Map ASSERTIONS_CHL0013 = [C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '']
	private static final Map MEASURES_CHL0013  = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 1], C3373: [num: 0, denom: 0]]
	
	private static final String CHL0014 = "src/test/resources/samples/hedis-chl/SampleCHL0014.xml" //Num Met
	private static final Map ASSERTIONS_CHL0014 = [C1651:'', C2519:'', C2766:'', C2836:'', C31:  '', C539: '', C54:  '', C545:'']
	private static final Map MEASURES_CHL0014 = [C2601: [num: 1, denom: 1], C3372: [num: 1, denom: 1], C3373: [num: 0, denom: 0]]

	private static final String CHL0015 = "src/test/resources/samples/hedis-chl/SampleCHL0015.xml" //Num Met
	private static final Map ASSERTIONS_CHL0015 = [C1651:'', C2519:'', C2766:'', C2836:'', C31:  '', C539: '', C54:  '', C545:'']
	private static final Map MEASURES_CHL0015  = [C2601: [num: 1, denom: 1], C3372: [num: 1, denom: 1], C3373: [num: 0, denom: 0]]

	
	private static final String CHL0016 = "src/test/resources/samples/hedis-chl/SampleCHL0016.xml" //Denom Not Met
	private static final Map ASSERTIONS_CHL0016 = [C1693:'', C2519:'', C2766:'', C2836:'', C2841:'', C31:  '', C544: '', C545:'']
	private static final Map MEASURES_CHL0016  = [C2601: [num: 0, denom: 0], C3372: [num: 0, denom: 0], C3373: [num: 0, denom: 0]]
	
	private static final String CHL0017 = "src/test/resources/samples/hedis-chl/SampleCHL0017.xml" //Denom Not Met
	private static final Map ASSERTIONS_CHL0017 = [C1693:'', C2519:'', C2766:'', C2836:'', C2841:'', C31:  '', C544: '', C545:'']
	private static final Map MEASURES_CHL0017  = [C2601: [num: 0, denom: 0], C3372: [num: 0, denom: 0], C3373: [num: 0, denom: 0]]

	
	private static final String CHL0018 = "src/test/resources/samples/hedis-chl/SampleCHL0018.xml" //Denom Met
	private static final Map ASSERTIONS_CHL0018 = [C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '']
	private static final Map MEASURES_CHL0018  = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 1], C3373: [num: 0, denom: 0]]

	private static final String CHL0019 = "src/test/resources/samples/hedis-chl/SampleCHL0019.xml" //Denom Met
	private static final Map ASSERTIONS_CHL0019 = [C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '']
	private static final Map MEASURES_CHL0019 = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 1], C3373: [num: 0, denom: 0]]

	private static final String CHL0020 = "src/test/resources/samples/hedis-chl/SampleCHL0020.xml" //Denom Met
	private static final Map ASSERTIONS_CHL0020 = [C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '']
	private static final Map MEASURES_CHL0020  = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 1], C3373: [num: 0, denom: 0]]
	
	private static final String CHL0021 = "src/test/resources/samples/hedis-chl/SampleCHL0021.xml" //Num Met
	private static final Map ASSERTIONS_CHL0021 = [C1651:'', C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C539: '', C54: '', C545:'']
	private static final Map MEASURES_CHL0021  = [C2601: [num: 1, denom: 1], C3372: [num: 1, denom: 1], C3373: [num: 0, denom: 0]]
	
	private static final String CHL0022 = "src/test/resources/samples/hedis-chl/SampleCHL0022.xml" //Num Met
	private static final Map ASSERTIONS_CHL0022 = [C1651:'', C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C539: '', C54: '', C545:'']
	private static final Map MEASURES_CHL0022  = [C2601: [num: 1, denom: 1], C3372: [num: 1, denom: 1], C3373: [num: 0, denom: 0]]
	
	private static final String CHL0023 = "src/test/resources/samples/hedis-chl/SampleCHL0023.xml" //Denom Met
	private static final Map ASSERTIONS_CHL0023 = [C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '']
	private static final Map MEASURES_CHL0023  = [C2601: [num: 0, denom: 1], C3372: [num: 0, denom: 1], C3373: [num: 0, denom: 0]]
	

/*
Concepts used:
INPUT
"	1 -> year(s)"
"	2840 -> HEDIS-CHL Table E Medications to Identify Exclusions"
"	5 -> day(s)"
"	C1468 -> Sexual Activity"
"	C1651 -> Chlamydia Tests"
"	C1693 -> Pregnancy Test"
"	C2511 -> HEDIS 2014"
"	C2839 -> HEDIS-CHL Table A Prescriptions to Identify Contraceptives"
"	C3035 -> HEDIS-Pregnancy Tests"
"	C3047 -> HEDIS-Sexual Activity"
"	C3087 -> HEDIS-Chlamydia Tests"
"	C31 -> Female"
"	C3104 -> HEDIS-Diagnostic Radiology"
"	C3143 -> HEDIS-Pregnancy"
"	C36 -> OpenCDS"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
"	C844 -> Pregnancy"
OUTPUT
"	C1468 -> Sexual Activity"
"	C1651 -> Chlamydia Tests"
"	C1693 -> Pregnancy Test"
"	C2519 -> Denominator Inclusions by Claims Data"
"	C2520 -> Denominator Inclusions by Pharmacy Data"
"	C2601 -> QM HEDIS-CHL Chlamydia Screening in Women"
"	C2766 -> Patient Age GE 16 and LT 21 Years"
"	C2794 -> Advanced Care Planning"
"	C2836 -> Patient Age GE 16 and LT 25 Years"
"	C2837 -> Patient Age GE 21 and LT 25 Years"
"	C2841 -> Radiology - Diagnostic Radiology"
"	C31 -> Female"
"	C3372 -> QM HEDIS-CHL Chlamydia Screening in Women (Age 16-20)"
"	C3373 -> QM HEDIS-CHL Chlamydia Screening in Women (Age 21-24)"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"
"	C844 -> Pregnancy"

*/
	
	@Unroll
	def "test HEDIS CHL v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CHL', version: '2014.1.0'],
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
		CHL0001 | ASSERTIONS_CHL0001| MEASURES_CHL0001
		CHL0002 | ASSERTIONS_CHL0002| MEASURES_CHL0002
		CHL0003 | ASSERTIONS_CHL0003| MEASURES_CHL0003
		CHL0004 | ASSERTIONS_CHL0004| MEASURES_CHL0004
		CHL0005 | ASSERTIONS_CHL0005| MEASURES_CHL0005
		CHL0006 | ASSERTIONS_CHL0006| MEASURES_CHL0006
		CHL0007 | ASSERTIONS_CHL0007| MEASURES_CHL0007
		CHL0008 | ASSERTIONS_CHL0008| MEASURES_CHL0008
		CHL0009 | ASSERTIONS_CHL0009| MEASURES_CHL0009
		CHL0010 | ASSERTIONS_CHL0010| MEASURES_CHL0010
		CHL0011 | ASSERTIONS_CHL0011| MEASURES_CHL0011
		CHL0012 | ASSERTIONS_CHL0012| MEASURES_CHL0012
		CHL0013 | ASSERTIONS_CHL0013| MEASURES_CHL0013
		CHL0014 | ASSERTIONS_CHL0014| MEASURES_CHL0014
		CHL0015 | ASSERTIONS_CHL0015| MEASURES_CHL0015
		CHL0016 | ASSERTIONS_CHL0016| MEASURES_CHL0016
		CHL0017 | ASSERTIONS_CHL0017| MEASURES_CHL0017
		CHL0018 | ASSERTIONS_CHL0018| MEASURES_CHL0018
		CHL0019 | ASSERTIONS_CHL0019| MEASURES_CHL0019
		CHL0020 | ASSERTIONS_CHL0020| MEASURES_CHL0020
		CHL0021 | ASSERTIONS_CHL0021| MEASURES_CHL0021
		CHL0022 | ASSERTIONS_CHL0022| MEASURES_CHL0022
		CHL0023 | ASSERTIONS_CHL0023| MEASURES_CHL0023

	}
}
