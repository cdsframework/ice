package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_LBP_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2583: [num: -1, denom: -1]]
	
    private static final String LBP0001 = "src/test/resources/samples/hedis-lbp/SampleLBP0001.xml" //Denom Not Met
	private static final Map ASSERTIONS_LBP0001 = [C2050:'', C2772:'', C2876:'', C2878:'', C288: '', C544: '', C545: '', C946: '']
    private static final Map MEASURES_LBP0001  = [C2583: [num: 0, denom: 0]]

	
    private static final String LBP0002 = "src/test/resources/samples/hedis-lbp/SampleLBP0002.xml" //Denom Not Met
	private static final Map ASSERTIONS_LBP0002 = [C2772:'', C54:  '', C545: '', C946: '']
	private static final Map MEASURES_LBP0002  = [C2583: [num: 0, denom: 1]]
	
    private static final String LBP0003 = "src/test/resources/samples/hedis-lbp/SampleLBP0003.xml" // Denom Met
	private static final Map ASSERTIONS_LBP0003 = [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '']
	private static final Map MEASURES_LBP0003  = [C2583: [num: 1, denom: 1]]

	
    private static final String LBP0004 = "src/test/resources/samples/hedis-lbp/SampleLBP0004.xml" //Denom Not Met
	private static final Map ASSERTIONS_LBP0004 = [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '']
	private static final Map MEASURES_LBP0004  = [C2583: [num: 1, denom: 1]]
	
	private static final String LBP0005 = "src/test/resources/samples/hedis-lbp/SampleLBP0005.xml" //Denom Not Met
	private static final Map ASSERTIONS_LBP0005 = [C2772:'', C54:  '', C545: '', C946: '']
	private static final Map MEASURES_LBP0005  = [C2583: [num: 0, denom: 1]]

	
	private static final String LBP0006 = "src/test/resources/samples/hedis-lbp/SampleLBP0006.xml" //Denom Not Met
	private static final Map ASSERTIONS_LBP0006 = [C2772:'']
	private static final Map MEASURES_LBP0006  = [C2583: [num: 0, denom: 0]]
	
	private static final String LBP0007 = "src/test/resources/samples/hedis-lbp/SampleLBP0007.xml" //Denom Not Met
	private static final Map ASSERTIONS_LBP0007 = [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '']
	private static final Map MEASURES_LBP0007  = [C2583: [num: 1, denom: 1]]

	
	private static final String LBP0008 = "src/test/resources/samples/hedis-lbp/SampleLBP0008.xml" //Num Met
	private static final Map ASSERTIONS_LBP0008 = [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '']
	private static final Map MEASURES_LBP0008  = [C2583: [num: 1, denom: 1]]

	private static final String LBP0009 = "src/test/resources/samples/hedis-lbp/SampleLBP0009.xml" //Num Met
	private static final Map ASSERTIONS_LBP0009 = [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '']
	private static final Map MEASURES_LBP0009 = [C2583: [num: 1, denom: 1]]

	
	private static final String LBP0010 = "src/test/resources/samples/hedis-lbp/SampleLBP0010.xml" //Num Met
	private static final Map ASSERTIONS_LBP0010 = [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '']
	private static final Map MEASURES_LBP0010  = [C2583: [num: 1, denom: 1]]
	
	private static final String LBP0011 = "src/test/resources/samples/hedis-lbp/SampleLBP0011.xml" //Num Met
	private static final Map ASSERTIONS_LBP0011 = [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '']
	private static final Map MEASURES_LBP0011  = [C2583: [num: 1, denom: 1]]
	
	private static final String LBP0012 = "src/test/resources/samples/hedis-lbp/SampleLBP0012.xml" //Num Not Met
	private static final Map ASSERTIONS_LBP0012 = [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '']
	private static final Map MEASURES_LBP0012  = [C2583: [num: 1, denom: 1]]
	
	private static final String LBP0013 = "src/test/resources/samples/hedis-lbp/SampleLBP0013.xml" //Denom Met
	private static final Map ASSERTIONS_LBP0013 = [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '']
	private static final Map MEASURES_LBP0013  = [C2583: [num: 1, denom: 1]]
	
	private static final String LBP0014 = "src/test/resources/samples/hedis-lbp/SampleLBP0014.xml" //Num Met
	private static final Map ASSERTIONS_LBP0014 = [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '']
	private static final Map MEASURES_LBP0014 = [C2583: [num: 1, denom: 1]]

	private static final String LBP0015 = "src/test/resources/samples/hedis-lbp/SampleLBP0015.xml" //Num Met
	private static final Map ASSERTIONS_LBP0015 = [C2772:'', C2878:'', C544: '', C545: '', C946: '']
	private static final Map MEASURES_LBP0015  = [C2583: [num: 0, denom: 0]]

	
	private static final String LBP0016 = "src/test/resources/samples/hedis-lbp/SampleLBP0016.xml" //Num Met
	private static final Map ASSERTIONS_LBP0016 = [C2772:'', C544: '', C545: '', C946: '']
	private static final Map MEASURES_LBP0016  = [C2583: [num: 0, denom: 0]]
	
	private static final String LBP0017 = "src/test/resources/samples/hedis-lbp/SampleLBP0017.xml" //Denom Met
	private static final Map ASSERTIONS_LBP0017 = [C2772:'', C544: '', C545: '', C946: '']
	private static final Map MEASURES_LBP0017  = [C2583: [num: 0, denom: 0]]

	
	private static final String LBP0018 = "src/test/resources/samples/hedis-lbp/SampleLBP0018.xml" //Num Met
	private static final Map ASSERTIONS_LBP0018 = [C2772:'', C544: '', C545: '', C946: '']
	private static final Map MEASURES_LBP0018  = [C2583: [num: 0, denom: 0]]

	private static final String LBP0019 = "src/test/resources/samples/hedis-lbp/SampleLBP0019.xml" //Num Met
	private static final Map ASSERTIONS_LBP0019 = [C2772:'', C544: '', C545: '', C946: '']
	private static final Map MEASURES_LBP0019 = [C2583: [num: 0, denom: 0]]

	private static final String LBP0020 = "src/test/resources/samples/hedis-lbp/SampleLBP0020.xml" //Num Met
	private static final Map ASSERTIONS_LBP0020 = [C2772:'', C544: '', C545: '', C946: '']
	private static final Map MEASURES_LBP0020  = [C2583: [num: 0, denom: 0]]
	
	private static final String LBP0021 = "src/test/resources/samples/hedis-lbp/SampleLBP0021.xml" //Denom Met
	private static final Map ASSERTIONS_LBP0021 = [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '']
	private static final Map MEASURES_LBP0021  = [C2583: [num: 1, denom: 1]]
	
	private static final String LBP0022 = "src/test/resources/samples/hedis-lbp/SampleLBP0022.xml" //Num Met
	private static final Map ASSERTIONS_LBP0022 = [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '']
	private static final Map MEASURES_LBP0022  = [C2583: [num: 1, denom: 1]]
	
	private static final String LBP0023 = "src/test/resources/samples/hedis-lbp/SampleLBP0023.xml" //Num Met
	private static final Map ASSERTIONS_LBP0023 = [:]
	private static final Map MEASURES_LBP0023  = [C2583: [num: 0, denom: 0]]

	private static final String LBP0024 = "src/test/resources/samples/hedis-lbp/SampleLBP0024.xml" //Denom Met
	private static final Map ASSERTIONS_LBP0024 = [:]
	private static final Map MEASURES_LBP0024  = [C2583: [num: 0, denom: 0]]

/*
Concepts used:
INPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C2511 -> HEDIS 2014"
"	C2772 -> Patient Age GE 19 and LT 51 Years"
"	C2964 -> HEDIS-Outpatient"
"	C2968 -> HEDIS-ED"
"	C2971 -> HEDIS-Acute Inpatient"
"	C2994 -> HEDIS-Imaging Study"
"	C3020 -> HEDIS-Nonacute Inpatient"
"	C3022 -> HEDIS-Observation"
"	C3025 -> HEDIS-Osteopathic Manipulative Treatment"
"	C3121 -> HEDIS-History of Malignant Neoplasm"
"	C3125 -> HEDIS-IV Drug Abuse"
"	C3128 -> HEDIS-Low Back Pain"
"	C3131 -> HEDIS-Malignant Neoplasms"
"	C3137 -> HEDIS-Neurologic Impairment"
"	C3140 -> HEDIS-Other Neoplasms"
"	C3149 -> HEDIS-Trauma"
"	C36 -> OpenCDS"
"	C405 -> Part of"
"	C416 -> Primary"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C2050 -> Neoplasm"
"	C2583 -> QM HEDIS-LBP Imaging for Low Back Pain"
"	C2772 -> Patient Age GE 19 and LT 51 Years"
"	C2871 -> Imaging Study"
"	C2872 -> Intravenous (IV) Drug Abuse"
"	C2873 -> Neurologic Impairment"
"	C2876 -> Malignant Neoplasm"
"	C2878 -> Malignant Neoplasm"
"	C288 -> Trauma"
"	C3155 -> Low Back Pain 180 Days Before IESD"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"
"	C946 -> Low Back Pain"
*/
	
	@Unroll
	def "test HEDIS LBP v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_LBP', version: '2014.1.0'],
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
//		LBP0001 | ASSERTIONS_LBP0001| MEASURES_LBP0001
//		LBP0002 | ASSERTIONS_LBP0002| MEASURES_LBP0002
		LBP0003 | ASSERTIONS_LBP0003| MEASURES_LBP0003
		LBP0004 | ASSERTIONS_LBP0004| MEASURES_LBP0004
		LBP0005 | ASSERTIONS_LBP0005| MEASURES_LBP0005
		LBP0006 | ASSERTIONS_LBP0006| MEASURES_LBP0006
		LBP0007 | ASSERTIONS_LBP0007| MEASURES_LBP0007
		LBP0008 | ASSERTIONS_LBP0008| MEASURES_LBP0008
		LBP0009 | ASSERTIONS_LBP0009| MEASURES_LBP0009
		LBP0010 | ASSERTIONS_LBP0010| MEASURES_LBP0010
		LBP0011 | ASSERTIONS_LBP0011| MEASURES_LBP0011
		LBP0012 | ASSERTIONS_LBP0012| MEASURES_LBP0012
		LBP0013 | ASSERTIONS_LBP0013| MEASURES_LBP0013
		LBP0014 | ASSERTIONS_LBP0014| MEASURES_LBP0014
		LBP0015 | ASSERTIONS_LBP0015| MEASURES_LBP0015
		LBP0016 | ASSERTIONS_LBP0016| MEASURES_LBP0016
		LBP0017 | ASSERTIONS_LBP0017| MEASURES_LBP0017
		LBP0018 | ASSERTIONS_LBP0018| MEASURES_LBP0018
		LBP0019 | ASSERTIONS_LBP0019| MEASURES_LBP0019
		LBP0020 | ASSERTIONS_LBP0020| MEASURES_LBP0020
		LBP0021 | ASSERTIONS_LBP0021| MEASURES_LBP0021
		LBP0022 | ASSERTIONS_LBP0022| MEASURES_LBP0022
//		LBP0023 | ASSERTIONS_LBP0023| MEASURES_LBP0023
//		LBP0024 | ASSERTIONS_LBP0024| MEASURES_LBP0024
	}
}
