package org.opencds.service.HEDIS_2015_0;

import java.util.Map;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_FPC_SecondDelivery_FunctionalSpec extends Specification 
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
    /* 0 - Denom check: HEDIS Delivery by ICD9Dx just before high cutoff */
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0004 = ["O.01":'']
    private static final Map MEASURES_FPC_0004 = [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]
	
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
	/* 0 - Denom check: HEDIS Delivery by ICD9Dx, just after low cutoff	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0007 =  ["O.01":'']
    private static final Map MEASURES_FPC_0007 =  [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]

	
    private static final String FPC_0008 = "src/test/resources/samples/hedis-fpc/FPC_008.xml" 
	/* 0 - Denom check: HEDIS Delivery by CPT, just after low cutoff	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0008 =  ["O.01":'']
    private static final Map MEASURES_FPC_0008 =  [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]
	
    private static final String FPC_0009 = "src/test/resources/samples/hedis-fpc/FPC_009.xml" 
    /* 0 - Denom check: HEDIS Delivery by ICD9Px, just after low cutoff, w/gestational age = 8	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0009 =  ["O.01":'']
    private static final Map MEASURES_FPC_0009 =  [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]
	
    private static final String FPC_0010 = "src/test/resources/samples/hedis-fpc/FPC_010.xml" 
	/* 0 - Denom check: HEDIS Delivery by ICD9Px, not live birth	*/
	/* 0 - Num check: nothing  */
	private static final Map ASSERTIONS_FPC_0010 = ["O.01":'']
    private static final Map MEASURES_FPC_0010 = [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]

	
	private static final String FPC_0011 = "src/test/resources/samples/hedis-fpc/FPC_011.xml" 
	/* 0 - Denom check: HEDIS Delivery by ICD9Px	*/
	/* 0 - Num check: one standalone prenatal visit by CPT, w/provider, 1 month before delivery  */
	private static final Map ASSERTIONS_FPC_0011 =  ["O.01":'']
    private static final Map MEASURES_FPC_0011 =  [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]
	
    private static final String FPC_0012 = "src/test/resources/samples/hedis-fpc/FPC_012.xml" 
	/* 0 - Denom check: HEDIS Delivery by ICD9Px	*/
	/* 0 - Num check: one standalone prenatal visit by CPT, w/o provider, 1 month before delivery  */
	private static final Map ASSERTIONS_FPC_0012 =  ["O.01":'']
    private static final Map MEASURES_FPC_0012 =  [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]

	
	private static final String FPC_0013 = "src/test/resources/samples/hedis-fpc/FPC_013.xml" 
	/* 0 - Denom check: HEDIS Delivery by ICD9Px	*/
	/* 0 - Num check: one standalone prenatal visit by HCPCS, w/ provider, 1 month before delivery  */
	private static final Map ASSERTIONS_FPC_0013 =  ["O.01":'']
    private static final Map MEASURES_FPC_0013 =  [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]
	
    private static final String FPC_0014 = "src/test/resources/samples/hedis-fpc/FPC_014.xml" 
	/* 0 - Denom check: HEDIS Delivery by ICD9Px	*/
	/* 0 - Num check: one standalone prenatal visit by HCPCS, w/o provider, 1 month before delivery  */
	private static final Map ASSERTIONS_FPC_0014 =  ["O.01":'']
    private static final Map MEASURES_FPC_0014 =  [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]
	
    private static final String FPC_0015 = "src/test/resources/samples/hedis-fpc/FPC_015.xml" 
    /* 0 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43	*/
	/* 0 - Num check: full set standalone prenatal visit by HCPCS, w/ provider, count of 17 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0015 =  ["O.01":'']	
    private static final Map MEASURES_FPC_0015 =  [C3386: [num: 0, denom: 0], C3387: [num: 0, denom: 0], C3388: [num: 0, denom: 0], 
												 C3389: [num: 0, denom: 0], C3390: [num: 0, denom: 0]]

	
	private static final String FPC_0016 = "src/test/resources/samples/hedis-fpc/FPC_016.xml" 
    /* 1 - Denom check: HEDIS Delivery by ICD9Px, gestation age 43, 2nd delivery by ICD9Px, gestational age 38	*/
	/* 1 - Num check: full set standalone prenatal visit by HCPCS, w/ provider, count of 17 visits before delivery  */
	private static final Map ASSERTIONS_FPC_0016 = [C3308: '', Percent: '100', PrenatalVisitDistinctDateCount: '17']	
    private static final Map MEASURES_FPC_0016 = [C3386: [num: 0, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1], 
												 C3389: [num: 0, denom: 1], C3390: [num: 1, denom: 1]]
	
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
	def "test HEDIS FPC_SecondDelivery v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_FPC_SecondDelivery', version: '2014.1.0'],
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
	}
}
