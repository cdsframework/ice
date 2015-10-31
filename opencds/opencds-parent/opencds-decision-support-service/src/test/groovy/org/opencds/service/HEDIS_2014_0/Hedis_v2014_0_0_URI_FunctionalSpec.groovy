package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_URI_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_URI0001 = "src/test/resources/samples/hedis-uri/SampleURI0001.xml"
	private static final String SAMPLE_URI0002 = "src/test/resources/samples/hedis-uri/SampleURI0002.xml"
	private static final String SAMPLE_URI0003 = "src/test/resources/samples/hedis-uri/SampleURI0003.xml"
	private static final String SAMPLE_URI0004 = "src/test/resources/samples/hedis-uri/SampleURI0004.xml"
	private static final String SAMPLE_URI0005 = "src/test/resources/samples/hedis-uri/SampleURI0005.xml"
	private static final String SAMPLE_URI0006 = "src/test/resources/samples/hedis-uri/SampleURI0006.xml"
	private static final String SAMPLE_URI0007 = "src/test/resources/samples/hedis-uri/SampleURI0007.xml"
	@Unroll
	def "test HEDIS URI"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_URI', version: '2014.0.0'],
			specifiedTime: '2012-02-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)

	   then:
		def data = new XmlSlurper().parseText(responsePayload)
        def results = VMRUtil.getResults(data, '\\|')
		assertions.size() == results.assertions.size()
		assertions.each {entry ->
			assert results.assertions.containsKey(entry.key)
			if (entry.value) {
				assert results.assertions.get(entry.key) == entry.value
			}
		}
		numerator == results.measure.numerator
		denominator == results.measure.denominator
		
		where:
		vmr | assertions | denominator | numerator
		SAMPLE_ALL0001 | [C529: '', C569: '', reject: '', wrapper : ''] | '0' | '0'  
		//huge vMR with lots of encounters with corresponding exclusions. Only one encounter should stay in the list.
		SAMPLE_URI0001 | [C1527:'', C3216:'', C539: '', C54:  '', C545: '', numMet: '',IESD:'2011-02-13 11:50:00',  wrapper : ''] | '1' | '1' 
		//Denominator met: 1 outpatient encounter with URI followed by antibiotic dispensation event 
		SAMPLE_URI0002 | [C1527:'', C3216:'', C539: '', C54:  '', C545: '', numMet: '',IESD:'2011-02-23 11:50:00',  wrapper : ''] | '1' | '1'
		//Denominator not met: 2 outpatient encounters with URI followed by antibiotic dispensation event in the later one
		SAMPLE_URI0003 | [C3216:'', C54:  '', C545: '', IESD:'2011-01-23 11:50:00', numNotMet: '', wrapper : ''] | '1' | '0'
		//Denom not met: encounter with multiple diagnosis
		SAMPLE_URI0004 | [C3216:'', C545: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		//Exclusions check: active medication
		SAMPLE_URI0005 | [C3216:'', C545: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		//Exclusion check: dispensed medication
		SAMPLE_URI0006 | [C3216:'', C545: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		//Exclusion check: competing diagnosis
		SAMPLE_URI0007 | [C3216:'', C545: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		}
}