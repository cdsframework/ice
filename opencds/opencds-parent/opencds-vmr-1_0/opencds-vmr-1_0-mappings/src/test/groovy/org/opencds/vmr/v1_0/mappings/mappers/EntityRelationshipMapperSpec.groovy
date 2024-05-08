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

import org.opencds.common.exceptions.OpenCDSRuntimeException
import org.opencds.vmr.v1_0.internal.ClinicalStatement
import org.opencds.vmr.v1_0.internal.EntityRelationship;
import org.opencds.vmr.v1_0.mappings.in.FactLists
import org.opencds.vmr.v1_0.schema.CD
import org.opencds.vmr.v1_0.schema.II
import org.opencds.vmr.v1_0.schema.IVLTS

import spock.lang.Specification

class EntityRelationshipMapperSpec extends Specification {

    def "test pullIn with null source and target"() {
        given:
        def source = null as II
        def target = null as II
        def targetRelationshipToSource = new CD()
        def relationshipTimeInterval = new IVLTS()
        def factLists = new FactLists()
        
        when:
        EntityRelationshipMapper.pullIn(source, target, targetRelationshipToSource, relationshipTimeInterval, factLists)
        
        then:
        thrown(OpenCDSRuntimeException)
    }
    
    def "test pullIn with null target"() {
        given:
        def source = "my^source"
        def target = null as II
        def targetRelationshipToSource = new CD()
        def relationshipTimeInterval = new IVLTS()
        def factLists = new FactLists()
        
        when:
        EntityRelationshipMapper.pullIn(source, target, targetRelationshipToSource, relationshipTimeInterval, factLists)
        
        then:
        thrown(OpenCDSRuntimeException)
    }
    
    def "test pullIn with distinct source and target"() {
        given:
        def source = 'my^source'
        def target = new II(root: 'my', extension: 'target')
        def targetRelationshipToSource = new CD()
        def relationshipTimeInterval = new IVLTS()
        def factLists = new FactLists()
        
        when:
        EntityRelationshipMapper.pullIn(source, target, targetRelationshipToSource, relationshipTimeInterval, factLists)
        
        then:
        def data = factLists.get(EntityRelationship.class)
        data != null
        data.size() == 1
        println "DATA -> " + data
        data[0].sourceId == source
        data[0].targetEntityId == "${target.root}^${target.extension}"
    }
    
    def "test pullIn with distinct source and target -- root only"() {
        given:
        def source = 'src'
        def target = new II(root: 'tgt')
        def targetRelationshipToSource = new CD()
        def relationshipTimeInterval = new IVLTS()
        def factLists = new FactLists()
        
        when:
        EntityRelationshipMapper.pullIn(source, target, targetRelationshipToSource, relationshipTimeInterval, factLists)
        
        then:
        factLists.get(EntityRelationship.class) != null
    }
    
    def "test pullIn with equal source and target"() {
        given:
        def source = 'my^source'
        def target = new II(root: 'my', extension: 'source')
        def targetRelationshipToSource = new CD()
        def relationshipTimeInterval = new IVLTS()
        def factLists = new FactLists()
        
        when:
        EntityRelationshipMapper.pullIn(source, target, targetRelationshipToSource, relationshipTimeInterval, factLists)
        
        then:
        thrown(OpenCDSRuntimeException)
    }
    
    def "test pullIn with equal source and target -- root only"() {
        given:
        def source = 'my'
        def target = new II(root: 'my')
        def targetRelationshipToSource = new CD()
        def relationshipTimeInterval = new IVLTS()
        def factLists = new FactLists()
        
        when:
        EntityRelationshipMapper.pullIn(source, target, targetRelationshipToSource, relationshipTimeInterval, factLists)
        
        then:
        thrown(OpenCDSRuntimeException)
    }
    
}
