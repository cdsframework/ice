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

package org.opencds.hooks.model.r4.util

import org.opencds.hooks.model.discovery.Services
import org.opencds.hooks.model.request.CdsRequest
import org.opencds.hooks.model.response.CdsResponse

import spock.lang.Specification

class JsonUtilSpec extends Specification {
	private static final JSON_SERVICES = 'src/test/resources/services-test.json'
	private static final JSON_REQUEST = 'src/test/resources/hooks-request-test.json'
	private static final JSON_REQUEST_EVAL = 'src/test/resources/hooks-request-test-with-evaltime.json'
	private static final JSON_RESPONSE = 'src/test/resources/hooks-response-test.json'

    R4JsonUtil jsonUtil

	def setup() {
		jsonUtil = new R4JsonUtil()
	}

	def "test unmarshalling services"() {
		given:
		String json = new File(JSON_SERVICES).text

		when:
		def services = jsonUtil.fromJson(json, Services.class)

		then:
		services
		services instanceof Services
		println services

		when:
		def result = jsonUtil.toJson(services)

		then:
		result
		println result
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

	def "test unmarshalling request with evaltime"() {
		given:
		String json = new File(JSON_REQUEST_EVAL).text

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

	def "test unmarshalling response"() {
		given:
		String json = new File(JSON_RESPONSE).text

		when:
		def response = jsonUtil.fromJson(json, CdsResponse.class)

		then:
		response
		response instanceof CdsResponse
		println response

		when:
		def responseString = jsonUtil.toJson(response)

		then:
		responseString
		println responseString
	}
}
