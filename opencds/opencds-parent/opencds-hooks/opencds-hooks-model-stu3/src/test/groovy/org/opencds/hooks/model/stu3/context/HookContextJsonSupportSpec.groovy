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

package org.opencds.hooks.model.stu3.context

import ca.uhn.fhir.context.FhirVersionEnum
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.Resource
import org.opencds.hooks.lib.fhir.FhirContextFactory
import org.opencds.hooks.model.context.ReadOnlyHookContext
import org.opencds.hooks.model.context.support.HookContextJsonSupport
import org.opencds.hooks.model.stu3.support.ResourceJsonSupport
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.opencds.hooks.model.stu3.util.Stu3JsonUtil
import spock.lang.Specification

class HookContextJsonSupportSpec extends Specification {
    private static final FILE_UNK = 'src/test/resources/context/unknown-context.json'

    private static Stu3JsonUtil jsonUtil

	def setupSpec() {
        jsonUtil = new Stu3JsonUtil()
	}

    def 'test deserialize UNKNOWN'() {
        given:
        String json = new File(FILE_UNK).text

        when:
        ReadOnlyHookContext response = jsonUtil.fromJson(json, ReadOnlyHookContext.class)

        then:
        response
        response instanceof ReadOnlyHookContext
        String patientId = response.get('patientId', String.class)
        println patientId
        String encounterId = response.get('encounterId', String.class)
        println encounterId
        Bundle medicationOrderBundle = response.get('medications', Bundle.class)
        println medicationOrderBundle

        println FhirContextFactory.get(FhirVersionEnum.DSTU3).newJsonParser().encodeResourceToString(medicationOrderBundle)

        when:
        String jsonResponse = jsonUtil.toJson(response);

        then:
        jsonResponse
        println jsonResponse
    }

}
