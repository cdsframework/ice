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

package org.opencds.config.client.util

import jakarta.ws.rs.core.MediaType
import org.opencds.config.cli.util.ResourceUtil
import spock.lang.Specification

class ResourceUtilSpec extends Specification {
    private static final String EXEC_ENG_XML = "src/test/resources/executionEngines.xml"
    private static final String KM_PKG = "src/test/resources/org.opencds^bounce^1.5.5.drl"

    def "get contents of the xml file into the map"() {
        given:
        def data = EXEC_ENG_XML

        when:
        def result = ResourceUtil.get(data)

        then:
        result.input
        result.input instanceof String
        result.mediaType == MediaType.APPLICATION_XML
        result.type == 'executionEngines'
    }

    def "get contents of a file that is not found returns FileNotFoundException"() {
        given:
        def data = "I_dont_exist"

        when:
        def result = ResourceUtil.get(data)

        then:
        result == null
        thrown(Exception)
    }

    def "get contents of the knowledgePackage file into the map"() {
        given:
        def data = KM_PKG

        when:
        def result = ResourceUtil.get(data)
        InputStream input = result.input()

        then:
        input
        input instanceof FileInputStream
        result.mediaType == MediaType.APPLICATION_OCTET_STREAM

        and:
        input.close()
    }

}
