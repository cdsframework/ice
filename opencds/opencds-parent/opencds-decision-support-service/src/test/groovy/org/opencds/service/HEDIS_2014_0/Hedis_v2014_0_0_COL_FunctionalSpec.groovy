package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_COL_FunctionalSpec extends Specification {
	
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-awc/SampleALL0001.xml"
    private static final String SAMPLE_COL0001 = "src/test/resources/samples/hedis-col/SampleCOL0001.xml"
    private static final String SAMPLE_COL0002 = "src/test/resources/samples/hedis-col/SampleCOL0002.xml"
    private static final String SAMPLE_COL0003 = "src/test/resources/samples/hedis-col/SampleCOL0003.xml"
    private static final String SAMPLE_COL0004 = "src/test/resources/samples/hedis-col/SampleCOL0004.xml"
    private static final String SAMPLE_COL0005 = "src/test/resources/samples/hedis-col/SampleCOL0005.xml"
    private static final String SAMPLE_COL0006 = "src/test/resources/samples/hedis-col/SampleCOL0006.xml"
    private static final String SAMPLE_COL0007 = "src/test/resources/samples/hedis-col/SampleCOL0007.xml"
    private static final String SAMPLE_COL0008 = "src/test/resources/samples/hedis-col/SampleCOL0008.xml"
    private static final String SAMPLE_COL0009 = "src/test/resources/samples/hedis-col/SampleCOL0009.xml"
    private static final String SAMPLE_COL0010 = "src/test/resources/samples/hedis-col/SampleCOL0010.xml"
    private static final String SAMPLE_COL0011 = "src/test/resources/samples/hedis-col/SampleCOL0011.xml"
    private static final String SAMPLE_COL0012 = "src/test/resources/samples/hedis-col/SampleCOL0012.xml"
    private static final String SAMPLE_COL0013 = "src/test/resources/samples/hedis-col/SampleCOL0013.xml"
    private static final String SAMPLE_COL0014 = "src/test/resources/samples/hedis-col/SampleCOL0014.xml"
    private static final String SAMPLE_COL0015 = "src/test/resources/samples/hedis-col/SampleCOL0015.xml"
    private static final String SAMPLE_COL0016 = "src/test/resources/samples/hedis-col/SampleCOL0016.xml"
    private static final String SAMPLE_COL0017 = "src/test/resources/samples/hedis-col/SampleCOL0017.xml"
    private static final String SAMPLE_COL0018 = "src/test/resources/samples/hedis-col/SampleCOL0018.xml"
    private static final String SAMPLE_COL0019 = "src/test/resources/samples/hedis-col/SampleCOL0019.xml"
    private static final String SAMPLE_COL0020 = "src/test/resources/samples/hedis-col/SampleCOL0020.xml"
    private static final String SAMPLE_COL0021 = "src/test/resources/samples/hedis-col/SampleCOL0021.xml"
    private static final String SAMPLE_COL0022 = "src/test/resources/samples/hedis-col/SampleCOL0022.xml"
    private static final String SAMPLE_COL0023 = "src/test/resources/samples/hedis-col/SampleCOL0023.xml"
    private static final String SAMPLE_COL0024 = "src/test/resources/samples/hedis-col/SampleCOL0024.xml"
    
    @Unroll
    def "test HEDIS ColoRectal"() {
        when:
        def input = new File(vmr).text
        def params = [
            kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_COL', version: '2014.0.0'],
            specifiedTime: '2012-02-01'
        ]
        def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)

       then:
        def data = new XmlSlurper().parseText(responsePayload)
        def results = VMRUtil.getResults(data)
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
        SAMPLE_COL0001 | [denomNotMet: '', wrapper : ''] | '0' | '0'
        SAMPLE_COL0002 | [denomNotMet: '', wrapper : ''] | '0' | '0'
        SAMPLE_COL0003 | [C2707: '', C54: '', C545: '', numNotMet: '', wrapper : '']  | '1' | '0'
        SAMPLE_COL0004 | [C2707: '', C2708: '', C544: '', C545: '', denomNotMet: '', wrapper: ''] | '0' | '0'
        SAMPLE_COL0005 | [C2707: '', C2708: '', C544: '', C545: '', denomNotMet: '', wrapper: ''] | '0' | '0'
        SAMPLE_COL0006 | [C2707: '', C2709: '', C544: '', C545: '', denomNotMet: '', wrapper: ''] | '0' | '0'
        SAMPLE_COL0007 | [C2707: '', C2709: '', C544: '', C545: '', denomNotMet: '', wrapper: ''] | '0' | '0'
        SAMPLE_COL0008 | [C2514: '', C2707: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
        SAMPLE_COL0009 | [C2514: '', C2707: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
        SAMPLE_COL0010 | [C2514: '', C2707: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
        SAMPLE_COL0011 | [C2514: '', C2707: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
        SAMPLE_COL0012 | [C2707: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
        SAMPLE_COL0013 | [C2738: '', C2707: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
        SAMPLE_COL0014 | [C2738: '', C2707: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
        SAMPLE_COL0015 | [C2707: '', C2738: '',  C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
        SAMPLE_COL0016 | [C2707: '', C2738: '',  C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
        SAMPLE_COL0017 | [C2707: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
        SAMPLE_COL0018 | [C2739: '', C2707: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
        SAMPLE_COL0019 | [C2739: '', C2707: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
        SAMPLE_COL0020 | [C2739: '', C2707: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
        SAMPLE_COL0021 | [C2707: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
		SAMPLE_COL0022 | [C2739: '', C2707: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_COL0023 | [C2739: '', C2707: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_COL0024 | [C2707: '', C54: '', C545: '', numNotMet: '', wrapper: ''] | '1' | '0'
    }

}