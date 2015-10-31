package org.opencds.terminology.apelon.cache;

import spock.lang.Specification;

public class CacheKeySpec extends Specification {
    
    def "test with two keys, one value each"() {
        when:
        def ck1 = CacheKey.create([someValue: "some value"])
        def ck2 = CacheKey.create([someValue: "some value"])
        
        then:
        ck1 == ck2
        ck1.equals(ck2)
        ck1.hashCode() == ck2.hashCode()
    }
    
    def "test with two keys, two equal values each"() {
        when:
        def ck1 = CacheKey.create([someValue: "some value", secondValue: "second value"])
        def ck2 = CacheKey.create([someValue: "some value", secondValue: "second value"])
        
        then:
        ck1 == ck2
        ck1.equals(ck2)
        ck1.hashCode() == ck2.hashCode()
    }
    
    def "test with two keys, two values each, swapped"() {
        when:
        def ck1 = CacheKey.create([secondValue: "second value", someValue: "some value"])
        def ck2 = CacheKey.create([someValue: "some value", secondValue: "second value"])
        
        then:
        ck1 != ck2
        !ck1.equals(ck2)
        ck1.hashCode() != ck2.hashCode()
    }

    def "test with two keys, hax0ored"() {
        when:
        def ck1 = CacheKey.create([one: "one value| and", two: "another value"])
        def ck2 = CacheKey.create([one: "one value", two: " and|another value"])

        then:
        ck1 != ck2
        !ck1.equals(ck2)
        ck1.hashCode() != ck2.hashCode()
    }
    
}
