package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_CDC_LDL_Screening_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"

	private static final String SAMPLE_CDC0001 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0002.xml"
	private static final String SAMPLE_CDC0002 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0003.xml"
	private static final String SAMPLE_CDC0003 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0004.xml"
	private static final String SAMPLE_CDC0004 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0005.xml"
	private static final String SAMPLE_CDC0005 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0006.xml"
	private static final String SAMPLE_CDC0006 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0007.xml"
	private static final String SAMPLE_CDC0007 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0008.xml"
	private static final String SAMPLE_CDC0008 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0009.xml"
	private static final String SAMPLE_CDC0009 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0010.xml"
	private static final String SAMPLE_CDC0010 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0011.xml"
	private static final String SAMPLE_CDC0011 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0012.xml"
	private static final String SAMPLE_CDC0012 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0013.xml"
	private static final String SAMPLE_CDC0013 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0014.xml"
	private static final String SAMPLE_CDC0014 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0015.xml"
	private static final String SAMPLE_CDC0015 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0016.xml"
	private static final String SAMPLE_CDC0016 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0017.xml"
	private static final String SAMPLE_CDC0017 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_Denom0018.xml"
	private static final String SAMPLE_CDC0018 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_LDLScreen001.xml"
	private static final String SAMPLE_CDC0019 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_LDLScreen002.xml"
	private static final String SAMPLE_CDC0020 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_LDLScreen003.xml"
	private static final String SAMPLE_CDC0021 = "src/test/resources/samples/hedis-cdc-LDL_Screen/CDC_LDLScreen004.xml"
	
	private static final String SAMPLE_CDC0050 = "src/test/resources/samples/hedis-cdc-eye/CDC_Denom0025.xml"


	@Unroll
	def "test HEDIS CDC"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CDC_LDL_Screen', version: '2014.0.0'],
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
*/		
		
		where:
		vmr | assertions | denominator | numerator
		SAMPLE_ALL0001 | [C529: '', C569: '', reject: '', wrapper : ''] | '0' | '0'
		SAMPLE_CDC0001 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0002 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0003 | [C2519:'',C2579:'',C2606:'',C54:'',C545:'',EmergencyDepartmentEncounters:'1',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0004 | [C2519:'',C2579:'',C2606:'',C54:'', C545:'',EmergencyDepartmentEncounters:'1',numNotMet:'',wrapper:'']| '1' | '0'
		SAMPLE_CDC0005 | [C2606:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0006 | [C2606:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0007 | [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0008 | [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0009 | [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0010 | [C2606:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0011 | [C2606:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0012 | [AcuteInpatientEncounters:'1',C2579:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0013 | [AcuteInpatientEncounters:'1',C2579:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0014 | [C2520:'',C2606:'',C2607:'',C54:'',C545:'',numNotMet:'',wrapper:'']| '1' | '0'
		SAMPLE_CDC0015 | [C2606:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0016 | [C2606:'',denomNotMet:'',wrapper:'']| '0' | '0'
		SAMPLE_CDC0017 | [C2606:'',denomNotMet:'',wrapper:'']| '0' | '0'
		
		SAMPLE_CDC0018 | [AcuteInpatientEncounters:'1',C1678:'',C2519:'',C2579:'',C2606:'',C539:'',C54:'',C545:'',numMet:'',wrapper:'']| '1' | '1'
		SAMPLE_CDC0019 | [AcuteInpatientEncounters:'1',C1678:'',C2519:'',C2579:'',C2606:'',C539:'',C54:'',C545:'',numMet:'',wrapper:'']| '1' | '1'
		SAMPLE_CDC0020 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',numNotMet:'',wrapper:'']| '1' | '0'
		SAMPLE_CDC0021 | [AcuteInpatientEncounters:'1',C1678:'',C2519:'',C2579:'',C2606:'',C539:'',C54:'',C545:'',numMet:'',wrapper:'']| '1' | '1'
		
		SAMPLE_CDC0050 | [C2520:'',C2606:'',C2607:'',C54:'',C545:'',numNotMet:'',wrapper:'']| '1' | '0'
		}
}


