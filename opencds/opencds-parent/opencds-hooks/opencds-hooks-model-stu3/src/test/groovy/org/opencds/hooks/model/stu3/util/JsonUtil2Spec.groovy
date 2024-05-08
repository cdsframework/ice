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

package org.opencds.hooks.model.stu3.util

import org.opencds.hooks.model.discovery.Services
import org.opencds.hooks.model.request.CdsRequest
import org.opencds.hooks.model.response.CdsResponse

import spock.lang.Specification

class JsonUtil2Spec extends Specification {
	private static final JSON_REQUEST = 'src/test/resources/cql-payload.json'

	Stu3JsonUtil jsonUtil

	def setup() {
		jsonUtil = new Stu3JsonUtil()
	}

	def "test unmarshalling request"() {
		given:
		String json = new File(JSON_REQUEST).text

		when:
		def request = jsonUtil.fromJson(json, CdsRequest.class)

		then:
		request
		request instanceof CdsRequest
		println request

		when:
		def result = jsonUtil.toJson(request)

		then:
		result
		println result
	}

}
