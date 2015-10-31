package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_PBH_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2618: [num: -1, denom: -1]]
	
    private static final String PBH0001 = "src/test/resources/samples/hedis-pbh/SamplePBH0001.xml" //Denom Not Met
	private static final Map ASSERTIONS_PBH0001 = [C2788: '', C2789: '', C3170: '', C341: '', C39: '', C42: '', C544: '', C545: '']
    private static final Map MEASURES_PBH0001  = [C2618: [num: 0, denom: 0]]

	
    private static final String PBH0002 = "src/test/resources/samples/hedis-pbh/SamplePBH0002.xml" //Num Met
	private static final Map ASSERTIONS_PBH0002 = [C3170: '', C341: '', C54: '', C545: '', DaysSupplied:'140']
	private static final Map MEASURES_PBH0002  = [C2618: [num: 1, denom: 1]]
	
    private static final String PBH0003 = "src/test/resources/samples/hedis-pbh/SamplePBH0003.xml" //Num Met
	private static final Map ASSERTIONS_PBH0003 = [C3170: '', C341: '', C54: '', C545: '', DaysSupplied: '130']
	private static final Map MEASURES_PBH0003  = [C2618: [num: 0, denom: 1]]

	
    private static final String PBH0004 = "src/test/resources/samples/hedis-pbh/SamplePBH0004.xml" //Denom Met
	private static final Map ASSERTIONS_PBH0004 = [C3170: '', C341: '', C39: '', C544: '', C545: '']
	private static final Map MEASURES_PBH0004  = [C2618: [num: 0, denom: 0]]
	
	private static final String PBH0005 = "src/test/resources/samples/hedis-pbh/SamplePBH0005.xml" //Denom Met
	private static final Map ASSERTIONS_PBH0005 = [C3170: '', C341: '', C2877: '', C544: '', C545: '']
	private static final Map MEASURES_PBH0005  = [C2618: [num: 0, denom: 0]]

	
	private static final String PBH0006 = "src/test/resources/samples/hedis-pbh/SamplePBH0006.xml" //Denom Not Met
	private static final Map ASSERTIONS_PBH0006 = [C3170: '', C341: '', C2789: '', C544: '', C545: '']
	private static final Map MEASURES_PBH0006  = [C2618: [num: 0, denom: 0]]
	
	private static final String PBH0007 = "src/test/resources/samples/hedis-pbh/SamplePBH0007.xml" //Denom Not Met
	private static final Map ASSERTIONS_PBH0007 = [C3170: '', C341: '', C42: '', C544: '', C545: '']
	private static final Map MEASURES_PBH0007  = [C2618: [num: 0, denom: 0]]

	
	private static final String PBH0008 = "src/test/resources/samples/hedis-pbh/SamplePBH0008.xml" //Num Met
	private static final Map ASSERTIONS_PBH0008 = [C3170: '', C341: '', C2788: '', C544: '', C545: '']
	private static final Map MEASURES_PBH0008  = [C2618: [num: 0, denom: 0]]

	private static final String PBH0009 = "src/test/resources/samples/hedis-pbh/SamplePBH0009.xml" //Denom Not Met
	private static final Map ASSERTIONS_PBH0009 = [C3170: '', C341: '', C2854: '', C544: '', C545: '']
	private static final Map MEASURES_PBH0009 = [C2618: [num: 0, denom: 0]]

	
	private static final String PBH0010 = "src/test/resources/samples/hedis-pbh/SamplePBH0010.xml" //Denom Not Met
	private static final Map ASSERTIONS_PBH0010 = [C3170: '', C341: '', C479: '', C54: '', C545: '', DaysSupplied:'121']
	private static final Map MEASURES_PBH0010  = [C2618: [num: 0, denom: 1]]
	
	private static final String PBH0011 = "src/test/resources/samples/hedis-pbh/SamplePBH0011.xml" //Denom Not Met
	private static final Map ASSERTIONS_PBH0011 = [C3170: '', C341: '', C3179: '', C54: '', C545: '', DaysSupplied:'121']
	private static final Map MEASURES_PBH0011  = [C2618: [num: 0, denom: 1]]
	
	private static final String PBH0012 = "src/test/resources/samples/hedis-pbh/SamplePBH0012.xml" //Denom Not Met
	private static final Map ASSERTIONS_PBH0012 = [C3170: '', C341: '', C478: '', C544: '', C545: '']
	private static final Map MEASURES_PBH0012  = [C2618: [num: 0, denom: 0]]
	
	private static final String PBH0013 = "src/test/resources/samples/hedis-pbh/SamplePBH0013.xml" //Denom Not Met
	private static final Map ASSERTIONS_PBH0013 = [C3170: '', C341: '', C54: '', C545: '', DaysSupplied:'140']
	private static final Map MEASURES_PBH0013  = [C2618: [num: 1, denom: 1]]

/*
Concepts used:
"	1 -> year(s)"
"	2 -> month(s)"
"	5 -> day(s)"
"	C2511 -> HEDIS 2014"
"	C2853 -> HEDIS-PBH Table B Beta-Blocker Medications"
"	C2854 -> HEDIS-PBH Table D Medications to Identify Exclusions (History of Asthma)"
"	C2971 -> HEDIS-Acute Inpatient"
"	C3068 -> HEDIS-AMI"
"	C3071 -> HEDIS-Asthma"
"	C3072 -> HEDIS-Beta-Blocker Contraindications"
"	C3088 -> HEDIS-Chronic Bronchitis"
"	C3089 -> HEDIS-Chronic Respiratory Conditions Due To Fumes/Vapors"
"	C3094 -> HEDIS-COPD"
"	C3170 -> Patient Age GE 18 Years"
"	C3178 -> Facility Billing"
"	C3179 -> Transfer to Non-Acute Care Facility"
"	C341 -> Myocardial Infarction"
"	C36 -> OpenCDS"
"	C405 -> Part of"
"	C478 -> Deceased"
"	C479 -> Transfer to Acute Care Faciity"
"	C489 -> Discharge Disposition"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	5 -> day(s)"
"	C2618 -> QM HEDIS-PBH Persist Beta-Blocker post MI"
"	C2788 -> Bronchitis"
"	C2789 -> Chronic Respiratory Conditions due to Fumes or Vapors"
"	C2854 -> HEDIS-PBH Table D Medications to Identify Exclusions (History of Asthma)"
"	C2877 -> Beta-blocker contraindications"
"	C3170 -> Patient Age GE 18 Years"
"	C3179 -> Transfer to Non-Acute Care Facility"
"	C341 -> Myocardial Infarction"
"	C39 -> Asthma"
"	C42 -> Chronic Obstructive Pulmonary Disease (COPD)"
"	C478 -> Deceased"
"	C479 -> Transfer to Acute Care Faciity"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"

*/
	
	@Unroll
	def "test HEDIS PBH v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_PBH', version: '2014.1.0'],
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
		PBH0001 | ASSERTIONS_PBH0001| MEASURES_PBH0001
		PBH0002 | ASSERTIONS_PBH0002| MEASURES_PBH0002
		PBH0003 | ASSERTIONS_PBH0003| MEASURES_PBH0003
		PBH0004 | ASSERTIONS_PBH0004| MEASURES_PBH0004
		PBH0005 | ASSERTIONS_PBH0005| MEASURES_PBH0005
		PBH0006 | ASSERTIONS_PBH0006| MEASURES_PBH0006
		PBH0007 | ASSERTIONS_PBH0007| MEASURES_PBH0007
		PBH0008 | ASSERTIONS_PBH0008| MEASURES_PBH0008
		PBH0009 | ASSERTIONS_PBH0009| MEASURES_PBH0009
		PBH0010 | ASSERTIONS_PBH0010| MEASURES_PBH0010
		PBH0011 | ASSERTIONS_PBH0011| MEASURES_PBH0011
		PBH0012 | ASSERTIONS_PBH0012| MEASURES_PBH0012
		PBH0013 | ASSERTIONS_PBH0013| MEASURES_PBH0013
	}
}
