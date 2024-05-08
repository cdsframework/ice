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

package org.opencds.hooks.evaluation.service

import org.hl7.fhir.dstu2.model.Bundle
import org.hl7.fhir.dstu2.model.IdType
import org.hl7.fhir.dstu2.model.MedicationDispense
import org.hl7.fhir.dstu2.model.MedicationOrder
import org.opencds.hooks.model.dstu2.request.prefetch.Dstu2PrefetchHelper
import org.opencds.hooks.model.request.CdsRequest
import org.opencds.hooks.model.request.WritableCdsRequest
import org.opencds.hooks.model.request.prefetch.PrefetchResult

import spock.lang.Specification

class ResourceListBuilderSpec extends Specification {

    private ResourceListBuilder builder

    def setup() {
        builder = new ResourceListBuilder()
    }

    def "test builder"() {
        given:
        CdsRequest request = new WritableCdsRequest()
        request.addPrefetchResource('one', new Bundle()
            .addEntry(new Bundle.BundleEntryComponent(resource: new MedicationOrder(id: new IdType("one"))))
            .addEntry(new Bundle.BundleEntryComponent(resource: new MedicationOrder(id: new IdType("two"))))
            .addEntry(new Bundle.BundleEntryComponent(resource: new MedicationOrder(id: new IdType("three"))))
            .addEntry(new Bundle.BundleEntryComponent(resource: new MedicationDispense(id: new IdType("four"))))
        )

        when:
        def result = builder.buildAllFactLists(request)

        then:
        println result
        result.keySet().size() == 1
        result.get(CdsRequest.class).size() == 1
        PrefetchResult pr = CdsRequest.class.cast(result.get(CdsRequest.class)[0]).getPrefetchResource('one', new Dstu2PrefetchHelper())
        Bundle bundle = pr.getResource(Bundle.class);
        bundle.entry.size() == 4
    }
}
