package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_AAP_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
    private static final String SAMPLE_AAP0001 = "src/test/resources/samples/hedis-aap/SampleAAP0001.xml"
	private static final String SAMPLE_AAP0002 = "src/test/resources/samples/hedis-aap/SampleAAP0002.xml"
	private static final String SAMPLE_AAP0003 = "src/test/resources/samples/hedis-aap/SampleAAP0003.xml"
	private static final String SAMPLE_AAP0004 = "src/test/resources/samples/hedis-aap/SampleAAP0004.xml"
	private static final String SAMPLE_AAP0005 = "src/test/resources/samples/hedis-aap/SampleAAP0005.xml"
	private static final String SAMPLE_AAP0006 = "src/test/resources/samples/hedis-aap/SampleAAP0006.xml"
	private static final String SAMPLE_AAP0007 = "src/test/resources/samples/hedis-aap/SampleAAP0007.xml"
	private static final String SAMPLE_AAP0008 = "src/test/resources/samples/hedis-aap/SampleAAP0008.xml"
	private static final String SAMPLE_AAP0009 = "src/test/resources/samples/hedis-aap/SampleAAP0009.xml"
	private static final String SAMPLE_AAP0010 = "src/test/resources/samples/hedis-aap/SampleAAP0010.xml"
	private static final String SAMPLE_AAP0011 = "src/test/resources/samples/hedis-aap/SampleAAP0011.xml"
	private static final String SAMPLE_AAP0012 = "src/test/resources/samples/hedis-aap/SampleAAP0012.xml"
	private static final String SAMPLE_AAP0013 = "src/test/resources/samples/hedis-aap/SampleAAP0013.xml"
	@Unroll
    def "test HEDIS AAP"() {
        when:
        def input = new File(vmr).text
        def params = [
            kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_AAP', version: '2014.0.0'],
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
		SAMPLE_AAP0001 | [C2746: '', C2857: '', C2858: '', C2859: '',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAP0002 | [C2746: '', C2857: '', C2859: '',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAP0003 | [C2746: '', C2857: '', C2859: '',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAP0004 | [C2746: '', C2857: '', C2859: '',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAP0005 | [C2746: '', C2857: '', C2859: '',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAP0006 | [C2746: '', C2857: '', C2859: '',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAP0007 | [C2746: '', C2858: '', C2859: '',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAP0008 | [C2746: '', C2858: '', C2859: '',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAP0009 | [C2746: '', C2858: '', C2859: '',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAP0010 | [denomNotMet: '', wrapper : ''] | '0' | '0'
		SAMPLE_AAP0011 | [C2747: '', C2857: '', C2858: '',C2859: '',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		SAMPLE_AAP0012 | [C2746:'', C2859:'',  C54:'',C545:'', numNotMet: '', wrapper : ''] | '1' | '0'
		SAMPLE_AAP0013 | [C2608: '', C2857: '', C2858: '', C2859: '',C539:'',C54:'',C545:'',numMet: '', wrapper : ''] | '1' | '1'
		}
}