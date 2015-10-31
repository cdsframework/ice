package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_AAB_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2610: [num: -1, denom: -1]]
	
    private static final String AAB0001 = "src/test/resources/samples/hedis-aab/SampleAAB0001.xml" //Num Met
	private static final Map ASSERTIONS_AAB0001 = [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00']
    private static final Map MEASURES_AAB0001  = [C2610: [num: 1, denom: 1]]

	
    private static final String AAB0002 = "src/test/resources/samples/hedis-aab/SampleAAB0002.xml" //Num Met
	private static final Map ASSERTIONS_AAB0002 = [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-10-23 11:50:00', IESD_PLUS_3D:'2011-10-26 11:50:00']
	private static final Map MEASURES_AAB0002  = [C2610: [num: 1, denom: 1]]
	
    private static final String AAB0003 = "src/test/resources/samples/hedis-aab/SampleAAB0003.xml" //Num Met
	private static final Map ASSERTIONS_AAB0003 = [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-10-23 11:50:00', IESD_PLUS_3D:'2011-10-26 11:50:00']
	private static final Map MEASURES_AAB0003  = [C2610: [num: 1, denom: 1]]

	
    private static final String AAB0004 = "src/test/resources/samples/hedis-aab/SampleAAB0004.xml" //Denom Met
	private static final Map ASSERTIONS_AAB0004 = [C3199:'', C54:  '', C545: '', IESD:'2011-12-13 11:50:00']
	private static final Map MEASURES_AAB0004  = [C2610: [num: 0, denom: 1]]
	
	private static final String AAB0005 = "src/test/resources/samples/hedis-aab/SampleAAB0005.xml" //Num Met
	private static final Map ASSERTIONS_AAB0005 = [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00']
	private static final Map MEASURES_AAB0005  = [C2610: [num: 1, denom: 1]]

	
	private static final String AAB0006 = "src/test/resources/samples/hedis-aab/SampleAAB0006.xml" //Num Met
	private static final Map ASSERTIONS_AAB0006 = [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00']
	private static final Map MEASURES_AAB0006  = [C2610: [num: 1, denom: 1]]
	
	private static final String AAB0007 = "src/test/resources/samples/hedis-aab/SampleAAB0007.xml" //Num Met
	private static final Map ASSERTIONS_AAB0007 = [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00']
	private static final Map MEASURES_AAB0007  = [C2610: [num: 1, denom: 1]]

	
	private static final String AAB0008 = "src/test/resources/samples/hedis-aab/SampleAAB0008.xml" //Num Met
	private static final Map ASSERTIONS_AAB0008 = [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00']
	private static final Map MEASURES_AAB0008  = [C2610: [num: 1, denom: 1]]

	private static final String AAB0009 = "src/test/resources/samples/hedis-aab/SampleAAB0009.xml" //Num Met
	private static final Map ASSERTIONS_AAB0009 = [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00']
	private static final Map MEASURES_AAB0009 = [C2610: [num: 1, denom: 1]]

	
	private static final String AAB0010 = "src/test/resources/samples/hedis-aab/SampleAAB0010.xml" //Num Met
	private static final Map ASSERTIONS_AAB0010 = [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00']
	private static final Map MEASURES_AAB0010  = [C2610: [num: 1, denom: 1]]
	
	private static final String AAB0011 = "src/test/resources/samples/hedis-aab/SampleAAB0011.xml" //Num Met
	private static final Map ASSERTIONS_AAB0011 = [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00']
	private static final Map MEASURES_AAB0011  = [C2610: [num: 1, denom: 1]]
	
	private static final String AAB0012 = "src/test/resources/samples/hedis-aab/SampleAAB0012.xml" //Num Met
	private static final Map ASSERTIONS_AAB0012 = [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00']
	private static final Map MEASURES_AAB0012  = [C2610: [num: 1, denom: 1]]
	
	private static final String AAB0013 = "src/test/resources/samples/hedis-aab/SampleAAB0013.xml" //Num Met
	private static final Map ASSERTIONS_AAB0013 = [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00']
	private static final Map MEASURES_AAB0013  = [C2610: [num: 1, denom: 1]]
	
	private static final String AAB0014 = "src/test/resources/samples/hedis-aab/SampleAAB0014.xml" //Num Met
	private static final Map ASSERTIONS_AAB0014 = [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-10-23 11:50:00', IESD_PLUS_3D:'2011-10-26 11:50:00']
	private static final Map MEASURES_AAB0014 = [C2610: [num: 1, denom: 1]]
	


/*
Concepts used:
INPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C2511 -> HEDIS 2014"
"	C2842 -> HEDIS-AAB Table D Antibiotic Medications"
"	C2964 -> HEDIS-Outpatient"
"	C2968 -> HEDIS-ED"
"	C2971 -> HEDIS-Acute Inpatient"
"	C3020 -> HEDIS-Nonacute Inpatient"
"	C3022 -> HEDIS-Observation"
"	C3065 -> HEDIS-Acute Bronchitis"
"	C3092 -> HEDIS-Comorbid Conditions"
"	C3093 -> HEDIS-Competing Diagnosis"
"	C3094 -> HEDIS-COPD"
"	C3095 -> HEDIS-Cystic Fibrosis"
"	C3111 -> HEDIS-Emphysema"
"	C3122 -> HEDIS-HIV"
"	C3131 -> HEDIS-Malignant Neoplasms"
"	C3141 -> HEDIS-Pharyngitis"
"	C3199 -> Patient Age GE 18 LT 65 Years"
"	C36 -> OpenCDS"
"	C405 -> Part of"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	5 -> day(s)"
"	C1527 -> Antibiotic"
"	C2610 -> QM HEDIS-AAB Avoid AntiBx in Adults Acute Bronc."
"	C3199 -> Patient Age GE 18 LT 65 Years"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"


*/
	
	@Unroll
	def "test HEDIS AAB v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_AAB', version: '2014.1.0'],
			specifiedTime: '2012-01-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
		println responsePayload

		then:
		def data = new XmlSlurper().parseText(responsePayload)
		def results = VMRUtil.getResults(data, '\\|')
//		assertions.size() == results.assertions.size()
		results.assertions.each {entry ->
			System.err.println "${entry.key} -> ${entry.value}"
		}
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
		AAB0001 | ASSERTIONS_AAB0001| MEASURES_AAB0001
		AAB0002 | ASSERTIONS_AAB0002| MEASURES_AAB0002
		AAB0003 | ASSERTIONS_AAB0003| MEASURES_AAB0003
		AAB0004 | ASSERTIONS_AAB0004| MEASURES_AAB0004
		AAB0005 | ASSERTIONS_AAB0005| MEASURES_AAB0005
		AAB0006 | ASSERTIONS_AAB0006| MEASURES_AAB0006
		AAB0007 | ASSERTIONS_AAB0007| MEASURES_AAB0007
		AAB0008 | ASSERTIONS_AAB0008| MEASURES_AAB0008
		AAB0009 | ASSERTIONS_AAB0009| MEASURES_AAB0009
		AAB0010 | ASSERTIONS_AAB0010| MEASURES_AAB0010
		AAB0011 | ASSERTIONS_AAB0011| MEASURES_AAB0011
		AAB0012 | ASSERTIONS_AAB0012| MEASURES_AAB0012
		AAB0013 | ASSERTIONS_AAB0013| MEASURES_AAB0013
		AAB0014 | ASSERTIONS_AAB0014| MEASURES_AAB0014

	}
}
