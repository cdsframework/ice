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
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility
import org.opencds.vmr.v1_0.schema.EntityRelationship
import org.opencds.vmr.v1_0.schema.II

import spock.lang.Specification

public class EvaluatedPersonRelationshipMapperSpec extends Specification {

    def mappingUtility = new MappingUtility()
    
    def "test pullIn with null source and target"() {
        when:
        def target = EvaluatedPersonRelationshipMapper.pullIn((EntityRelationship) null, mappingUtility)
        
        then:
        target == null
    }
    
    def "test pullIn with null target"() {
        given:
        def external = [sourceId: new II(root: 'my', extension: 'source')] as EntityRelationship
        
        when:
        def target = EvaluatedPersonRelationshipMapper.pullIn(external, mappingUtility)
        
        then:
        thrown(OpenCDSRuntimeException)
    }
    
    def "test pullIn with distinct source and target"() {
        given:
        def external = new EntityRelationship(sourceId: new II(root: 'my', extension: 'source'), targetEntityId: new II(root: 'my', extension: 'target'))
        
        when:
        def target = EvaluatedPersonRelationshipMapper.pullIn(external, mappingUtility)
        
        then:
        target != null
    }
    
    def "test pullIn with distinct source and target -- root only"() {
        given:
        def external = [sourceId: new II(root: 'src'), targetEntityId: new II(root: 'tgt')] as EntityRelationship
        
        when:
        def target = EvaluatedPersonRelationshipMapper.pullIn(external, mappingUtility)
        
        then:
        target != null
    }
    
    def "test pullIn with equal source and target"() {
        given:
        def external = [sourceId: new II(root: 'my', extension: 'source'), targetEntityId: new II(root: 'my', extension: 'source')] as EntityRelationship
        
        when:
        def target = EvaluatedPersonRelationshipMapper.pullIn(external, mappingUtility)
        
        then:
        thrown(OpenCDSRuntimeException)
    }
    
    def "test pullIn with equal source and target -- root only"() {
        given:
        def external = [sourceId: new II(root: 'my'), targetEntityId: new II(root: 'my')] as EntityRelationship
        
        when:
        def target = EvaluatedPersonRelationshipMapper.pullIn(external, mappingUtility)
        
        then:
        thrown(OpenCDSRuntimeException)
    }
    
}
