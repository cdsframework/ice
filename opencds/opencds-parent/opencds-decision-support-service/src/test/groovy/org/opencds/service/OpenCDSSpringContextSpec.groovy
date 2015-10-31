package org.opencds.service;

import javax.inject.Inject

import org.opencds.dss.evaluate.EvaluationSoapService
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

@ContextConfiguration(locations="/WEB-INF/beans.xml")
public class OpenCDSSpringContextSpec extends Specification {

	@Inject
	private EvaluationSoapService evaluationSoapService
	
	def setup() {
		assert evaluationSoapService != null
		assert evaluationSoapService.evaluation != null
	}
	
	def "test me"() {
		expect:
		true
	}
}
