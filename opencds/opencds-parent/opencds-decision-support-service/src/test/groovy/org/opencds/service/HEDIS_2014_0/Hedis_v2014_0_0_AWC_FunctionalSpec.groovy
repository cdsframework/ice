package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_AWC_FunctionalSpec extends Specification {
	
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-awc/SampleALL0001.xml"
    private static final String SAMPLE_AWC0001 = "src/test/resources/samples/hedis-awc/SampleAWC0001.xml"
    private static final String SAMPLE_AWC0002 = "src/test/resources/samples/hedis-awc/SampleAWC0002.xml"
    private static final String SAMPLE_AWC0003 = "src/test/resources/samples/hedis-awc/SampleAWC0003.xml"
    private static final String SAMPLE_AWC0004 = "src/test/resources/samples/hedis-awc/SampleAWC0004.xml"
    private static final String SAMPLE_AWC0005 = "src/test/resources/samples/hedis-awc/SampleAWC0005.xml"
    private static final String SAMPLE_AWC0006 = "src/test/resources/samples/hedis-awc/SampleAWC0006.xml"
    private static final String SAMPLE_AWC0007 = "src/test/resources/samples/hedis-awc/SampleAWC0007.xml"
    private static final String SAMPLE_AWC0008 = "src/test/resources/samples/hedis-awc/SampleAWC0008.xml"
    private static final String SAMPLE_AWC0009 = "src/test/resources/samples/hedis-awc/SampleAWC0009.xml"
	
	@Unroll
	def "test HEDIS AWC"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_AWC', version: '2014.0.0'],
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
		SAMPLE_AWC0001 | [C2679: '', C2680: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_AWC0002 | [C2679: '', C2680: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_AWC0003 | [C2679: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_AWC0004 | [denomNotMet: '', wrapper: ''] | '0' | '0'
		SAMPLE_AWC0005 | [denomNotMet: '', wrapper: ''] | '0' | '0'
		SAMPLE_AWC0006 | [C2679: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_AWC0007 | [C2679: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_AWC0008 | [C2679: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_AWC0009 | [C2679: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
    }
}
