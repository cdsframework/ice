package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_CDC_HbA1c_PoorControl_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"

	private static final String SAMPLE_CDC0001 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0002.xml"
	private static final String SAMPLE_CDC0002 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0003.xml"
	private static final String SAMPLE_CDC0003 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0004.xml"
	private static final String SAMPLE_CDC0004 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0005.xml"
	private static final String SAMPLE_CDC0005 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0006.xml"
	private static final String SAMPLE_CDC0006 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0007.xml"
	private static final String SAMPLE_CDC0007 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0008.xml"
	private static final String SAMPLE_CDC0008 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0009.xml"
	private static final String SAMPLE_CDC0009 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0010.xml"
	private static final String SAMPLE_CDC0010 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0011.xml"
	private static final String SAMPLE_CDC0011 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0012.xml"
	private static final String SAMPLE_CDC0012 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0013.xml"
	private static final String SAMPLE_CDC0013 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0014.xml"
	private static final String SAMPLE_CDC0014 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0015.xml"
	private static final String SAMPLE_CDC0015 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0016.xml"
	private static final String SAMPLE_CDC0016 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0017.xml"
	private static final String SAMPLE_CDC0017 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_Denom0018.xml"
	private static final String SAMPLE_CDC0018 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_HbA1cOutofcontrol001.xml"
	private static final String SAMPLE_CDC0019 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_HbA1cOutofcontrol002.xml"
	private static final String SAMPLE_CDC0020 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_HbA1cOutofcontrol003.xml"
	private static final String SAMPLE_CDC0021 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_HbA1cOutofcontrol004.xml"
	private static final String SAMPLE_CDC0022 = "src/test/resources/samples/hedis-cdc-HbA1c_poorControl/CDC_HbA1cOutofcontrol005.xml"
	
	private static final String SAMPLE_CDC0050 = "src/test/resources/samples/hedis-cdc-eye/CDC_Denom0025.xml"

	@Unroll
	def "test HEDIS CDC"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CDC_HbA1c_PoorControl', version: '2014.0.0'],
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
*/		
		
		where:
		vmr | assertions | denominator | numerator
		SAMPLE_ALL0001 | [C529: '', C569: '', reject: '', wrapper : ''] | '0' | '0'
		SAMPLE_CDC0001 | [C3291:'', AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3293:'',C3294:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0002 | [C3291:'', AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3293:'',C3294:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0003 | [C3291:'', C2519:'',C2579:'',C2606:'',C3293:'',C3294:'',EmergencyDepartmentEncounters:'1', C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1' 
		SAMPLE_CDC0004 | [C3291:'', C2519:'',C2579:'',C2606:'',C54:'', C3293:'',C3294:'', EmergencyDepartmentEncounters:'1',C539:'',C54:'',C545:'',numMet:'',wrapper:'']| '1' | '1'
		SAMPLE_CDC0005 | [C2606:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0006 | [C2606:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0007 | [C3291:'', C2519:'',C2579:'',C2606:'',C3293:'',C3294:'',C539:'',C54:'',C545:'',NonAcuteEncounters:'2',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0008 | [C3291:'', C2519:'',C2579:'',C2606:'',C3293:'',C3294:'',C539:'',C54:'',C545:'',NonAcuteEncounters:'2',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0009 | [C3291:'', C2519:'',C2579:'',C2606:'',C3293:'',C3294:'',C539:'',C54:'',C545:'',NonAcuteEncounters:'2',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0010 | [C2606:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0011 | [C2606:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0012 | [AcuteInpatientEncounters:'1',C2579:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0013 | [AcuteInpatientEncounters:'1',C2579:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0014 | [C3291:'', C2520:'',C2606:'',C2607:'',C3293:'',C3294:'',C539:'', C54:'',C545:'',numMet:'',wrapper:'']| '1' | '1'
		SAMPLE_CDC0015 | [C2606:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0016 | [C2606:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0017 | [C2606:'',denomNotMet:'',wrapper:'']| '0' | '0'
		
		SAMPLE_CDC0018 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3293:'',C54:'',C545:'',numNotMet:'',wrapper:'']| '1' | '0'
		SAMPLE_CDC0019 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3288:'',C3293:'',C539:'',C54:'',C545:'',numMet:'',wrapper:'']| '1' | '1'
		SAMPLE_CDC0020 | [C3294:'',AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3288:'',C539:'',C54:'',C545:'',numMet:'',wrapper:'']| '1' | '1'
		SAMPLE_CDC0021 | [C3294:'',AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3288:'',C539:'',C54:'',C545:'',numMet:'',wrapper:'']| '1' | '1'
		
		SAMPLE_CDC0050 | [C3291:'',C3293:'',C3294:'',C539:'',C2520:'',C2606:'',C2607:'',C54:'',C545:'',numMet:'',wrapper:'']| '1' | '1'
		}
}


