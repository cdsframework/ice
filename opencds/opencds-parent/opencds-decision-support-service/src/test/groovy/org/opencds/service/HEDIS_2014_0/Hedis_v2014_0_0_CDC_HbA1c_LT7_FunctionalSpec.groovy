package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_CDC_HbA1c_LT7_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_CDC0001 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop01.xml"
	private static final String SAMPLE_CDC0002 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop02.xml"
	private static final String SAMPLE_CDC0003 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop03.xml"
	private static final String SAMPLE_CDC0004 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop04.xml"
	private static final String SAMPLE_CDC0005 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop05.xml"
	private static final String SAMPLE_CDC0006 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop06.xml"
	private static final String SAMPLE_CDC0007 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop07.xml"
	private static final String SAMPLE_CDC0008 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop08.xml"
	private static final String SAMPLE_CDC0009 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop09.xml"
	private static final String SAMPLE_CDC0010 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop10.xml"
	private static final String SAMPLE_CDC0011 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop11.xml"
	private static final String SAMPLE_CDC0012 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop12.xml"
	private static final String SAMPLE_CDC0013 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop13.xml"
	private static final String SAMPLE_CDC0014 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop14.xml"
	private static final String SAMPLE_CDC0015 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop15.xml"
	private static final String SAMPLE_CDC0016 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop16.xml"
	private static final String SAMPLE_CDC0017 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop17.xml"
	private static final String SAMPLE_CDC0018 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop18.xml"
	private static final String SAMPLE_CDC0019 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop19.xml"
	private static final String SAMPLE_CDC0020 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop20.xml"
	private static final String SAMPLE_CDC0021 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop21.xml"
	private static final String SAMPLE_CDC0022 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop22.xml"
	private static final String SAMPLE_CDC0023 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop23.xml"
	private static final String SAMPLE_CDC0024 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop24.xml"
	private static final String SAMPLE_CDC0025 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop25.xml"
	private static final String SAMPLE_CDC0026 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop26.xml"
	private static final String SAMPLE_CDC0027 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_SpecialPop27.xml"
	private static final String SAMPLE_CDC0028 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_LT7_001.xml"
	private static final String SAMPLE_CDC0029 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_LT7_002.xml"
	private static final String SAMPLE_CDC0030 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_LT7_003.xml"
	private static final String SAMPLE_CDC0031 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_LT7_004.xml"
	private static final String SAMPLE_CDC0032 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_LT7_005.xml"
	private static final String SAMPLE_CDC0033 = "src/test/resources/samples/hedis-cdc-HbA1c_LT7/CDC_LT7_006.xml"

	private static final String SAMPLE_CDC0050 = "src/test/resources/samples/hedis-cdc-eye/CDC_Denom0025.xml"


	@Unroll
	def "test HEDIS CDC"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CDC_HbA1c_LT7', version: '2014.0.0'],
			specifiedTime: '2012-01-01'
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
	
		
/*
C2519=Denominator Inclusions by Claims Data
C2579=Diabetes by Claims Data
C2606=Patient Age GE 18 and LT 76 Years
C54=Denominator Criteria Met
C545=Denominator Inclusions Met
C2607=Diabetes by Pharmacy Data
C2520=Denominator Inclusions by Pharmacy Data
C3290=HbA1c Testing by Observation
C539=Numerator Criteria Met
C3293=
C3288=HbA1c GT 9.0 Pct
3294=
C2523=HbA1c Control LT 8.0 Pct
C1678=Cholesterol, Low Density Lipoprotein (LDL-C)
C2736=LDL-C Control LT 100 mg per dL
C2608=Patient Age GE 65 Years
C2541=CABG - Coronary Artery Bypass Graft
C3264=Thoracic Aortic Aneurysm encounter in prior year
C2542=Coronary Intervention, Percutaneous (PCI)
C2543=Vascular Disease, Ischemic (IVD)
C3196=Vascular Disease, Ischemic (IVD) encounter in measurement year
C3197=Vascular Disease, Ischemic (IVD) encounter in prior year
C2544=Aortic Aneurysm, Thoracic
C3263=Thoracic Aortic Aneurysm encounter in measurement year
C2545=Heart Failure, Congestive
C2661=Comorbid Disease
C340=Kidney Disease, Chronic
C1922=Dementia
C2546=Myocardial Infarction, Prior
C2549=Blindness
C2550=Amputation, Lower Extremity
C339=Kidney Failure, Acute
C2524=HbA1c Control LT 7.0 Pct
C3265=Named Dates Inserted
*/		
		
		where:
		vmr | assertions | denominator | numerator
		SAMPLE_ALL0001 | [C529: '', C569: '', reject: '', wrapper : ''] | '0' | '0'
		SAMPLE_CDC0001 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2608:'',C3265:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0002 | [AcuteInpatientEncounters:'1',C2519:'',C2541:'',C2579:'',C2606:'',C3265:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0003 | [AcuteInpatientEncounters:'1',C2519:'',C2541:'',C2579:'',C2606:'',C3265:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0004 | [AcuteInpatientEncounters:'1',C2519:'',C2541:'',C2579:'',C2606:'',C3265:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0005 | [AcuteInpatientEncounters:'1',C2519:'',C2542:'',C2579:'',C2606:'',C3265:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0006 | [AcuteInpatientEncounters:'1',C2519:'',C2542:'',C2579:'',C2606:'',C3265:'',C544:'',C545:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0007 | [AcuteInpatientEncounters:'1',C2519:'',C2542:'',C2579:'',C2606:'',C3265:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0008 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3265:'',C54:'',C545:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0009 | [AcuteInpatientEncounters:'1',C2519:'',C2543:'',C2579:'',C2606:'',C3196:'',C3197:'',C3265:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0010 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3197:'',C3265:'',C54:'',C545:'',numNotMet:'',wrapper:'']| '1' | '0'
		SAMPLE_CDC0011 | [AcuteInpatientEncounters:'1',C2519:'',C2544:'',C2579:'',C2606:'',C3263:'',C3264:'',C3265:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0012 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3264:'',C3265:'',C54:'',C545:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0013 | [AcuteInpatientEncounters:'1',C2519:'',C2545:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0014 | [AcuteInpatientEncounters:'1',C2519:'',C2546:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C544:'',C545:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0015 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C340:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0016 | [AcuteInpatientEncounters:'1',C1922:'',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0017 | [AcuteInpatientEncounters:'1',C2519:'',C2549:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		
		SAMPLE_CDC0018 | [AcuteInpatientEncounters:'1',C2519:'',C2550:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0019 | [AcuteInpatientEncounters:'1',C2519:'',C2550:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C544:'',C545:'',denomNotMet:'',wrapper:'']|'0' | '0'
		SAMPLE_CDC0020 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C339:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0021 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C339:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0022 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C339:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0023 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C339:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0024 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C339:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0025 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3264:'',C3265:'',C54:'',C545:'',numNotMet:'',wrapper:'']| '1' | '0'
		SAMPLE_CDC0026 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C339:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0027 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2661:'',C3264:'',C3265:'',C339:'',C544:'',C545:'',denomNotMet:'',wrapper:''] | '0' | '0'
		SAMPLE_CDC0028 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0029 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0030 | [AcuteInpatientEncounters:'1',C2519:'',C2524:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0031 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0032 | [AcuteInpatientEncounters:'1',C2519:'',C2524:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0033 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'

		SAMPLE_CDC0050 | [C2520:'',C2606:'',C2607:'',C54:'',C545:'', C3265:'',numNotMet:'',wrapper:'']| '1' | '0'
		}
}


