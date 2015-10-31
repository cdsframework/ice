package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_LBP_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_LBP0001 = "src/test/resources/samples/hedis-lbp/SampleLBP0001.xml"
	private static final String SAMPLE_LBP0002 = "src/test/resources/samples/hedis-lbp/SampleLBP0002.xml"
	private static final String SAMPLE_LBP0003 = "src/test/resources/samples/hedis-lbp/SampleLBP0003.xml"
	private static final String SAMPLE_LBP0004 = "src/test/resources/samples/hedis-lbp/SampleLBP0004.xml"
	private static final String SAMPLE_LBP0005 = "src/test/resources/samples/hedis-lbp/SampleLBP0005.xml"
	private static final String SAMPLE_LBP0006 = "src/test/resources/samples/hedis-lbp/SampleLBP0006.xml"
	private static final String SAMPLE_LBP0007 = "src/test/resources/samples/hedis-lbp/SampleLBP0007.xml"
	private static final String SAMPLE_LBP0008 = "src/test/resources/samples/hedis-lbp/SampleLBP0008.xml"
	private static final String SAMPLE_LBP0009 = "src/test/resources/samples/hedis-lbp/SampleLBP0009.xml"
	private static final String SAMPLE_LBP0010 = "src/test/resources/samples/hedis-lbp/SampleLBP0010.xml"
	private static final String SAMPLE_LBP0011 = "src/test/resources/samples/hedis-lbp/SampleLBP0011.xml"
	private static final String SAMPLE_LBP0012 = "src/test/resources/samples/hedis-lbp/SampleLBP0012.xml"
	private static final String SAMPLE_LBP0013 = "src/test/resources/samples/hedis-lbp/SampleLBP0013.xml"
	private static final String SAMPLE_LBP0014 = "src/test/resources/samples/hedis-lbp/SampleLBP0014.xml"
	private static final String SAMPLE_LBP0015 = "src/test/resources/samples/hedis-lbp/SampleLBP0015.xml"
	private static final String SAMPLE_LBP0016 = "src/test/resources/samples/hedis-lbp/SampleLBP0016.xml"
	private static final String SAMPLE_LBP0017 = "src/test/resources/samples/hedis-lbp/SampleLBP0017.xml"
	private static final String SAMPLE_LBP0018 = "src/test/resources/samples/hedis-lbp/SampleLBP0018.xml"
	private static final String SAMPLE_LBP0019 = "src/test/resources/samples/hedis-lbp/SampleLBP0019.xml"
	private static final String SAMPLE_LBP0020 = "src/test/resources/samples/hedis-lbp/SampleLBP0020.xml"
	private static final String SAMPLE_LBP0021 = "src/test/resources/samples/hedis-lbp/SampleLBP0021.xml"
	private static final String SAMPLE_LBP0022 = "src/test/resources/samples/hedis-lbp/SampleLBP0022.xml"
	private static final String SAMPLE_LBP0023 = "src/test/resources/samples/hedis-lbp/SampleLBP0023.xml"
	private static final String SAMPLE_LBP0024 = "src/test/resources/samples/hedis-lbp/SampleLBP0024.xml"
	@Unroll
	def "test HEDIS LBP Low Back Pain Imaging Studies"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_LBP', version: '2014.0.0'],
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
		SAMPLE_LBP0001 | [C2050:'', C2772:'', C2876:'', C2878:'', C288: '', C544: '', C545: '', C946: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_LBP0002 | [C2772:'', C54:  '', C545: '', C946: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_LBP0003 | [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_LBP0004 | [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_LBP0005 | [C2772:'', C54:  '', C545: '', C946: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_LBP0006 | [C2772:'', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_LBP0007 | [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_LBP0008 | [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_LBP0009 | [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_LBP0010 | [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_LBP0011 | [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_LBP0012 | [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_LBP0013 | [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_LBP0014 | [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_LBP0015 | [C2772:'', C2878:'', C544: '', C545: '', C946: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_LBP0016 | [C2772:'', C2050:'', C544: '', C545: '', C946: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_LBP0017 | [C2772:'', C2876:'', C544: '', C545: '', C946: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_LBP0018 | [C2772:'', C2872:'', C544: '', C545: '', C946: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_LBP0019 | [C2772:'', C2873:'', C544: '', C545: '', C946: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_LBP0020 | [C2772:'', C288: '', C544: '', C545: '', C946: '', denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_LBP0021 | [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_LBP0022 | [C2772:'', C2871:'', C539: '', C54:  '', C545: '', C946: '', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_LBP0023 | [denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_LBP0024 | [denomNotMet: '', wrapper : ''] | '0' | '0'
		}
}