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

package org.opencds.hooks.model.dstu2

import org.hl7.fhir.dstu2.model.Bundle
import org.hl7.fhir.dstu2.model.Enumerations
import org.hl7.fhir.dstu2.model.HumanName
import org.hl7.fhir.dstu2.model.MedicationOrder
import org.hl7.fhir.dstu2.model.Patient
import org.opencds.hooks.model.context.WritableHookContext
import org.opencds.hooks.model.dstu2.util.Dstu2JsonUtil
import org.opencds.hooks.model.request.WritableCdsRequest
import org.opencds.hooks.lib.json.JsonUtil
import spock.lang.Specification

class WritableCdsRequestSpec extends Specification {
    JsonUtil jsonUtil

    def setup() {
        jsonUtil = new Dstu2JsonUtil()
    }

    def 'test creation of cdsrequest'() {
        given:
        WritableCdsRequest req = new WritableCdsRequest()
        WritableHookContext context = new WritableHookContext()

        when:
        req.setHook('hook')
        req.setHookInstance(UUID.randomUUID().toString())
        context.add('patientId', UUID.randomUUID().toString())
        context.add('medications', new Bundle().addEntry(new Bundle.BundleEntryComponent().setResource(new MedicationOrder())))
        req.setContext(context)
        Patient patient = new Patient()
        patient.setId('id')
        patient.setGender(Enumerations.AdministrativeGender.MALE)
        patient.addName(new HumanName().addFamily('Family').addGiven('Given'))
        req.addPrefetchResource('patient', patient)
        req.addPrefetchResource('medOrders', new Bundle().addEntry(new Bundle.BundleEntryComponent().setResource(new MedicationOrder())))
        String json = jsonUtil.toJson(req)

        then:
        println json
        json
    }
}
