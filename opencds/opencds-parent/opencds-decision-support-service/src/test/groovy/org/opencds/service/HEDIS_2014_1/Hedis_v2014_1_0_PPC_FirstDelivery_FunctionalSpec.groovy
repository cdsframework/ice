package org.opencds.service.HEDIS_2014_1;

import java.util.Map;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_PPC_FirstDelivery_FunctionalSpec extends Specification 
{
	private static final String PPC_0000 = "src/test/resources/samples/hedis-fpc/FPC_000.xml"
	/* 0 : Denom check: Missing DOB value, this is NOT invalid data for this measure 	*/
	/* 0 : Num check: nothing  */
	private static final Map ASSERTIONS_PPC_0000 = ["O.01":'']
	private static final Map MEASURES_PPC_0000 =  [C2648: [num: 0, denom: 0]]

	
    private static final String PPC_0001 = "src/test/resources/samples/hedis-fpc/FPC_001.xml" 
	/* 0 : Denom check: Gender Male value, this is NOT invalid data for this measure*/
	/* 0 : Num check: nothing  */
	private static final Map ASSERTIONS_PPC_0001 = ["O.01":'']
    private static final Map MEASURES_PPC_0001 =  [C2648: [num: 0, denom: 0]]

	
    private static final String PPC_0002 = "src/test/resources/samples/hedis-fpc/FPC_002.xml" 
    /* 0 : Denom check: Missing Gender value, this is NOT invalid data for this measure 	*/
	/* 0 : Num check: nothing  */
	private static final Map ASSERTIONS_PPC_0002 = ["O.01":'']
    private static final Map MEASURES_PPC_0002 =  [C2648: [num: 0, denom: 0]]
	
    private static final String PPC_0003 = "src/test/resources/samples/hedis-fpc/FPC_003.xml" 
    /* 0 : Denom check: Missing Gender value and DOB, this is NOT invalid data for this measure 	*/
	/* 0 : Num check: nothing  */
	private static final Map ASSERTIONS_PPC_0003 = ["O.01":'']
    private static final Map MEASURES_PPC_0003 =  [C2648: [num: 0, denom: 0]]

	
    private static final String PPC_0004 = "src/test/resources/samples/hedis-fpc/FPC_004.xml" 
    /* 1 - Denom check: HEDIS Delivery by ICD9Dx just before high cutoff */
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_PPC_0004 = ["O.01":'']
    private static final Map MEASURES_PPC_0004 =  [C2648: [num: 0, denom: 1]]
	
    private static final String PPC_0005 = "src/test/resources/samples/hedis-fpc/FPC_005.xml" 
	/* 0 - Denom check: HEDIS Delivery by ICD9Dx, just before low cutoff	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_PPC_0005 = ["O.01":'']
    private static final Map MEASURES_PPC_0005 =  [C2648: [num: 0, denom: 0]]

	
	private static final String PPC_0006 = "src/test/resources/samples/hedis-fpc/FPC_006.xml" 
    /* 0 - Denom check: HEDIS Delivery by ICD9Dx, just after high cutoff	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_PPC_0006 = ["O.01":'']
    private static final Map MEASURES_PPC_0006 =  [C2648: [num: 0, denom: 0]]
	
    private static final String PPC_0007 = "src/test/resources/samples/hedis-fpc/FPC_007.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Dx, just after low cutoff	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_PPC_0007 = ["O.01":'']
    private static final Map MEASURES_PPC_0007 =  [C2648: [num: 0, denom: 1]]

	
    private static final String PPC_0008 = "src/test/resources/samples/hedis-fpc/FPC_008.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_PPC_0008 = ["O.01":'']
    private static final Map MEASURES_PPC_0008 =  [C2648: [num: 0, denom: 1]]
	
    private static final String PPC_0009 = "src/test/resources/samples/hedis-fpc/FPC_009.xml" 
    /* 1 - Denom check: HEDIS Delivery by ICD9Px, just after low cutoff, w/gestational age = 8	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_PPC_0009 = ["O.01":'']
    private static final Map MEASURES_PPC_0009 =  [C2648: [num: 0, denom: 1]]
	
    private static final String PPC_0010 = "src/test/resources/samples/hedis-fpc/FPC_010.xml" 
	/* 0 - Denom check: HEDIS Delivery by ICD9Px, not live birth	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_PPC_0010 = ["O.01":'']
    private static final Map MEASURES_PPC_0010 =  [C2648: [num: 0, denom: 0]]

	
	private static final String PPC_0011 = "src/test/resources/samples/hedis-fpc/FPC_011.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
	/* 1 - Num check: one standalone prenatal visit by CPT, w/provider, 1 month before delivery  */
	private static final Map ASSERTIONS_PPC_0011 = ["O.01":'']
    private static final Map MEASURES_PPC_0011 =  [C2648: [num: 0, denom: 1]]
	
    private static final String PPC_0012 = "src/test/resources/samples/hedis-fpc/FPC_012.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
	/* 0 - Num check: one standalone prenatal visit by CPT, w/o provider, 1 month before delivery  */
	private static final Map ASSERTIONS_PPC_0012 = ["O.01":'']
    private static final Map MEASURES_PPC_0012 =  [C2648: [num: 0, denom: 1]]

	
	private static final String PPC_0013 = "src/test/resources/samples/hedis-fpc/FPC_013.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
	/* 1 - Num check: one standalone prenatal visit by HCPCS, w/ provider, 1 month before delivery  */
	private static final Map ASSERTIONS_PPC_0013 = ["O.01":'']
    private static final Map MEASURES_PPC_0013 = [C2648: [num: 0, denom: 1]]
	
    private static final String PPC_0014 = "src/test/resources/samples/hedis-fpc/FPC_014.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
	/* 0 - Num check: one standalone prenatal visit by HCPCS, w/o provider, 1 month before delivery  */
	private static final Map ASSERTIONS_PPC_0014 = ["O.01":'']
    private static final Map MEASURES_PPC_0014 = [C2648: [num: 0, denom: 1]]
	
    private static final String PPC_0015 = "src/test/resources/samples/hedis-fpc/FPC_015.xml" 
    /* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: full set standalone prenatal visit by HCPCS, w/ provider, count of 17 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0015 = ["O.01":'']	
    private static final Map MEASURES_PPC_0015 = [C2648: [num: 1, denom: 1]]

	
	private static final String PPC_0016 = "src/test/resources/samples/hedis-fpc/FPC_016.xml" 
    /* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43, 2nd delivery by ICD9Px, gestational age 38	*/
	/* 1 - Num check: full set standalone prenatal visit by HCPCS, w/ provider, count of 17 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0016 = ["O.01":'']	
    private static final Map MEASURES_PPC_0016 = [C2648: [num: 1, denom: 1]]
	
	private static final String PPC_0017 = "src/test/resources/samples/hedis-fpc/FPC_017.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: OB Panel 1st trimester by CPT  */
	private static final Map ASSERTIONS_PPC_0017 = ["O.01":'']
    private static final Map MEASURES_PPC_0017 = [C2648: [num: 1, denom: 1]]
	
    private static final String PPC_0018 = "src/test/resources/samples/hedis-fpc/FPC_018.xml" 
    /* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: Ultrasound 1st trimester by CPT */
	private static final Map ASSERTIONS_PPC_0018 = ["O.01":'']
    private static final Map MEASURES_PPC_0018 = [C2648: [num: 1, denom: 1]]
	
	private static final String PPC_0019 = "src/test/resources/samples/hedis-fpc/FPC_019.xml" 
    /* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: Pregnancy Dx 1st trimester  */
	private static final Map ASSERTIONS_PPC_0019 = ["O.01":'']
	private static final Map MEASURES_PPC_0019 = [C2648: [num: 1, denom: 1]]
	
	private static final String PPC_0020 = "src/test/resources/samples/hedis-fpc/FPC_020.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: OB Panel 1st trimester by ICD9Px  */
	private static final Map ASSERTIONS_PPC_0020 = ["O.01":'']
	private static final Map MEASURES_PPC_0020 = [C2648: [num: 1, denom: 1]]
	
	private static final String PPC_0021 = "src/test/resources/samples/hedis-fpc/FPC_021.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: antibody tests 1st trimester by CPT  */
	private static final Map ASSERTIONS_PPC_0021 = ["O.01":'']
	private static final Map MEASURES_PPC_0021 = [C2648: [num: 1, denom: 1]]
	
	private static final String PPC_0022 = "src/test/resources/samples/hedis-fpc/FPC_022.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: antibody tests 1st trimester by LOINC  */
	private static final Map ASSERTIONS_PPC_0022 = ["O.01":'']
	private static final Map MEASURES_PPC_0022 = [C2648: [num: 1, denom: 1]]
	
	private static final String PPC_0023 = "src/test/resources/samples/hedis-fpc/FPC_023.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: antibody tests 1st trimester by LOINC, minus Toxoplasma  */
	private static final Map ASSERTIONS_PPC_0023 = ["O.01":'']
	private static final Map MEASURES_PPC_0023 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0024 = "src/test/resources/samples/hedis-fpc/FPC_024.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: antibody tests 1st trimester by LOINC, minus Rubella  */
	private static final Map ASSERTIONS_PPC_0024 = ["O.01":'']
	private static final Map MEASURES_PPC_0024 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0025 = "src/test/resources/samples/hedis-fpc/FPC_025.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: antibody tests 1st trimester by LOINC, minus cytomegalovirus  */
	private static final Map ASSERTIONS_PPC_0025 = ["O.01":'']
	private static final Map MEASURES_PPC_0025 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0026 = "src/test/resources/samples/hedis-fpc/FPC_026.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: antibody tests 1st trimester by LOINC, minus herpes simplex  */
	private static final Map ASSERTIONS_PPC_0026 = ["O.01":'']
	private static final Map MEASURES_PPC_0026 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0027 = "src/test/resources/samples/hedis-fpc/FPC_027.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: rubella and ABO in 1st trimester by LOINC */
	private static final Map ASSERTIONS_PPC_0027 = ["O.01":'']
	private static final Map MEASURES_PPC_0027 = [C2648: [num: 1, denom: 1]]
	
	private static final String PPC_0028 = "src/test/resources/samples/hedis-fpc/FPC_028.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: rubella and ABO in 1st trimester by CPT */
	private static final Map ASSERTIONS_PPC_0028 = ["O.01":'']
	private static final Map MEASURES_PPC_0028 = [C2648: [num: 1, denom: 1]]
	
	private static final String PPC_0029 = "src/test/resources/samples/hedis-fpc/FPC_029.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: rubella and ABO in 1st trimester by CPT, minus rubella */
	private static final Map ASSERTIONS_PPC_0029 = ["O.01":'']	
	private static final Map MEASURES_PPC_0029 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0030 = "src/test/resources/samples/hedis-fpc/FPC_030.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: rubella and ABO in 1st trimester by CPT, minus ABO */
	private static final Map ASSERTIONS_PPC_0030 = ["O.01":'']
	private static final Map MEASURES_PPC_0030 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0031 = "src/test/resources/samples/hedis-fpc/FPC_031.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: rubella and Rh in 1st trimester by CPT */
	private static final Map ASSERTIONS_PPC_0031 = ["O.01":'']
	private static final Map MEASURES_PPC_0031 = [C2648: [num: 1, denom: 1]]
	
	private static final String PPC_0032 = "src/test/resources/samples/hedis-fpc/FPC_032.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: rubella and Rh in 1st trimester by LOINC */
	private static final Map ASSERTIONS_PPC_0032 = ["O.01":'']	
	private static final Map MEASURES_PPC_0032 = [C2648: [num: 1, denom: 1]]
	
	private static final String PPC_0033 = "src/test/resources/samples/hedis-fpc/FPC_033.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: rubella and Rh in 1st trimester by LOINC, minus rubella */
	private static final Map ASSERTIONS_PPC_0033 = ["O.01":'']
	private static final Map MEASURES_PPC_0033 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0034 = "src/test/resources/samples/hedis-fpc/FPC_034.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: rubella by CPT and ABO_RH by LOINC in 1st trimester */
	private static final Map ASSERTIONS_PPC_0034 = ["O.01":'']
	private static final Map MEASURES_PPC_0034 = [C2648: [num: 1, denom: 1]]
	
	private static final String PPC_0035 = "src/test/resources/samples/hedis-fpc/FPC_035.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: rubella by LOINC and ABO_RH by LOINC in 1st trimester */
	private static final Map ASSERTIONS_PPC_0035 = ["O.01":'']
	private static final Map MEASURES_PPC_0035 = [C2648: [num: 1, denom: 1]]
	
	private static final String PPC_0036 = "src/test/resources/samples/hedis-fpc/FPC_036.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: ABO_RH by LOINC in 1st trimester */
	private static final Map ASSERTIONS_PPC_0036 = ["O.01":'']
	private static final Map MEASURES_PPC_0036 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0037 = "src/test/resources/samples/hedis-fpc/FPC_037.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, encounter before, procedure after low cutoff	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_PPC_0037 = ["O.01":'']
	private static final Map MEASURES_PPC_0037 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0038 = "src/test/resources/samples/hedis-fpc/FPC_038.xml" 
	/* 0 - Denom check: HEDIS Delivery by CPT, encounter before, procedure after high cutoff	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_PPC_0038 = ["O.01":'']
	private static final Map MEASURES_PPC_0038 = [C2648: [num: 0, denom: 0]]
		
	private static final String PPC_0039 = "src/test/resources/samples/hedis-fpc/FPC_039.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_PPC_0039 = ["O.01":'']
	private static final Map MEASURES_PPC_0039 = [C2648: [num: 0, denom: 1]]

	private static final String PPC_0040 = "src/test/resources/samples/hedis-fpc/FPC_040.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check:  set standalone prenatal visit by HCPCS, w/ provider, count of 16 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0040 = ["O.01":'']
	private static final Map MEASURES_PPC_0040 = [C2648: [num: 1, denom: 1]]
	
	private static final String PPC_0041 = "src/test/resources/samples/hedis-fpc/FPC_041.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check:  set standalone prenatal visit by HCPCS, w/ provider, count of 15 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0041 = ["O.01":'']
	private static final Map MEASURES_PPC_0041 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0042 = "src/test/resources/samples/hedis-fpc/FPC_042.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 14 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0042 = ["O.01":'']	
	private static final Map MEASURES_PPC_0042 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0043 = "src/test/resources/samples/hedis-fpc/FPC_043.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 13 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0043 = ["O.01":'']
	private static final Map MEASURES_PPC_0043 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0044 = "src/test/resources/samples/hedis-fpc/FPC_044.xml"
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 12 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0044 = ["O.01":'']	
	private static final Map MEASURES_PPC_0044 = [C2648: [num: 0, denom: 1]]
		
	private static final String PPC_0045 = "src/test/resources/samples/hedis-fpc/FPC_045.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 11 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0045 = ["O.01":'']
	private static final Map MEASURES_PPC_0045 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0046 = "src/test/resources/samples/hedis-fpc/FPC_046.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 10 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0046 = ["O.01":'']
	private static final Map MEASURES_PPC_0046 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0047 = "src/test/resources/samples/hedis-fpc/FPC_047.xml" 
    /* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 9 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0047 = ["O.01":'']
	private static final Map MEASURES_PPC_0047 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0048 = "src/test/resources/samples/hedis-fpc/FPC_048.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 8 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0048 = ["O.01":'']
	private static final Map MEASURES_PPC_0048 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0049 = "src/test/resources/samples/hedis-fpc/FPC_049.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 7 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0049 = ["O.01":'']
	private static final Map MEASURES_PPC_0049 = [C2648: [num: 0, denom: 1]]

	private static final String PPC_0050 = "src/test/resources/samples/hedis-fpc/FPC_050.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 5 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0050 = ["O.01":'']
	private static final Map MEASURES_PPC_0050 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0051 = "src/test/resources/samples/hedis-fpc/FPC_051.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 5 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0051 = ["O.01":'']
	private static final Map MEASURES_PPC_0051 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0052 = "src/test/resources/samples/hedis-fpc/FPC_052.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 4 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0052 = ["O.01":'']
	private static final Map MEASURES_PPC_0052 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0053 = "src/test/resources/samples/hedis-fpc/FPC_053.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 3 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0053 = ["O.01":'']
	private static final Map MEASURES_PPC_0053 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0054 = "src/test/resources/samples/hedis-fpc/FPC_054.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 2 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0054 = ["O.01":'']
	private static final Map MEASURES_PPC_0054 = [C2648: [num: 0, denom: 1]]
		
	private static final String PPC_0055 = "src/test/resources/samples/hedis-fpc/FPC_055.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 1 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0055 = ["O.01":'']
	private static final Map MEASURES_PPC_0055 = [C2648: [num: 0, denom: 1]]
	
	private static final String PPC_0056 = "src/test/resources/samples/hedis-fpc/FPC_056.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 0 visits before delivery  */
	private static final Map ASSERTIONS_PPC_0056 = ["O.01":'']
	private static final Map MEASURES_PPC_0056 = [C2648: [num: 0, denom: 1]]
	

/*
Concepts used:
INPUT
"	3 -> week(s)"
"	C2511 -> HEDIS 2014"
"	C2557 -> Performer"
"	C2704 -> Provider Primary Care (PCP)"
"	C2895 -> HEDIS-Stand Alone Prenatal Visits"
"	C2899 -> HEDIS-Pregnancy Diagnosis"
"	C2975 -> HEDIS-Deliveries"
"	C2988 -> HEDIS-Herpes Simplex Antibody"
"	C3023 -> HEDIS-Obstetric Panel"
"	C3037 -> HEDIS-Prenatal Ultrasound"
"	C3038 -> HEDIS-Prenatal Visits"
"	C3039 -> HEDIS-Rh"
"	C3044 -> HEDIS-Rubella Antibody"
"	C3059 -> HEDIS-Toxoplasma Antibody"
"	C3063 -> HEDIS-ABO"
"	C3064 -> HEDIS-ABO and Rh"
"	C3096 -> HEDIS-Cytomegalovirus Antibody"
"	C3138 -> HEDIS-Non-live Births"
"	C3265 -> Named Dates Inserted"
"	C3292 -> Gestational Age"
"	C3295 -> Encounter with Delivery in Relaxed Timeframe"
"	C3297 -> Provider Prenatal Care or Primary Care"
"	C36 -> OpenCDS"
"	C405 -> Part of"
"	C54 -> Denominator Criteria Met"
OUTPUT
"	2 -> month(s)"
"	3 -> week(s)"
"	5 -> day(s)"
"	C2648 -> QM HEDIS-PPC Prenatal First Delivery"
"	C2895 -> HEDIS-Stand Alone Prenatal Visits"
"	C2899 -> HEDIS-Pregnancy Diagnosis"
"	C3023 -> HEDIS-Obstetric Panel"
"	C3037 -> HEDIS-Prenatal Ultrasound"
"	C3265 -> Named Dates Inserted"
"	C3295 -> Encounter with Delivery in Relaxed Timeframe"
"	C3296 -> Two Encounters with Deliveries in Relaxed Timeframe"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"

*/
	
	@Unroll
	def "test HEDIS PPC_FirstDelivery v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_PPC_FirstDelivery', version: '2014.1.0'],
			specifiedTime: '2012-01-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
		println responsePayload

		then:
		def data = new XmlSlurper().parseText(responsePayload)
		def results = VMRUtil.getResults(data, '\\|')
//		assertions.size() == results.assertions.size()
		results.assertions.each {entry ->
			System.err.println "${entry.key} -> ${entry.value}"
		}
		if (!assertions) {
			assert assertions == results.assertions
		} else {
		assertions.each {entry ->
			assert results.assertions.containsKey(entry.key);
			if (entry?.value) {
				assert results.assertions.get(entry.key) == entry.value
			}
		}
		}
//        measures.size() == results.measures.size()
        measures.each {entry ->
            assert results.measures.containsKey(entry.key)
            assert results.measures.get(entry.key).num == entry.value.num
            assert results.measures.get(entry.key).denom == entry.value.denom			
        }
//		results.measures.each {entry ->
//			System.err.println "${entry.key} -> ${entry.value.num} ${entry.value.denom}"
//		}
//		results.assertions.each {entry ->
//			System.err.println "${entry.key} -> ${entry.value}"
//		}

		where:
		vmr | assertions | measures
		PPC_0000 | ASSERTIONS_PPC_0000| MEASURES_PPC_0000
		PPC_0001 | ASSERTIONS_PPC_0001| MEASURES_PPC_0001
		PPC_0002 | ASSERTIONS_PPC_0002| MEASURES_PPC_0002
		PPC_0003 | ASSERTIONS_PPC_0003| MEASURES_PPC_0003
		PPC_0004 | ASSERTIONS_PPC_0004| MEASURES_PPC_0004
		PPC_0005 | ASSERTIONS_PPC_0005| MEASURES_PPC_0005
		PPC_0006 | ASSERTIONS_PPC_0006| MEASURES_PPC_0006
		PPC_0007 | ASSERTIONS_PPC_0007| MEASURES_PPC_0007
		PPC_0008 | ASSERTIONS_PPC_0008| MEASURES_PPC_0008
		PPC_0009 | ASSERTIONS_PPC_0009| MEASURES_PPC_0009
		PPC_0010 | ASSERTIONS_PPC_0010| MEASURES_PPC_0010
		PPC_0011 | ASSERTIONS_PPC_0011| MEASURES_PPC_0011
		PPC_0012 | ASSERTIONS_PPC_0012| MEASURES_PPC_0012
		PPC_0013 | ASSERTIONS_PPC_0013| MEASURES_PPC_0013
		PPC_0014 | ASSERTIONS_PPC_0014| MEASURES_PPC_0014
		PPC_0015 | ASSERTIONS_PPC_0015| MEASURES_PPC_0015
		PPC_0016 | ASSERTIONS_PPC_0016| MEASURES_PPC_0016
		PPC_0017 | ASSERTIONS_PPC_0017| MEASURES_PPC_0017
		PPC_0018 | ASSERTIONS_PPC_0018| MEASURES_PPC_0018
		PPC_0019 | ASSERTIONS_PPC_0019| MEASURES_PPC_0019
		PPC_0020 | ASSERTIONS_PPC_0020| MEASURES_PPC_0020
		PPC_0021 | ASSERTIONS_PPC_0021| MEASURES_PPC_0021
		PPC_0022 | ASSERTIONS_PPC_0022| MEASURES_PPC_0022
		PPC_0023 | ASSERTIONS_PPC_0023| MEASURES_PPC_0023
		PPC_0024 | ASSERTIONS_PPC_0024| MEASURES_PPC_0024
		PPC_0025 | ASSERTIONS_PPC_0025| MEASURES_PPC_0025
		PPC_0026 | ASSERTIONS_PPC_0026| MEASURES_PPC_0026
		PPC_0027 | ASSERTIONS_PPC_0027| MEASURES_PPC_0027
		PPC_0028 | ASSERTIONS_PPC_0028| MEASURES_PPC_0028
		PPC_0029 | ASSERTIONS_PPC_0029| MEASURES_PPC_0029
		PPC_0030 | ASSERTIONS_PPC_0030| MEASURES_PPC_0030
		PPC_0031 | ASSERTIONS_PPC_0031| MEASURES_PPC_0031
		PPC_0032 | ASSERTIONS_PPC_0032| MEASURES_PPC_0032
		PPC_0033 | ASSERTIONS_PPC_0033| MEASURES_PPC_0033
		PPC_0034 | ASSERTIONS_PPC_0034| MEASURES_PPC_0034
		PPC_0035 | ASSERTIONS_PPC_0035| MEASURES_PPC_0035
		PPC_0036 | ASSERTIONS_PPC_0036| MEASURES_PPC_0036
		PPC_0037 | ASSERTIONS_PPC_0037| MEASURES_PPC_0037
		PPC_0038 | ASSERTIONS_PPC_0038| MEASURES_PPC_0038
		PPC_0039 | ASSERTIONS_PPC_0039| MEASURES_PPC_0039
		PPC_0040 | ASSERTIONS_PPC_0040| MEASURES_PPC_0040
		PPC_0041 | ASSERTIONS_PPC_0041| MEASURES_PPC_0041
		PPC_0042 | ASSERTIONS_PPC_0042| MEASURES_PPC_0042
		PPC_0043 | ASSERTIONS_PPC_0043| MEASURES_PPC_0043
		PPC_0044 | ASSERTIONS_PPC_0044| MEASURES_PPC_0044
		PPC_0045 | ASSERTIONS_PPC_0045| MEASURES_PPC_0045
		PPC_0046 | ASSERTIONS_PPC_0046| MEASURES_PPC_0046
		PPC_0047 | ASSERTIONS_PPC_0047| MEASURES_PPC_0047
		PPC_0048 | ASSERTIONS_PPC_0048| MEASURES_PPC_0048
		PPC_0049 | ASSERTIONS_PPC_0049| MEASURES_PPC_0049
		PPC_0050 | ASSERTIONS_PPC_0050| MEASURES_PPC_0050
		PPC_0051 | ASSERTIONS_PPC_0051| MEASURES_PPC_0051
		PPC_0052 | ASSERTIONS_PPC_0052| MEASURES_PPC_0052
		PPC_0053 | ASSERTIONS_PPC_0053| MEASURES_PPC_0053
		PPC_0054 | ASSERTIONS_PPC_0054| MEASURES_PPC_0054
		PPC_0055 | ASSERTIONS_PPC_0055| MEASURES_PPC_0055
		PPC_0056 | ASSERTIONS_PPC_0056| MEASURES_PPC_0056 
	}
}
