/*
 * Copyright 2014-2020 OpenCDS.org
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

package org.opencds.config.client.rest

import jakarta.ws.rs.InternalServerErrorException
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import spock.lang.Specification

class ExecutionEngineRestClientFunctionalSpec extends Specification {
    private static final String URI = "http://localhost:18080/opencds-decision-support-service/config/v1"
    private static final String EE = "src/test/resources/executionEngines.xml"

    RestClient restClient
    ExecutionEngineRestClient eeClient

    def setup() {
        Client c = ClientBuilder.newClient()
        restClient = new RestClient(c.target(URI), 'username', 'my1pass1')
        eeClient = new ExecutionEngineRestClient(restClient)
    }

    def "get execution engines"() {
        when:
        def ees = eeClient.getExecutionEngines(InputStream.class)
        println ees.text

        then:
        ees != null
    }

    def "put and get executionengines"() {
        given:
        def input = new FileInputStream(EE)

        when:
        eeClient.putExecutionEngines(input)

        then:
        def e = thrown(InternalServerErrorException)
        e.message == 'HTTP/1.1 500 Internal Server Error : Cannot persist to file store through dao API'
    }

    def "get specific executionengine"() {
        given:
        def id = 'org.opencds.client.drools.DroolsDecisionEngine53DSSEvaluationAdapter'

        when:
        def ee = eeClient.getExecutionEngine(id, InputStream.class)
        println ee.text

        then:
        ee != null
    }
}
