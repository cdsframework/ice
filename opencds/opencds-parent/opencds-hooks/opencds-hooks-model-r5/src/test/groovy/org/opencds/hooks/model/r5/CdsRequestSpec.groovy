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

package org.opencds.hooks.model.r5

import org.hl7.fhir.r5.model.OperationOutcome
import org.hl7.fhir.r5.model.Patient
import org.hl7.fhir.r5.model.Resource
import org.opencds.hooks.model.r5.request.prefetch.R5PrefetchHelper
import org.opencds.hooks.model.r5.util.R5JsonUtil
import org.opencds.hooks.model.request.CdsRequest
import org.opencds.hooks.model.request.prefetch.PrefetchResult
import org.opencds.hooks.lib.json.JsonUtil
import spock.lang.Specification

class CdsRequestSpec extends Specification {
    private static final String REQUEST = 'src/test/resources/simple-request.json'

    private JsonUtil jsonUtil = new R5JsonUtil()
    private R5PrefetchHelper helper = new R5PrefetchHelper()

    def "getPrefetchResource"() {
        given:
        String json = new File(REQUEST).text
        CdsRequest request = jsonUtil.fromJson(json, CdsRequest.class)

        when:
        PrefetchResult<OperationOutcome, Resource> prefetchResult = request.getPrefetchResource('Patient', helper)

        then:
        prefetchResult

        when:
        Patient patient = prefetchResult.getResource(Patient)

        then:
        patient
        patient.getBirthDate().toString() == 'Sun Mar 12 00:00:00 MST 1939'
    }
}
