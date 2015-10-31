package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.OpenCDSRuntimeException
import org.opencds.vmr.v1_0.internal.datatypes.CD
import org.opencds.vmr.v1_0.mappings.in.FactLists
import org.opencds.vmr.v1_0.schema.ClinicalStatementRelationship
import org.opencds.vmr.v1_0.schema.II

import spock.lang.Specification

public class ClinicalStatementRelationshipMapperSpec extends Specification {

    def "pullIn method for CSR and FL should not throw an OpenCDSRuntimeException when the targetId != sourceId"() {
        given:
        def external = new ClinicalStatementRelationship(sourceId: new II(root: 'abcd', extension: '1234'), targetId: new II(root: 'efgh', extension: '5678'))
        def factLists = new FactLists()
        
        when:
        ClinicalStatementRelationshipMapper.pullIn(external, factLists)
        
        then:
        notThrown(OpenCDSRuntimeException)
        def data = factLists.get(org.opencds.vmr.v1_0.internal.ClinicalStatementRelationship.class)
        data != null
        data.size() == 1
        data[0].sourceId == "${external.sourceId.root}^${external.sourceId.extension}"
        data[0].targetId == "${external.targetId.root}^${external.targetId.extension}"
    }
    
    def "pullIn method for CSR and FL should not throw an OpenCDSRuntimeException when the targetId != sourceId without extensions"() {
        given:
        def external = new ClinicalStatementRelationship(sourceId: new II(root: 'abcd'), targetId: new II(root: 'efgh'))
        def factLists = new FactLists()
        
        when:
        ClinicalStatementRelationshipMapper.pullIn(external, factLists)
        
        then:
        notThrown(OpenCDSRuntimeException)
        def data = factLists.get(org.opencds.vmr.v1_0.internal.ClinicalStatementRelationship.class)
        data != null
        data.size() == 1
        data[0].sourceId == external.sourceId.root
        data[0].targetId == external.targetId.root
    }
    
    def "pullIn method for CSR and FL should throw an OpenCDSRuntimeException when the targetId == sourceId"() {
        given:
        def external = new ClinicalStatementRelationship(sourceId: new II(root: 'abcd', extension: '1234'), targetId: new II(root: 'abcd', extension: '1234'))
        def factLists = new FactLists()
        
        when:
        ClinicalStatementRelationshipMapper.pullIn(external, factLists)
        
        then:
        thrown(OpenCDSRuntimeException)
    }
    
    def "pullIn method for CSR and FL should throw an OpenCDSRuntimeException when the targetId == sourceId with no extensions"() {
        given:
        def external = new ClinicalStatementRelationship(sourceId: new II(root: 'abcd'), targetId: new II(root: 'abcd'))
        def factLists = new FactLists()
        
        when:
        ClinicalStatementRelationshipMapper.pullIn(external, factLists)
        
        then:
        thrown(OpenCDSRuntimeException)
    }
    
    def "pullIn method for string,string,CD,FL should not throw an OpenCDSRuntimeException when the targetId != sourceId"() {
        given:
        def source = "abcd"
        def target = "efgh"
        def cd = new CD()
        def factLists = new FactLists()
        
        when:
        ClinicalStatementRelationshipMapper.pullIn(source, target, cd, factLists)
        
        then:
        notThrown(OpenCDSRuntimeException)
        def data = factLists.get(org.opencds.vmr.v1_0.internal.ClinicalStatementRelationship.class)
        data != null
        data.size() == 1
        def elem = data.get(0)
        data[0].sourceId == source
        data[0].targetId == target
    }
    
    def "pullIn method for string,string,CD,FL should throw an OpenCDSRuntimeException when the targetId == sourceId"() {
        given:
        def source = "abcd"
        def target = "abcd"
        def cd = new CD()
        def factLists = new FactLists()
        
        when:
        ClinicalStatementRelationshipMapper.pullIn(source, target, cd, factLists)
        
        then:
        thrown(OpenCDSRuntimeException)
    }
    
}
