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


import jakarta.ws.rs.NotAuthorizedException
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import spock.lang.Specification

class RestAuthFunctionalSpec extends Specification {
    private static final String URI = "http://localhost:18080/opencds-decision-support-service/config/v1"

    Client c
    RestClient restClient
    ConceptDeterminationMethodRestClient client

    def "wrong username"() {
        given:
        c = ClientBuilder.newClient()
        restClient = new RestClient(c.target(URI), 'some-other-user', 'bad pass')
        client = new ConceptDeterminationMethodRestClient(restClient)

        when:
        def result = client.getConceptDeterminationMethods(String.class)

        then:
        def e = thrown(NotAuthorizedException)
        e.message == 'HTTP 401 Unauthorized'
    }

    def "correct username, wrong password"() {
        given:
        c = ClientBuilder.newClient()
        restClient = new RestClient(c.target(URI), 'username', 'bad pass')
        client = new ConceptDeterminationMethodRestClient(restClient)

        when:
        def result = client.getConceptDeterminationMethods(String.class)

        then:
        def e = thrown(NotAuthorizedException)
        e.message == 'HTTP 401 Unauthorized'
    }

}
