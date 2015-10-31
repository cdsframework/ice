package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_COL_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2600: [num: -1, denom: -1]]
	
    private static final String COL0001 = "src/test/resources/samples/hedis-col/SampleCOL0001.xml" //Denom Not Met
	private static final Map ASSERTIONS_COL0001 = [:]
    private static final Map MEASURES_COL0001  = [C2600: [num: 0, denom: 0]]

	
    private static final String COL0002 = "src/test/resources/samples/hedis-col/SampleCOL0002.xml" //Denom Not Met
	private static final Map ASSERTIONS_COL0002 = [:]
	private static final Map MEASURES_COL0002  = [C2600: [num: 0, denom: 0]]
	
    private static final String COL0003 = "src/test/resources/samples/hedis-col/SampleCOL0003.xml" // Denom Met
	private static final Map ASSERTIONS_COL0003 = [C2707: '', C54: '', C545: '']
	private static final Map MEASURES_COL0003  = [C2600: [num: 0, denom: 1]]

	
    private static final String COL0004 = "src/test/resources/samples/hedis-col/SampleCOL0004.xml" //Denom Not Met
	private static final Map ASSERTIONS_COL0004 = [C2707: '', C2708: '', C544: '', C545: '']
	private static final Map MEASURES_COL0004  = [C2600: [num: 0, denom: 0]]
	
	private static final String COL0005 = "src/test/resources/samples/hedis-col/SampleCOL0005.xml" //Denom Not Met
	private static final Map ASSERTIONS_COL0005 = [C2707: '', C2708: '', C544: '', C545: '']
	private static final Map MEASURES_COL0005  = [C2600: [num: 0, denom: 0]]

	
	private static final String COL0006 = "src/test/resources/samples/hedis-col/SampleCOL0006.xml" //Denom Not Met
	private static final Map ASSERTIONS_COL0006 = [C2707: '', C2709: '', C544: '', C545: '']
	private static final Map MEASURES_COL0006  = [C2600: [num: 0, denom: 0]]
	
	private static final String COL0007 = "src/test/resources/samples/hedis-col/SampleCOL0007.xml" //Denom Not Met
	private static final Map ASSERTIONS_COL0007 = [C2707: '', C2709: '', C544: '', C545: '']
	private static final Map MEASURES_COL0007  = [C2600: [num: 0, denom: 0]]

	
	private static final String COL0008 = "src/test/resources/samples/hedis-col/SampleCOL0008.xml" //Num Met
	private static final Map ASSERTIONS_COL0008 = [C2514: '', C2707: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_COL0008  = [C2600: [num: 1, denom: 1]]

	private static final String COL0009 = "src/test/resources/samples/hedis-col/SampleCOL0009.xml" //Num Met
	private static final Map ASSERTIONS_COL0009 = [C2514: '', C2707: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_COL0009 = [C2600: [num: 1, denom: 1]]

	
	private static final String COL0010 = "src/test/resources/samples/hedis-col/SampleCOL0010.xml" //Num Met
	private static final Map ASSERTIONS_COL0010 = [C2514: '', C2707: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_COL0010  = [C2600: [num: 1, denom: 1]]
	
	private static final String COL0011 = "src/test/resources/samples/hedis-col/SampleCOL0011.xml" //Num Met
	private static final Map ASSERTIONS_COL0011 = [C2514: '', C2707: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_COL0011  = [C2600: [num: 1, denom: 1]]
	
	private static final String COL0012 = "src/test/resources/samples/hedis-col/SampleCOL0012.xml" //Num Not Met
	private static final Map ASSERTIONS_COL0012 = [C2707: '', C54: '', C545: '']
	private static final Map MEASURES_COL0012  = [C2600: [num: 0, denom: 1]]
	
	private static final String COL0013 = "src/test/resources/samples/hedis-col/SampleCOL0013.xml" //Denom Met
	private static final Map ASSERTIONS_COL0013 = [C2738: '', C2707: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_COL0013  = [C2600: [num: 1, denom: 1]]
	
	private static final String COL0014 = "src/test/resources/samples/hedis-col/SampleCOL0014.xml" //Num Met
	private static final Map ASSERTIONS_COL0014 = [C2738: '', C2707: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_COL0014 = [C2600: [num: 1, denom: 1]]

	private static final String COL0015 = "src/test/resources/samples/hedis-col/SampleCOL0015.xml" //Num Met
	private static final Map ASSERTIONS_COL0015 = [C2707: '', C2738: '',  C539: '', C54: '', C545: '']
	private static final Map MEASURES_COL0015  = [C2600: [num: 1, denom: 1]]

	
	private static final String COL0016 = "src/test/resources/samples/hedis-col/SampleCOL0016.xml" //Num Met
	private static final Map ASSERTIONS_COL0016 = [C2707: '', C2738: '',  C539: '', C54: '', C545: '']
	private static final Map MEASURES_COL0016  = [C2600: [num: 1, denom: 1]]
	
	private static final String COL0017 = "src/test/resources/samples/hedis-col/SampleCOL0017.xml" //Denom Met
	private static final Map ASSERTIONS_COL0017 = [C2707: '', C54: '', C545: '']
	private static final Map MEASURES_COL0017  = [C2600: [num: 0, denom: 1]]

	
	private static final String COL0018 = "src/test/resources/samples/hedis-col/SampleCOL0018.xml" //Num Met
	private static final Map ASSERTIONS_COL0018 = [C2739: '', C2707: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_COL0018  = [C2600: [num: 1, denom: 1]]

	private static final String COL0019 = "src/test/resources/samples/hedis-col/SampleCOL0019.xml" //Num Met
	private static final Map ASSERTIONS_COL0019 = [C2739: '', C2707: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_COL0019 = [C2600: [num: 1, denom: 1]]

	private static final String COL0020 = "src/test/resources/samples/hedis-col/SampleCOL0020.xml" //Num Met
	private static final Map ASSERTIONS_COL0020 = [C2739: '', C2707: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_COL0020  = [C2600: [num: 1, denom: 1]]
	
	private static final String COL0021 = "src/test/resources/samples/hedis-col/SampleCOL0021.xml" //Denom Met
	private static final Map ASSERTIONS_COL0021 = [C2707: '', C54: '', C545: '']
	private static final Map MEASURES_COL0021  = [C2600: [num: 0, denom: 1]]
	
	private static final String COL0022 = "src/test/resources/samples/hedis-col/SampleCOL0022.xml" //Num Met
	private static final Map ASSERTIONS_COL0022 = [C2739: '', C2707: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_COL0022  = [C2600: [num: 1, denom: 1]]
	
	private static final String COL0023 = "src/test/resources/samples/hedis-col/SampleCOL0023.xml" //Num Met
	private static final Map ASSERTIONS_COL0023 = [C2739: '', C2707: '', C539: '', C54: '', C545: '']
	private static final Map MEASURES_COL0023  = [C2600: [num: 1, denom: 1]]

	private static final String COL0024 = "src/test/resources/samples/hedis-col/SampleCOL0024.xml" //Denom Met
	private static final Map ASSERTIONS_COL0024 = [C2707: '', C54: '', C545: '']
	private static final Map MEASURES_COL0024  = [C2600: [num: 0, denom: 1]]

/*
Concepts used:
INPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C2511 -> HEDIS 2014"
"	C2707 -> Patient Age GE 51 and LT 76 Years"
"	C2974 -> HEDIS-Colorectal Cancer"
"	C2976 -> HEDIS-FOBT"
"	C3058 -> HEDIS-Total Colectomy"
"	C3091 -> HEDIS-Colonoscopy"
"	C3115 -> HEDIS-Flexible Sigmoidoscopy"
"	C36 -> OpenCDS"
"	C413 -> Data from EHR or Clinician"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	C2514 -> Colonoscopy"
"	C2600 -> QM HEDIS-COL Colorectal Cancer Screening"
"	C2707 -> Patient Age GE 51 and LT 76 Years"
"	C2708 -> Cancer"
"	C2709 -> Colectomy total"
"	C2738 -> Sigmoidoscopy"
"	C2739 -> Fecal Occult Blood Testing"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"


*/
	
	@Unroll
	def "test HEDIS COL v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_COL', version: '2014.1.0'],
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
//		COL0001 | ASSERTIONS_COL0001| MEASURES_COL0001
//		COL0002 | ASSERTIONS_COL0002| MEASURES_COL0002
		COL0003 | ASSERTIONS_COL0003| MEASURES_COL0003
		COL0004 | ASSERTIONS_COL0004| MEASURES_COL0004
		COL0005 | ASSERTIONS_COL0005| MEASURES_COL0005
		COL0006 | ASSERTIONS_COL0006| MEASURES_COL0006
		COL0007 | ASSERTIONS_COL0007| MEASURES_COL0007
		COL0008 | ASSERTIONS_COL0008| MEASURES_COL0008
		COL0009 | ASSERTIONS_COL0009| MEASURES_COL0009
		COL0010 | ASSERTIONS_COL0010| MEASURES_COL0010
		COL0011 | ASSERTIONS_COL0011| MEASURES_COL0011
		COL0012 | ASSERTIONS_COL0012| MEASURES_COL0012
		COL0013 | ASSERTIONS_COL0013| MEASURES_COL0013
		COL0014 | ASSERTIONS_COL0014| MEASURES_COL0014
		COL0015 | ASSERTIONS_COL0015| MEASURES_COL0015
		COL0016 | ASSERTIONS_COL0016| MEASURES_COL0016
		COL0017 | ASSERTIONS_COL0017| MEASURES_COL0017
		COL0018 | ASSERTIONS_COL0018| MEASURES_COL0018
		COL0019 | ASSERTIONS_COL0019| MEASURES_COL0019
		COL0020 | ASSERTIONS_COL0020| MEASURES_COL0020
		COL0021 | ASSERTIONS_COL0021| MEASURES_COL0021
		COL0022 | ASSERTIONS_COL0022| MEASURES_COL0022
		COL0023 | ASSERTIONS_COL0023| MEASURES_COL0023
		COL0024 | ASSERTIONS_COL0024| MEASURES_COL0024
	}
}
