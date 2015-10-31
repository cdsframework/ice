package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_CAP_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_CAP0001 = "src/test/resources/samples/hedis-cap/SampleCAP0001.xml"
	private static final String SAMPLE_CAP0002 = "src/test/resources/samples/hedis-cap/SampleCAP0002.xml"
	private static final String SAMPLE_CAP0003 = "src/test/resources/samples/hedis-cap/SampleCAP0003.xml"
	private static final String SAMPLE_CAP0004 = "src/test/resources/samples/hedis-cap/SampleCAP0004.xml"
	private static final String SAMPLE_CAP0005 = "src/test/resources/samples/hedis-cap/SampleCAP0005.xml"

	@Unroll
	def "test HEDIS CAP"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CAP', version: '2014.0.0'],
			specifiedTime: '2012-01-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
		println responsePayload

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
	
		
/*
C2742=Patient Age GE 7 and LT 12 Years
C2856=Patient Age GE 12 Months and LT 20 Years
C3287=Patient Age GE 7 and  LT 20 Years
C539=Numerator Criteria Met
C54=Denominator Criteria Met
C545=Denominator Inclusions Met
C2741=Patient Age GE 25 and LT 84 Months
C2740=Patient Age GE 12 and LT 25 Months
C2743=Patient Age GE 12 and LT 20 Years
C3286=Patient Age GE 12 Months and LT 7 Years
*/		
		
		where:
		vmr | assertions | denominator | numerator
		SAMPLE_ALL0001 | [C529: '', C569: '', reject: '', wrapper : ''] | '0' | '0'
		SAMPLE_CAP0001 | [C2742:'',C2856:'',C3287:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CAP0002 | [C2742:'',C2856:'',C3287:'',C539:'',C54:'',C545:'',numMet:'',wrapper:'']| '1' | '1'
		SAMPLE_CAP0003 | [C2741:'',C2856:'',C3286:'',C54:'',C545:'',numNotMet:'',wrapper:'']| '1' | '0'
		SAMPLE_CAP0004 | [C2740:'',C2856:'',C3286:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CAP0005 | [C2743:'',C2856:'',C3287:'',C539:'',C54:'',C545:'',numMet:'',wrapper:'']| '1' | '1'

		
		}
}


