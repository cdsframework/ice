package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_CHL_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_CHL0001 = "src/test/resources/samples/hedis-chl/SampleCHL0001.xml"
	private static final String SAMPLE_CHL0002 = "src/test/resources/samples/hedis-chl/SampleCHL0002.xml"
	private static final String SAMPLE_CHL0003 = "src/test/resources/samples/hedis-chl/SampleCHL0003.xml"
	private static final String SAMPLE_CHL0004 = "src/test/resources/samples/hedis-chl/SampleCHL0004.xml"
	private static final String SAMPLE_CHL0005 = "src/test/resources/samples/hedis-chl/SampleCHL0005.xml"
	private static final String SAMPLE_CHL0006 = "src/test/resources/samples/hedis-chl/SampleCHL0006.xml"
	private static final String SAMPLE_CHL0007 = "src/test/resources/samples/hedis-chl/SampleCHL0007.xml"
	private static final String SAMPLE_CHL0008 = "src/test/resources/samples/hedis-chl/SampleCHL0008.xml"
	private static final String SAMPLE_CHL0009 = "src/test/resources/samples/hedis-chl/SampleCHL0009.xml"
	private static final String SAMPLE_CHL0010 = "src/test/resources/samples/hedis-chl/SampleCHL0010.xml"
	private static final String SAMPLE_CHL0011 = "src/test/resources/samples/hedis-chl/SampleCHL0011.xml"
	private static final String SAMPLE_CHL0012 = "src/test/resources/samples/hedis-chl/SampleCHL0012.xml"
	private static final String SAMPLE_CHL0013 = "src/test/resources/samples/hedis-chl/SampleCHL0013.xml"
	private static final String SAMPLE_CHL0014 = "src/test/resources/samples/hedis-chl/SampleCHL0014.xml"
	private static final String SAMPLE_CHL0015 = "src/test/resources/samples/hedis-chl/SampleCHL0015.xml"
	private static final String SAMPLE_CHL0016 = "src/test/resources/samples/hedis-chl/SampleCHL0016.xml"
	private static final String SAMPLE_CHL0017 = "src/test/resources/samples/hedis-chl/SampleCHL0017.xml"
	private static final String SAMPLE_CHL0018 = "src/test/resources/samples/hedis-chl/SampleCHL0018.xml"
	private static final String SAMPLE_CHL0019 = "src/test/resources/samples/hedis-chl/SampleCHL0019.xml"
	private static final String SAMPLE_CHL0020 = "src/test/resources/samples/hedis-chl/SampleCHL0020.xml"
	private static final String SAMPLE_CHL0021 = "src/test/resources/samples/hedis-chl/SampleCHL0021.xml"
	private static final String SAMPLE_CHL0022 = "src/test/resources/samples/hedis-chl/SampleCHL0022.xml"
	private static final String SAMPLE_CHL0023 = "src/test/resources/samples/hedis-chl/SampleCHL0023.xml"
	@Unroll
	def "test HEDIS W34 Well Child 3-6 years"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CHL', version: '2014.0.0'],
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
		SAMPLE_CHL0001 | [C1468:'', C1651:'', C1693:'', C2519:'', C2520:'', C2766:'', C2836:'', C31: '', C539:'', C54: '',C545:'',C844:'', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_CHL0002 | [C1468:'', C2519:'', C2836:'', C2837:'', C31:  '', C54:  '', C545: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CHL0003 | [C1468:'', C2520:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CHL0004 | [C1468:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CHL0005 | [C1468:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CHL0006 | [C1468:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CHL0007 | [C1468:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CHL0008 | [C1468:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CHL0009 | [C1468:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CHL0010 | [C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', C844: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CHL0011 | [C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CHL0012 | [C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CHL0013 | [C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CHL0014 | [C1651:'', C2519:'', C2766:'', C2836:'', C31:  '', C539: '', C54:  '', C545:'', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_CHL0015 | [C1651:'', C2519:'', C2766:'', C2836:'', C31:  '', C539: '', C54:  '', C545:'', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_CHL0016 | [C1693:'', C2519:'', C2766:'', C2836:'', C2841:'', C31:  '', C544: '', C545:'',  denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_CHL0017 | [C1693:'', C2519:'', C2766:'', C2836:'', C2841:'', C31:  '', C544: '', C545:'',  denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_CHL0018 | [C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CHL0019 | [C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CHL0020 | [C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_CHL0021 | [C1651:'', C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C539: '', C54: '', C545:'', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_CHL0022 | [C1651:'', C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C539: '', C54: '', C545:'', numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_CHL0023 | [C1693:'', C2519:'', C2766:'', C2836:'', C31:  '', C54:  '', C545: '', numNotMet: '', wrapper : ''] | '1' | '0'
		}
}