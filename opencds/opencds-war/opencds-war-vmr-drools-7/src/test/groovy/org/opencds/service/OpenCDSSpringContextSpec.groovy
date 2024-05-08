package org.opencds.service

import org.junit.runner.RunWith
import org.opencds.war.vmr.drools7.test.TestConfiguration
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject

import org.opencds.dss.evaluate.EvaluationSoapService
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
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
