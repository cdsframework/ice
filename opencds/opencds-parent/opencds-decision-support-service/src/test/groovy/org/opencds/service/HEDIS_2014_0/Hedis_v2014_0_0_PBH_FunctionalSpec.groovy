package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_PBH_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_PBH0001 = "src/test/resources/samples/hedis-pbh/SamplePBH0001.xml"
	private static final String SAMPLE_PBH0002 = "src/test/resources/samples/hedis-pbh/SamplePBH0002.xml"
	private static final String SAMPLE_PBH0003 = "src/test/resources/samples/hedis-pbh/SamplePBH0003.xml"
	private static final String SAMPLE_PBH0004 = "src/test/resources/samples/hedis-pbh/SamplePBH0004.xml"
	private static final String SAMPLE_PBH0005 = "src/test/resources/samples/hedis-pbh/SamplePBH0005.xml"
	private static final String SAMPLE_PBH0006 = "src/test/resources/samples/hedis-pbh/SamplePBH0006.xml"
	private static final String SAMPLE_PBH0007 = "src/test/resources/samples/hedis-pbh/SamplePBH0007.xml"
	private static final String SAMPLE_PBH0008 = "src/test/resources/samples/hedis-pbh/SamplePBH0008.xml"
	private static final String SAMPLE_PBH0009 = "src/test/resources/samples/hedis-pbh/SamplePBH0009.xml"
	private static final String SAMPLE_PBH0010 = "src/test/resources/samples/hedis-pbh/SamplePBH0010.xml"
	private static final String SAMPLE_PBH0011 = "src/test/resources/samples/hedis-pbh/SamplePBH0011.xml"
	private static final String SAMPLE_PBH0012 = "src/test/resources/samples/hedis-pbh/SamplePBH0012.xml"
	private static final String SAMPLE_PBH0013 = "src/test/resources/samples/hedis-pbh/SamplePBH0013.xml"
	private static final String SAMPLE_PBH0014 = "src/test/resources/samples/hedis-pbh/SamplePBH0014.xml"
	@Unroll
	def "test HEDIS PBH"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_PBH', version: '2014.0.0'],
			specifiedTime: '2012-02-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)

	   then:
		def data = new XmlSlurper().parseText(responsePayload)
        def results = VMRUtil.getResults(data, '\\|')
//		assertions.size() == results.assertions.size()
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
		SAMPLE_PBH0001 | [C2788: '', C2789: '', C3170: '', C341: '', C39: '', C42: '', C544: '', C545: '', DaysSupplied: '', denomNotMet: '', wrapper: ''] | '0' | '0'
		SAMPLE_PBH0002 | [C3170: '', C341: '', C539: '', C54: '', C545: '', DaysSupplied:'140',numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_PBH0003 | [C3170: '', C341: '', C54: '', C545: '', DaysSupplied: '130',numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_PBH0004 | [C3170: '', C341: '', C39: '', C544: '', C545: '', DaysSupplied:'140', denomNotMet: '', wrapper: ''] | '0' | '0'
		SAMPLE_PBH0005 | [C3170: '', C341: '', C2877: '', C544: '', C545: '', DaysSupplied:'140', denomNotMet: '', wrapper: ''] | '0' | '0'
		SAMPLE_PBH0006 | [C3170: '', C341: '', C2789: '', C544: '', C545: '', DaysSupplied:'140', denomNotMet: '', wrapper: ''] | '0' | '0'
		SAMPLE_PBH0007 | [C3170: '', C341: '', C42: '', C544: '', C545: '', DaysSupplied:'140', denomNotMet: '', wrapper: ''] | '0' | '0'
		SAMPLE_PBH0008 | [C3170: '', C341: '', C2788: '', C544: '', C545: '', DaysSupplied:'140', denomNotMet: '', wrapper: ''] | '0' | '0'
		SAMPLE_PBH0009 | [C3170: '', C341: '', C2854: '', C544: '', C545: '', DaysSupplied:'140', denomNotMet: '', wrapper: ''] | '0' | '0'
		SAMPLE_PBH0010 | [C3170: '', C341: '', C479: '', C54: '', C545: '', DaysSupplied:'121', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_PBH0011 | [C3170: '', C341: '', C3179: '', C54: '', C545: '', DaysSupplied:'121', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_PBH0012 | [C3170: '', C341: '', C478: '', C544: '', C545: '', DaysSupplied:'140',denomNotMet: '', wrapper: ''] | '0' | '0'
		SAMPLE_PBH0013 | [C3170: '', C341: '', C539: '', C54: '', C545: '', DaysSupplied:'140',numMet: '', wrapper: ''] | '1' | '1'
/*		SAMPLE_PBH0014 | [C3170: '', C341: '', C39: '', C539: '', C544: '', C545: '', DaysSupplied:'140', denomNotMet: '', wrapper: ''] | '0' | '0'*/
		}
}