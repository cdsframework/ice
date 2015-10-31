package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_URI_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2605: [num: -1, denom: -1]]
	
    private static final String URI0001 = "src/test/resources/samples/hedis-uri/SampleURI0001.xml" 
	//huge vMR with lots of encounters with corresponding exclusions. Only one encounter should stay in the list.
	private static final Map ASSERTIONS_URI0001 = [C1527:'', C3216:'', C539: '', C54:  '', C545: '', IESD:'2011-02-13 11:50:00']
    private static final Map MEASURES_URI0001  = [C2605: [num: 1, denom: 1]]

	
    private static final String URI0002 = "src/test/resources/samples/hedis-uri/SampleURI0002.xml" 
	//Denominator met: 1 outpatient encounter with URI followed by antibiotic dispensation event 
	private static final Map ASSERTIONS_URI0002 = [C1527:'', C3216:'', C539: '', C54:  '', C545: '', IESD:'2011-02-23 11:50:00']
	private static final Map MEASURES_URI0002  = [C2605: [num: 1, denom: 1]]
	
    private static final String URI0003 = "src/test/resources/samples/hedis-uri/SampleURI0003.xml" 
	//Denominator not met: 2 outpatient encounters with URI followed by antibiotic dispensation event in the later one
	private static final Map ASSERTIONS_URI0003 = [C3216:'', C54:  '', C545: '', IESD:'2011-01-23 11:50:00']
	private static final Map MEASURES_URI0003  = [C2605: [num: 0, denom: 1]]

	
    private static final String URI0004 = "src/test/resources/samples/hedis-uri/SampleURI0004.xml" 
	//Denom not met: encounter with multiple diagnosis
	private static final Map ASSERTIONS_URI0004 = [C3216:'', C545: '']
	private static final Map MEASURES_URI0004  = [C2605: [num: 0, denom: 0]]
	
	private static final String URI0005 = "src/test/resources/samples/hedis-uri/SampleURI0005.xml" 
	//Exclusions check: active medication
	private static final Map ASSERTIONS_URI0005 = [C3216:'', C545: '']
	private static final Map MEASURES_URI0005  = [C2605: [num: 0, denom: 0]]

	
	private static final String URI0006 = "src/test/resources/samples/hedis-uri/SampleURI0006.xml" 
	//Exclusion check: dispensed medication
	private static final Map ASSERTIONS_URI0006 = [C3216:'', C545: '']
	private static final Map MEASURES_URI0006  = [C2605: [num: 0, denom: 0]]
	
	private static final String URI0007 = "src/test/resources/samples/hedis-uri/SampleURI0007.xml" 
	//Exclusion check: competing diagnosis
	private static final Map ASSERTIONS_URI0007 = [C3216:'', C545: '']
	private static final Map MEASURES_URI0007  = [C2605: [num: 0, denom: 0]]

/*
Concepts used:


*/
	
	@Unroll
	def "test HEDIS URI v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_URI', version: '2014.1.0'],
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
		URI0001 | ASSERTIONS_URI0001| MEASURES_URI0001
		URI0002 | ASSERTIONS_URI0002| MEASURES_URI0002
		URI0003 | ASSERTIONS_URI0003| MEASURES_URI0003
		URI0004 | ASSERTIONS_URI0004| MEASURES_URI0004
		URI0005 | ASSERTIONS_URI0005| MEASURES_URI0005
		URI0006 | ASSERTIONS_URI0006| MEASURES_URI0006
		URI0007 | ASSERTIONS_URI0007| MEASURES_URI0007

	}
}
