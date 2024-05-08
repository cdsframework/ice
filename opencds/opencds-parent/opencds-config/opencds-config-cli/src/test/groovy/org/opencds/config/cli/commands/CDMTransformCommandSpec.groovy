/*
 * Copyright 2015-2020 OpenCDS.org
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

package org.opencds.config.cli.commands

import spock.lang.Ignore
import spock.lang.Specification

class CDMTransformCommandSpec extends Specification {

    @Ignore
	def "test xml to psv"() {
		given:
		def input = new FileInputStream('src/test/resources/cdm.xml')
		def output = new FileOutputStream('src/test/resources/my-output.psv') // System.out
		Closure command = new  CDMTransformCommand(input, output, 'spec').cdmToPsv
		
		when:
		command()
		
		then:
		true
	}
	
/*
	def "test psv to xml"() {
		given:
		def input = new FileInputStream('src/test/resources/cdm_pqrs.psv')
		def output = new FileOutputStream('src/test/resources/cdm-2016.xml')
		Closure command = new CDMTransformCommand(input, output, 'spec').psvToCdm
		
		when:
		command()
		
		then:
		true
		
	}
*/	
}
