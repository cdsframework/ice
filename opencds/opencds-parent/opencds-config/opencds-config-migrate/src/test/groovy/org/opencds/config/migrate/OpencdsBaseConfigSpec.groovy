/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
