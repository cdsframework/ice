package org.opencds.config.util;

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
