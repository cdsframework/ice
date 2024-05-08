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
import org.opencds.plugin.support.PluginDataCacheImpl
import org.opencds.vmr.v1_0.internal.EvalTime
import org.opencds.vmr.v1_0.internal.EvaluatedPerson
import org.opencds.vmr.v1_0.internal.FocalPersonId

import spock.lang.Specification

class AssertionsPluginSpec extends Specification {
    static final CONCEPTS = 'src/test/resources/concepts.txt'
    
    Map allFactLists
    Map namedObjects
    Set assertions
    Map results
    Map supportingData
    PluginDataCache cache

    def setup() {
        namedObjects = [:]
        cache = Mock()
        results = [:]
        supportingData = [:]
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
        def p = new Properties()
        p.load(new StringReader(new File(CONCEPTS).text))
    }

    def "test concepts"() {
        given:
        assertions = ['C103', 'C10', 'C50']
        PostProcessPluginContext context = PluginContext.createPostProcessPluginContext(
                allFactLists, namedObjects, assertions, results, supportingData, cache)
        
        
        and:
        0 * _._

        when:
        new AssertionsPlugin().execute(context)
        
        then:
        println context.namedObjects
        context.resultFactLists.ObservationResult != null
        context.resultFactLists.ObservationResult.size() > 0
        context.resultFactLists.ObservationResult[0].observationValue.text == 'C10=|C103=|C50='
    }
    
    def "test OpenCDS concepts"() {
        given:
        assertions = ['C103', 'C10', 'C50']
        supportingData = ['concepts' : SupportingData.create('concepts', 'kmid',
        '', 'pkgid', 'properties', new Date(), [
                get:{[
                        getFile:{new File(CONCEPTS).text},
                        getBytes:{new File(CONCEPTS).text.bytes}] as SupportingDataPackage}] as SupportingDataPackageSupplier)]
        cache = new PluginDataCacheImpl() 
        PostProcessPluginContext context = PluginContext.createPostProcessPluginContext(
                allFactLists, namedObjects, assertions, results, supportingData, cache)
        
        and:
        0 * _._

        when:
        new AssertionsPlugin().execute(context)
        
        then:
        println context.namedObjects
        context.resultFactLists.ObservationResult != null
        context.resultFactLists.ObservationResult.size() > 0
        context.resultFactLists.ObservationResult[0].observationValue.text == 'C10=|C103=|C50='
    }
    
}
