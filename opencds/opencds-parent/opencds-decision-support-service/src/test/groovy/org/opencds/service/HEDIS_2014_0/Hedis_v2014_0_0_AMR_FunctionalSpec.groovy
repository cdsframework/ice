package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_AMR_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_AMR0001 = "src/test/resources/samples/hedis-amr/SampleAMR0001.xml"
	private static final String SAMPLE_AMR0002 = "src/test/resources/samples/hedis-amr/SampleAMR0002.xml"
	private static final String SAMPLE_AMR0003 = "src/test/resources/samples/hedis-amr/SampleAMR0003.xml"
	private static final String SAMPLE_AMR0004 = "src/test/resources/samples/hedis-amr/SampleAMR0004.xml"
	private static final String SAMPLE_AMR0005 = "src/test/resources/samples/hedis-amr/SampleAMR0005.xml"
	private static final String SAMPLE_AMR0006 = "src/test/resources/samples/hedis-amr/SampleAMR0006.xml"
	private static final String SAMPLE_AMR0007 = "src/test/resources/samples/hedis-amr/SampleAMR0007.xml"
	private static final String SAMPLE_AMR0008 = "src/test/resources/samples/hedis-amr/SampleAMR0008.xml"
	private static final String SAMPLE_AMR0009 = "src/test/resources/samples/hedis-amr/SampleAMR0009.xml"
	private static final String SAMPLE_AMR0010 = "src/test/resources/samples/hedis-amr/SampleAMR0010.xml"
	private static final String SAMPLE_AMR0011 = "src/test/resources/samples/hedis-amr/SampleAMR0011.xml"
	private static final String SAMPLE_AMR0012 = "src/test/resources/samples/hedis-amr/SampleAMR0012.xml"
	private static final String SAMPLE_AMR0013 = "src/test/resources/samples/hedis-amr/SampleAMR0013.xml"
	private static final String SAMPLE_AMR0014 = "src/test/resources/samples/hedis-amr/SampleAMR0014.xml"
	private static final String SAMPLE_AMR0015 = "src/test/resources/samples/hedis-amr/SampleAMR0015.xml"
	private static final String SAMPLE_AMR0019 = "src/test/resources/samples/hedis-amr/SampleAMR0019.xml"

	@Unroll
	def "test HEDIS AMR"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_AMR', version: '2014.0.0'],
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
		SAMPLE_AMR0001 | [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C539:'',C54:'',C545:'', Controllers:'2',Relievers:'1',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_AMR0002 | [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C539:'',C54:'',C545:'',Controllers:'4',Relievers:'3',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_AMR0003 | [C2772:'',C3265:'',C3268:'',C3269:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_AMR0004 | [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C3272:'',C539:'',C54:'',C545:'',Controllers:'5',Relievers:'4',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_AMR0005 | [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C3271:'',C3272:'',Controllers:'5',Relievers:'0',numMet:'',wrapper:'']| '1' | '1'
		SAMPLE_AMR0006 | [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C3271:'',C3272:'',C539:'',C54:'',C545:'',Controllers:'5',Relievers:'1',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_AMR0007 | [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C3271:'',C3272:'',C539:'',C54:'',C545:'',Controllers:'6',Relievers:'1',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_AMR0008 | [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C3271:'',C3272:'',C539:'',C54:'',C545:'',numMet:'',wrapper:'']| '1' | '1'
		SAMPLE_AMR0009 | [denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_AMR0010 | [C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C3271:'',C3272:'',C539:'',C54:'',C545:'',Controllers:'7',Relievers:'1',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_AMR0011 | [C1938:'',C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_AMR0012 | [C1938:'',C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C544:'',C545:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_AMR0013 | [C2789:'',C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C544:'',C545:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_AMR0014 | [C42:'',C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C544:'',C545:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_AMR0015 | [C2788:'',C2772:'',C3265:'',C3268:'',C3269:'',C3270:'',C544:'',C545:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_AMR0019 | [C2773:'',C3265:'',C3268:'',C3269:'',C3270:'',C544:'',C545:'',denomNotMet:'',wrapper:'']| '0' | '0'

		}
}


