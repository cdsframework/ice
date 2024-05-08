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

package org.opencds.hooks.model.dstu2.context

import org.hl7.fhir.dstu2.model.Bundle
import org.hl7.fhir.dstu2.model.IdType
import org.hl7.fhir.dstu2.model.MedicationOrder
import org.hl7.fhir.dstu2.model.Resource
import org.opencds.hooks.model.context.HookContext
import org.opencds.hooks.model.context.ReadOnlyHookContext
import org.opencds.hooks.model.context.WritableHookContext
import org.opencds.hooks.model.context.support.HookContextJsonSupport
import org.opencds.hooks.model.dstu2.support.ResourceJsonSupport

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.opencds.hooks.model.dstu2.util.Dstu2JsonUtil
import spock.lang.Specification

class HookContextJsonSupportSpec extends Specification {
    private static final FILE = 'src/test/resources/context/context-med-pres-1.json'
    private static final FILE_PV = 'src/test/resources/context/hooks-patient-view-request.json'
    private static final FILE_MP = 'src/test/resources/context/hooks-request-test.json'
    private static final FILE_OR = 'src/test/resources/context/context-order-review.json'

    private static Dstu2JsonUtil jsonUtil

    def setupSpec() {
        jsonUtil = new Dstu2JsonUtil()
    }

    def 'test deserialize'() {
        given:
        String json = new File(FILE).text

        when:
        ReadOnlyHookContext response = jsonUtil.fromJson(json, ReadOnlyHookContext.class)

        then:
        response
        response instanceof ReadOnlyHookContext
        response.get('patientId', String) == '1288992'
        response.get('encounterId', String) == '89284'
        Bundle meds = response.get('medications', Bundle)
        meds?.getEntry()?.size() == 2
        meds?.getEntry()[0].getResource().getIdElement().getIdPart() == 'smart-MedicationOrder-103'
        meds?.getEntry()[0].getResource().dosageInstruction[0].textElement.value == '5 mL bid x 10 days'
        meds?.getEntry()[1].getResource().getIdElement().getIdPart() == 'smart-MedicationOrder-104'
        meds?.getEntry()[1].getResource().dosageInstruction[0].textElement.value == '15 mL daily x 3 days'
    }

    def 'test deserialize PV'() {
        given:
        String json = new File(FILE_PV).text

        when:
        ReadOnlyHookContext response = jsonUtil.fromJson(json, ReadOnlyHookContext.class)

        then:
        response
        response.get('patientId', String) == 'b628024e-5d06-4a5a-a112-dec4ee95e4dd'
        response.get('encounterId', String) == 'e11eb771-89d5-46a8-b72f-284660997230'
    }

    def 'test deserialize MP'() {
        given:
        String json = new File(FILE_MP).text

        when:
        ReadOnlyHookContext response = jsonUtil.fromJson(json, ReadOnlyHookContext.class)

        then:
        response
        response.get('patientId', String) == '1288992'
        response.get('encounterId', String) == '982734678'
        response.get('medications', Bundle)?.getEntry().size() == 2
    }

    /*
     * NOTE: I'll need to rethink this test...
     */

    def 'test serialize'() {
        given: "a note about the jsonsupport; the test case has a 'hook' added to it, as the jsonsupport impl requires it (it's put there by CdsRequestJsonSupport)"
        String patientId = '12390'
        String encounterId = '43280'
        Bundle medications = new Bundle()
        medications.addEntry(new Bundle.BundleEntryComponent().setResource(new MedicationOrder().setId(new IdType().setValue('879289'))))
        medications.addEntry(new Bundle.BundleEntryComponent().setResource(new MedicationOrder().setId(new IdType().setValue('234632'))))
        HookContext ctx = new WritableHookContext()
        ctx.setHook('hook')
        ctx.add('patientId', patientId);
        ctx.add('encounterId', encounterId);
        ctx.add('medications', medications);

        when:
        String response = jsonUtil.toJson(ctx);

        then:
        response == '{"patientId":"12390","medications":{"resourceType":"Bundle","entry":[{"resource":{"resourceType":"MedicationOrder","id":"879289"}},{"resource":{"resourceType":"MedicationOrder","id":"234632"}}]},"encounterId":"43280"}'
    }

    def 'test deserialize OR'() {
        given:
        String json = new File(FILE_OR).text

        when:
        ReadOnlyHookContext response = jsonUtil.fromJson(json, ReadOnlyHookContext.class)

        then:
        response
        response.get('patientId', String) == '06a21673-cd25-4b80-b225-01dae3a35129'
        response.get('encounterId', String) == 'b09abae7-1851-4fbb-b5f3-17a5ab5ca4bd'
        response.get('orders', Bundle)?.getEntry().size() == 0

        when:
        String jsonResponse = jsonUtil.toJson(response);

        then:
        jsonResponse
        println jsonResponse
    }

}
