package org.opencds.service


import org.opencds.service.util.OpencdsClient
import org.springframework.ws.soap.client.SoapFaultClientException;

import spock.lang.Specification
import spock.lang.Unroll

public class StackOverflowFunctionalSpec extends Specification {
	private static final String FPC_0013 = "src/test/resources/samples/SO-test.xml"
	/* 1 - Denom check: HEDIS Delivery by ICD9Px	*/
	/* 1 - Num check: one standalone prenatal visit by HCPCS, w/ provider, 1 month before delivery  */
	private static final Map ASSERTIONS_FPC_0013 = [ 'Percent(1)': '7', 'PrenatalVisitDistinctDateCount(1)': '1']
	private static final Map MEASURES_FPC_0013 = [C3386: [num: 1, denom: 1], C3387: [num: 0, denom: 1], C3388: [num: 0, denom: 1],
		C3389: [num: 0, denom: 1], C3390: [num: 0, denom: 1]]


	@Unroll
	def "get proper message from StackOverflowError"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'org.opencds', businessId: 'bounce', version: '7'],
			specifiedTime: '2012-01-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input, false)

		then:
		def e = thrown(SoapFaultClientException)
		e.message == 'OpenCDS encountered Exception when building output: CdsOutputResultSetBuilder received a StackOverflowError, possibly caused by duplicate ids in the output from the ExecutionEngine'

		where:
		vmr | assertions | measuresList
		FPC_0013 | ASSERTIONS_FPC_0013| MEASURES_FPC_0013
	}
}
