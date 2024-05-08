/*
 * Copyright 2020 OpenCDS.org
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

package org.opencds.hooks.model.r5.util

import org.opencds.hooks.model.request.CdsRequest
import spock.lang.Specification

class BadRequestSpec extends Specification {
	private static final JSON_SERVICES = 'src/test/resources/fhir.cygni.cc.500.json'

	R5JsonUtil jsonUtil

	def setup() {
		jsonUtil = new R5JsonUtil()
	}

	def "test unmarshalling services"() {
		given:
		String json = new File(JSON_SERVICES).text

		when:
		def cdsrequest = jsonUtil.fromJson(json, CdsRequest.class)

		then:
		cdsrequest
		cdsrequest instanceof CdsRequest
		println cdsrequest

		when:
		def result = jsonUtil.toJson(cdsrequest)

		then:
		result
		println result
	}

}
