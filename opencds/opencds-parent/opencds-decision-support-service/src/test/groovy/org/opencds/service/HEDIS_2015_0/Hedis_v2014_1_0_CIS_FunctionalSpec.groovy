package org.opencds.service.HEDIS_2015_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_CIS_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
    private static final Map MEASURES_EMPTY0001 = [C2593: [num: -1, denom: -1]]
//DTaP	
    private static final String CIS0001 = "src/test/resources/samples/hedis-cis/SampleCIS_DTaP0001.xml" 
	//DTaP: Num Met
    private static final Map MEASURES_CIS0001 = 
	[C3172: [num: 1, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 1, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 1, denom: 1], 
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1], 
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]

	
    private static final String CIS0002 = "src/test/resources/samples/hedis-cis/SampleCIS_DTaP0002.xml" 
	//DTaP: Num Not Met
    private static final Map MEASURES_CIS0002 = 
	[C3172: [num: 1, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 1, denom: 1], 
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1], 
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
    private static final String CIS0003 = "src/test/resources/samples/hedis-cis/SampleCIS_DTaP0003.xml" 
	//Denom not met
    private static final Map MEASURES_CIS0003 = 
	[C3172: [num: 0, denom: 0], C3173: [num: 0, denom: 0], C3174: [num: 0, denom: 0], C3175: [num: 0, denom: 0], C3176: [num: 0, denom: 0], 
	 C3177: [num: 0, denom: 0], C3182: [num: 0, denom: 0], C3183: [num: 0, denom: 0], C3184: [num: 0, denom: 0], C3185: [num: 0, denom: 0],
	 C3332: [num: 0, denom: 0], C3333: [num: 0, denom: 0], C3334: [num: 0, denom: 0], C3335: [num: 0, denom: 0], C3336: [num: 0, denom: 0], 
	 C3337: [num: 0, denom: 0], C3338: [num: 0, denom: 0], C3339: [num: 0, denom: 0], C3340: [num: 0, denom: 0]]

	
    private static final String CIS0004 = "src/test/resources/samples/hedis-cis/SampleCIS_DTaP0004.xml" 
	//Denom not met
    private static final Map MEASURES_CIS0004 = 
	[C3172: [num: 0, denom: 0], C3173: [num: 0, denom: 0], C3174: [num: 0, denom: 0], C3175: [num: 0, denom: 0], C3176: [num: 0, denom: 0], 
	 C3177: [num: 0, denom: 0], C3182: [num: 0, denom: 0], C3183: [num: 0, denom: 0], C3184: [num: 0, denom: 0], C3185: [num: 0, denom: 0],
	 C3332: [num: 0, denom: 0], C3333: [num: 0, denom: 0], C3334: [num: 0, denom: 0], C3335: [num: 0, denom: 0], C3336: [num: 0, denom: 0], 
	 C3337: [num: 0, denom: 0], C3338: [num: 0, denom: 0], C3339: [num: 0, denom: 0], C3340: [num: 0, denom: 0]]

//HepA	
	private static final String CIS0005 = "src/test/resources/samples/hedis-cis/SampleCIS_HepA0001.xml"
	//HepA: Num Met
	private static final Map MEASURES_CIS0005 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 1, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]

	private static final String CIS0006 = "src/test/resources/samples/hedis-cis/SampleCIS_HepA0002.xml"
	//HepA: Num Not Met
	private static final Map MEASURES_CIS0006 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]

	
	private static final String CIS0007 = "src/test/resources/samples/hedis-cis/SampleCIS_HepA0005.xml"
	//HepA: Num Met
	private static final Map MEASURES_CIS0007 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 1, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]

//HepB	
	private static final String CIS0008 = "src/test/resources/samples/hedis-cis/SampleCIS_HepB0001.xml"
	//HepB: Num  Met
	private static final Map MEASURES_CIS0008 =
	[C3172: [num: 1, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 1, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0009 = "src/test/resources/samples/hedis-cis/SampleCIS_HepB0002.xml"
	//Num Met: Num Not Met
	private static final Map MEASURES_CIS0009 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0010 = "src/test/resources/samples/hedis-cis/SampleCIS_HepB0005.xml"
	//Num Met: Num Met
	private static final Map MEASURES_CIS0010 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 1, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]

//HiB	
	private static final String CIS0011 = "src/test/resources/samples/hedis-cis/SampleCIS_HiB0001.xml"
	//HiB: Num  Met
	private static final Map MEASURES_CIS0011 =
	[C3172: [num: 1, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 1, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0012 = "src/test/resources/samples/hedis-cis/SampleCIS_HiB0002.xml"
	//HiB: Num Not Met
	private static final Map MEASURES_CIS0012 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]

//Influenza

	private static final String CIS0013 = "src/test/resources/samples/hedis-cis/SampleCIS_Influenza0001.xml"
	//Influenza: Num  Met
	private static final Map MEASURES_CIS0013 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 1, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0014 = "src/test/resources/samples/hedis-cis/SampleCIS_Influenza0002.xml"
	//Influenza: Num Not Met
	private static final Map MEASURES_CIS0014 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 1, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0015 = "src/test/resources/samples/hedis-cis/SampleCIS_Influenza0003.xml"
	//Influenza: Num Met
	private static final Map MEASURES_CIS0015 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]

//IPV
	
	private static final String CIS0016 = "src/test/resources/samples/hedis-cis/SampleCIS_IPV0001.xml"
	//IPV: Num  Met
	private static final Map MEASURES_CIS0016 =
	[C3172: [num: 1, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 1, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0017 = "src/test/resources/samples/hedis-cis/SampleCIS_IPV0002.xml"
	//IPV: Num Not Met
	private static final Map MEASURES_CIS0017 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
//MMR	
	private static final String CIS0018 = "src/test/resources/samples/hedis-cis/SampleCIS_MMR0001.xml"
	//MMR: Num Met
	private static final Map MEASURES_CIS0018 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 1, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]

	private static final String CIS0019 = "src/test/resources/samples/hedis-cis/SampleCIS_MMR0002.xml"
	//MMR: Num Not Met
	private static final Map MEASURES_CIS0019 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]

	private static final String CIS0020 = "src/test/resources/samples/hedis-cis/SampleCIS_MMR0003.xml"
	//MMR: Num Met
	private static final Map MEASURES_CIS0020 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 1, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]

	private static final String CIS0021 = "src/test/resources/samples/hedis-cis/SampleCIS_MMR0004.xml"
	//MMR: Num Met
	private static final Map MEASURES_CIS0021 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 1, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	
	private static final String CIS0022 = "src/test/resources/samples/hedis-cis/SampleCIS_MMR0005.xml"
	//MMR: Num Not Met
	private static final Map MEASURES_CIS0022 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]

	private static final String CIS0023 = "src/test/resources/samples/hedis-cis/SampleCIS_MMR0006.xml"
	//MMR: Num Met
	private static final Map MEASURES_CIS0023 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 1, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0024 = "src/test/resources/samples/hedis-cis/SampleCIS_MMR0007.xml"
	//MMR: Num Not Met
	private static final Map MEASURES_CIS0024 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]

	private static final String CIS0025 = "src/test/resources/samples/hedis-cis/SampleCIS_MMR0008.xml"
	//MMR: Num Met
	private static final Map MEASURES_CIS0025 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 1, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	
	private static final String CIS0026 = "src/test/resources/samples/hedis-cis/SampleCIS_MMR0009.xml"
	//MMR: Num Not Met
	private static final Map MEASURES_CIS0026 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]

//PCV	
	private static final String CIS0027 = "src/test/resources/samples/hedis-cis/SampleCIS_PCV0001.xml"
	//PCV: Num Met
	private static final Map MEASURES_CIS0027 =
	[C3172: [num: 0, denom: 1], C3173: [num: 1, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	
	private static final String CIS0028 = "src/test/resources/samples/hedis-cis/SampleCIS_PCV0002.xml"
	//PCV: Num Not Met
	private static final Map MEASURES_CIS0028 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]

//RV
	private static final String CIS0029 = "src/test/resources/samples/hedis-cis/SampleCIS_RV0001.xml"
	//RV: Num Met
	private static final Map MEASURES_CIS0029 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 1, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	
	private static final String CIS0030 = "src/test/resources/samples/hedis-cis/SampleCIS_RV0002.xml"
	//RV: Num Not Met
	private static final Map MEASURES_CIS0030 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0031 = "src/test/resources/samples/hedis-cis/SampleCIS_RV0003.xml"
	//RV: Num Not Met
	private static final Map MEASURES_CIS0031 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0032 = "src/test/resources/samples/hedis-cis/SampleCIS_RV0004.xml"
	//RV: Num Not Met
	private static final Map MEASURES_CIS0032 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0033 = "src/test/resources/samples/hedis-cis/SampleCIS_RV0005.xml"
	//RV: Num Met
	private static final Map MEASURES_CIS0033 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 1, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0034 = "src/test/resources/samples/hedis-cis/SampleCIS_RV0006.xml"
	//RV: Num Not Met
	private static final Map MEASURES_CIS0034 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0035 = "src/test/resources/samples/hedis-cis/SampleCIS_RV0007.xml"
	//RV: Num Not Met
	private static final Map MEASURES_CIS0035 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0036 = "src/test/resources/samples/hedis-cis/SampleCIS_RV0010.xml"
	//RV: Num Met
	private static final Map MEASURES_CIS0036 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 1, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0037 = "src/test/resources/samples/hedis-cis/SampleCIS_RV0011.xml"
	//RV: Num Not Met
	private static final Map MEASURES_CIS0037 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0038 = "src/test/resources/samples/hedis-cis/SampleCIS_RV0012.xml"
	//RV: Num Not Met
	private static final Map MEASURES_CIS0038 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
//VZV
	private static final String CIS0039 = "src/test/resources/samples/hedis-cis/SampleCIS_VZV0001.xml"
	//VZV: Num Met
	private static final Map MEASURES_CIS0039 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 1, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0040 = "src/test/resources/samples/hedis-cis/SampleCIS_VZV0002.xml"
	//VZV: Num Met
	private static final Map MEASURES_CIS0040 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 1, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]
	
	private static final String CIS0041 = "src/test/resources/samples/hedis-cis/SampleCIS_VZV0003.xml"
	//VZV: Num Not Met
	private static final Map MEASURES_CIS0041 =
	[C3172: [num: 0, denom: 1], C3173: [num: 0, denom: 1], C3174: [num: 0, denom: 1], C3175: [num: 0, denom: 1], C3176: [num: 0, denom: 1],
	 C3177: [num: 0, denom: 1], C3182: [num: 0, denom: 1], C3183: [num: 0, denom: 1], C3184: [num: 0, denom: 1], C3185: [num: 0, denom: 1],
	 C3332: [num: 0, denom: 1], C3333: [num: 0, denom: 1], C3334: [num: 0, denom: 1], C3335: [num: 0, denom: 1], C3336: [num: 0, denom: 1],
	 C3337: [num: 0, denom: 1], C3338: [num: 0, denom: 1], C3339: [num: 0, denom: 1], C3340: [num: 0, denom: 1]]

/*
Concepts used:
INPUT
"	2 -> month(s)"
"	C2511 -> HEDIS 2014"
"	C2700 -> Patient Age GE 24 and LT 37 Months"
"	C2986 -> HEDIS-Hepatitis A"
"	C2987 -> HEDIS-Hepatitis B"
"	C2989 -> HEDIS-HiB"
"	C2995 -> HEDIS-Influenza"
"	C2996 -> HEDIS-IPV"
"	C3010 -> HEDIS-Measles"
"	C3011 -> HEDIS-Measles/Rubella"
"	C3016 -> HEDIS-MMR"
"	C3017 -> HEDIS-Mumps"
"	C3032 -> HEDIS-Pneumococcal Conjugate"
"	C3041 -> HEDIS-Rotavirus Three Dose Schedule"
"	C3042 -> HEDIS-Rotavirus Two Dose Schedule"
"	C3043 -> HEDIS-Rubella"
"	C3061 -> HEDIS-VZV"
"	C3110 -> HEDIS-DTaP"
"	C3120 -> HEDIS-Hepatitis B Diagnosis"
"	C3186 -> Vaccine Hepatitis A"
"	C3187 -> Vaccine Hepatitis B"
"	C3192 -> Vaccine Influenza"
"	C36 -> OpenCDS"
"	C54 -> Denominator Criteria Met"
"	C544 -> Denominator Exclusions Met"
"	C545 -> Denominator Inclusions Met"
"	C852 -> Vaccine Diphtheria Toxoids"
"	C854 -> Vaccine Hemophilus Influenzae Type B"
"	C857 -> Vaccine Inactivated Polio (IPV)"
"	C858 -> Vaccine Measles"
"	C859 -> Vaccine Pneumococcal Conjugate (PCV)"
"	C860 -> Vaccine Rotavirus"
"	C861 -> Vaccine Varicella Zoster (VZV)"
OUTPUT
"	1 -> year(s)"
"	5 -> day(s)"
"	C2166 -> Varicella"
"	C2593 -> QM HEDIS-CIS Childhood Immunization Status"
"	C2700 -> Patient Age GE 24 and LT 37 Months"
"	C3172 -> QM HEDIS-CIS (IPV) Childhood Immunization Status"
"	C3173 -> QM HEDIS-CIS (PCV) Childhood Immunization Status"
"	C3174 -> QM HEDIS-CIS (DTaP) Childhood Immunization Status"
"	C3175 -> QM HEDIS-CIS (VZV) Childhood Immunization Status"
"	C3176 -> QM HEDIS-CIS (HIB) Childhood Immunization Status"
"	C3177 -> QM HEDIS-CIS (HepA) Childhood Immunization Status"
"	C3182 -> QM HEDIS-CIS (MMR) Childhood Immunization Status"
"	C3183 -> QM HEDIS-CIS (RV) Childhood Immunization Status"
"	C3184 -> QM HEDIS-CIS (Influenza) Childhood Immunization Status"
"	C3185 -> QM HEDIS-CIS (HepB) Childhood Immunization Status"
"	C3186 -> Vaccine Hepatitis A"
"	C3187 -> Vaccine Hepatitis B"
"	C3190 -> Vaccine Rotavirus PENTAVALENT 3 DOSE LIVE ORAL"
"	C3191 -> Vaccine Rotavirus HUMAN ATTENUATED 2 DOSE LIVE ORAL"
"	C3192 -> Vaccine Influenza"
"	C3193 -> Measles/Rubella Vaccine and Mumps (Vaccine or Disease)"
"	C3194 -> Measles (Vaccine or Disease) and Mumps (Vaccine or Disease) and Rubella (Vaccine or Disease)"
"	C3332 -> QM HEDIS-CIS Childhood Immunization Status Combination 2"
"	C3333 -> QM HEDIS-CIS Childhood Immunization Status Combination 3"
"	C3334 -> QM HEDIS-CIS Childhood Immunization Status Combination 4"
"	C3335 -> QM HEDIS-CIS Childhood Immunization Status Combination 5"
"	C3336 -> QM HEDIS-CIS Childhood Immunization Status Combination 6"
"	C3337 -> QM HEDIS-CIS Childhood Immunization Status Combination 7"
"	C3338 -> QM HEDIS-CIS Childhood Immunization Status Combination 8"
"	C3339 -> QM HEDIS-CIS Childhood Immunization Status Combination 9"
"	C3340 -> QM HEDIS-CIS Childhood Immunization Status Combination 10"
"	C529 -> Rejected for Missing or Bad Data"
"	C54 -> Denominator Criteria Met"
"	C545 -> Denominator Inclusions Met"
"	C569 -> Missing Data for Date of Birth"
"	C852 -> Vaccine Diphtheria Toxoids"
"	C854 -> Vaccine Hemophilus Influenzae Type B"
"	C857 -> Vaccine Inactivated Polio (IPV)"
"	C858 -> Vaccine Measles"
"	C859 -> Vaccine Pneumococcal Conjugate (PCV)"
"	C860 -> Vaccine Rotavirus"
"	C861 -> Vaccine Varicella Zoster (VZV)"

*/
	
	@Unroll
	def "test HEDIS CIS v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_CIS', version: '2014.1.0'],
			specifiedTime: '2012-01-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
		println responsePayload

		then:
		def data = new XmlSlurper().parseText(responsePayload)
		def results = VMRUtil.getResults(data, '\\|')
//		assertions.size() == results.assertions.size()
/* 		if (!assertions) {
			assert assertions == results.assertions
		} else {
		assertions.each {entry ->
			assert results.assertions.containsKey(entry.key);
			if (entry?.value) {
				assert results.assertions.get(entry.key) == entry.value
			}
		}
		}
*/
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
		vmr | /*assertions |*/ measures
		EMPTY0001 |/* ASSERTIONS_EMPTY0001|*/ MEASURES_EMPTY0001 
		CIS0001 | /*ASSERTIONS_CIS0001|*/ MEASURES_CIS0001
		CIS0002 | /*ASSERTIONS_CIS0002|*/ MEASURES_CIS0002
		CIS0003 | /*ASSERTIONS_CIS0003|*/ MEASURES_CIS0003
		CIS0004 | /*ASSERTIONS_CIS0004|*/ MEASURES_CIS0004 
		CIS0005 | /*ASSERTIONS_CIS0005|*/ MEASURES_CIS0005
		CIS0006 | /*ASSERTIONS_CIS0006|*/ MEASURES_CIS0006
		CIS0007 | /*ASSERTIONS_CIS0007|*/ MEASURES_CIS0007
		CIS0008 | /*ASSERTIONS_CIS0008|*/ MEASURES_CIS0008
		CIS0009 | /*ASSERTIONS_CIS0009|*/ MEASURES_CIS0009
		CIS0010 | /*ASSERTIONS_CIS0010|*/ MEASURES_CIS0010
		CIS0011 | /*ASSERTIONS_CIS0011|*/ MEASURES_CIS0011
		CIS0012 | /*ASSERTIONS_CIS0012|*/ MEASURES_CIS0012
		CIS0013 | /*ASSERTIONS_CIS0013|*/ MEASURES_CIS0013
		CIS0014 | /*ASSERTIONS_CIS0014|*/ MEASURES_CIS0014
		CIS0015 | /*ASSERTIONS_CIS0015|*/ MEASURES_CIS0015
		CIS0016 | /*ASSERTIONS_CIS0016|*/ MEASURES_CIS0016
		CIS0017 | /*ASSERTIONS_CIS0017|*/ MEASURES_CIS0017
		CIS0018 | /*ASSERTIONS_CIS0018|*/ MEASURES_CIS0018
		CIS0019 | /*ASSERTIONS_CIS0019|*/ MEASURES_CIS0019
		CIS0020 | /*ASSERTIONS_CIS0020|*/ MEASURES_CIS0020
		CIS0021 | /*ASSERTIONS_CIS0021|*/ MEASURES_CIS0021
		CIS0022 | /*ASSERTIONS_CIS0022|*/ MEASURES_CIS0022
		CIS0023 | /*ASSERTIONS_CIS0023|*/ MEASURES_CIS0023
		CIS0024 | /*ASSERTIONS_CIS0024|*/ MEASURES_CIS0024
		CIS0025 | /*ASSERTIONS_CIS0025|*/ MEASURES_CIS0025
		CIS0026 | /*ASSERTIONS_CIS0026|*/ MEASURES_CIS0026
		CIS0027 | /*ASSERTIONS_CIS0025|*/ MEASURES_CIS0027
		CIS0028 | /*ASSERTIONS_CIS0026|*/ MEASURES_CIS0028
		CIS0029 | /*ASSERTIONS_CIS0029|*/ MEASURES_CIS0029
		CIS0030 | /*ASSERTIONS_CIS0030|*/ MEASURES_CIS0030
		CIS0031 | /*ASSERTIONS_CIS0031|*/ MEASURES_CIS0031
		CIS0032 | /*ASSERTIONS_CIS0032|*/ MEASURES_CIS0032
		CIS0033 | /*ASSERTIONS_CIS0033|*/ MEASURES_CIS0033
		CIS0034 | /*ASSERTIONS_CIS0034|*/ MEASURES_CIS0034
		CIS0035 | /*ASSERTIONS_CIS0035|*/ MEASURES_CIS0035
		CIS0036 | /*ASSERTIONS_CIS0036|*/ MEASURES_CIS0036
		CIS0037 | /*ASSERTIONS_CIS0035|*/ MEASURES_CIS0037
		CIS0038 | /*ASSERTIONS_CIS0036|*/ MEASURES_CIS0038
		CIS0039 | /*ASSERTIONS_CIS0039|*/ MEASURES_CIS0039
		CIS0040 | /*ASSERTIONS_CIS0040|*/ MEASURES_CIS0040
		CIS0041 | /*ASSERTIONS_CIS0041|*/ MEASURES_CIS0041
	}
}
