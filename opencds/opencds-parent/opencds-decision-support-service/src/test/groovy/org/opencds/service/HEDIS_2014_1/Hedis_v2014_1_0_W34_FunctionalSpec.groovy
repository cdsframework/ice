package org.opencds.service.HEDIS_2014_1;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_1_0_W34_FunctionalSpec extends Specification 
{
	private static final String EMPTY0001 = "src/test/resources/samples/hedis-all/SampleALL0001.xml" //missing DOB
	private static final Map ASSERTIONS_EMPTY0001 = [reject:'']
    private static final Map MEASURES_EMPTY0001 = [C2625: [num: -1, denom: -1]]
	
    private static final String W340001 = "src/test/resources/samples/hedis-w34/SampleW340001.xml" //Num Met
	private static final Map ASSERTIONS_W340001 = [C2680:'', C2706: '', C539:'', C54: '', C545: '']
    private static final Map MEASURES_W340001  = [C2625: [num: 1, denom: 1]]

	
    private static final String W340002 = "src/test/resources/samples/hedis-w34/SampleW340002.xml" //Num Met
	private static final Map ASSERTIONS_W340002 = [C2680:'', C2706: '', C539:'', C54: '', C545: '']
	private static final Map MEASURES_W340002  = [C2625: [num: 1, denom: 1]]
	
    private static final String W340003 = "src/test/resources/samples/hedis-w34/SampleW340003.xml" //Num Met
	private static final Map ASSERTIONS_W340003 = [C2680:'', C2706: '', C539:'', C54: '', C545: '']
	private static final Map MEASURES_W340003  = [C2625: [num: 1, denom: 1]]

	
    private static final String W340004 = "src/test/resources/samples/hedis-w34/SampleW340004.xml" //Num Met
	private static final Map ASSERTIONS_W340004 = [C2680:'', C2706: '', C539:'', C54: '', C545: '']
	private static final Map MEASURES_W340004  = [C2625: [num: 1, denom: 1]]
	
	private static final String W340005 = "src/test/resources/samples/hedis-w34/SampleW340005.xml" //Denom Met
	private static final Map ASSERTIONS_W340005 = [C2706:'',C54:'',C545:'']
	private static final Map MEASURES_W340005  = [C2625: [num: 0, denom: 1]]

	
	private static final String W340006 = "src/test/resources/samples/hedis-w34/SampleW340006.xml" //Denom Not Met
	private static final Map ASSERTIONS_W340006 = [:]
	private static final Map MEASURES_W340006  = [C2625: [num: 0, denom: 0]]
	
	private static final String W340007 = "src/test/resources/samples/hedis-w34/SampleW340007.xml" //Denom Not Met
	private static final Map ASSERTIONS_W340007 = [:]
	private static final Map MEASURES_W340007  = [C2625: [num: 0, denom: 0]]

	
	private static final String W340008 = "src/test/resources/samples/hedis-w34/SampleW340008.xml" //Denom Met
	private static final Map ASSERTIONS_W340008 = [C2706:'',C54:'',C545:'']
	private static final Map MEASURES_W340008  = [C2625: [num: 0, denom: 1]]

	private static final String W340009 = "src/test/resources/samples/hedis-w34/SampleW340009.xml" //Denom Met
	private static final Map ASSERTIONS_W340009 = [C2706:'',C54:'',C545:'']
	private static final Map MEASURES_W340009 = [C2625: [num: 0, denom: 1]]

	
	private static final String W340010 = "src/test/resources/samples/hedis-w34/SampleW340010.xml" //Denom Met
	private static final Map ASSERTIONS_W340010 = [C2706:'',C54:'',C545:'']
	private static final Map MEASURES_W340010  = [C2625: [num: 0, denom: 1]]
	
	private static final String W340011 = "src/test/resources/samples/hedis-w34/SampleW340011.xml" //Num Met
	private static final Map ASSERTIONS_W340011 = [C2680:'', C2706: '', C539:'', C54: '', C545: '']
	private static final Map MEASURES_W340011  = [C2625: [num: 1, denom: 1]]
	


/*
Concepts used:


*/
	
	@Unroll
	def "test HEDIS W34 v2014.1.0"() 
	{
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_W34', version: '2014.1.0'],
			specifiedTime: '2012-02-01'
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
		W340001 | ASSERTIONS_W340001| MEASURES_W340001
		W340002 | ASSERTIONS_W340002| MEASURES_W340002
		W340003 | ASSERTIONS_W340003| MEASURES_W340003
		W340004 | ASSERTIONS_W340004| MEASURES_W340004
		W340005 | ASSERTIONS_W340005| MEASURES_W340005
//		W340006 | ASSERTIONS_W340006| MEASURES_W340006
//		W340007 | ASSERTIONS_W340007| MEASURES_W340007
		W340008 | ASSERTIONS_W340008| MEASURES_W340008
		W340009 | ASSERTIONS_W340009| MEASURES_W340009
		W340010 | ASSERTIONS_W340010| MEASURES_W340010
		W340011 | ASSERTIONS_W340011| MEASURES_W340011
	}
}
