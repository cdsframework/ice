package org.opencds.config.migrate;

import org.opencds.common.exceptions.OpenCDSRuntimeException

import spock.lang.Specification
import spock.lang.Unroll

class OpencdsBaseConfigSpec extends Specification {

	@Unroll
	def "test values for kr type enum, expect SIMPLE_FILE and CLASSPATH support"() {
		expect:
		bclType == BaseConfigLocationType.resolveType(type)

		where:
		bclType	| type
		BaseConfigLocationType.SIMPLE_FILE | 'SIMPLE_FILE'
		BaseConfigLocationType.CLASSPATH | 'CLASSPATH'
		null	| 'nothing'
	}

	def "construction of OpencdsConfigurator with SIMPLE_FILE type yields a valid OC"() {
		given:
		def type = 'SIMPLE_FILE'
		def path = '/my/path/'
		def map = [(ConfigResourceType.CODE_SYSTEMS): new ConfigResource()]
		
		when:
		def oc = new OpencdsBaseConfig(type, path, map)

		then:
		notThrown(OpenCDSRuntimeException)
		oc
		oc.baseConfigLocation
		oc.baseConfigLocation.type == BaseConfigLocationType.SIMPLE_FILE
		oc.baseConfigLocation.type.scheme == 'file'
		oc.baseConfigLocation.path == path
	}
	
	def "construction of OpencdsConfigurator with CLASSPATH type yields a valid OC"() {
		given:
		def type = 'CLASSPATH'
		def path = '/my/path/'
		def map = [(ConfigResourceType.CODE_SYSTEMS): new ConfigResource()]

		when:
		def oc = new OpencdsBaseConfig(type, path, map)

		then:
		notThrown(OpenCDSRuntimeException)
		oc
		oc.baseConfigLocation
		oc.baseConfigLocation.type == BaseConfigLocationType.CLASSPATH
		oc.baseConfigLocation.type.scheme == 'classpath'
		oc.baseConfigLocation.path == path
	}

	def "construction of OpencdsConfigurator with CLASSPATH type yields a valid OC with normalized path"() {
		given:
		def type = 'CLASSPATH'
		def path = '/my/path'
		def map = [(ConfigResourceType.CODE_SYSTEMS): new ConfigResource()]

		when:
		def oc = new OpencdsBaseConfig(type, path, map)

		then:
		notThrown(OpenCDSRuntimeException)
		oc
		oc.baseConfigLocation
		oc.baseConfigLocation.type == BaseConfigLocationType.CLASSPATH
		oc.baseConfigLocation.path == path + '/'
	}

	def "construction of OpencdsConfigurator with unsupported type yields an exception"() {
		given:
		def type = 'UNSUPPORTED'
		def path = '/my/path'
		def map = [(ConfigResourceType.CODE_SYSTEMS): new ConfigResource()]

		when:
		def oc = new OpencdsBaseConfig(type, path, map)

		then:
		thrown(OpenCDSRuntimeException)
	}

}
