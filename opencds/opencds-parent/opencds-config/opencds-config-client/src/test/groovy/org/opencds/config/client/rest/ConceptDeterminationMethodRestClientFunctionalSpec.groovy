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
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import spock.lang.Specification

class ConceptDeterminationMethodRestClientFunctionalSpec extends Specification {
    private static final String URI = "http://localhost:18080/opencds-decision-support-service/config/v1"
    private static final String CDM = "src/test/resources/conceptDeterminationMethods/cdm.xml"

    RestClient restClient
    ConceptDeterminationMethodRestClient client

    def setup() {
        Client c = ClientBuilder.newClient()
        restClient = new RestClient(c.target(URI), 'username', 'my1pass1')
        client = new ConceptDeterminationMethodRestClient(restClient)
    }

    def "get cdms"() {
        when:
        def result = client.getConceptDeterminationMethods(InputStream.class)
        println result.text.size()

        then:
        result != null
    }

    def "get unknown cdm"() {
        given:
        def cdmId = "2.16.840.1.113883.3.795.12.1.1^C00^1.0"

        when:
        def cdm = client.getConceptDeterminationMethod(cdmId, String.class)

        then:
        thrown(NotFoundException)
    }

    def "get known cdm"() {
        given:
        def cdmId = "2.16.840.1.113883.3.795.12.1.1^C74^1.0"

        when:
        def cdm = client.getConceptDeterminationMethod(cdmId, String.class)

        then:
        cdm != null
    }

    def "post cdms"() {
        given:
        String input = new File(CDM).text

        when:
        client.putConceptDeterminationMethods(input)

        then:
        def e = thrown(InternalServerErrorException)
        println e.message
    }
}
