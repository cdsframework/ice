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


import org.hl7.fhir.dstu2.model.Resource
import org.opencds.hooks.model.context.ReadOnlyHookContext
import org.opencds.hooks.model.context.support.HookContextJsonSupport
import org.opencds.hooks.model.discovery.Services
import org.opencds.hooks.model.dstu2.support.ResourceJsonSupport
import org.opencds.hooks.model.request.CdsRequest
import org.opencds.hooks.model.request.support.CdsRequestJsonSupport
import org.opencds.hooks.model.response.CdsResponse
import org.opencds.hooks.model.Extension
import org.opencds.hooks.model.response.Indicator
import org.opencds.hooks.model.support.ExtensionJsonSupport
import org.opencds.hooks.model.response.support.IndicatorJsonSupport

import com.google.gson.Gson
import com.google.gson.GsonBuilder

import spock.lang.Specification

class GsonSpec extends Specification {
	private static final JSON_SERVICES = 'src/test/resources/services-test.json'
	private static final JSON_SANDBOX_REQUEST = 'src/test/resources/sandbox-test.json'
	private static final JSON_REQUEST = 'src/test/resources/hooks-request-test.json'
	private static final JSON_RESPONSE = 'src/test/resources/hooks-response-test.json'
	private static final JSON_REQUEST_RESOURCE_NOT_BEC = 'src/test/resources/test-payload-resource-not-bec.json'

	def "test unmarshalling services"() {
		given:
		String json = new File(JSON_SERVICES).text
		Gson gson = new Gson()

		when:
		def services = gson.fromJson(json, Services.class)

		then:
		services
		services instanceof Services
		println services

		when:
		def result = gson.toJson(services)

		then:
		result
		println result
	}

	def "test unmarshalling request"() {
		given:
		String json = new File(JSON_REQUEST).text
		GsonBuilder gsonBuilder = new GsonBuilder()
		gsonBuilder.registerTypeAdapter(CdsRequest.class, new CdsRequestJsonSupport());
		gsonBuilder.registerTypeAdapter(ReadOnlyHookContext.class, new HookContextJsonSupport());
		gsonBuilder.registerTypeAdapter(Resource.class, new ResourceJsonSupport());
		Gson gson = gsonBuilder.create()

		when:
		def request = gson.fromJson(json, CdsRequest.class)

		then:
		request
		request instanceof CdsRequest
		println request

		when:
		def result = gson.toJson(request)

		then:
		result
		println result
	}

	def "test unmarshalling sandbox request"() {
		given:
		String json = new File(JSON_SANDBOX_REQUEST).text
		GsonBuilder gsonBuilder = new GsonBuilder()
		gsonBuilder.registerTypeAdapter(CdsRequest.class, new CdsRequestJsonSupport());
		gsonBuilder.registerTypeAdapter(ReadOnlyHookContext.class, new HookContextJsonSupport());
		gsonBuilder.registerTypeAdapter(Resource.class, new ResourceJsonSupport());
		Gson gson = gsonBuilder.create()

		when:
		def request = gson.fromJson(json, CdsRequest.class)

		then:
		request
		request instanceof CdsRequest
		println request

		when:
		def result = gson.toJson(request)

		then:
		result
		println result
	}

	def "test unmarshalling response"() {
		given:
		String json = new File(JSON_RESPONSE).text

		GsonBuilder gsonBuilder = new GsonBuilder()
		gsonBuilder.registerTypeAdapter(Indicator.class, new IndicatorJsonSupport())
        gsonBuilder.registerTypeAdapter(Extension.class, new ExtensionJsonSupport())
		Gson gson = gsonBuilder.create()

		when:
		def response = gson.fromJson(json, CdsResponse.class)

		then:
		response
		response instanceof CdsResponse
		println response

		when:
		def responseString = gson.toJson(response)

		then:
		responseString
		println responseString
	}

	def "test unmarshalling request failing"() {
		given:
		String json = new File(JSON_REQUEST_RESOURCE_NOT_BEC).text
		GsonBuilder gsonBuilder = new GsonBuilder()
		gsonBuilder.registerTypeAdapter(CdsRequest.class, new CdsRequestJsonSupport());
		gsonBuilder.registerTypeAdapter(ReadOnlyHookContext.class, new HookContextJsonSupport());
		gsonBuilder.registerTypeAdapter(Resource.class, new ResourceJsonSupport());
		Gson gson = gsonBuilder.create()

		when:
		def request = gson.fromJson(json, CdsRequest.class)

		then:
		request
		request instanceof CdsRequest
		println request

		when:
		def result = gson.toJson(request)

		then:
		result
		println result
	}


}
