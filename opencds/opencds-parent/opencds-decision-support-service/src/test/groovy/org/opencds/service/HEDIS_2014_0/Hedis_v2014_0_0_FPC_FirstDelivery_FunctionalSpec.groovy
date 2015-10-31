package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_FPC_FirstDelivery_FunctionalSpec extends Specification {

/* Assumptions: Measurement Year is 2011, encounter low date is proxy for delivery date */
	private static final String FPC_000 = "src/test/resources/samples/hedis-fpc/FPC_000.xml"
	private static final String FPC_001 = "src/test/resources/samples/hedis-fpc/FPC_001.xml"
	private static final String FPC_002 = "src/test/resources/samples/hedis-fpc/FPC_002.xml"
	private static final String FPC_003 = "src/test/resources/samples/hedis-fpc/FPC_003.xml"
	private static final String FPC_004 = "src/test/resources/samples/hedis-fpc/FPC_004.xml"
	private static final String FPC_005 = "src/test/resources/samples/hedis-fpc/FPC_005.xml"
	private static final String FPC_006 = "src/test/resources/samples/hedis-fpc/FPC_006.xml"
	private static final String FPC_007 = "src/test/resources/samples/hedis-fpc/FPC_007.xml"
	private static final String FPC_008 = "src/test/resources/samples/hedis-fpc/FPC_008.xml"
	private static final String FPC_009 = "src/test/resources/samples/hedis-fpc/FPC_009.xml"
	private static final String FPC_010 = "src/test/resources/samples/hedis-fpc/FPC_010.xml"
	private static final String FPC_011 = "src/test/resources/samples/hedis-fpc/FPC_011.xml"
	private static final String FPC_012 = "src/test/resources/samples/hedis-fpc/FPC_012.xml"
	private static final String FPC_013 = "src/test/resources/samples/hedis-fpc/FPC_013.xml"
	private static final String FPC_014 = "src/test/resources/samples/hedis-fpc/FPC_014.xml"
	private static final String FPC_015 = "src/test/resources/samples/hedis-fpc/FPC_015.xml"
	private static final String FPC_016 = "src/test/resources/samples/hedis-fpc/FPC_016.xml"
	private static final String FPC_017 = "src/test/resources/samples/hedis-fpc/FPC_017.xml"
	private static final String FPC_018 = "src/test/resources/samples/hedis-fpc/FPC_018.xml"
 	private static final String FPC_019 = "src/test/resources/samples/hedis-fpc/FPC_019.xml"
	private static final String FPC_020 = "src/test/resources/samples/hedis-fpc/FPC_020.xml"
	private static final String FPC_021 = "src/test/resources/samples/hedis-fpc/FPC_021.xml"
	private static final String FPC_022 = "src/test/resources/samples/hedis-fpc/FPC_022.xml"
	private static final String FPC_023 = "src/test/resources/samples/hedis-fpc/FPC_023.xml"
	private static final String FPC_024 = "src/test/resources/samples/hedis-fpc/FPC_024.xml"
	private static final String FPC_025 = "src/test/resources/samples/hedis-fpc/FPC_025.xml"
	private static final String FPC_026 = "src/test/resources/samples/hedis-fpc/FPC_026.xml"
	private static final String FPC_027 = "src/test/resources/samples/hedis-fpc/FPC_027.xml"
	private static final String FPC_028 = "src/test/resources/samples/hedis-fpc/FPC_028.xml"
	private static final String FPC_029 = "src/test/resources/samples/hedis-fpc/FPC_029.xml"
	private static final String FPC_030 = "src/test/resources/samples/hedis-fpc/FPC_030.xml"
	private static final String FPC_031 = "src/test/resources/samples/hedis-fpc/FPC_031.xml"
	private static final String FPC_032 = "src/test/resources/samples/hedis-fpc/FPC_032.xml"
	private static final String FPC_033 = "src/test/resources/samples/hedis-fpc/FPC_033.xml"
	private static final String FPC_034 = "src/test/resources/samples/hedis-fpc/FPC_034.xml"
	private static final String FPC_035 = "src/test/resources/samples/hedis-fpc/FPC_035.xml"
	private static final String FPC_036 = "src/test/resources/samples/hedis-fpc/FPC_036.xml"
	private static final String FPC_037 = "src/test/resources/samples/hedis-fpc/FPC_037.xml"
	private static final String FPC_038 = "src/test/resources/samples/hedis-fpc/FPC_038.xml"
	private static final String FPC_039 = "src/test/resources/samples/hedis-fpc/FPC_039.xml"
	private static final String FPC_040 = "src/test/resources/samples/hedis-fpc/FPC_040.xml"
	private static final String FPC_041 = "src/test/resources/samples/hedis-fpc/FPC_041.xml"
    private static final String FPC_042 = "src/test/resources/samples/hedis-fpc/FPC_042.xml"
	private static final String FPC_043 = "src/test/resources/samples/hedis-fpc/FPC_043.xml"
	private static final String FPC_044 = "src/test/resources/samples/hedis-fpc/FPC_044.xml"
	private static final String FPC_045 = "src/test/resources/samples/hedis-fpc/FPC_045.xml"
	private static final String FPC_046 = "src/test/resources/samples/hedis-fpc/FPC_046.xml"
	private static final String FPC_047 = "src/test/resources/samples/hedis-fpc/FPC_047.xml"
	private static final String FPC_048 = "src/test/resources/samples/hedis-fpc/FPC_048.xml"
	private static final String FPC_049 = "src/test/resources/samples/hedis-fpc/FPC_049.xml"
	private static final String FPC_050 = "src/test/resources/samples/hedis-fpc/FPC_050.xml"
	private static final String FPC_051 = "src/test/resources/samples/hedis-fpc/FPC_051.xml"
	private static final String FPC_052 = "src/test/resources/samples/hedis-fpc/FPC_052.xml"
	private static final String FPC_053 = "src/test/resources/samples/hedis-fpc/FPC_053.xml"
	private static final String FPC_054 = "src/test/resources/samples/hedis-fpc/FPC_054.xml"
	private static final String FPC_055 = "src/test/resources/samples/hedis-fpc/FPC_055.xml"
	private static final String FPC_056 = "src/test/resources/samples/hedis-fpc/FPC_056.xml"
//	private static final String FPC_057 = "H:/KMM ITS/Projects/OpenCDS/Quality Measures/HEDIS/testing/fpc-phi-057.xml"
//	private static final String FPC_058 = "H:/KMM ITS/Projects/OpenCDS/Quality Measures/HEDIS/testing/fpc-phi-058.xml"
//	private static final String FPC_059 = "H:/KMM ITS/Projects/OpenCDS/Quality Measures/HEDIS/testing/fpc-phi-059.xml"
//	private static final String FPC_060 = "H:/KMM ITS/Projects/OpenCDS/Quality Measures/HEDIS/testing/fpc-phi-060.xml"
//	private static final String FPC_061 = "H:/KMM ITS/Projects/OpenCDS/Quality Measures/HEDIS/testing/fpc-phi-061.xml"
//	private static final String FPC_062 = "H:/KMM ITS/Projects/OpenCDS/Quality Measures/HEDIS/testing/fpc-phi-062.xml"
//	private static final String FPC_063 = "H:/KMM ITS/Projects/OpenCDS/Quality Measures/HEDIS/testing/fpc-phi-063.xml"
//	private static final String FPC_064 = "H:/KMM ITS/Projects/OpenCDS/Quality Measures/HEDIS/testing/fpc-phi-064.xml"
//	private static final String FPC_065 = "H:/KMM ITS/Projects/OpenCDS/Quality Measures/HEDIS/testing/fpc-phi-065.xml"	
//	private static final String FPC_099 = "H:/KMM ITS/Projects/OpenCDS/Quality Measures/HEDIS/testing/fpc-phi-099.xml"	

	@Unroll
	def "test HEDIS FPC_firstDelivery"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_FPC_FirstDelivery', version: '2014.0.0'],
			specifiedTime: '2012-01-01'
//			specifiedTime: '2014-01-01'
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
		assertions.each {entry ->
			assert results.assertions.containsKey(entry.key)
			if (entry.value) {
				assert results.assertions.get(entry.key) == entry.value
			}
		}
		numerator == results.measure.numerator
		denominator == results.measure.denominator

		where:
		vmr | measureFocus | denominator | numerator | assertions
 
			/* 
			 * Assumptions: Measurement Year is 2011, encounter low date is proxy for delivery date when it is missing from procedure 
			 * C3298 = FPC First Delivery
			 * C3304 - 0 - 20% expected visits
			 * C3305 - 21 - 40%
			 * C3306 - 41 - 60%
			 * C3307 - 61 - 80%
			 * C3308 - 81 - 100%
			 */
		
        	/* 0 : Denom check: Missing DOB value, this is NOT invalid data for this measure 	*/
			/* 0 : Num check: nothing  */
		FPC_000 | ['C3298'] | '0' | '0' | [denomNotMet: '']      
		
			/* 0 : Denom check: Gender Male value, this is NOT invalid data for this measure 	*/
			/* 0 : Num check: nothing  */
		FPC_001 | ['C3298'] | '0' | '0' | [denomNotMet: ''] 
		
        	/* 0 : Denom check: Missing Gender value, this is NOT invalid data for this measure 	*/
			/* 0 : Num check: nothing  */
		FPC_002 | ['C3298'] | '0' | '0' | [denomNotMet: '']  	
		
        	/* 0 : Denom check: Missing Gender value and DOB, this is NOT invalid data for this measure 	*/
			/* 0 : Num check: nothing  */
		FPC_003 | ['C3298'] | '0' | '0' | [denomNotMet: '']  	
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Dx just before high cutoff */
			/* 0 - Num check: nothing  */
		FPC_004 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']	
		
        	/* 0 - Denom check: HEDIS Delivery by ICD9Dx, just before low cutoff	*/
			/* 0 - Num check: nothing  */
		FPC_005 | ['C3298'] | '0' | '0' | [denomNotMet: ''] 	
		
        	/* 0 - Denom check: HEDIS Delivery by ICD9Dx, just after high cutoff	*/
			/* 0 - Num check: nothing  */
		FPC_006 | ['C3298'] | '0' | '0' | [denomNotMet: ''] 	
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Dx, just after low cutoff	*/
			/* 0 - Num check: nothing  */
		FPC_007 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0'] 	
		
			/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 0 - Num check: nothing  */
		FPC_008 | ['C3298'] | '1' | '0'	| [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']	
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, just after low cutoff, w/gestational age = 8	*/
			/* 0 - Num check: nothing  */
		FPC_009 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']	
		
        	/* 0 - Denom check: HEDIS Delivery by ICD9Px, not live birth	*/
			/* 0 - Num check: nothing  */
		FPC_010 | ['C3298'] | '0' | '0' | [denomNotMet: '']	
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
			/* 1 - Num check: one standalone prenatal visit by CPT, w/provider, 1 month before delivery  */
		FPC_011 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']	
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
			/* 0 - Num check: one standalone prenatal visit by CPT, w/o provider, 1 month before delivery  */
		FPC_012 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']	
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
			/* 1 - Num check: one standalone prenatal visit by HCPCS, w/ provider, 1 month before delivery  */
		FPC_013 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']	
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
			/* 0 - Num check: one standalone prenatal visit by HCPCS, w/o provider, 1 month before delivery  */
		FPC_014 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: full set standalone prenatal visit by HCPCS, w/ provider, count of 17 visits before delivery  */
		FPC_015 | ['C3298'] | '1' | '1' | [C3308: '', Percent: '100', PrenatalVisitDistinctDateCount: '17']	
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43, 2nd delivery by ICD9Px, gestational age 38	*/
			/* 1 - Num check: full set standalone prenatal visit by HCPCS, w/ provider, count of 17 visits before delivery  */
		FPC_016 | ['C3298'] | '1' | '1' | [C3308: '', Percent: '100', PrenatalVisitDistinctDateCount: '17']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 1 - Num check: OB Panel 1st trimester by CPT  */
		FPC_017 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 1 - Num check: Ultrasound 1st trimester by CPT */
		FPC_018 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 1 - Num check: Pregnancy Dx 1st trimester  */
		FPC_019 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 1 - Num check: OB Panel 1st trimester by ICD9Px  */
		FPC_020 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 1 - Num check: antibody tests 1st trimester by CPT  */
		FPC_021 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 0 - Num check: antibody tests 1st trimester by LOINC  */
		FPC_022 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 0 - Num check: antibody tests 1st trimester by LOINC, minus Toxoplasma  */
		FPC_023 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 0 - Num check: antibody tests 1st trimester by LOINC, minus Rubella  */
		FPC_024 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 0 - Num check: antibody tests 1st trimester by LOINC, minus cytomegalovirus  */
		FPC_025 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 0 - Num check: antibody tests 1st trimester by LOINC, minus herpes simplex  */
		FPC_026 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 1 - Num check: rubella and ABO in 1st trimester by LOINC */
		FPC_027 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 1 - Num check: rubella and ABO in 1st trimester by CPT */
		FPC_028 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 0 - Num check: rubella and ABO in 1st trimester by CPT, minus rubella */
		FPC_029 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 0 - Num check: rubella and ABO in 1st trimester by CPT, minus ABO */
		FPC_030 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 1 - Num check: rubella and Rh in 1st trimester by CPT */
		FPC_031 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']	
			
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 1 - Num check: rubella and Rh in 1st trimester by LOINC */
		FPC_032 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 1 - Num check: rubella and Rh in 1st trimester by LOINC, minus rubella */
		FPC_033 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 1 - Num check: rubella by CPT and ABO_RH by LOINC in 1st trimester */
		FPC_034 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']	
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 1 - Num check: rubella by LOINC and ABO_RH by LOINC in 1st trimester */
		FPC_035 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']
			
        	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
			/* 0 - Num check: ABO_RH by LOINC in 1st trimester */
		FPC_036 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']
		
        	/* 1 - Denom check: HEDIS Delivery by CPT, encounter before, procedure after low cutoff	*/
			/* 0 - Num check: nothing  */
		FPC_037 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']	
		
        	/* 0 - Denom check: HEDIS Delivery by CPT, encounter before, procedure after high cutoff	*/
			/* 0 - Num check: nothing  */
		FPC_038 | ['C3298'] | '0' | '0' | [denomNotMet: '']
		
        	/* 1 - Denom check: HEDIS Delivery by CPT	*/
			/* 0 - Num check: nothing  */
		FPC_039 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']	
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check:  set standalone prenatal visit by HCPCS, w/ provider, count of 16 visits before delivery  */
		FPC_040 | ['C3298'] | '1' | '1' | [C3308: '', Percent: '94', PrenatalVisitDistinctDateCount: '16']	
			
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check:  set standalone prenatal visit by HCPCS, w/ provider, count of 15 visits before delivery  */
		FPC_041 | ['C3298'] | '1' | '1' | [C3308: '', Percent: '88', PrenatalVisitDistinctDateCount: '15']	
				
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 14 visits before delivery  */
		FPC_042 | ['C3298'] | '1' | '1' | [C3308: '', Percent: '82', PrenatalVisitDistinctDateCount: '14']	
		  
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 13 visits before delivery  */
		FPC_043 | ['C3298'] | '1' | '0' | [C3307: '', Percent: '76', PrenatalVisitDistinctDateCount: '13']	 
		 
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 12 visits before delivery  */
		FPC_044 | ['C3298'] | '1' | '0' | [C3307: '', Percent: '71', PrenatalVisitDistinctDateCount: '12']	 
		 
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 11 visits before delivery  */
		FPC_045 | ['C3298'] | '1' | '0' | [C3307: '', Percent: '65', PrenatalVisitDistinctDateCount: '11']	
			  
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 10 visits before delivery  */
		FPC_046 | ['C3298'] | '1' | '0' | [C3306: '', Percent: '59', PrenatalVisitDistinctDateCount: '10']	  
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 9 visits before delivery  */
		FPC_047 | ['C3298'] | '1' | '0' | [C3306: '', Percent: '53', PrenatalVisitDistinctDateCount: '9']
		 
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 8 visits before delivery  */
		FPC_048 | ['C3298'] | '1' | '0' | [C3306: '', Percent: '47', PrenatalVisitDistinctDateCount: '8']	
		
			/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 7 visits before delivery  */
		FPC_049 | ['C3298'] | '1' | '0' | [C3306: '', Percent: '41', PrenatalVisitDistinctDateCount: '7']
			   
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 6 visits before delivery  */
		FPC_050 | ['C3298'] | '1' | '0' | [C3305: '', Percent: '35', PrenatalVisitDistinctDateCount: '6']	
			
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 5 visits before delivery  */
		FPC_051 | ['C3298'] | '1' | '0' | [C3305: '', Percent: '29', PrenatalVisitDistinctDateCount: '5']	
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 4 visits before delivery  */
		FPC_052 | ['C3298'] | '1' | '0' | [C3305: '', Percent: '24', PrenatalVisitDistinctDateCount: '4']
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 3 visits before delivery  */
		FPC_053 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '18', PrenatalVisitDistinctDateCount: '3']	
		
			/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 2 visits before delivery  */
		FPC_054 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '12', PrenatalVisitDistinctDateCount: '2']	
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 1 visits before delivery  */
		FPC_055 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '6', PrenatalVisitDistinctDateCount: '1']	
		
        	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
			/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 0 visits before delivery  */
		FPC_056 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']	
		
//		FPC_057 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '77', PrenatalVisitDistinctDateCount: '10']	
//		FPC_058 | ['C3298'] | '1' | '1' | [C3308: '', Percent: '93', PrenatalVisitDistinctDateCount: '14']	
//		FPC_059 | ['C3298'] | '0' | '0' | [denomNotMet: '']	
//		FPC_060 | ['C3298'] | '0' | '0' | [denomNotMet: '']		
//		FPC_061 | ['C3298'] | '1' | '0' | [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']	
//		FPC_062 | ['C3298'] | '1' | '0' | [C3307: '', Percent: '67', PrenatalVisitDistinctDateCount: '8']	
//		FPC_063 | ['C3298'] | '0' | '0' | [denomNotMet: '']		
//		FPC_064 | ['C3298'] | '0' | '0' | [denomNotMet: '']		
//		FPC_065 | ['C3298'] | '1' | '0' | [C3307: '', Percent: '77', PrenatalVisitDistinctDateCount: '10']	
//		FPC_099 | ['C3298'] | '0' | '0' | [denomNotMet: '']		
		}
}