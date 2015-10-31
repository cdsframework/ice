package org.opencds.service;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class tomcatdebug extends Specification {
	private static final String SAMPLE = "C:/Users/u0734599/problemVMR.xml"
	@Unroll
	def "test tomcat debug"() {
		when:
		def input = new File(SAMPLE).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_AAP', version: '2014.0.0'],
			specifiedTime: '2014-01-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
        print responsePayload

	   then:
        true
	}
}