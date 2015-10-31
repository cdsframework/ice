package org.opencds.service.HEDIS_2015_0;

import java.util.Map;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_FPC_FirstDelivery_FunctionalSpec extends Specification 
{
	private static final String FPC_0000 = "src/test/resources/samples/hedis-fpc/FPC_000.xml"
	/* 0 : Denom check: Missing DOB value, this is NOT invalid data for this measure 	*/
	/* 0 : Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0000 = ["O.01":'']
	private static final Map MEASURES_FPC_0000 = [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]

	
    private static final String FPC_0001 = "src/test/resources/samples/hedis-fpc/FPC_001.xml" 
	/* 0 : Denom check: Gender Male value, this is NOT invalid data for this measure*/
	/* 0 : Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0001 = ["O.01":'']
    private static final Map MEASURES_FPC_0001 = [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]

	
    private static final String FPC_0002 = "src/test/resources/samples/hedis-fpc/FPC_002.xml" 
    /* 0 : Denom check: Missing Gender value, this is NOT invalid data for this measure 	*/
	/* 0 : Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0002 = ["O.01":'']
    private static final Map MEASURES_FPC_0002 = [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]
	
    private static final String FPC_0003 = "src/test/resources/samples/hedis-fpc/FPC_003.xml" 
    /* 0 : Denom check: Missing Gender value and DOB, this is NOT invalid data for this measure 	*/
	/* 0 : Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0003 = ["O.01":'']
    private static final Map MEASURES_FPC_0003 = [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]

	
    private static final String FPC_0004 = "src/test/resources/samples/hedis-fpc/FPC_004.xml" 
    /* 1 - Denom check: HEDIS Delivery by ICD9Dx just before high cutoff */
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0004 = ["O.01":'', Percent: '0', PrenatalVisitDistinctDateCount: '0']
    private static final Map MEASURES_FPC_0004 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
    private static final String FPC_0005 = "src/test/resources/samples/hedis-fpc/FPC_005.xml" 
	/* 0 - Denom check: HEDIS Delivery by ICD9Dx, just before low cutoff	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0005 = ["O.01":'']
    private static final Map MEASURES_FPC_0005 = [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]

	
	private static final String FPC_0006 = "src/test/resources/samples/hedis-fpc/FPC_006.xml" 
    /* 0 - Denom check: HEDIS Delivery by ICD9Dx, just after high cutoff	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0006 = ["O.01":'']
    private static final Map MEASURES_FPC_0006 = [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]
	
    private static final String FPC_0007 = "src/test/resources/samples/hedis-fpc/FPC_007.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Dx, just after low cutoff	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0007 = ["O.01":'', Percent: '0', PrenatalVisitDistinctDateCount: '0']
    private static final Map MEASURES_FPC_0007 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]

	
    private static final String FPC_0008 = "src/test/resources/samples/hedis-fpc/FPC_008.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0008 = ["O.01":'', Percent: '0', PrenatalVisitDistinctDateCount: '0']
    private static final Map MEASURES_FPC_0008 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
    private static final String FPC_0009 = "src/test/resources/samples/hedis-fpc/FPC_009.xml" 
    /* 1 - Denom check: HEDIS Delivery by ICD9Px, just after low cutoff, w/gestational age = 8	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0009 = ["O.01":'', Percent: '0', PrenatalVisitDistinctDateCount: '0']
    private static final Map MEASURES_FPC_0009 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
    private static final String FPC_0010 = "src/test/resources/samples/hedis-fpc/FPC_010.xml" 
	/* 0 - Denom check: HEDIS Delivery by ICD9Px, not live birth	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0010 = ["O.01":'']
    private static final Map MEASURES_FPC_0010 = [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]

	
	private static final String FPC_0011 = "src/test/resources/samples/hedis-fpc/FPC_011.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
	/* 1 - Num check: one standalone prenatal visit by CPT, w/provider, 1 month before delivery  */
	private static final Map ASSERTIONS_FPC_0011 = [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']
    private static final Map MEASURES_FPC_0011 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
    private static final String FPC_0012 = "src/test/resources/samples/hedis-fpc/FPC_012.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
	/* 0 - Num check: one standalone prenatal visit by CPT, w/o provider, 1 month before delivery  */
	private static final Map ASSERTIONS_FPC_0012 = [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']
    private static final Map MEASURES_FPC_0012 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]

	
	private static final String FPC_0013 = "src/test/resources/samples/hedis-fpc/FPC_013.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
	/* 1 - Num check: one standalone prenatal visit by HCPCS, w/ provider, 1 month before delivery  */
	private static final Map ASSERTIONS_FPC_0013 = [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']
    private static final Map MEASURES_FPC_0013 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
    private static final String FPC_0014 = "src/test/resources/samples/hedis-fpc/FPC_014.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
	/* 0 - Num check: one standalone prenatal visit by HCPCS, w/o provider, 1 month before delivery  */
	private static final Map ASSERTIONS_FPC_0014 = [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']
    private static final Map MEASURES_FPC_0014 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
    private static final String FPC_0015 = "src/test/resources/samples/hedis-fpc/FPC_015.xml" 
    /* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: full set standalone prenatal visit by HCPCS, w/ provider, count of 17 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0015 = [C3308: '', Percent: '100', PrenatalVisitDistinctDateCount: '17']	
    private static final Map MEASURES_FPC_0015 = [C3386: [num: 0, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 1, denom: 1]]

	
	private static final String FPC_0016 = "src/test/resources/samples/hedis-fpc/FPC_016.xml" 
    /* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43, 2nd delivery by ICD9Px, gestational age 38	*/
	/* 1 - Num check: full set standalone prenatal visit by HCPCS, w/ provider, count of 17 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0016 = [C3308: '', Percent: '100', PrenatalVisitDistinctDateCount: '17']	
    private static final Map MEASURES_FPC_0016 = [C3386: [num: 0, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 1, denom: 1]]
	
	private static final String FPC_0017 = "src/test/resources/samples/hedis-fpc/FPC_017.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: OB Panel 1st trimester by CPT  */
	private static final Map ASSERTIONS_FPC_0017 = [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']
    private static final Map MEASURES_FPC_0017 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
    private static final String FPC_0018 = "src/test/resources/samples/hedis-fpc/FPC_018.xml" 
    /* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: Ultrasound 1st trimester by CPT */
	private static final Map ASSERTIONS_FPC_0018 = [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']
    private static final Map MEASURES_FPC_0018 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0019 = "src/test/resources/samples/hedis-fpc/FPC_019.xml" 
    /* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: Pregnancy Dx 1st trimester  */
	private static final Map ASSERTIONS_FPC_0019 = [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']
	private static final Map MEASURES_FPC_0019 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0020 = "src/test/resources/samples/hedis-fpc/FPC_020.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: OB Panel 1st trimester by ICD9Px  */
	private static final Map ASSERTIONS_FPC_0020 = [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']
	private static final Map MEASURES_FPC_0020 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0021 = "src/test/resources/samples/hedis-fpc/FPC_021.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: antibody tests 1st trimester by CPT  */
	private static final Map ASSERTIONS_FPC_0021 = [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']
	private static final Map MEASURES_FPC_0021 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0022 = "src/test/resources/samples/hedis-fpc/FPC_022.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: antibody tests 1st trimester by LOINC  */
	private static final Map ASSERTIONS_FPC_0022 = [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']
	private static final Map MEASURES_FPC_0022 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0023 = "src/test/resources/samples/hedis-fpc/FPC_023.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: antibody tests 1st trimester by LOINC, minus Toxoplasma  */
	private static final Map ASSERTIONS_FPC_0023 = [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']
	private static final Map MEASURES_FPC_0023 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0024 = "src/test/resources/samples/hedis-fpc/FPC_024.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: antibody tests 1st trimester by LOINC, minus Rubella  */
	private static final Map ASSERTIONS_FPC_0024 = [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']
	private static final Map MEASURES_FPC_0024 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0025 = "src/test/resources/samples/hedis-fpc/FPC_025.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: antibody tests 1st trimester by LOINC, minus cytomegalovirus  */
	private static final Map ASSERTIONS_FPC_0025 = [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']
	private static final Map MEASURES_FPC_0025 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0026 = "src/test/resources/samples/hedis-fpc/FPC_026.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: antibody tests 1st trimester by LOINC, minus herpes simplex  */
	private static final Map ASSERTIONS_FPC_0026 = [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']
	private static final Map MEASURES_FPC_0026 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0027 = "src/test/resources/samples/hedis-fpc/FPC_027.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: rubella and ABO in 1st trimester by LOINC */
	private static final Map ASSERTIONS_FPC_0027 = [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']
	private static final Map MEASURES_FPC_0027 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0028 = "src/test/resources/samples/hedis-fpc/FPC_028.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: rubella and ABO in 1st trimester by CPT */
	private static final Map ASSERTIONS_FPC_0028 = [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']
	private static final Map MEASURES_FPC_0028 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0029 = "src/test/resources/samples/hedis-fpc/FPC_029.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: rubella and ABO in 1st trimester by CPT, minus rubella */
	private static final Map ASSERTIONS_FPC_0029 = [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']	
	private static final Map MEASURES_FPC_0029 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0030 = "src/test/resources/samples/hedis-fpc/FPC_030.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: rubella and ABO in 1st trimester by CPT, minus ABO */
	private static final Map ASSERTIONS_FPC_0030 = [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']
	private static final Map MEASURES_FPC_0030 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0031 = "src/test/resources/samples/hedis-fpc/FPC_031.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: rubella and Rh in 1st trimester by CPT */
	private static final Map ASSERTIONS_FPC_0031 = [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']
	private static final Map MEASURES_FPC_0031 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0032 = "src/test/resources/samples/hedis-fpc/FPC_032.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: rubella and Rh in 1st trimester by LOINC */
	private static final Map ASSERTIONS_FPC_0032 = [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']	
	private static final Map MEASURES_FPC_0032 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0033 = "src/test/resources/samples/hedis-fpc/FPC_033.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: rubella and Rh in 1st trimester by LOINC, minus rubella */
	private static final Map ASSERTIONS_FPC_0033 = [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']
	private static final Map MEASURES_FPC_0033 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0034 = "src/test/resources/samples/hedis-fpc/FPC_034.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: rubella by CPT and ABO_RH by LOINC in 1st trimester */
	private static final Map ASSERTIONS_FPC_0034 = [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']
	private static final Map MEASURES_FPC_0034 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0035 = "src/test/resources/samples/hedis-fpc/FPC_035.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 1 - Num check: rubella by LOINC and ABO_RH by LOINC in 1st trimester */
	private static final Map ASSERTIONS_FPC_0035 = [C3304: '', Percent: '7', PrenatalVisitDistinctDateCount: '1']
	private static final Map MEASURES_FPC_0035 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0036 = "src/test/resources/samples/hedis-fpc/FPC_036.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: ABO_RH by LOINC in 1st trimester */
	private static final Map ASSERTIONS_FPC_0036 = [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']
	private static final Map MEASURES_FPC_0036 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0037 = "src/test/resources/samples/hedis-fpc/FPC_037.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT, encounter before, procedure after low cutoff	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0037 = [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']
	private static final Map MEASURES_FPC_0037 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0038 = "src/test/resources/samples/hedis-fpc/FPC_038.xml" 
	/* 0 - Denom check: HEDIS Delivery by CPT, encounter before, procedure after high cutoff	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0038 = ["O.01":'']
	private static final Map MEASURES_FPC_0038 = [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]
		
	private static final String FPC_0039 = "src/test/resources/samples/hedis-fpc/FPC_039.xml" 
	/* 1 - Denom check: HEDIS Delivery by CPT	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0039 = [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']
	private static final Map MEASURES_FPC_0039 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]

	private static final String FPC_0040 = "src/test/resources/samples/hedis-fpc/FPC_040.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check:  set standalone prenatal visit by HCPCS, w/ provider, count of 16 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0040 = [C3308: '', Percent: '94', PrenatalVisitDistinctDateCount: '16']
	private static final Map MEASURES_FPC_0040 = [C3386: [num: 0, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 1, denom: 1]]
	
	private static final String FPC_0041 = "src/test/resources/samples/hedis-fpc/FPC_041.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check:  set standalone prenatal visit by HCPCS, w/ provider, count of 15 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0041 = [C3308: '', Percent: '88', PrenatalVisitDistinctDateCount: '15']
	private static final Map MEASURES_FPC_0041 = [C3386: [num: 0, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 1, denom: 1]]
	
	private static final String FPC_0042 = "src/test/resources/samples/hedis-fpc/FPC_042.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 14 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0042 = [C3308: '', Percent: '82', PrenatalVisitDistinctDateCount: '14']	
	private static final Map MEASURES_FPC_0042 = [C3386: [num: 0, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 1, denom: 1]]
	
	private static final String FPC_0043 = "src/test/resources/samples/hedis-fpc/FPC_043.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 13 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0043 = [C3307: '', Percent: '76', PrenatalVisitDistinctDateCount: '13']
	private static final Map MEASURES_FPC_0043 = [C3386: [num: 0, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 1, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0044 = "src/test/resources/samples/hedis-fpc/FPC_044.xml"
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 12 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0044 = [C3307: '', Percent: '71', PrenatalVisitDistinctDateCount: '12']	
	private static final Map MEASURES_FPC_0044 = [C3386: [num: 0, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 1, denom: 1], C3390: [num: 0, denom: 1]]
		
	private static final String FPC_0045 = "src/test/resources/samples/hedis-fpc/FPC_045.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 11 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0045 = [C3307: '', Percent: '65', PrenatalVisitDistinctDateCount: '11']
	private static final Map MEASURES_FPC_0045 = [C3386: [num: 0, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 1, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0046 = "src/test/resources/samples/hedis-fpc/FPC_046.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 10 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0046 = [C3306: '', Percent: '59', PrenatalVisitDistinctDateCount: '10']
	private static final Map MEASURES_FPC_0046 = [C3386: [num: 0, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 1, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0047 = "src/test/resources/samples/hedis-fpc/FPC_047.xml" 
    /* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 9 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0047 = [C3306: '', Percent: '53', PrenatalVisitDistinctDateCount: '9']
	private static final Map MEASURES_FPC_0047 = [C3386: [num: 0, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 1, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0048 = "src/test/resources/samples/hedis-fpc/FPC_048.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 8 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0048 = [C3306: '', Percent: '47', PrenatalVisitDistinctDateCount: '8']
	private static final Map MEASURES_FPC_0048 = [C3386: [num: 0, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 1, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0049 = "src/test/resources/samples/hedis-fpc/FPC_049.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 7 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0049 = [C3306: '', Percent: '41', PrenatalVisitDistinctDateCount: '7']
	private static final Map MEASURES_FPC_0049 = [C3386: [num: 0, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 1, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]

	private static final String FPC_0050 = "src/test/resources/samples/hedis-fpc/FPC_050.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 5 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0050 = [C3305: '', Percent: '35', PrenatalVisitDistinctDateCount: '6']
	private static final Map MEASURES_FPC_0050 = [C3386: [num: 0, denom: 1], C3387: [num: 1, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0051 = "src/test/resources/samples/hedis-fpc/FPC_051.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 5 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0051 = [C3305: '', Percent: '29', PrenatalVisitDistinctDateCount: '5']
	private static final Map MEASURES_FPC_0051 = [C3386: [num: 0, denom: 1], C3387: [num: 1, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0052 = "src/test/resources/samples/hedis-fpc/FPC_052.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 4 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0052 = [C3305: '', Percent: '24', PrenatalVisitDistinctDateCount: '4']
	private static final Map MEASURES_FPC_0052 = [C3386: [num: 0, denom: 1], C3387: [num: 1, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0053 = "src/test/resources/samples/hedis-fpc/FPC_053.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 3 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0053 = [C3304: '', Percent: '18', PrenatalVisitDistinctDateCount: '3']
	private static final Map MEASURES_FPC_0053 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0054 = "src/test/resources/samples/hedis-fpc/FPC_054.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 2 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0054 = [C3304: '', Percent: '12', PrenatalVisitDistinctDateCount: '2']
	private static final Map MEASURES_FPC_0054 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
		
	private static final String FPC_0055 = "src/test/resources/samples/hedis-fpc/FPC_055.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 1 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0055 = [C3304: '', Percent: '6', PrenatalVisitDistinctDateCount: '1']
	private static final Map MEASURES_FPC_0055 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	
	private static final String FPC_0056 = "src/test/resources/samples/hedis-fpc/FPC_056.xml" 
	/* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 1 - Num check: set standalone prenatal visit by HCPCS, w/ provider, count of 0 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0056 = [C3304: '', Percent: '0', PrenatalVisitDistinctDateCount: '0']
	private static final Map MEASURES_FPC_0056 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]
	

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
"	C2895 -> HEDIS-Stand Alone Prenatal Visits"
"	C2899 -> HEDIS-Pregnancy Diagnosis"
"	C3023 -> HEDIS-Obstetric Panel"
"	C3037 -> HEDIS-Prenatal Ultrasound"
"	C3265 -> Named Dates Inserted"
"	C3295 -> Encounter with Delivery in Relaxed Timeframe"
"	C3296 -> Two Encounters with Deliveries in Relaxed Timeframe"
"	C3298 -> QM HEDIS-FPC First Delivery"
"	C3304 -> Percent LT 21"
"	C3305 -> Percent GE 21 LE 40"
"	C3306 -> Percent GE 41 LE 60"
"	C3307 -> Percent GE 61 LE 80"
"	C3308 -> Percent GE 81"
"	C3386 -> QM HEDIS-FPC 0-20 Percent"
"	C3387 -> QM HEDIS-FPC 21-40 Percent"
"	C3388 -> QM HEDIS-FPC 41-60 Percent"
"	C3389 -> QM HEDIS-FPC 61-80 Percent"
"	C3390 -> QM HEDIS-FPC 81-100 Percent"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"   C569 -> Missing Data for Date of Birth"

*/
	
	@Unroll
	def "test HEDIS FPC_FirstDelivery v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_FPC_FirstDelivery', version: '2014.1.0'],
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
		FPC_0000 | ASSERTIONS_FPC_0000| MEASURES_FPC_0000
		FPC_0001 | ASSERTIONS_FPC_0001| MEASURES_FPC_0001
		FPC_0002 | ASSERTIONS_FPC_0002| MEASURES_FPC_0002
		FPC_0003 | ASSERTIONS_FPC_0003| MEASURES_FPC_0003
		FPC_0004 | ASSERTIONS_FPC_0004| MEASURES_FPC_0004
		FPC_0005 | ASSERTIONS_FPC_0005| MEASURES_FPC_0005
		FPC_0006 | ASSERTIONS_FPC_0006| MEASURES_FPC_0006
		FPC_0007 | ASSERTIONS_FPC_0007| MEASURES_FPC_0007
		FPC_0008 | ASSERTIONS_FPC_0008| MEASURES_FPC_0008
		FPC_0009 | ASSERTIONS_FPC_0009| MEASURES_FPC_0009
		FPC_0010 | ASSERTIONS_FPC_0010| MEASURES_FPC_0010
		FPC_0011 | ASSERTIONS_FPC_0011| MEASURES_FPC_0011
		FPC_0012 | ASSERTIONS_FPC_0012| MEASURES_FPC_0012
		FPC_0013 | ASSERTIONS_FPC_0013| MEASURES_FPC_0013
		FPC_0014 | ASSERTIONS_FPC_0014| MEASURES_FPC_0014
		FPC_0015 | ASSERTIONS_FPC_0015| MEASURES_FPC_0015
		FPC_0016 | ASSERTIONS_FPC_0016| MEASURES_FPC_0016
		FPC_0017 | ASSERTIONS_FPC_0017| MEASURES_FPC_0017
		FPC_0018 | ASSERTIONS_FPC_0018| MEASURES_FPC_0018
		FPC_0019 | ASSERTIONS_FPC_0019| MEASURES_FPC_0019
		FPC_0020 | ASSERTIONS_FPC_0020| MEASURES_FPC_0020
		FPC_0021 | ASSERTIONS_FPC_0021| MEASURES_FPC_0021
		FPC_0022 | ASSERTIONS_FPC_0022| MEASURES_FPC_0022
		FPC_0023 | ASSERTIONS_FPC_0023| MEASURES_FPC_0023
		FPC_0024 | ASSERTIONS_FPC_0024| MEASURES_FPC_0024
		FPC_0025 | ASSERTIONS_FPC_0025| MEASURES_FPC_0025
		FPC_0026 | ASSERTIONS_FPC_0026| MEASURES_FPC_0026
		FPC_0027 | ASSERTIONS_FPC_0027| MEASURES_FPC_0027
		FPC_0028 | ASSERTIONS_FPC_0028| MEASURES_FPC_0028
		FPC_0029 | ASSERTIONS_FPC_0029| MEASURES_FPC_0029
		FPC_0030 | ASSERTIONS_FPC_0030| MEASURES_FPC_0030
		FPC_0031 | ASSERTIONS_FPC_0031| MEASURES_FPC_0031
		FPC_0032 | ASSERTIONS_FPC_0032| MEASURES_FPC_0032
		FPC_0033 | ASSERTIONS_FPC_0033| MEASURES_FPC_0033
		FPC_0034 | ASSERTIONS_FPC_0034| MEASURES_FPC_0034
		FPC_0035 | ASSERTIONS_FPC_0035| MEASURES_FPC_0035
		FPC_0036 | ASSERTIONS_FPC_0036| MEASURES_FPC_0036
		FPC_0037 | ASSERTIONS_FPC_0037| MEASURES_FPC_0037
		FPC_0038 | ASSERTIONS_FPC_0038| MEASURES_FPC_0038
		FPC_0039 | ASSERTIONS_FPC_0039| MEASURES_FPC_0039
		FPC_0040 | ASSERTIONS_FPC_0040| MEASURES_FPC_0040
		FPC_0041 | ASSERTIONS_FPC_0041| MEASURES_FPC_0041
		FPC_0042 | ASSERTIONS_FPC_0042| MEASURES_FPC_0042
		FPC_0043 | ASSERTIONS_FPC_0043| MEASURES_FPC_0043
		FPC_0044 | ASSERTIONS_FPC_0044| MEASURES_FPC_0044
		FPC_0045 | ASSERTIONS_FPC_0045| MEASURES_FPC_0045
		FPC_0046 | ASSERTIONS_FPC_0046| MEASURES_FPC_0046
		FPC_0047 | ASSERTIONS_FPC_0047| MEASURES_FPC_0047
		FPC_0048 | ASSERTIONS_FPC_0048| MEASURES_FPC_0048
		FPC_0049 | ASSERTIONS_FPC_0049| MEASURES_FPC_0049
		FPC_0050 | ASSERTIONS_FPC_0050| MEASURES_FPC_0050
		FPC_0051 | ASSERTIONS_FPC_0051| MEASURES_FPC_0051
		FPC_0052 | ASSERTIONS_FPC_0052| MEASURES_FPC_0052
		FPC_0053 | ASSERTIONS_FPC_0053| MEASURES_FPC_0053
		FPC_0054 | ASSERTIONS_FPC_0054| MEASURES_FPC_0054
		FPC_0055 | ASSERTIONS_FPC_0055| MEASURES_FPC_0055
		FPC_0056 | ASSERTIONS_FPC_0056| MEASURES_FPC_0056 
	}
}
