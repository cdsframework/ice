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

package org.opencds.plugin

import org.opencds.plugin.PluginContext.PostProcessPluginContext
import org.opencds.vmr.v1_0.internal.EvalTime
import org.opencds.vmr.v1_0.internal.EvaluatedPerson
import org.opencds.vmr.v1_0.internal.FocalPersonId
import org.opencds.vmr.v1_0.internal.concepts.ProcedureConcept

import spock.lang.Specification

class DebugObjectsPluginSpec extends Specification {

    Map allFactLists
    Map namedObjects
    Set assertions
    Map results
    Map supportingData
    PluginDataCache cache

    def setup() {
        namedObjects = [:]
        cache = Mock()
        assertions = Mock()
        results = [:]
        supportingData = Mock()
        allFactLists = [
            (EvalTime): [
                new EvalTime(evalTimeValue : new Date())
            ],
            (EvaluatedPerson) : [
                new EvaluatedPerson(id : UUID.randomUUID() as String)
            ],
            (FocalPersonId) : [
                new FocalPersonId(UUID.randomUUID() as String)]
        ]
    }

    def "test concepts"() {
        given:
        namedObjects << ['debug:one' : 'ONE']
        namedObjects << ['debug:two' : 'TWO']
        PostProcessPluginContext context = PluginContext.createPostProcessPluginContext(
                allFactLists, namedObjects, assertions, results, supportingData, cache)

        and:
        0 * _._

        when:
        new DebugObjectsPlugin().execute(context)

        then:
        context.resultFactLists.ObservationResult != null
        context.resultFactLists.ObservationResult.size() > 0
        context.resultFactLists.ObservationResult[0].observationValue.text == 'one=ONE|two=TWO'
    }
}
