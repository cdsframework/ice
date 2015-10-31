package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_AAB_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_AAB0001 = "src/test/resources/samples/hedis-aab/SampleAAB0001.xml"
	private static final String SAMPLE_AAB0002 = "src/test/resources/samples/hedis-aab/SampleAAB0002.xml"
	private static final String SAMPLE_AAB0003 = "src/test/resources/samples/hedis-aab/SampleAAB0003.xml"
	private static final String SAMPLE_AAB0004 = "src/test/resources/samples/hedis-aab/SampleAAB0004.xml"
	private static final String SAMPLE_AAB0005 = "src/test/resources/samples/hedis-aab/SampleAAB0005.xml"
	private static final String SAMPLE_AAB0006 = "src/test/resources/samples/hedis-aab/SampleAAB0006.xml"
	private static final String SAMPLE_AAB0007 = "src/test/resources/samples/hedis-aab/SampleAAB0007.xml"
	private static final String SAMPLE_AAB0008 = "src/test/resources/samples/hedis-aab/SampleAAB0008.xml"
	private static final String SAMPLE_AAB0009 = "src/test/resources/samples/hedis-aab/SampleAAB0009.xml"
	private static final String SAMPLE_AAB0010 = "src/test/resources/samples/hedis-aab/SampleAAB0010.xml"
	private static final String SAMPLE_AAB0011 = "src/test/resources/samples/hedis-aab/SampleAAB0011.xml"
	private static final String SAMPLE_AAB0012 = "src/test/resources/samples/hedis-aab/SampleAAB0012.xml"
	private static final String SAMPLE_AAB0013 = "src/test/resources/samples/hedis-aab/SampleAAB0013.xml"
	private static final String SAMPLE_AAB0014 = "src/test/resources/samples/hedis-aab/SampleAAB0014.xml"
	@Unroll
	def "test HEDIS AAB"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_AAB', version: '2014.0.0'],
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
		SAMPLE_AAB0001 | [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAB0002 | [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-10-23 11:50:00', IESD_PLUS_3D:'2011-10-26 11:50:00',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAB0003 | [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-10-23 11:50:00', IESD_PLUS_3D:'2011-10-26 11:50:00',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAB0004 | [C3199:'', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_AAB0005 | [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAB0006 | [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAB0007 | [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAB0008 | [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAB0009 | [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAB0010 | [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAB0011 | [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAB0012 | [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAB0013 | [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-12-13 11:50:00', IESD_PLUS_3D:'2011-12-16 11:50:00',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAB0014 | [C1527:'', C3199:'', C539: '', C54:  '', C545: '', IESD:'2011-10-23 11:50:00', IESD_PLUS_3D:'2011-10-26 11:50:00',numMet: '', wrapper : ''] | '1' | '1'
		}
}