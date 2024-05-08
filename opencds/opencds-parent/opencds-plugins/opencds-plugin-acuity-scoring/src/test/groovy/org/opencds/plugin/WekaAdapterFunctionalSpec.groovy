/*
 * Copyright 2016-2020 OpenCDS.org
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

import org.joda.time.LocalDateTime
import org.opencds.config.api.EvaluationContext
import org.opencds.config.api.ExecutionEngineContext
import org.opencds.config.api.model.KnowledgeModule
import org.opencds.config.api.service.KnowledgePackageService
import org.opencds.plugin.PluginContext.PreProcessPluginContext
import org.opencds.service.weka.WekaAdapter
import org.opencds.service.weka.WekaExecutionEngineContext
import org.opencds.service.weka.WekaInput
import org.opencds.service.weka.WekaKnowledgeLoader
import org.opencds.service.weka.WekaPackage
import org.opencds.vmr.v1_0.internal.ObservationResult

import spock.lang.Specification

class WekaAdapterFunctionalSpec extends Specification {
    private static List sepsis = [
        "/Users/phillip/projects/opencds/sepsis/models/1/sepsis-1.zip",
        "/Users/phillip/projects/opencds/sepsis/models/2/sepsis-2.zip",
        "/Users/phillip/projects/opencds/sepsis/models/3/sepsis-3.zip",
        "/Users/phillip/projects/opencds/sepsis/models/4/sepsis-4.zip",
        "/Users/phillip/projects/opencds/sepsis/models/6/sepsis-6.zip",
        "/Users/phillip/projects/opencds/sepsis/models/12/sepsis-12.zip",
        "/Users/phillip/projects/opencds/sepsis/models/48/sepsis-48.zip"
    ]
    private static List sepsisHours = [1, 2, 3, 4, 6, 12, 48]
    private static List sepsisTests = [
        "/Users/phillip/projects/opencds/sepsis/models/1/Test.arff",
        "/Users/phillip/projects/opencds/sepsis/models/2/Test.arff",
        "/Users/phillip/projects/opencds/sepsis/models/3/Test.arff",
        "/Users/phillip/projects/opencds/sepsis/models/4/Test.arff",
        "/Users/phillip/projects/opencds/sepsis/models/6/Test.arff",
        "/Users/phillip/projects/opencds/sepsis/models/12/Test.arff",
        "/Users/phillip/projects/opencds/sepsis/models/48/Test.arff"
    ]

    static WekaPackage wekaPkg

    static LocalDateTime now

    static List<Map<Class<?>, List<?>>> afl
    static List<Boolean> outcomes
    static long start
	
	private WekaSepsisPreProcessor preproc = new WekaSepsisPreProcessor()
	private WekaSepsisPostProcessor postproc = new WekaSepsisPostProcessor()

    def setupSpec() {
        now = new LocalDateTime()
        int index = 0
        KnowledgePackageService kps = Mock()
        1 * kps.getPackageInputStream(_) >> new FileInputStream(sepsis[index])
        WekaKnowledgeLoader loader = new WekaKnowledgeLoader()
        KnowledgeModule km = Mock()
        1 * km.getPackageType() >> "ZIP"
        wekaPkg = loader.loadKnowledgePackage(kps, km)

		System.err.println "Index: $index"
        Map<Map<Class<?>, List<?>>, Boolean> everything = FactListsUtil.buildFactLists(now, sepsisTests[index], sepsisHours[index])

        
        outcomes = [] as ArrayList
        afl = everything.inject([] as ArrayList) {List a, k, v ->
            outcomes << v
            a << k
        }
        start = System.currentTimeMillis()
    }
    
    def cleanupSpec() {
        System.err.println("Timing: " + (System.currentTimeMillis() - start))
    }

    def "test"() {
        given:
        // need to iterate!!!  move expectations to test method
        EvaluationContext evaluationCtx = Mock()
        _ * evaluationCtx.getAllFactLists() >> allFactLists
        ExecutionEngineContext<WekaInput, Map<String, Boolean>> eeContext = new WekaExecutionEngineContext()
        eeContext.setEvaluationContext(evaluationCtx);
        PluginDataCache pdCache = Mock()
        PreProcessPluginContext preContext = PluginContext.createPreProcessPluginContext(allFactLists, [:], [:], [:], pdCache)
        WekaAdapter adapter = new WekaAdapter()
        0 * _

        expect:
        def ctx = adapter.execute(wekaPkg, eeContext)
        List<ObservationResult> results = ctx.getResults().get("ObservationResult")
        List<ObservationResult> result = results.grep {ObservationResult r ->
            r.observationFocus.code == "C308" && r.observationFocus.codeSystem == "2.16.840.1.113883.3.795.12.1.1"
        }
        System.out.println "Predicted: ${result[0].observationValue.get_boolean().value}  Expected: $outcome"
        result[0].observationValue.get_boolean().value == outcome

        where:
        outcome << outcomes
        allFactLists << afl
    }
}
