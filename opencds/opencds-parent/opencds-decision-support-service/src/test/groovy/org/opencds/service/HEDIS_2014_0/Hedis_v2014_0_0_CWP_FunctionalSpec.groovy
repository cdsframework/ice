package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_CWP_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_CWP0001 = "src/test/resources/samples/hedis-cwp/SampleCWP0001.xml"
	private static final String SAMPLE_CWP0002 = "src/test/resources/samples/hedis-cwp/SampleCWP0002.xml"
	private static final String SAMPLE_CWP0003 = "src/test/resources/samples/hedis-cwp/SampleCWP0003.xml"
	private static final String SAMPLE_CWP0004 = "src/test/resources/samples/hedis-cwp/SampleCWP0004.xml"
	private static final String SAMPLE_CWP0005 = "src/test/resources/samples/hedis-cwp/SampleCWP0005.xml"
	private static final String SAMPLE_CWP0006 = "src/test/resources/samples/hedis-cwp/SampleCWP0006.xml"
	private static final String SAMPLE_CWP0007 = "src/test/resources/samples/hedis-cwp/SampleCWP0007.xml"
	private static final String SAMPLE_CWP0008 = "src/test/resources/samples/hedis-cwp/SampleCWP0008.xml"
	private static final String SAMPLE_CWP0009 = "src/test/resources/samples/hedis-cwp/SampleCWP0009.xml"
	private static final String SAMPLE_CWP0010 = "src/test/resources/samples/hedis-cwp/SampleCWP0010.xml"
	@Unroll
	def "test HEDIS CWP"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CWP', version: '2014.0.0'],
			specifiedTime: '2012-02-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
		println responsePayload

	   then:
		def data = new XmlSlurper().parseText(responsePayload)
        def results = VMRUtil.getResults(data, '\\|')
		//assertions.size() == results.assertions.size()
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
		SAMPLE_CWP0001 | [C2791:'',C539:'',C54:'',C545:'',IESD:'2011-02-13 11:50:00',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CWP0002 | [C2791:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CWP0003 | [C2791:'',C539:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CWP0004 | [C2791:'',C539:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CWP0005 | [C2791:'',C539:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CWP0006 | [C2791:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00',numNotMet:'',wrapper:'']| '1' | '0'
		SAMPLE_CWP0007 | [C2791:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CWP0008 | [C2791:'',C539:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00',numMet:'',wrapper:'']|'1' | '1'
		SAMPLE_CWP0009 | [C2791:'',C539:'',C54:'',C545:'',IESD:'2011-02-23 11:50:00',numMet:'',wrapper:'']| '1' | '1'
		SAMPLE_CWP0010 | [C2791:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		}
}
