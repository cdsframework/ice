package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_ASM_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_ASM0001 = "src/test/resources/samples/hedis-asm/SampleASM0001.xml"
	private static final String SAMPLE_ASM0002 = "src/test/resources/samples/hedis-asm/SampleASM0002.xml"
	private static final String SAMPLE_ASM0003 = "src/test/resources/samples/hedis-asm/SampleASM0003.xml"
	private static final String SAMPLE_ASM0004 = "src/test/resources/samples/hedis-asm/SampleASM0004.xml"
	private static final String SAMPLE_ASM0005 = "src/test/resources/samples/hedis-asm/SampleASM0005.xml"
	private static final String SAMPLE_ASM0006 = "src/test/resources/samples/hedis-asm/SampleASM0006.xml"
	private static final String SAMPLE_ASM0007 = "src/test/resources/samples/hedis-asm/SampleASM0007.xml"
	private static final String SAMPLE_ASM0008 = "src/test/resources/samples/hedis-asm/SampleASM0008.xml"
	private static final String SAMPLE_ASM0009 = "src/test/resources/samples/hedis-asm/SampleASM0009.xml"
	private static final String SAMPLE_ASM0010 = "src/test/resources/samples/hedis-asm/SampleASM0010.xml"

	@Unroll
	def "test HEDIS ASM"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_ASM', version: '2014.0.0'],
			specifiedTime: '2012-02-01'
//			specifiedTime: '2014-01-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
		println responsePayload

	   then:
		def data = new XmlSlurper().parseText(responsePayload)
        def results = VMRUtil.getResults(data, '\\|')
//		measureFocus.size() == results.measureFocus.size()
//		measureFocus.each {String focus -> 
//			assert results.measureFocus.contains(focus)
//		}
//		assertions.size() == results.assertions.size()
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
		
/*
C2772=Patient Age GE 19 and LT 51 Years
C3265=Named Dates Inserted
C3268=Patient Age GE 5 and LT 65 Years
C3269=Asthma Criterion Met, One Year Ago
C3270=Asthma Criterion Met, Two Years Ago
C539=Numerator Criteria Met
C54=Denominator Criteria Met
C545=Denominator Inclusions Met
C3271=Asthma Criterion Met, Four Medications, Two Years Ago
C3272=Asthma Criterion Met, Four Medications, One Year Ago
C1938=Emphysema
C2789=Chronic Respiratory Conditions due to Fumes or Vapors
C42=Chronic Obstructive Pulmonary Disease (COPD)
C544=Denominator Exclusions Met
C2788=Bronchitis, Obstructive Chronic
*/		
		where:
		vmr | assertions | denominator | numerator
		SAMPLE_ALL0001 | [C529: '', C569: '', reject: '', wrapper : ''] | '0' | '0'
		SAMPLE_ASM0001 | [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C539:'', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_ASM0002 | [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C539:'', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_ASM0003 | [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C539:'', C54: '', C545: '', NonAcuteEncountersYr2:'4', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_ASM0004 | [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C539:'', C54: '', C545: '', NonAcuteEncountersYr1:'4', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_ASM0005 | [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C3271: '', C3272: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_ASM0006 | [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C3271: '', C3272: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_ASM0007 | [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C3271: '', C3272: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'
		SAMPLE_ASM0008 | [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C3271: '', C3272: '', C539: '', C54: '', C545: '', numMet: '', wrapper: '']| '1' | '1'
		SAMPLE_ASM0009 | [denomNotMet: '', wrapper: ''] | '0' | '0'
		SAMPLE_ASM0010 | [C2772: '', C3265: '', C3268: '', C3269: '', C3270: '', C3271: '', C3272: '', C539: '', C54: '', C545: '', numMet: '', wrapper: ''] | '1' | '1'

		}
}