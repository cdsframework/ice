package org.opencds.service;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class CDS_Outpatient_1MonthFunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_Outpatient0001 = "src/test/resources/samples/cds-outpatient-1month/SampleCDSOutpatient0001.xml"
	private static final String SAMPLE_Outpatient0002 = "src/test/resources/samples/cds-outpatient-1month/SampleCDSOutpatient0002.xml"
	private static final String SAMPLE_Outpatient0003 = "src/test/resources/samples/cds-outpatient-1month/SampleCDSOutpatient0003.xml"
	private static final String SAMPLE_Outpatient0004 = "src/test/resources/samples/cds-outpatient-1month/SampleCDSOutpatient0004.xml"
	@Unroll
	def "test CDS Outpatient 1 Month"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'CDS_Outpatient_PastMonth', version: '1.0.0'],
			specifiedTime: '2012-01-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)

	   then:
		def data = new XmlSlurper().parseText(responsePayload)
        def results = VMRUtil.getResults(data, '\\|')
		//assertions.size() == results.assertions.size()
		results.assertions.each {entry ->
			System.err.println "${entry.key} -> ${entry.value}"
		}
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
		SAMPLE_ALL0001 | [wrapper : ''] | '0' | '0'
		SAMPLE_Outpatient0001 | [C44:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_Outpatient0002 | [wrapper : ''] | '0' | '0'
		SAMPLE_Outpatient0003 | [wrapper : ''] | '0' | '0'
		SAMPLE_Outpatient0004 | [wrapper : ''] | '0' | '0'
		}
}