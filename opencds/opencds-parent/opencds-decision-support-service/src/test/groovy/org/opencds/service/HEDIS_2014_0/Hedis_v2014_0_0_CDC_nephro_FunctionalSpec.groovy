package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_CDC_nephro_FunctionalSpec extends Specification {
	private static final String SAMPLE_ALL0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml"
	private static final String SAMPLE_CDC0001 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_001.xml"
	private static final String SAMPLE_CDC0002 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_002.xml"
	private static final String SAMPLE_CDC0003 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_003.xml"
	private static final String SAMPLE_CDC0004 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_004.xml"
	private static final String SAMPLE_CDC0005 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_005.xml"
	private static final String SAMPLE_CDC0006 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_006.xml"
	private static final String SAMPLE_CDC0007 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_007.xml"
	private static final String SAMPLE_CDC0008 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_008.xml"
	private static final String SAMPLE_CDC0009 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_009.xml"
	private static final String SAMPLE_CDC0010 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_010.xml"
	private static final String SAMPLE_CDC0011 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_011.xml"
	private static final String SAMPLE_CDC0012 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_012.xml"
	private static final String SAMPLE_CDC0013 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_013.xml"
	private static final String SAMPLE_CDC0014 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_014.xml"
	private static final String SAMPLE_CDC0015 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_015.xml"
	private static final String SAMPLE_CDC0016 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_016.xml"
	private static final String SAMPLE_CDC0017 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_017.xml"
	private static final String SAMPLE_CDC0018 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_018.xml"
	private static final String SAMPLE_CDC0019 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_019.xml"
	private static final String SAMPLE_CDC0020 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_020.xml"
	private static final String SAMPLE_CDC0021 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_021.xml"
	private static final String SAMPLE_CDC0022 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_022.xml"
	private static final String SAMPLE_CDC0023 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_023.xml"
	private static final String SAMPLE_CDC0024 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_024.xml"
	private static final String SAMPLE_CDC0025 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_025.xml"
	private static final String SAMPLE_CDC0026 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_026.xml"
	private static final String SAMPLE_CDC0027 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_027.xml"
	private static final String SAMPLE_CDC0028 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_028.xml"
	private static final String SAMPLE_CDC0029 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_029.xml"
	private static final String SAMPLE_CDC0030 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_030.xml"
	private static final String SAMPLE_CDC0031 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_031.xml"
	private static final String SAMPLE_CDC0032 = "src/test/resources/samples/hedis-cdc-nephro/CDC_nephro_032.xml"
	
	private static final String SAMPLE_CDC0050 = "src/test/resources/samples/hedis-cdc-eye/CDC_Denom0025.xml"



	@Unroll
	def "test HEDIS CDC"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CDC_nephro', version: '2014.0.0'],
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
*/		
		
		where:
		vmr | assertions | denominator | numerator
		SAMPLE_ALL0001 | [C529: '', C569: '', reject: '', wrapper : ''] | '0' | '0'
		SAMPLE_CDC0001 | [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0002 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0003 | [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0004 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0005 | [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0006 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0007 | [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0008 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0009 | [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0010 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0011 | [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0012 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0013 | [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0014 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0015 | [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0016 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0017 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		
		SAMPLE_CDC0018 | [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0019 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0020 | [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0021 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0022 | [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0023 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0024 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0025 | [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0026 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0027 | [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0028 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0029 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0030 | [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		SAMPLE_CDC0031 | [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:''] | '1' | '0'
		SAMPLE_CDC0032 | [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C539:'',C54:'',C545:'',numMet:'',wrapper:''] | '1' | '1'
		
		SAMPLE_CDC0050 | [C2520:'',C2606:'',C2607:'',C54:'',C545:'',C3265:'',numNotMet:'',wrapper:'']| '1' | '0'

		}
}


