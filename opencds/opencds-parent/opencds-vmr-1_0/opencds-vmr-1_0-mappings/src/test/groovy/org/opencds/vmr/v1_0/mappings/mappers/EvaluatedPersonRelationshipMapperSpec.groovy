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
