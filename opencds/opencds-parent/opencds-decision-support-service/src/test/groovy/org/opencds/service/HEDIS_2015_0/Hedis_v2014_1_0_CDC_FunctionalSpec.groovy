package org.opencds.service.HEDIS_2015_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_CDC_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2580: [num: -1, denom: -1]]
	
    private static final String CDC0001 = "src/test/resources/samples/hedis-cdc/CDC0001.xml" 
	//Denom check: acute inpatient encounter : CPT=99223 from 0-2 years ago EncDx diabetes ICD9CM: 250 and 22 years old, female (denomMet)
	private static final Map ASSERTIONS_CDC0001 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
    private static final Map MEASURES_CDC0001 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1], 
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]

	
    private static final String CDC0002 = "src/test/resources/samples/hedis-cdc/CDC0002.xml" 
	//Denom check: acute inpatient encounter :ubrev from 0-2 years ago EncDx diabetes ICD9CM: 250 and 22 years old, female (denomMet)
	private static final Map ASSERTIONS_CDC0002 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
    private static final Map MEASURES_CDC0002 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1], 
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
    private static final String CDC0003 = "src/test/resources/samples/hedis-cdc/CDC0003.xml" 
	//Denom check: acute inpatient encounter : CPT=99223 from 0-2 years ago EncDx diabetes ICD9CM: 250 and 22 years old, female (denomMet)
	private static final Map ASSERTIONS_CDC0003 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',EmergencyDepartmentEncounters:'1']
    private static final Map MEASURES_CDC0003 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1], 
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]

	
    private static final String CDC0004 = "src/test/resources/samples/hedis-cdc/CDC0004.xml" 
	// Denom check: ED encounter : from 0-2 years ago EncDx diabetes  and 22 years old, female (denomMet)
	private static final Map ASSERTIONS_CDC0004 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',EmergencyDepartmentEncounters:'1']
    private static final Map MEASURES_CDC0004 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1], 
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
    private static final String CDC0005 = "src/test/resources/samples/hedis-cdc/CDC0005.xml" 
	// Denom not Met
	private static final Map ASSERTIONS_CDC0005 = [C2606:'']
    private static final Map MEASURES_CDC0005 = [C3201: [num: 0, denom: 0], C3202: [num: 0, denom: 0], C3203: [num: 0, denom: 0], 
												 C3204: [num: 0, denom: 0], C3205: [num: 0, denom: 0], C3207: [num: 0, denom: 0],
												 C3208: [num: 0, denom: 0], C3209: [num: 0, denom: 0], C3210: [num: 0, denom: 0]]

	
	private static final String CDC0006 = "src/test/resources/samples/hedis-cdc/CDC0006.xml" 
	// Denom not Met
	private static final Map ASSERTIONS_CDC0006 = [C2606:'']
    private static final Map MEASURES_CDC0006 = [C3201: [num: 0, denom: 0], C3202: [num: 0, denom: 0], C3203: [num: 0, denom: 0], 
												 C3204: [num: 0, denom: 0], C3205: [num: 0, denom: 0], C3207: [num: 0, denom: 0],
												 C3208: [num: 0, denom: 0], C3209: [num: 0, denom: 0], C3210: [num: 0, denom: 0]]
	
    private static final String CDC0007 = "src/test/resources/samples/hedis-cdc/CDC0007.xml" 
	// Denom check: CPT Observation and Outpatient encounters in 2 different years in time frame EncDx diabetes  and 22 years old, female (denomMet)
	private static final Map ASSERTIONS_CDC0007 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2']
    private static final Map MEASURES_CDC0007 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1], 
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]

	
    private static final String CDC0008 = "src/test/resources/samples/hedis-cdc/CDC0008.xml" 
	// Denom check: CPT nonacute inpt and HCPCS Outpatient encounters in same year in time frame EncDx diabetes  and 22 years old, female (denomMet)
	private static final Map ASSERTIONS_CDC0008 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2']
    private static final Map MEASURES_CDC0008 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1], 
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
    private static final String CDC0009 = "src/test/resources/samples/hedis-cdc/CDC0009.xml" 
	// Denom check: ubrev nonacute inpt and ubrev Outpatient encounters in same year in time frame EncDx diabetes  and 22 years old, female (denomMet)
	private static final Map ASSERTIONS_CDC0009 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2']
    private static final Map MEASURES_CDC0009 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1], 
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
    private static final String CDC0010 = "src/test/resources/samples/hedis-cdc/CDC0010.xml" 
	// Denom not Met
	private static final Map ASSERTIONS_CDC0010 = [C2606:'']
    private static final Map MEASURES_CDC0010 = [C3201: [num: 0, denom: 0], C3202: [num: 0, denom: 0], C3203: [num: 0, denom: 0], 
												 C3204: [num: 0, denom: 0], C3205: [num: 0, denom: 0], C3207: [num: 0, denom: 0],
												 C3208: [num: 0, denom: 0], C3209: [num: 0, denom: 0], C3210: [num: 0, denom: 0]]

	
	private static final String CDC0011 = "src/test/resources/samples/hedis-cdc/CDC0011.xml" 
	// Denom not Met
	private static final Map ASSERTIONS_CDC0011 = [C2606:'']
    private static final Map MEASURES_CDC0011 = [C3201: [num: 0, denom: 0], C3202: [num: 0, denom: 0], C3203: [num: 0, denom: 0], 
												 C3204: [num: 0, denom: 0], C3205: [num: 0, denom: 0], C3207: [num: 0, denom: 0],
												 C3208: [num: 0, denom: 0], C3209: [num: 0, denom: 0], C3210: [num: 0, denom: 0]]
	
    private static final String CDC0012 = "src/test/resources/samples/hedis-cdc/CDC0012.xml" 
	// denom check: too young <18years on dec 31, 2011- born jan 1, 1994 - (denomNotMet)
	private static final Map ASSERTIONS_CDC0012 = ["O.01":'']
    private static final Map MEASURES_CDC0012 = [C3201: [num: 0, denom: 0], C3202: [num: 0, denom: 0], C3203: [num: 0, denom: 0], 
												 C3204: [num: 0, denom: 0], C3205: [num: 0, denom: 0], C3207: [num: 0, denom: 0],
												 C3208: [num: 0, denom: 0], C3209: [num: 0, denom: 0], C3210: [num: 0, denom: 0]]

	
	private static final String CDC0013 = "src/test/resources/samples/hedis-cdc/CDC0013.xml" 
	// denom check: too old  not <75years on dec 31, 2011- born dec 31, 1936 (denomNotMet)
	private static final Map ASSERTIONS_CDC0013 = ["O.01":'']
    private static final Map MEASURES_CDC0013 = [C3201: [num: 0, denom: 0], C3202: [num: 0, denom: 0], C3203: [num: 0, denom: 0], 
												 C3204: [num: 0, denom: 0], C3205: [num: 0, denom: 0], C3207: [num: 0, denom: 0],
												 C3208: [num: 0, denom: 0], C3209: [num: 0, denom: 0], C3210: [num: 0, denom: 0]]
	
    private static final String CDC0014 = "src/test/resources/samples/hedis-cdc/CDC0014.xml" 
	// Denomenator check:  pharmacy -expect Denomenator met
	private static final Map ASSERTIONS_CDC0014 = [C2520:'',C2606:'',C2607:'',C54:'',C545:'']
    private static final Map MEASURES_CDC0014 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1], 
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
    private static final String CDC0015 = "src/test/resources/samples/hedis-cdc/CDC0015.xml" 
	// Denomenator check:  pharmacy too long ago	-expect Denomenator not met
	private static final Map ASSERTIONS_CDC0015 = [C2606:'']
    private static final Map MEASURES_CDC0015 = [C3201: [num: 0, denom: 0], C3202: [num: 0, denom: 0], C3203: [num: 0, denom: 0], 
												 C3204: [num: 0, denom: 0], C3205: [num: 0, denom: 0], C3207: [num: 0, denom: 0],
												 C3208: [num: 0, denom: 0], C3209: [num: 0, denom: 0], C3210: [num: 0, denom: 0]]

	
	private static final String CDC0016 = "src/test/resources/samples/hedis-cdc/CDC0016.xml" 
	// one non-acute visit  denom NOT met
	private static final Map ASSERTIONS_CDC0016 = [C2606:'']
    private static final Map MEASURES_CDC0016 = [C3201: [num: 0, denom: 0], C3202: [num: 0, denom: 0], C3203: [num: 0, denom: 0], 
												 C3204: [num: 0, denom: 0], C3205: [num: 0, denom: 0], C3207: [num: 0, denom: 0],
												 C3208: [num: 0, denom: 0], C3209: [num: 0, denom: 0], C3210: [num: 0, denom: 0]]
	
	private static final String CDC0017 = "src/test/resources/samples/hedis-cdc/CDC0017.xml" 
	// Denom not Met
	private static final Map ASSERTIONS_CDC0017 = [C2606:'']
    private static final Map MEASURES_CDC0017 = [C3201: [num: 0, denom: 0], C3202: [num: 0, denom: 0], C3203: [num: 0, denom: 0], 
												 C3204: [num: 0, denom: 0], C3205: [num: 0, denom: 0], C3207: [num: 0, denom: 0],
												 C3208: [num: 0, denom: 0], C3209: [num: 0, denom: 0], C3210: [num: 0, denom: 0]]
	
    private static final String CDC0018 = "src/test/resources/samples/hedis-cdc/CDC0018.xml" 
	// Denomenator check:  pharmacy during an emergency visit -expect Denomenator met
	private static final Map ASSERTIONS_CDC0018 = [C2520:'',C2606:'',C2607:'',C54:'',C545:'']
    private static final Map MEASURES_CDC0018 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1], 
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0019 = "src/test/resources/samples/hedis-cdc/CDC0019.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0019 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2']
	private static final Map MEASURES_CDC0019 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0020 = "src/test/resources/samples/hedis-cdc/CDC0020.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0020 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2']
	private static final Map MEASURES_CDC0020 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0021 = "src/test/resources/samples/hedis-cdc/CDC0021.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0021 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2']
	private static final Map MEASURES_CDC0021 = [C3201: [num: 0, denom: 1], C3202: [num: 1, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0022 = "src/test/resources/samples/hedis-cdc/CDC0022.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0022 = [C2519:'',C2529:'',C2579:'',C2606:'',C539:'',C54:'',C545:'',NonAcuteEncounters:'2']
	private static final Map MEASURES_CDC0022 = [C3201: [num: 1, denom: 1], C3202: [num: 1, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	private static final String CDC0023 = "src/test/resources/samples/hedis-cdc/CDC0023.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0023 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2']
	private static final Map MEASURES_CDC0023 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0024 = "src/test/resources/samples/hedis-cdc/CDC0024.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0024 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2']
	private static final Map MEASURES_CDC0024 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0025 = "src/test/resources/samples/hedis-cdc/CDC0025.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0025 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2']
	private static final Map MEASURES_CDC0025 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0026 = "src/test/resources/samples/hedis-cdc/CDC0026.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0026 = [C2519:'',C2529:'',C2579:'',C2606:'',C539:'',C54:'',C545:'',NonAcuteEncounters:'2']
	private static final Map MEASURES_CDC0026 = [C3201: [num: 1, denom: 1], C3202: [num: 1, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0027 = "src/test/resources/samples/hedis-cdc/CDC0027.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0027 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2']
	private static final Map MEASURES_CDC0027 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0028 = "src/test/resources/samples/hedis-cdc/CDC0028.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0028 = [C2519:'',C2529:'',C2579:'',C2606:'',C539:'',C54:'',C545:'',NonAcuteEncounters:'2']
	private static final Map MEASURES_CDC0028 = [C3201: [num: 1, denom: 1], C3202: [num: 1, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0029 = "src/test/resources/samples/hedis-cdc/CDC0029.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0029 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2']
	private static final Map MEASURES_CDC0029 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0030 = "src/test/resources/samples/hedis-cdc/CDC0030.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0030 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2']
	private static final Map MEASURES_CDC0030 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	private static final String CDC0031 = "src/test/resources/samples/hedis-cdc/CDC0031.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0031 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2']
	private static final Map MEASURES_CDC0031 = [C3201: [num: 1, denom: 1], C3202: [num: 1, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0032 = "src/test/resources/samples/hedis-cdc/CDC0032.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0032 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2']
	private static final Map MEASURES_CDC0032 = [C3201: [num: 0, denom: 1], C3202: [num: 1, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0033 = "src/test/resources/samples/hedis-cdc/CDC0033.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0033 = [C2519:'',C2579:'',C2606:'',C54:'',C545:'',NonAcuteEncounters:'2']
	private static final Map MEASURES_CDC0033 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0034 = "src/test/resources/samples/hedis-cdc/CDC0034.xml" 
	// BP numerator check: base record
	private static final Map ASSERTIONS_CDC0034 = [C2519:'',C2522:'',C2579:'']
	private static final Map MEASURES_CDC0034 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
//HbA1c Testing
	
	private static final String CDC0035 = "src/test/resources/samples/hedis-cdc/CDC0035.xml" 
	// HBA1c Testing Numerator check:  lab test one in the past year	-expect Numerator met
	private static final Map ASSERTIONS_CDC0035 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2735:'',C3290:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0035 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 1, denom: 1],
												 C3204: [num: 0, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0036 = "src/test/resources/samples/hedis-cdc/CDC0036.xml" 
	// HBA1c Testing Numerator check:  -expect Numerator met
	private static final Map ASSERTIONS_CDC0036 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2735:'',C3289:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0036 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 1, denom: 1],
												 C3204: [num: 0, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0037 = "src/test/resources/samples/hedis-cdc/CDC0037.xml" 
	// HBA1c Testing Numerator check:   lab test one in the past year		-expect Numerator met
	private static final Map ASSERTIONS_CDC0037 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0037 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0038 = "src/test/resources/samples/hedis-cdc/CDC0038.xml" 
	// HBA1c Testing Numerator check:  lab test one in the past year of 90 mg/dL		-expect Numerator met
	private static final Map ASSERTIONS_CDC0038 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C2735:'',C3290:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0038 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 1, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	// HbA1c LT8
		
	private static final String CDC0039 = "src/test/resources/samples/hedis-cdc/CDC0039.xml" 
	// HBA1c LT8 Numerator check:  lab test one in the past year of 9%		-expect Numerator met
	private static final Map ASSERTIONS_CDC0039 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0039 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 1, denom: 1],
												 C3204: [num: 0, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]

	private static final String CDC0040 = "src/test/resources/samples/hedis-cdc/CDC0040.xml" 
	// HBA1c LT8 Numerator check:  lab test one in the past year of 9%	-expect Numerator met
	private static final Map ASSERTIONS_CDC0040 = [AcuteInpatientEncounters:'1',C2519:'',C2523:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0040 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 1, denom: 1],
												 C3204: [num: 0, denom: 1], C3205: [num: 1, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0041 = "src/test/resources/samples/hedis-cdc/CDC0041.xml" 
	// HBA1c LT8 Numerator check:  lab test CPT in the past year of 7 - 9%	-expect Numerator met
	private static final Map ASSERTIONS_CDC0041 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0041 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 1, denom: 1],
												 C3204: [num: 0, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0042 = "src/test/resources/samples/hedis-cdc/CDC0042.xml" 
	// HBA1c LT8 Numerator check:  lab test CPT in the past year of < 7%	-expect Numerator met
	private static final Map ASSERTIONS_CDC0042 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0042 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0043 = "src/test/resources/samples/hedis-cdc/CDC0043.xml" 
	// HBA1c LT8 Numerator check:  lab test one in the past year of 9%	-expect Numerator met
	private static final Map ASSERTIONS_CDC0043 = [AcuteInpatientEncounters:'1',C2519:'',C2523:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0043 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 1, denom: 1],
												 C3204: [num: 0, denom: 1], C3205: [num: 1, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0044 = "src/test/resources/samples/hedis-cdc/CDC0044.xml"
	// HBA1c LT8 Numerator check:  2 results - one cpt and one PQ  fnd the latest which is PQ for 10 	-expect Numerator met for testing but also out of control	-expect Numerator met
	private static final Map ASSERTIONS_CDC0044 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0044 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 1, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
// HbA1c Poor Control
		
	private static final String CDC0045 = "src/test/resources/samples/hedis-cdc/CDC0045.xml" 
	// HBA1c Poor Control Num check:  lab test one in the past year of 9%	-expect Numerator met
	private static final Map ASSERTIONS_CDC0045 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3293:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0045 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 1, denom: 1],
												 C3204: [num: 0, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0046 = "src/test/resources/samples/hedis-cdc/CDC0046.xml" 
	// HBA1c Poor Control Num check:  lab test one in the past year of 9%	-expect Numerator met
	private static final Map ASSERTIONS_CDC0046 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3288:'',C3293:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0046 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 1, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0047 = "src/test/resources/samples/hedis-cdc/CDC0047.xml" 
	// HBA1c Poor Control Num check:  lab test one in the past year of 9%	-expect Numerator met
	private static final Map ASSERTIONS_CDC0047 = [C3294:'',AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3288:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0047 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 1, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0048 = "src/test/resources/samples/hedis-cdc/CDC0048.xml" 
	// HBA1c Poor Control Num check:  lab test one in the past year of 9%	-expect Numerator met for testing but also out of control	-expect Numerator met
	private static final Map ASSERTIONS_CDC0048 = [C3294:'',AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3288:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0048 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 1, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0049 = "src/test/resources/samples/hedis-cdc/CDC0049.xml" 
	// HBA1c Poor Control Num check:  lab test done but no result reported	-expect Numerator met
	private static final Map ASSERTIONS_CDC0049 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0049 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 1, denom: 1],
												 C3204: [num: 0, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]

//Eye Exam
	private static final String CDC0050 = "src/test/resources/samples/hedis-cdc/CDC0050.xml" 
	// Eye Exam: Denom check: acute inpatient encounter : CPT=99223 from 0-2 years ago EncDx diabetes ICD9CM: 250 and 22 years old, female (denomMet)	-- Num check: diabetic retinal screening within last year CPT F code by itself -> meet C3207 numerator   -->
	private static final Map ASSERTIONS_CDC0050 = [AcuteInpatientEncounters:'1',C2558:'',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0050 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 1, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0051 = "src/test/resources/samples/hedis-cdc/CDC0051.xml" 
	// Eye Exam:  diabetic retinal screening NOT within last year CPT F code by itself -> does NOT meet C3207 numerator
	private static final Map ASSERTIONS_CDC0051 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0051 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0052 = "src/test/resources/samples/hedis-cdc/CDC0052.xml" 
	// Eye Exam: diabetic retinal screening within last year CPT code with eye care professional as performer
	private static final Map ASSERTIONS_CDC0052 = [AcuteInpatientEncounters:'1',C2558:'',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0052 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 1, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0053 = "src/test/resources/samples/hedis-cdc/CDC0053.xml" 
	// Eye Exam:  diabetic retinal screening NOT within last year CPT code with eye care professional as performer
	private static final Map ASSERTIONS_CDC0053 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0053 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0054 = "src/test/resources/samples/hedis-cdc/CDC0054.xml" 
	// Eye Exam:  diabetic retinal screening within last year CPT code NOT with eye care professional as performer 
	private static final Map ASSERTIONS_CDC0054 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0054 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
		
	private static final String CDC0055 = "src/test/resources/samples/hedis-cdc/CDC0055.xml" 
	// Eye Exam:  diabetic retinal screening within last year CPT F code for negative result
	private static final Map ASSERTIONS_CDC0055 = [AcuteInpatientEncounters:'1',C3267:'',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0055 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 1, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0056 = "src/test/resources/samples/hedis-cdc/CDC0056.xml" 
	// Eye Exam:  diabetic retinal screening within last 2 years CPT F code for negative result
	private static final Map ASSERTIONS_CDC0056 = [AcuteInpatientEncounters:'1',C3266:'',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0056 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 1, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0057 = "src/test/resources/samples/hedis-cdc/CDC0057.xml" 
	// Eye Exam:  diabetic retinal screening NOT within last 2 years CPT F code for negative result 
	private static final Map ASSERTIONS_CDC0057 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0057 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0058 = "src/test/resources/samples/hedis-cdc/CDC0058.xml" 
	// Eye Exam:  diabetic retinal screening within last 2 years as Observation with code for negative result
	private static final Map ASSERTIONS_CDC0058 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0058 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0059 = "src/test/resources/samples/hedis-cdc/CDC0059.xml" 
	// Eye Exam:  diabetic retinal screening within last 2 years as Observation with code for positive result
	private static final Map ASSERTIONS_CDC0059 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0059 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
//	LDL-C screening
	private static final String CDC0060 = "src/test/resources/samples/hedis-cdc/CDC0060.xml" 
	// LDL-C Screening: lab test one in the past year -expect Numerator met
	private static final Map ASSERTIONS_CDC0060 = [AcuteInpatientEncounters:'1',C1678:'',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0060 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 1, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0061 = "src/test/resources/samples/hedis-cdc/CDC0061.xml" 
	// LDL-C screening:lab test one in the past year -expect Numerator met
	private static final Map ASSERTIONS_CDC0061 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0061 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 1, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0062 = "src/test/resources/samples/hedis-cdc/CDC0062.xml" 
	// LDL-C screening: lab test one in the past year -expect Numerator met
	private static final Map ASSERTIONS_CDC0062 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0062 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0063 = "src/test/resources/samples/hedis-cdc/CDC0063.xml" 
	// LDL-C screening: lab test one in the past year of 90 mg/dL -expect Numerator met
	private static final Map ASSERTIONS_CDC0063 = [AcuteInpatientEncounters:'1',C1678:'',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0063 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 1, denom: 1], C3209: [num: 1, denom: 1], C3210: [num: 0, denom: 1]]
//LDL-C control LT 100 mg/dL
	
	private static final String CDC0064 = "src/test/resources/samples/hedis-cdc/CDC0064.xml" 
	// LDL-C control:  2 lab tests using the CPT codes.  the LDL-C Tests was second so it should fail.-expect Numerator Not met-
	private static final Map ASSERTIONS_CDC0064 = [AcuteInpatientEncounters:'1',C2736:'',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0064 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 1, denom: 1], C3209: [num: 1, denom: 1], C3210: [num: 0, denom: 1]]
		
	private static final String CDC0065 = "src/test/resources/samples/hedis-cdc/CDC0065.xml" 
	// LDL-C control:  lab test too long ago
	private static final Map ASSERTIONS_CDC0065 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0065 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0066 = "src/test/resources/samples/hedis-cdc/CDC0066.xml" 
	// LDL-C control:  Include 2 values, latest value >100 (NumNotMet)
	private static final Map ASSERTIONS_CDC0066 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0066 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 1, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0067 = "src/test/resources/samples/hedis-cdc/CDC0067.xml" 
	// LDL-C control:  2 lab tests using the CPT codes.  the LDL-C Tests was second so it should fail.
	private static final Map ASSERTIONS_CDC0067 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0067 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 1, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0068 = "src/test/resources/samples/hedis-cdc/CDC0068.xml" 
	// LDL-C control:  lab test too long ago
	private static final Map ASSERTIONS_CDC0068 = [AcuteInpatientEncounters:'1',C2736:'',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0068 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 1, denom: 1], C3209: [num: 1, denom: 1], C3210: [num: 0, denom: 1]]

	//Nephropathy
		
	private static final String CDC0069 = "src/test/resources/samples/hedis-cdc/CDC0069.xml" 
	// Nephropathy:  
	private static final Map ASSERTIONS_CDC0069 = [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0069 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 1, denom: 1]]

	private static final String CDC0070 = "src/test/resources/samples/hedis-cdc/CDC0070.xml" 
	// Nephropathy: 
	private static final Map ASSERTIONS_CDC0070 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'']
	private static final Map MEASURES_CDC0070 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0071 = "src/test/resources/samples/hedis-cdc/CDC0071.xml" 
	// Nephropathy:  
	private static final Map ASSERTIONS_CDC0071 = [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0071 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 1, denom: 1]]
	
	private static final String CDC0072 = "src/test/resources/samples/hedis-cdc/CDC0072.xml" 
	// Nephropathy:  
	private static final Map ASSERTIONS_CDC0072 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'']
	private static final Map MEASURES_CDC0072 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0073 = "src/test/resources/samples/hedis-cdc/CDC0073.xml" 
	// Nephropathy:  
	private static final Map ASSERTIONS_CDC0073 = [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0073 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 1, denom: 1]]
	
	private static final String CDC0074 = "src/test/resources/samples/hedis-cdc/CDC0074.xml" 
	// Nephropathy: 
	private static final Map ASSERTIONS_CDC0074 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'']
	private static final Map MEASURES_CDC0074 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
		
	private static final String CDC0075 = "src/test/resources/samples/hedis-cdc/CDC0075.xml" 
	// Nephropathy: HEDIS-Nephropathy Treatment EncDx] [1] or more times with [low] time in the past [1][yr] -expect numerator met 
	private static final Map ASSERTIONS_CDC0075 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'']
	private static final Map MEASURES_CDC0075 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 1, denom: 1]]
	
	private static final String CDC0076 = "src/test/resources/samples/hedis-cdc/CDC0076.xml" 
	// Nephropathy:   HEDIS-Nephropathy Treatment EncDx] [1] or more times with [low] time NOT in the past [1][yr] -expect numerator NOT met
	private static final Map ASSERTIONS_CDC0076 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'']
	private static final Map MEASURES_CDC0076 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0077 = "src/test/resources/samples/hedis-cdc/CDC0077.xml" 
	// Nephropathy:   HEDIS-CKD Stage 4 EncDx] [1] or more times with [low] time in the past [1][yr] -expect numerator met 
	private static final Map ASSERTIONS_CDC0077 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0077 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 1, denom: 1]]
	
	private static final String CDC0078 = "src/test/resources/samples/hedis-cdc/CDC0078.xml" 
	// Nephropathy:  HEDIS-CKD Stage 4 EncDx] [1] or more times with [low] time NOT in the past [1][yr] -expect numerator NOT met 
	private static final Map ASSERTIONS_CDC0078 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'']
	private static final Map MEASURES_CDC0078 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]

	
	private static final String CDC0079 = "src/test/resources/samples/hedis-cdc/CDC0079.xml" 
	// Nephropathy: HEDIS-ESRD CPT] [1] or more times with [low] time in the past [1][yr] -expect numerator met
	private static final Map ASSERTIONS_CDC0079 = [AcuteInpatientEncounters:'1',C2519:'',C2528:'',C2579:'',C2606:'',C3265:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0079 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 1, denom: 1]]
	
	private static final String CDC0080 = "src/test/resources/samples/hedis-cdc/CDC0080.xml" 
	// Nephropathy:HEDIS-Kidney Transplant EncDx] [1]  or more times with [low] time in the past [1][yr] -expect numerator NOT met
	private static final Map ASSERTIONS_CDC0080 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'']
	private static final Map MEASURES_CDC0080 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0081 = "src/test/resources/samples/hedis-cdc/CDC0081.xml" 
	// Nephropathy:HEDIS-ESRD EncDx] [1] or more times with [low] time in the past [1][yr] -expect numerator met
	private static final Map ASSERTIONS_CDC0081 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'']
	private static final Map MEASURES_CDC0081 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 1, denom: 1]]
	
	private static final String CDC0082 = "src/test/resources/samples/hedis-cdc/CDC0082.xml" 
	// Nephropathy:HEDIS-ESRD EncDx] [1] or more times with [low] time NOT in the past [1][yr] -expect numerator NOT met
	private static final Map ASSERTIONS_CDC0082 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3265:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0082 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0083 = "src/test/resources/samples/hedis-cdc/CDC0083.xml" 
	// Nephropathy:encounter in last year where [Healthcare Facility or Place of Service(POS)] of [HEDIS-ESRD] was [Part of] the encounter -expect numerator met 
	private static final Map ASSERTIONS_CDC0083 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'',C3265:'']
	private static final Map MEASURES_CDC0083 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 1, denom: 1]]
	
	private static final String CDC0084 = "src/test/resources/samples/hedis-cdc/CDC0084.xml" 
	// Nephropathy:encounter NOT in last year where [Healthcare Facility or Place of Service(POS)] of [HEDIS-ESRD] was [Part of] the encounter -expect numerator NOT met
	private static final Map ASSERTIONS_CDC0084 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C3265:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0084 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
		
	private static final String CDC0085 = "src/test/resources/samples/hedis-cdc/CDC0085.xml" 
	// Nephropathy:encounter in last year where [Healthcare Facility or Place of Service(POS)] of [HEDIS-ESRD] was NOT [Part of] the encounter -expect numerator NOT met
	private static final Map ASSERTIONS_CDC0085 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0085 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0086 = "src/test/resources/samples/hedis-cdc/CDC0086.xml" 
	// Nephropathy:HEDIS-Kidney Transplant CPT] [1] or more times with [low] time in the past [1][yr] -expect numerator met
	private static final Map ASSERTIONS_CDC0086 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0086 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 1, denom: 1]]
	
	private static final String CDC0087 = "src/test/resources/samples/hedis-cdc/CDC0087.xml" 
	// Nephropathy:HEDIS-Kidney Transplant CPT] [1] or more times with [low] time NOT in the past [1][yr] -expect numerator NOT met
	private static final Map ASSERTIONS_CDC0087 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0087 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0088 = "src/test/resources/samples/hedis-cdc/CDC0088.xml" 
	// Nephropathy:HEDIS-Kidney Transplant EncDx] [1]  or more times with [low] time in the past [1][yr] -expect numerator met
	private static final Map ASSERTIONS_CDC0088 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0088 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 1, denom: 1]]

	
	private static final String CDC0089 = "src/test/resources/samples/hedis-cdc/CDC0089.xml" 
	// Nephropathy:HEDIS-Kidney Transplant EncDx] [1]  or more times with [low] time in the past [1][yr] -expect numerator NOT met
	private static final Map ASSERTIONS_CDC0089 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0089 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0090 = "src/test/resources/samples/hedis-cdc/CDC0090.xml" 
	// Nephropathy:at least [1] procedure by [Provider Nephrology] who is [Performer] with [low] time [>=] [1YrAgo] and [<] [evalTime] ignoring time components of all dates -expect numerator met
	private static final Map ASSERTIONS_CDC0090 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0090 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 1, denom: 1]]
	
	private static final String CDC0091 = "src/test/resources/samples/hedis-cdc/CDC0091.xml" 
	// Nephropathy: procedure by [Provider Nephrology] who is [Performer] with [low] time NOT [>=] [1YrAgo] and [<] [evalTime] ignoring time components of all dates -expect numerator NOT met
	private static final Map ASSERTIONS_CDC0091 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0091 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0092 = "src/test/resources/samples/hedis-cdc/CDC0092.xml" 
	// Nephropathy:procedure by NOT [Provider Nephrology] who is [Performer] with [low] time [>=] [1YrAgo] and [<] [evalTime] ignoring time components of all dates -expect numerator NOT met
	private static final Map ASSERTIONS_CDC0092 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0092 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0093 = "src/test/resources/samples/hedis-cdc/CDC0093.xml" 
	// Nephropathy: A positive urine macroalbumin test (Positive Urine Macroalbumin Tests Value Set in past year -expect numerator met
	private static final Map ASSERTIONS_CDC0093 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0093 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 1, denom: 1]]
	
	private static final String CDC0094 = "src/test/resources/samples/hedis-cdc/CDC0094.xml" 
	// Nephropathy:A positive urine macroalbumin test (Positive Urine Macroalbumin Tests Value Set, not in past year -expect numerator NOT met
	private static final Map ASSERTIONS_CDC0094 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0094 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
		
	private static final String CDC0095 = "src/test/resources/samples/hedis-cdc/CDC0095.xml" 
	// Nephropathy:A urine macroalbumin test (Urine Macroalbumin Tests Value Set LOINC Observation) where laboratory data indicates a positive result -expect numerator met
	private static final Map ASSERTIONS_CDC0095 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0095 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 1, denom: 1]]
	
	private static final String CDC0096 = "src/test/resources/samples/hedis-cdc/CDC0096.xml" 
	// Nephropathy:encounter in last year where [Healthcare Facility or Place of Service(POS)] of [HEDIS-ESRD] was [Part of] the encounter -expect numerator met
	private static final Map ASSERTIONS_CDC0096 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0096 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0097 = "src/test/resources/samples/hedis-cdc/CDC0097.xml" 
	// Nephropathy:A urine macroalbumin test (Urine Macroalbumin Tests Value Set LOINC Observation) where laboratory data NOT last year -expect numerator NOT met
	private static final Map ASSERTIONS_CDC0097 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0097 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]
	
	private static final String CDC0098 = "src/test/resources/samples/hedis-cdc/CDC0098.xml" 
	// Nephropathy:At least one ACE inhibitor or ARB dispensing event in past year -expect numerator met
	private static final Map ASSERTIONS_CDC0098 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0098 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 1, denom: 1]]

	
	private static final String CDC0099 = "src/test/resources/samples/hedis-cdc/CDC0099.xml" 
	// Nephropathy:Num check: At least one ACE inhibitor or ARB dispensing event NOT in past year -expect numerator NOT met
	private static final Map ASSERTIONS_CDC0099 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0099 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 0, denom: 1]]

	private static final String CDC0100 = "src/test/resources/samples/hedis-cdc/CDC0100.xml" 
	// Nephropathy:encounter in last year where [Healthcare Facility or Place of Service(POS)] of [HEDIS-ESRD] was [Part of] the encounter -expect numerator met
	private static final Map ASSERTIONS_CDC0100 = [AcuteInpatientEncounters:'1',C2519:'',C2579:'',C2606:'',C54:'',C545:'']
	private static final Map MEASURES_CDC0100 = [C3201: [num: 0, denom: 1], C3202: [num: 0, denom: 1], C3203: [num: 0, denom: 1],
												 C3204: [num: 1, denom: 1], C3205: [num: 0, denom: 1], C3207: [num: 0, denom: 1],
												 C3208: [num: 0, denom: 1], C3209: [num: 0, denom: 1], C3210: [num: 1, denom: 1]]
/*
Concepts used:
INPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C239 -> Emergency Department"
"	C2511 -> HEDIS 2014"
"	C2556 -> Provider Eye Care Professional"
"	C2557 -> Performer"
"	C2559 -> Blood Pressure"
"	C2560 -> Blood Pressure"
"	C2579 -> Diabetes by Claims Data"
"	C2606 -> Patient Age GE 18 and LT 76 Years"
"	C2607 -> Diabetes by Pharmacy Data"
"	C2828 -> Healthcare Facility or Place of Service (POS)"
"	C2848 -> HEDIS-CDC Table L ACE Inhibitors/ARBs"
"	C2849 -> HEDIS-CDC Table A Prescriptions to Identify Members With Diabetes"
"	C2964 -> HEDIS-Outpatient"
"	C2968 -> HEDIS-ED"
"	C2971 -> HEDIS-Acute Inpatient"
"	C2972 -> HEDIS-ESRD"
"	C2983 -> HEDIS-HbA1c Level Greater Than 9.0"
"	C2984 -> HEDIS-HbA1c Level Less Than 7.0"
"	C2985 -> HEDIS-HbA1c Tests"
"	C2997 -> HEDIS-Kidney Transplant"
"	C3000 -> HEDIS-LDL-C Level Less Than 100"
"	C3001 -> HEDIS-LDL-C Tests"
"	C3018 -> HEDIS-Nephropathy Screening Tests"
"	C3019 -> HEDIS-Nephropathy Treatment"
"	C3020 -> HEDIS-Nonacute Inpatient"
"	C3022 -> HEDIS-Observation"
"	C3033 -> HEDIS-Positive Urine Macroalbumin Tests"
"	C3050 -> HEDIS-Systolic Greater Than or Equal To 140"
"	C3051 -> HEDIS-Systolic Less Than 140"
"	C3090 -> HEDIS-CKD Stage 4"
"	C3100 -> HEDIS-Diabetes"
"	C3101 -> HEDIS-Diabetic Retinal Screening"
"	C3102 -> HEDIS-Diabetic Retinal Screening Negative"
"	C3103 -> HEDIS-Diabetic Retinal Screening With Eye Care Professional"
"	C3105 -> HEDIS-Diastolic 80-89"
"	C3106 -> HEDIS-Diastolic Greater Than or Equal To 90"
"	C3107 -> HEDIS-Diastolic Less Than 80"
"	C3152 -> HEDIS-Urine Macroalbumin Tests"
"	C3262 -> Provider Nephrology"
"	C3265 -> Named Dates Inserted"
"	C3293 -> HbA1c No Result Available Among Procedures"
"	C3294 -> HbA1c No Result Available Among Observations"
"	C36 -> OpenCDS"
"	C405 -> Part of"
"	C44 -> Outpatient"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
OUTPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C1678 -> Cholesterol"
"	C2519 -> Denominator Inclusions by Claims Data"
"	C2520 -> Denominator Inclusions by Pharmacy Data"
"	C2522 -> HbA1c Poor Control (None or GT 9.0 Pct)"
"	C2523 -> HbA1c Control LT 8.0 Pct"
"	C2528 -> Medical Attention for Nephropathy"
"	C2529 -> Blood Pressure Control LT 140 - 80 mm Hg"
"	C2530 -> Blood Pressure Control LT 140 - 90 mm Hg"
"	C2555 -> Eye Exam"
"	C2558 -> Eye Exam"
"	C2579 -> Diabetes by Claims Data"
"	C2580 -> QM HEDIS-CDC Comprehensive Diabetes Care"
"	C2606 -> Patient Age GE 18 and LT 76 Years"
"	C2607 -> Diabetes by Pharmacy Data"
"	C2735 -> HbA1c Testing in Past Year"
"	C2736 -> LDL-C Control LT 100 mg per dL"
"	C3201 -> QM HEDIS-CDC (BP LT 140 / 80)"
"	C3202 -> QM HEDIS-CDC (BP LT 140 / 90)"
"	C3203 -> QM HEDIS-CDC (HbA1c testing)"
"	C3204 -> QM HEDIS-CDC (HbA1c poor control GT 9%)"
"	C3205 -> QM HEDIS-CDC (HbA1c control LT 8%)"
"	C3207 -> QM HEDIS-CDC (Eye Exam)"
"	C3208 -> QM HEDIS-CDC (LDL-C screening)"
"	C3209 -> QM HEDIS-CDC (LDL-C control LT 100 mg/dL)"
"	C3210 -> QM HEDIS-CDC (Nephropathy)"
"	C3265 -> Named Dates Inserted"
"	C3266 -> Eye Exam"
"	C3267 -> Eye Exam"
"	C3288 -> HbA1c GT 9.0 Pct"
"	C3289 -> HbA1c Testing by Procedure"
"	C3290 -> HbA1c Testing by Observation"
"	C3291 -> HbA1c No Result Available"
"	C3293 -> HbA1c No Result Available Among Procedures"
"	C3294 -> HbA1c No Result Available Among Observations"
"	C3378 -> LDL-C Screening"
"	C529 -> Rejected for Missing or Bad Data"
"	C539 -> Numerator Criteria Met"
"	C54 -> Denominator Criteria Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"


*/
	
	@Unroll
	def "test HEDIS CDC v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CDC', version: '2014.1.0'],
			specifiedTime: '2012-01-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
		println responsePayload

		then:
		def data = new XmlSlurper().parseText(responsePayload)
		def results = VMRUtil.getResults(data, '\\|')
//		assertions.size() == results.assertions.size()
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
		EMPTY0001 | ASSERTIONS_EMPTY0001| MEASURES_EMPTY0001 
		CDC0001 | ASSERTIONS_CDC0001| MEASURES_CDC0001
		CDC0002 | ASSERTIONS_CDC0002| MEASURES_CDC0002
		CDC0003 | ASSERTIONS_CDC0003| MEASURES_CDC0003
		CDC0004 | ASSERTIONS_CDC0004| MEASURES_CDC0004
		CDC0005 | ASSERTIONS_CDC0005| MEASURES_CDC0005
		CDC0006 | ASSERTIONS_CDC0006| MEASURES_CDC0006
		CDC0007 | ASSERTIONS_CDC0007| MEASURES_CDC0007
		CDC0008 | ASSERTIONS_CDC0008| MEASURES_CDC0008
		CDC0009 | ASSERTIONS_CDC0009| MEASURES_CDC0009
		CDC0010 | ASSERTIONS_CDC0010| MEASURES_CDC0010
		CDC0011 | ASSERTIONS_CDC0011| MEASURES_CDC0011
		CDC0012 | ASSERTIONS_CDC0012| MEASURES_CDC0012
		CDC0013 | ASSERTIONS_CDC0013| MEASURES_CDC0013
		CDC0014 | ASSERTIONS_CDC0014| MEASURES_CDC0014
		CDC0015 | ASSERTIONS_CDC0015| MEASURES_CDC0015
		CDC0016 | ASSERTIONS_CDC0016| MEASURES_CDC0016
		CDC0017 | ASSERTIONS_CDC0017| MEASURES_CDC0017
		CDC0018 | ASSERTIONS_CDC0018| MEASURES_CDC0018
		CDC0019 | ASSERTIONS_CDC0019| MEASURES_CDC0019
		CDC0020 | ASSERTIONS_CDC0020| MEASURES_CDC0020
		CDC0021 | ASSERTIONS_CDC0021| MEASURES_CDC0021
		CDC0022 | ASSERTIONS_CDC0022| MEASURES_CDC0022
		CDC0023 | ASSERTIONS_CDC0023| MEASURES_CDC0023
		CDC0024 | ASSERTIONS_CDC0024| MEASURES_CDC0024
		CDC0025 | ASSERTIONS_CDC0025| MEASURES_CDC0025
		CDC0026 | ASSERTIONS_CDC0026| MEASURES_CDC0026
		CDC0027 | ASSERTIONS_CDC0027| MEASURES_CDC0027
		CDC0028 | ASSERTIONS_CDC0028| MEASURES_CDC0028
		CDC0029 | ASSERTIONS_CDC0029| MEASURES_CDC0029
		CDC0030 | ASSERTIONS_CDC0030| MEASURES_CDC0030
		CDC0031 | ASSERTIONS_CDC0031| MEASURES_CDC0031
		CDC0032 | ASSERTIONS_CDC0032| MEASURES_CDC0032
		CDC0033 | ASSERTIONS_CDC0033| MEASURES_CDC0033
		CDC0034 | ASSERTIONS_CDC0034| MEASURES_CDC0034
		CDC0035 | ASSERTIONS_CDC0035| MEASURES_CDC0035
		CDC0036 | ASSERTIONS_CDC0036| MEASURES_CDC0036
		CDC0037 | ASSERTIONS_CDC0037| MEASURES_CDC0037
		CDC0038 | ASSERTIONS_CDC0038| MEASURES_CDC0038
		CDC0039 | ASSERTIONS_CDC0039| MEASURES_CDC0039
		CDC0040 | ASSERTIONS_CDC0040| MEASURES_CDC0040
		CDC0041 | ASSERTIONS_CDC0041| MEASURES_CDC0041
		CDC0042 | ASSERTIONS_CDC0042| MEASURES_CDC0042
		CDC0043 | ASSERTIONS_CDC0043| MEASURES_CDC0043
		CDC0044 | ASSERTIONS_CDC0044| MEASURES_CDC0044
		CDC0045 | ASSERTIONS_CDC0045| MEASURES_CDC0045
		CDC0046 | ASSERTIONS_CDC0046| MEASURES_CDC0046
		CDC0047 | ASSERTIONS_CDC0047| MEASURES_CDC0047
		CDC0048 | ASSERTIONS_CDC0048| MEASURES_CDC0048
		CDC0049 | ASSERTIONS_CDC0049| MEASURES_CDC0049
		CDC0050 | ASSERTIONS_CDC0050| MEASURES_CDC0050
		CDC0051 | ASSERTIONS_CDC0051| MEASURES_CDC0051
		CDC0052 | ASSERTIONS_CDC0052| MEASURES_CDC0052
		CDC0053 | ASSERTIONS_CDC0053| MEASURES_CDC0053
		CDC0054 | ASSERTIONS_CDC0054| MEASURES_CDC0054
		CDC0055 | ASSERTIONS_CDC0055| MEASURES_CDC0055
		CDC0056 | ASSERTIONS_CDC0056| MEASURES_CDC0056
		CDC0057 | ASSERTIONS_CDC0057| MEASURES_CDC0057
		CDC0058 | ASSERTIONS_CDC0058| MEASURES_CDC0058
		CDC0059 | ASSERTIONS_CDC0059| MEASURES_CDC0059
		CDC0060 | ASSERTIONS_CDC0060| MEASURES_CDC0060
		CDC0061 | ASSERTIONS_CDC0061| MEASURES_CDC0061
		CDC0062 | ASSERTIONS_CDC0062| MEASURES_CDC0062
		CDC0063 | ASSERTIONS_CDC0063| MEASURES_CDC0063
		CDC0064 | ASSERTIONS_CDC0064| MEASURES_CDC0064
		CDC0065 | ASSERTIONS_CDC0065| MEASURES_CDC0065
		CDC0066 | ASSERTIONS_CDC0066| MEASURES_CDC0066
		CDC0067 | ASSERTIONS_CDC0067| MEASURES_CDC0067
		CDC0068 | ASSERTIONS_CDC0068| MEASURES_CDC0068
		CDC0069 | ASSERTIONS_CDC0069| MEASURES_CDC0069
		CDC0070 | ASSERTIONS_CDC0070| MEASURES_CDC0070
		CDC0071 | ASSERTIONS_CDC0071| MEASURES_CDC0071
		CDC0072 | ASSERTIONS_CDC0072| MEASURES_CDC0072
		CDC0073 | ASSERTIONS_CDC0073| MEASURES_CDC0073
		CDC0074 | ASSERTIONS_CDC0074| MEASURES_CDC0074
		CDC0075 | ASSERTIONS_CDC0075| MEASURES_CDC0075
		CDC0076 | ASSERTIONS_CDC0076| MEASURES_CDC0076
		CDC0077 | ASSERTIONS_CDC0077| MEASURES_CDC0077
		CDC0078 | ASSERTIONS_CDC0078| MEASURES_CDC0078
		CDC0079 | ASSERTIONS_CDC0079| MEASURES_CDC0079
		CDC0080 | ASSERTIONS_CDC0080| MEASURES_CDC0080
		CDC0081 | ASSERTIONS_CDC0081| MEASURES_CDC0081
		CDC0082 | ASSERTIONS_CDC0082| MEASURES_CDC0082
		CDC0083 | ASSERTIONS_CDC0083| MEASURES_CDC0083
		CDC0084 | ASSERTIONS_CDC0084| MEASURES_CDC0084
		CDC0085 | ASSERTIONS_CDC0085| MEASURES_CDC0085
		CDC0086 | ASSERTIONS_CDC0086| MEASURES_CDC0086
		CDC0087 | ASSERTIONS_CDC0087| MEASURES_CDC0087
		CDC0088 | ASSERTIONS_CDC0088| MEASURES_CDC0088
		CDC0089 | ASSERTIONS_CDC0089| MEASURES_CDC0089
		CDC0090 | ASSERTIONS_CDC0090| MEASURES_CDC0090
		CDC0091 | ASSERTIONS_CDC0091| MEASURES_CDC0091
		CDC0092 | ASSERTIONS_CDC0092| MEASURES_CDC0092
		CDC0093 | ASSERTIONS_CDC0093| MEASURES_CDC0093
		CDC0094 | ASSERTIONS_CDC0094| MEASURES_CDC0094
		CDC0095 | ASSERTIONS_CDC0095| MEASURES_CDC0095
		CDC0096 | ASSERTIONS_CDC0096| MEASURES_CDC0096
		CDC0097 | ASSERTIONS_CDC0097| MEASURES_CDC0097
		CDC0098 | ASSERTIONS_CDC0098| MEASURES_CDC0098
		CDC0099 | ASSERTIONS_CDC0099| MEASURES_CDC0099
		CDC0100 | ASSERTIONS_CDC0100| MEASURES_CDC0100
	}
}
