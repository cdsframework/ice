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

package org.opencds.config.api.util;

import org.opencds.config.api.util.URIUtil

import spock.lang.Specification

class URIUtilSpec extends Specification {

    def "test getKMId from a String"() {
        given:
        def input = "one^two^three"
        
        when:
        def result = URIUtil.getKMId(input)
        
        then:
        result
        result.scopingEntityId == 'one'
        result.businessId == 'two'
        result.version == 'three'
    }
    
    def "test getKMId with a bad string"() {
        given:
        def input = "one^two"
        
        when:
        def result = URIUtil.getKMId(input)
        
        then:
        thrown(IllegalArgumentException)
    }

}