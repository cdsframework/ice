package org.opencds.vmr.v1_0.mappings.utilities;

import org.opencds.vmr.v1_0.internal.datatypes.IVLDate
import org.opencds.vmr.v1_0.schema.IVLTS

import spock.lang.Specification
import spock.lang.Unroll

class MappingUtilitySpec extends Specification {

//    public void testIVLTS2IVLDateInternal() {
//        fail("Not yet implemented");
//    }
//
//    public void testIVLDateInternal2IVLTS() {
//        fail("Not yet implemented");
//    }

    @Unroll
    def "test various values for iVLTS2IVLDateInternal"() {
        expect:
        MappingUtility.iVLTS2IVLDateInternal(ivlts) == ivldate
        
        where:
        ivlts                                  | ivldate
        null                                   | null
        [:] as IVLTS                           | [low: null, high: null, lowIsInclusive: null, highIsInclusive: null] as IVLDate
        [low: "null"] as IVLTS                 | [low: null, high: null, lowIsInclusive: null, highIsInclusive: null] as IVLDate
        [high: "null"] as IVLTS                | [low: null, high: null, lowIsInclusive: null, highIsInclusive: null] as IVLDate
        [lowIsInclusive: false] as IVLTS       | [low: null, high: null, lowIsInclusive: false, highIsInclusive: null] as IVLDate
        [highIsInclusive: false] as IVLTS      | [low: null, high: null, lowIsInclusive: null, highIsInclusive: false] as IVLDate
        [lowIsInclusive: true] as IVLTS        | [low: null, high: null, lowIsInclusive: true, highIsInclusive: null] as IVLDate
        [highIsInclusive: true] as IVLTS       | [low: null, high: null, lowIsInclusive: null, highIsInclusive: true] as IVLDate
        // while exceptions are printed to STDOUT, the values are actually set to null
        [low: "NULL"] as IVLTS                 | [low: null, high: null, lowIsInclusive: null, highIsInclusive: null] as IVLDate
        [high: "NULL"] as IVLTS                | [low: null, high: null, lowIsInclusive: null, highIsInclusive: null] as IVLDate
        [high: "FIX THIS"] as IVLTS            | [low: null, high: null, lowIsInclusive: null, highIsInclusive: null] as IVLDate
        
    }
    
}
