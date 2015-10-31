package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_MPM_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-mpm/Empty0001.xml" //missing DOB
    private static final Map MEASURES_EMPTY0001 = [C2619: [num: -1, denom: -1]]
	
    private static final String MPM0001 = "src/test/resources/samples/hedis-mpm/MPM0001.xml" //20 + 180 days wrong drugs, no tests
    private static final Map MEASURES_MPM0001 = [C2619: [num: 0, denom: 0], C3344: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C3346: [num: 0, denom: 0], 
		C3347: [num: 0, denom: 0], C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0] ]
	
	private static final String MPM0002 = "src/test/resources/samples/hedis-mpm/MPM0002.xml" //ace-i/arb 179 days NDC + 180 UUHC, no tests
	private static final Map MEASURES_MPM0002 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C3356: [num: 0, denom: 0], C2619: [num: 0, denom: 1], 
		C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 1], C3347: [num: 0, denom: 0] ]

	private static final String MPM0003 = "src/test/resources/samples/hedis-mpm/MPM0003.xml" //ace-i/arb 20 days NDC + 160 NDC, no tests
	private static final Map MEASURES_MPM0003 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 1]]

	private static final String MPM0004 = "src/test/resources/samples/hedis-mpm/MPM0004.xml" //ace-i/arb 20 days + 60 + 120 NDC -30 days overlap, no tests
	private static final Map MEASURES_MPM0004 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 0], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0005 = "src/test/resources/samples/hedis-mpm/MPM0005.xml" //ace-i/arb 180 days NDC, lab panel CPT
	private static final Map MEASURES_MPM0005 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 1, denom: 1]]

	private static final String MPM0006 = "src/test/resources/samples/hedis-mpm/MPM0006.xml" //ace-i/arb 180 days NDC, potassium LOINC, creatinine LOINC
	private static final Map MEASURES_MPM0006 = [ C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 1, denom: 1]]

	private static final String MPM0007 = "src/test/resources/samples/hedis-mpm/MPM0007.xml" //ace-i/arb 180 days NDC, potassium LOINC, blood urea LOINC
	private static final Map MEASURES_MPM0007 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 1, denom: 1]]

	private static final String MPM0008 = "src/test/resources/samples/hedis-mpm/MPM0008.xml" //ace-i/arb 180 days NDC, creatinine LOINC, blood urea LOINC
	private static final Map MEASURES_MPM0008 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 1]]

	private static final String MPM0009 = "src/test/resources/samples/hedis-mpm/MPM0009.xml" //ace-i/arb 180 days NDC, potassium LOINC, blood urea CPT
	private static final Map MEASURES_MPM0009 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 1, denom: 1]]

	private static final String MPM0010 = "src/test/resources/samples/hedis-mpm/MPM0010.xml" //180 days ace-i/arb, creatinine LOINC, blood urea CPT
	private static final Map MEASURES_MPM0010 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 1]]

	private static final String MPM0011 = "src/test/resources/samples/hedis-mpm/MPM0011.xml" //179 days digoxin
	private static final Map MEASURES_MPM0011 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 0], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0012 = "src/test/resources/samples/hedis-mpm/MPM0012.xml" //179 days + 1 day digoxin
	private static final Map MEASURES_MPM0012 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 1], C2619: [num: 0, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0013 = "src/test/resources/samples/hedis-mpm/MPM0013.xml" //180 days digoxin + lab panel
	private static final Map MEASURES_MPM0013 = [C3346: [num: 0, denom: 0], C3345: [num: 1, denom: 1], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0014 = "src/test/resources/samples/hedis-mpm/MPM0014.xml" //180 days digoxin + potassium
	private static final Map MEASURES_MPM0014 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 1], C2619: [num: 0, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0015 = "src/test/resources/samples/hedis-mpm/MPM0015.xml" //180 days digoxin + potassium + creatinine
	private static final Map MEASURES_MPM0015 = [C3346: [num: 0, denom: 0], C3345: [num: 1, denom: 1], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0016 = "src/test/resources/samples/hedis-mpm/MPM0016.xml" //180 days digoxin + potassium + blood urea
	private static final Map MEASURES_MPM0016 = [C3346: [num: 0, denom: 0], C3345: [num: 1, denom: 1], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0017 = "src/test/resources/samples/hedis-mpm/MPM0017.xml" //180 days digoxin + creatinine + blood urea
	private static final Map MEASURES_MPM0017 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 1], C2619: [num: 0, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0018 = "src/test/resources/samples/hedis-mpm/MPM0018.xml" //180 days digoxin + potassium + blood urea CPT
	private static final Map MEASURES_MPM0018 = [C3346: [num: 0, denom: 0], C3345: [num: 1, denom: 1], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0019 = "src/test/resources/samples/hedis-mpm/MPM0019.xml" //180 days digoxin + creatinine + blood urea CPT
	private static final Map MEASURES_MPM0019 = [C3346: [num: 0, denom: 0], C3345: [num: 1, denom: 1], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0020 = "src/test/resources/samples/hedis-mpm/MPM0020.xml" //180 days digoxin + potassium CPT + creatinine CPT
	private static final Map MEASURES_MPM0020 = [C3346: [num: 0, denom: 0], C3345: [num: 1, denom: 1], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0021 = "src/test/resources/samples/hedis-mpm/MPM0021.xml" //179 days diuretics
	private static final Map MEASURES_MPM0021 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 0], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0022 = "src/test/resources/samples/hedis-mpm/MPM0022.xml" //180 days diuretics
	private static final Map MEASURES_MPM0022 = [C3346: [num: 0, denom: 1], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0023 = "src/test/resources/samples/hedis-mpm/MPM0023.xml" //180 days diuretics + lab panel
	private static final Map MEASURES_MPM0023 = [C3346: [num: 1, denom: 1], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0024 = "src/test/resources/samples/hedis-mpm/MPM0024.xml" //180 days diuretics + potassium
	private static final Map MEASURES_MPM0024 = [C3346: [num: 0, denom: 1], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0025 = "src/test/resources/samples/hedis-mpm/MPM0025.xml" //180 days diuretics + potassium + creatinine
	private static final Map MEASURES_MPM0025 = [C3346: [num: 1, denom: 1], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0026 = "src/test/resources/samples/hedis-mpm/MPM0026.xml" //180 days diuretics + potassium + blood urea
	private static final Map MEASURES_MPM0026 = [C3346: [num: 1, denom: 1], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0027 = "src/test/resources/samples/hedis-mpm/MPM0027.xml" //180 days diuretics + creatinine + blood urea
	private static final Map MEASURES_MPM0027 = [C3346: [num: 0, denom: 1], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0028 = "src/test/resources/samples/hedis-mpm/MPM0028.xml" //180 days diuretics + potassium + blood urea CPT
	private static final Map MEASURES_MPM0028 = [C3346: [num: 1, denom: 1], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0029 = "src/test/resources/samples/hedis-mpm/MPM0029.xml" //180 days diuretics + creatinine + blood urea CPT
	private static final Map MEASURES_MPM0029 = [C3346: [num: 0, denom: 1], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0030 = "src/test/resources/samples/hedis-mpm/MPM0030.xml" //180 days diuretics + potassium CPT + creatinine CPT
	private static final Map MEASURES_MPM0030 = [C3346: [num: 1, denom: 1], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0031 = "src/test/resources/samples/hedis-mpm/MPM0031.xml" //180 days ace/arb, digoxin, diuretics, fire num-denoms for all of them
	private static final Map MEASURES_MPM0031 = [C3346: [num: 1, denom: 1], C3345: [num: 1, denom: 1], C2619: [num: 3, denom: 3], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 1, denom: 1]]
	
	private static final String MPM0032 = "src/test/resources/samples/hedis-mpm/MPM0032.xml" //180 days barbit, dibenzaz, hydantoin, misc, fire num-denoms for all of them
	private static final Map MEASURES_MPM0032 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 4, denom: 4], 
		C3356: [num: 1, denom: 1], C3357: [num: 1, denom: 1], C3358: [num: 1, denom: 1], C3359: [num: 1, denom: 1], C3344: [num: 0, denom: 0]]

	private static final String MPM0033 = "src/test/resources/samples/hedis-mpm/MPM0033.xml" //179 days barbit 
	private static final Map MEASURES_MPM0033 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 0], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0034 = "src/test/resources/samples/hedis-mpm/MPM0034.xml" //180 days barbit
	private static final Map MEASURES_MPM0034 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 1], 
		C3356: [num: 0, denom: 1], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0035 = "src/test/resources/samples/hedis-mpm/MPM0035.xml" //180 days barbit, barbit loinc
	private static final Map MEASURES_MPM0035 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 1, denom: 1], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0036 = "src/test/resources/samples/hedis-mpm/MPM0036.xml" //180 days barbit, barbit CPT
	private static final Map MEASURES_MPM0036 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 1, denom: 1], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0037 = "src/test/resources/samples/hedis-mpm/MPM0037.xml" //179 days dibenzaz
	private static final Map MEASURES_MPM0037 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 0], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0038 = "src/test/resources/samples/hedis-mpm/MPM0038.xml" //180 days dibenzaz
	private static final Map MEASURES_MPM0038 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 1], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0039 = "src/test/resources/samples/hedis-mpm/MPM0039.xml" //180 days dibenzaz, dibenzaz loinc
	private static final Map MEASURES_MPM0039 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 1, denom: 1], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0040 = "src/test/resources/samples/hedis-mpm/MPM0040.xml" //180 days dibenzaz, dibenzaz CPT
	private static final Map MEASURES_MPM0040 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 1, denom: 1], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0041 = "src/test/resources/samples/hedis-mpm/MPM0041.xml" //179 days hydantoin
	private static final Map MEASURES_MPM0041 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 0], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0042 = "src/test/resources/samples/hedis-mpm/MPM0042.xml" //180 days hydantoin
	private static final Map MEASURES_MPM0042 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 1], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0043 = "src/test/resources/samples/hedis-mpm/MPM0043.xml" //180 days hydantoin, hydantoin loinc
	private static final Map MEASURES_MPM0043 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 1, denom: 1], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]

	private static final String MPM0044 = "src/test/resources/samples/hedis-mpm/MPM0044.xml" //180 days hydantoin, hydantoin CPT
	private static final Map MEASURES_MPM0044 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 1, denom: 1], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]
	
	private static final String MPM0045 = "src/test/resources/samples/hedis-mpm/MPM0045.xml" //179 days misc
	private static final Map MEASURES_MPM0045 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 0], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 0], C3344: [num: 0, denom: 0]]
	
	private static final String MPM0046 = "src/test/resources/samples/hedis-mpm/MPM0046.xml" //180 days misc
	private static final Map MEASURES_MPM0046 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 0, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 0, denom: 1], C3344: [num: 0, denom: 0]]
	
	private static final String MPM0047 = "src/test/resources/samples/hedis-mpm/MPM0047.xml" //180 days misc, misc loinc
	private static final Map MEASURES_MPM0047 = [C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 1, denom: 1], C3344: [num: 0, denom: 0]]
	
	private static final String MPM0048 = "src/test/resources/samples/hedis-mpm/MPM0048.xml" //180 days misc, misc cpt
	private static final Map MEASURES_MPM0048 = [C3344: [num: 0, denom: 0], C3346: [num: 0, denom: 0], C3345: [num: 0, denom: 0], C2619: [num: 1, denom: 1], 
		C3356: [num: 0, denom: 0], C3357: [num: 0, denom: 0], C3358: [num: 0, denom: 0], C3359: [num: 1, denom: 1], C3344: [num: 0, denom: 0]]
	
	private static final String MPM0099 = "src/test/resources/samples/hedis-mpm/MPM0099.xml" //180 days ace/arb, digoxin, diuretics, anticonvulsants: fire num-denoms for all of them
	private static final Map MEASURES_MPM0099 = [C3344: [num: 1, denom: 1], C3345: [num: 1, denom: 1], C3346: [num: 1, denom: 1], C3347: [num: 4, denom: 4], 
		C3356: [num: 1, denom: 1], C3357: [num: 1, denom: 1], C3358: [num: 1, denom: 1], C3359: [num: 1, denom: 1], C2619: [num: 7, denom: 7]]


/*
	C2619: bad data
	C2850: diuretics
	C2852: digoxin
	C3230: barbiturates
	C3231: dibenzazepine
	C3232: hydantoin
	C3233: misc anticonvulsant
	C3261: ace-i / arb
	
	revised expectations for v2014.1.0:
C2619: QM HEDIS-MPM Annual Mon. Persistent Meds (C2619) for the total total (max 7/7)
C3347: QM HEDIS-MPM Annual Mon. Persistent Meds - Anticonvulsants (C3347) for the anticonvulsant total (max 4/4)

Note that the table-based Apelon concepts now use the QM HEDIS-based concepts:
C3356: QM HEDIS-MPM Annual Mon. Persistent Meds - Anticonvulsants-Barbiturate    C3356
C3357: QM HEDIS-MPM Annual Mon. Persistent Meds - Anticonvulsants-Dibenzazepine  C3357
C3358: QM HEDIS-MPM Annual Mon. Persistent Meds - Anticonvulsants-Hydantoin  C3358
C3359: QM HEDIS-MPM Annual Mon. Persistent Meds - Anticonvulsants-Miscellaneous C3359

C3344: QM HEDIS-MPM Annual Mon. Persistent Meds - ACE Inhibitor/ARB C3344
C3345: QM HEDIS-MPM Annual Mon. Persistent Meds - Digoxin C3345
C3346: QM HEDIS-MPM Annual Mon. Persistent Meds - Diuretics C3346

*/
	@Unroll
	def "test HEDIS MPM"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_MPM', version: '2014.1.0'],
			specifiedTime: '2012-01-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
		println responsePayload

		then:
		def data = new XmlSlurper().parseText(responsePayload)
		def results = VMRUtil.getResults(data, '\\|')
	/*	assertions.size() == results.assertions.size()
		assertions.each {entry ->
			assert results.assertions.containsKey(entry.key)
			if (entry.value) {
				assert results.assertions.get(entry.key) == entry.value
			}
		}*/
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
		vmr /*| assertions*/ | measures
		EMPTY0001 /*| ASSERTIONS_EMPTY0001*/| MEASURES_EMPTY0001 
		MPM0001 /*| ASSERTIONS_MPM0001*/| MEASURES_MPM0001
		MPM0002 /*| ASSERTIONS_MPM0002*/| MEASURES_MPM0002
		MPM0003 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0003
		MPM0004 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0004
		MPM0005 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0005
		MPM0006 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0006
		MPM0007 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0007
		MPM0008 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0008
		MPM0009 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0009
		MPM0010 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0010
		MPM0011 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0011
		MPM0012 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0012
		MPM0013 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0013
		MPM0014 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0014
		MPM0015 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0015
		MPM0016 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0016
		MPM0017 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0017
		MPM0018 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0018
		MPM0019 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0019
		MPM0020 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0020
		MPM0021 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0021
		MPM0022 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0022
		MPM0023 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0023
		MPM0024 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0024
		MPM0025 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0025
		MPM0026 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0026
		MPM0027 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0027
		MPM0028 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0028
		MPM0029 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0029
		MPM0030 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0030
		MPM0031 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0031
		MPM0032 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0032
		MPM0033 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0033
		MPM0034 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0034
		MPM0035 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0035
		MPM0036 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0036
		MPM0037 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0037
		MPM0038 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0038
		MPM0039 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0039
		MPM0040 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0040
		MPM0041 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0041
		MPM0042 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0042
		MPM0043 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0043
		MPM0044 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0044
		MPM0045 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0045
		MPM0046 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0046
		MPM0047 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0047
		MPM0048 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0048
		
		
		MPM0099 /*| ASSERTIONS_MPM0003*/| MEASURES_MPM0099
		
	}
}
