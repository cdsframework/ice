package org.opencds.service;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class tomcatdebug extends Specification {
	private static final String SAMPLE = "/usr/local/projects/ice3dev-resources/testing/ongoing-testing.xml"
	@Unroll
	def "test tomcat debug"() {
		when:
		def input = new File(SAMPLE).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'org.nyc.cir', businessId: 'ICE', version: '0.9.0'],
			specifiedTime: '2015-10-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
        print responsePayload

	   then:
        true
	}
}