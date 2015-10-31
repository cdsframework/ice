package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_PPC_Postpartum_SecondDelivery_FunctionalSpec extends Specification {

/* Assumptions: Measurement Year is 2011, encounter low date is proxy for delivery date */
	private static final String PPC_000 = "src/test/resources/samples/hedis-fpc/FPC_000.xml"
	private static final String PPC_001 = "src/test/resources/samples/hedis-fpc/FPC_001.xml"
	private static final String PPC_002 = "src/test/resources/samples/hedis-fpc/FPC_002.xml"
	private static final String PPC_003 = "src/test/resources/samples/hedis-fpc/FPC_003.xml"
	private static final String PPC_004 = "src/test/resources/samples/hedis-fpc/FPC_004.xml"
	private static final String PPC_005 = "src/test/resources/samples/hedis-fpc/FPC_005.xml"
	private static final String PPC_006 = "src/test/resources/samples/hedis-fpc/FPC_006.xml"
	private static final String PPC_007 = "src/test/resources/samples/hedis-fpc/FPC_007.xml"
	private static final String PPC_008 = "src/test/resources/samples/hedis-fpc/FPC_008.xml"
	private static final String PPC_009 = "src/test/resources/samples/hedis-fpc/FPC_009.xml"
	private static final String PPC_010 = "src/test/resources/samples/hedis-fpc/FPC_010.xml"
	private static final String PPC_011 = "src/test/resources/samples/hedis-fpc/FPC_011.xml"
	private static final String PPC_012 = "src/test/resources/samples/hedis-fpc/FPC_012.xml"
	private static final String PPC_013 = "src/test/resources/samples/hedis-fpc/FPC_013.xml"
	private static final String PPC_014 = "src/test/resources/samples/hedis-fpc/FPC_014.xml"
	private static final String PPC_015 = "src/test/resources/samples/hedis-fpc/FPC_015.xml"
	private static final String PPC_016 = "src/test/resources/samples/hedis-fpc/FPC_016.xml"
	private static final String PPC_017 = "src/test/resources/samples/hedis-fpc/FPC_017.xml"
	private static final String PPC_018 = "src/test/resources/samples/hedis-fpc/FPC_018.xml"
 	private static final String PPC_019 = "src/test/resources/samples/hedis-fpc/FPC_019.xml"
	private static final String PPC_020 = "src/test/resources/samples/hedis-fpc/FPC_020.xml"
	private static final String PPC_021 = "src/test/resources/samples/hedis-fpc/FPC_021.xml"
	private static final String PPC_022 = "src/test/resources/samples/hedis-fpc/FPC_022.xml"
	private static final String PPC_023 = "src/test/resources/samples/hedis-fpc/FPC_023.xml"
	private static final String PPC_024 = "src/test/resources/samples/hedis-fpc/FPC_024.xml"
	private static final String PPC_025 = "src/test/resources/samples/hedis-fpc/FPC_025.xml"
	private static final String PPC_026 = "src/test/resources/samples/hedis-fpc/FPC_026.xml"
	private static final String PPC_027 = "src/test/resources/samples/hedis-fpc/FPC_027.xml"
	private static final String PPC_028 = "src/test/resources/samples/hedis-fpc/FPC_028.xml"
	private static final String PPC_029 = "src/test/resources/samples/hedis-fpc/FPC_029.xml"
	private static final String PPC_030 = "src/test/resources/samples/hedis-fpc/FPC_030.xml"
	private static final String PPC_031 = "src/test/resources/samples/hedis-fpc/FPC_031.xml"
	private static final String PPC_032 = "src/test/resources/samples/hedis-fpc/FPC_032.xml"
	private static final String PPC_033 = "src/test/resources/samples/hedis-fpc/FPC_033.xml"
	private static final String PPC_034 = "src/test/resources/samples/hedis-fpc/FPC_034.xml"
	private static final String PPC_035 = "src/test/resources/samples/hedis-fpc/FPC_035.xml"
	private static final String PPC_036 = "src/test/resources/samples/hedis-fpc/FPC_036.xml"
	private static final String PPC_037 = "src/test/resources/samples/hedis-fpc/FPC_037.xml"
	private static final String PPC_038 = "src/test/resources/samples/hedis-fpc/FPC_038.xml"
	private static final String PPC_039 = "src/test/resources/samples/hedis-fpc/FPC_039.xml"
	private static final String PPC_040 = "src/test/resources/samples/hedis-fpc/FPC_040.xml"
	private static final String PPC_041 = "src/test/resources/samples/hedis-fpc/FPC_041.xml"
    private static final String PPC_042 = "src/test/resources/samples/hedis-fpc/FPC_042.xml"
	private static final String PPC_043 = "src/test/resources/samples/hedis-fpc/FPC_043.xml"
	private static final String PPC_044 = "src/test/resources/samples/hedis-fpc/FPC_044.xml"
	private static final String PPC_045 = "src/test/resources/samples/hedis-fpc/FPC_045.xml"
	private static final String PPC_046 = "src/test/resources/samples/hedis-fpc/FPC_046.xml"
	private static final String PPC_047 = "src/test/resources/samples/hedis-fpc/FPC_047.xml"
	private static final String PPC_048 = "src/test/resources/samples/hedis-fpc/FPC_048.xml"
	private static final String PPC_049 = "src/test/resources/samples/hedis-fpc/FPC_049.xml"
	private static final String PPC_050 = "src/test/resources/samples/hedis-fpc/FPC_050.xml"
	private static final String PPC_051 = "src/test/resources/samples/hedis-fpc/FPC_051.xml"
	private static final String PPC_052 = "src/test/resources/samples/hedis-fpc/FPC_052.xml"
	private static final String PPC_053 = "src/test/resources/samples/hedis-fpc/FPC_053.xml"
	private static final String PPC_054 = "src/test/resources/samples/hedis-fpc/FPC_054.xml"
	private static final String PPC_055 = "src/test/resources/samples/hedis-fpc/FPC_055.xml"
	private static final String PPC_056 = "src/test/resources/samples/hedis-fpc/FPC_056.xml"	
	private static final String PPC_057 = "src/test/resources/samples/hedis-ppc/PPC_057.xml"
	private static final String PPC_058 = "src/test/resources/samples/hedis-ppc/PPC_058.xml"
	private static final String PPC_059 = "src/test/resources/samples/hedis-ppc/PPC_059.xml"
	private static final String PPC_060 = "src/test/resources/samples/hedis-ppc/PPC_060.xml"
	private static final String PPC_061 = "src/test/resources/samples/hedis-ppc/PPC_061.xml"
	private static final String PPC_062 = "src/test/resources/samples/hedis-ppc/PPC_062.xml"
	private static final String PPC_063 = "src/test/resources/samples/hedis-ppc/PPC_063.xml"
	private static final String PPC_064 = "src/test/resources/samples/hedis-ppc/PPC_064.xml"
	private static final String PPC_065 = "src/test/resources/samples/hedis-ppc/PPC_065.xml"
	private static final String PPC_066 = "src/test/resources/samples/hedis-ppc/PPC_066.xml"
	private static final String PPC_067 = "src/test/resources/samples/hedis-ppc/PPC_067.xml"
	private static final String PPC_068 = "src/test/resources/samples/hedis-ppc/PPC_068.xml"
	private static final String PPC_069 = "src/test/resources/samples/hedis-ppc/PPC_069.xml"
	private static final String PPC_070 = "src/test/resources/samples/hedis-ppc/PPC_070.xml"
	private static final String PPC_071 = "src/test/resources/samples/hedis-ppc/PPC_071.xml"
	private static final String PPC_072 = "src/test/resources/samples/hedis-ppc/PPC_072.xml"
	private static final String PPC_073 = "src/test/resources/samples/hedis-ppc/PPC_073.xml"
	private static final String PPC_074 = "src/test/resources/samples/hedis-ppc/PPC_074.xml"	

	@Unroll
	def "test HEDIS PPC Postpartum secondDelivery"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_PPC_Postpartum_SecondDelivery', version: '2014.0.0'],
			specifiedTime: '2012-01-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
		println responsePayload

		then:
		def data = new XmlSlurper().parseText(responsePayload)
		def results = VMRUtil.getResults(data, '\\|')
		measureFocus.size() == results.measureFocus.size()
		measureFocus.each {String focus -> 
			assert results.measureFocus.contains(focus)
		}
//		assertions.size() == results.assertions.size()
		results.assertions.each {entry ->
			System.err.println "${entry.key} -> ${entry.value}"
		}
//		assertions.each {entry ->
//			assert results.assertions.containsKey(entry.key)
//			if (entry.value) {
//				assert results.assertions.get(entry.key) == entry.value
//			}
//		}
		numerator == results.measure.numerator
		denominator == results.measure.denominator

		where:
		vmr | measureFocus | denominator | numerator 
 
			/* 
			 * Assumptions: Measurement Year is 2011, encounter low date is proxy for delivery date when it is missing from procedure 
			 * C3310 - PPC Postpartum First Delivery
			 */
		
        	/* 0 : Denom check: Missing DOB value, this is NOT invalid data for this measure 	*/
			/* 0 : Num check: nothing  */
		PPC_000 | ['C3310'] | '0' | '0'       
		
			/* 0 : Denom check: Gender Male value, this is NOT invalid data for this measure 	*/
			/* 0 : Num check: nothing  */
		PPC_001 | ['C3310'] | '0' | '0' 
		
        	/* 0 : Denom check: Missing Gender value, this is NOT invalid data for this measure 	*/
			/* 0 : Num check: nothing  */
		PPC_002 | ['C3310'] | '0' | '0' 
		
        	/* 0 : Denom check: Missing Gender value and DOB, this is NOT invalid data for this measure 	*/
			/* 0 : Num check: nothing  */
		PPC_003 | ['C3310'] | '0' | '0' 
		
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Dx just before high cutoff */
//			/* 0 - Num check: nothing  */
//		PPC_004 | ['C3310'] | '1' | '0' 	
		
        	/* 0 - Denom check: HEDIS Delivery by ICD9Dx, just before low cutoff	*/
			/* 0 - Num check: nothing  */
		PPC_005 | ['C3310'] | '0' | '0' 	
		
//        	/* 0 - Denom check: HEDIS Delivery by ICD9Dx, just after high cutoff	*/
//			/* 0 - Num check: nothing  */
//		PPC_006 | ['C3310'] | '0' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Dx, just after low cutoff	*/
//			/* 0 - Num check: nothing  */
//		PPC_007 | ['C3310'] | '1' | '0' 
//		
//			/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 0 - Num check: nothing  */
//		PPC_008 | ['C3310'] | '1' | '0'	
//		
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, just after low cutoff, w/gestational age = 8	*/
//			/* 0 - Num check: nothing  */
//		PPC_009 | ['C3310'] | '1' | '0' 
		
        	/* 0 - Denom check: HEDIS Delivery by ICD9Px, not live birth	*/
			/* 0 - Num check: nothing  */
		PPC_010 | ['C3310'] | '0' | '0' 
		
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
//			/* 1 - Num check: one standalone prenatal visit by CPT, w/provider, 1 month before delivery  */
//		PPC_011 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
//			/* 0 - Num check: one standalone prenatal visit by CPT, w/o provider, 1 month before delivery  */
//		PPC_012 | ['C3310'] | '1' | '0' 	
//		
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
//			/* 1 - Num check: one standalone prenatal visit by HCPCS, w/ provider, 1 month before delivery  */
//		PPC_013 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
//			/* 0 - Num check: one standalone prenatal visit by HCPCS, w/o provider, 1 month before delivery  */
//		PPC_014 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: full set standalone prenatal visit by HCPCS, w/ provider, count of 17 visits before delivery  */
//		PPC_015 | ['C3310'] | '1' | '0' 
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43, 2nd delivery by ICD9Px, gestational age 38	*/
			/* 1 - Num check: full set standalone prenatal visit by HCPCS, w/ provider, count of 17 visits before delivery  */
		PPC_016 | ['C3310'] | '1' | '0' 
		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 1 - Num check: OB Panel 1st trimester by CPT  */
//		PPC_017 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 1 - Num check: Ultrasound 1st trimester by CPT */
//		PPC_018 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 1 - Num check: Pregnancy Dx 1st trimester  */
//		PPC_019 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 1 - Num check: OB Panel 1st trimester by ICD9Px  */
//		PPC_020 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 1 - Num check: antibody tests 1st trimester by CPT  */
//		PPC_021 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 0 - Num check: antibody tests 1st trimester by LOINC  */
//		PPC_022 | ['C3310'] | '1' | '0' 	
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 0 - Num check: antibody tests 1st trimester by LOINC, minus Toxoplasma  */
//		PPC_023 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 0 - Num check: antibody tests 1st trimester by LOINC, minus Rubella  */
//		PPC_024 | ['C3310'] | '1' | '0'
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 0 - Num check: antibody tests 1st trimester by LOINC, minus cytomegalovirus  */
//		PPC_025 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 0 - Num check: antibody tests 1st trimester by LOINC, minus herpes simplex  */
//		PPC_026 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 1 - Num check: rubella and ABO in 1st trimester by LOINC */
//		PPC_027 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 1 - Num check: rubella and ABO in 1st trimester by CPT */
//		PPC_028 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 0 - Num check: rubella and ABO in 1st trimester by CPT, minus rubella */
//		PPC_029 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 0 - Num check: rubella and ABO in 1st trimester by CPT, minus ABO */
//		PPC_030 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 1 - Num check: rubella and Rh in 1st trimester by CPT */
//		PPC_031 | ['C3310'] | '1' | '0' 
//			
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 1 - Num check: rubella and Rh in 1st trimester by LOINC */
//		PPC_032 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 1 - Num check: rubella and Rh in 1st trimester by LOINC, minus rubella */
//		PPC_033 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 1 - Num check: rubella by CPT and ABO_RH by LOINC in 1st trimester */
//		PPC_034 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 1 - Num check: rubella by LOINC and ABO_RH by LOINC in 1st trimester */
//		PPC_035 | ['C3310'] | '1' | '0' 
//			
//        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
//			/* 0 - Num check: ABO_RH by LOINC in 1st trimester */
//		PPC_036 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by CPT, encounter before, procedure after low cutoff	*/
//			/* 0 - Num check: nothing  */
//		PPC_037 | ['C3310'] | '1' | '0' 	
		
        	/* 0 - Denom check: HEDIS Delivery by CPT, encounter before, procedure after high cutoff	*/
			/* 0 - Num check: nothing  */
		PPC_038 | ['C3310'] | '0' | '0' 
		
//        	/* 1 - Denom check: HEDIS Delivery by CPT	*/
//			/* 0 - Num check: nothing  */
//		PPC_039 | ['C3310'] | '1' | '0' 	
//		
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check:  set standalone prenatal visit by HCPCS, w/ provider, count of 16 visits before delivery  */
//		PPC_040 | ['C3310'] | '1' | '0' 
//			
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check:  set standalone prenatal visit by HCPCS, w/ provider, count of 15 visits before delivery  */
//		PPC_041 | ['C3310'] | '1' | '0' 
//				
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 14 visits before delivery  */
//		PPC_042 | ['C3310'] | '1' | '0' 
//		  
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 13 visits before delivery  */
//		PPC_043 | ['C3310'] | '1' | '0'
//		 
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 12 visits before delivery  */
//		PPC_044 | ['C3310'] | '1' | '0'  
//		 
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 11 visits before delivery  */
//		PPC_045 | ['C3310'] | '1' | '0' 
//			  
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 10 visits before delivery  */
//		PPC_046 | ['C3310'] | '1' | '0'   
//		
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 9 visits before delivery  */
//		PPC_047 | ['C3310'] | '1' | '0'
//		 
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 8 visits before delivery  */
//		PPC_048 | ['C3310'] | '1' | '0' 
//		
//			/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 7 visits before delivery  */
//		PPC_049 | ['C3310'] | '1' | '0' 
//			   
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 6 visits before delivery  */
//		PPC_050 | ['C3310'] | '1' | '0' 	
//			
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 5 visits before delivery  */
//		PPC_051 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 4 visits before delivery  */
//		PPC_052 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 3 visits before delivery  */
//		PPC_053 | ['C3310'] | '1' | '0' 
//		
//			/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 2 visits before delivery  */
//		PPC_054 | ['C3310'] | '1' | '0'
//		
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 1 visits before delivery  */
//		PPC_055 | ['C3310'] | '1' | '0' 
//		
//        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
//			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 0 visits before delivery  */
//		PPC_056 | ['C3310'] | '1' | '0' 
		
        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43, 2nd delivery by ICD9Px, gestational age 38	*/
		/* 1 - Num check: full set standalone prenatal visit by HCPCS, w/ provider, count of 17 visits before delivery 1, 14 before delivery 2, 1 postpartum visit at 6 wks for each  */
		PPC_057 | ['C3310'] | '1' | '0' 
		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 1 - Num check: 1 standalone prenatal visit by HCPCS, w/ provider, 1 postpartum visit by CPT at 6 wks after delivery  */
//		PPC_058 | ['C3310'] | '1' | '1' 
//		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 1 - Num check: 1 standalone prenatal visit by HCPCS, w/ provider, 1 postpartum visit by HCPCS at 6 wks after delivery  */
//		PPC_059 | ['C3310'] | '1' | '1' 
//		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 1 - Num check: 1 standalone prenatal visit by HCPCS, w/ provider, 1 postpartum visit by ICD9Px at 6 wks after delivery  */
//		PPC_060 | ['C3310'] | '1' | '1' 
//		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 1 - Num check: 1 standalone prenatal visit by HCPCS, w/ provider, 1 postpartum visit by ICD9Dx at 6 wks after delivery  */
//		PPC_061 | ['C3310'] | '1' | '1' 
//		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 0 - Num check: 1 standalone prenatal visit by HCPCS, w/ provider, 1 postpartum visit by ICD9Dx at 20 days after delivery  */
//		PPC_062 | ['C3310'] | '1' | '0' 
//		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 1 - Num check: 1 standalone prenatal visit by HCPCS, w/ provider, 1 postpartum visit by ICD9Dx at 21 days after delivery  */
//		PPC_063 | ['C3310'] | '1' | '1' 
//		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 1 - Num check: 1 standalone prenatal visit by HCPCS, w/ provider, 1 postpartum visit by ICD9Dx at 56 days after delivery  */
//		PPC_064 | ['C3310'] | '1' | '1' 
//		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 0 - Num check: 1 standalone prenatal visit by HCPCS, w/ provider, 1 postpartum visit by ICD9Dx at 57 days after delivery  */
//		PPC_065 | ['C3310'] | '1' | '0' 
//		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 0 - Num check: 1 standalone prenatal visit by HCPCS w/ provider at 280 days before delivery, 1 postpartum visit by ICD9Px at 6 wks after delivery  */
//		PPC_066 | ['C3310'] | '1' | '1' 
//		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 1 - Num check: 1 standalone prenatal visit by HCPCS w/ provider at 279 days before delivery, 1 postpartum visit by ICD9Px at 6 wks after delivery  */
//		PPC_067 | ['C3310'] | '1' | '1' 
//		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 1 - Num check: 1 standalone prenatal visit by HCPCS w/ provider at 219 days before delivery, 1 postpartum visit by ICD9Px at 6 wks after delivery  */
//		PPC_068 | ['C3310'] | '1' | '1' 
//		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 0 - Num check: 1 standalone prenatal visit by HCPCS w/ provider at 218 days before delivery, 1 postpartum visit by ICD9Px at 6 wks after delivery  */
//		PPC_069 | ['C3310'] | '1' | '1' 
//		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 1 - Num check: 1 standalone prenatal visit by HCPCS w/ provider, 1 postpartum visit for cytology by CPT at 6 wks after delivery  */
//		PPC_070 | ['C3310'] | '1' | '1' 
//		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 1 - Num check: 1 standalone prenatal visit by HCPCS w/ provider, 1 postpartum visit for cytology by HCPCS at 6 wks after delivery  */
//		PPC_071 | ['C3310'] | '1' | '1' 
//		
//		/* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 1 - Num check: 1 standalone prenatal visit by HCPCS w/ provider, 1 postpartum visit for cytology by LOINC at 6 wks after delivery  */
// 		PPC_072 | ['C3310'] | '1' | '1' 
//		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 1 - Num check: 1 standalone prenatal visit by HCPCS w/ provider, 1 postpartum visit for cytology by UBREV at 6 wks after delivery  */
//		PPC_073 | ['C3310'] | '1' | '1' 
//		
//        /* 1 - Denom check: HEDIS 1st delivery by ICD9Px, gestation age 43	*/
//		/* 0 - Num check: 1 standalone prenatal visit by HCPCS w/ provider, 1 visit that doesn't qualify for postpartum care at 6 wks after delivery  */
//		PPC_074 | ['C3310'] | '1' | '0' 
		
		}
}