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

package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.ImproperUsageException
import org.opencds.common.exceptions.OpenCDSRuntimeException
import org.opencds.vmr.v1_0.internal.ClinicalStatement
import org.opencds.vmr.v1_0.internal.Problem
import org.opencds.vmr.v1_0.mappings.in.FactLists
import org.opencds.vmr.v1_0.schema.II

import spock.lang.Specification

public class ClinicalStatementMapperSpec extends Specification {

    def "test pullIn with null external and internal should yield no results and throw no exceptions"() {
        given:
        def external = null as org.opencds.vmr.v1_0.schema.ClinicalStatement
        def internal = null as ClinicalStatement
        def subjectPersonId = ""
        def focalPersonId = ""
        def factLists = new FactLists()
        
        when:
        ClinicalStatementMapper.pullIn(external, internal, subjectPersonId, focalPersonId, factLists)
        
        then:
        notThrown(OpenCDSRuntimeException)
    }
    
    def "test pullIn with null internal should throw an ImproperUsageException"() {
        given:
        def external = new org.opencds.vmr.v1_0.schema.Problem(id: new II(root: 'my', extension: 'source'))
        def internal = null as ClinicalStatement
        def subjectPersonId = ""
        def focalPersonId = ""
        def factLists = new FactLists()
        
        when:
        ClinicalStatementMapper.pullIn(external, internal, subjectPersonId, focalPersonId, factLists)
        
        then:
        thrown(ImproperUsageException)
    }

    def "test pullIn with null external id show throw an OpenCDSRuntimeException"() {
        given:
        def external = new org.opencds.vmr.v1_0.schema.Problem()
        def internal = new Problem(id: 'my^target')
        def subjectPersonId = ""
        def focalPersonId = ""
        def factLists = new FactLists()
        
        when:
        ClinicalStatementMapper.pullIn(external, internal, subjectPersonId, focalPersonId, factLists)
        
        then:
        thrown(OpenCDSRuntimeException)
    }
    
    def "test pullIn with null internal id -- no failure, no exception"() {
        given:
        def external = new org.opencds.vmr.v1_0.schema.Problem(id: new II(root: 'my', extension: 'source'))
        def internal = new Problem(id: null)
        def subjectPersonId = ""
        def focalPersonId = ""
        def factLists = new FactLists()
        
        when:
        ClinicalStatementMapper.pullIn(external, internal, subjectPersonId, focalPersonId, factLists)
        
        then:
        internal.clinicalStatementToBeRoot
        notThrown(OpenCDSRuntimeException)
    }
    
}
