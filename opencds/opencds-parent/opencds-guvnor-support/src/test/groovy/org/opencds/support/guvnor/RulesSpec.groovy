package org.opencds.support.guvnor

import org.drools.builder.KnowledgeBuilderErrors
import org.drools.builder.KnowledgeBuilderFactory
import org.drools.builder.ResourceType
import org.drools.io.ResourceFactory

import spock.lang.Specification

class RulesSpec extends Specification {

    def "build a knowledge package and test it"() {
        given:
        def builder = KnowledgeBuilderFactory.newKnowledgeBuilder()
        builder.add(ResourceFactory.newFileResource("src/main/resources/hello.function"), ResourceType.DRL)
        builder.add(ResourceFactory.newFileResource("src/main/resources/test.drl"), ResourceType.DRL)

        when:
        def x = 1
        if (builder.hasErrors()) {
            builder.errors.each { System.err.println it }
        }

        then:
        !builder.hasErrors()
    }
    
    def "build a knowledge package with a drl that references an unknown function and test it"() {
        given:
        def builder = KnowledgeBuilderFactory.newKnowledgeBuilder()
        builder.add(ResourceFactory.newFileResource("src/main/resources/hello.function"), ResourceType.DRL)
        builder.add(ResourceFactory.newFileResource("src/main/resources/test-bad.drl"), ResourceType.DRL)

        when:
        def x = 1
        if (builder.hasErrors()) {
            builder.errors.each { System.err.println it }
        }

        then:
        builder.hasErrors()
    }

    def "build a knowledge package with a drl that references a bad function and test it"() {
        given:
        def builder = KnowledgeBuilderFactory.newKnowledgeBuilder()
        builder.add(ResourceFactory.newFileResource("src/main/resources/hello-bad.function"), ResourceType.DRL)
        builder.add(ResourceFactory.newFileResource("src/main/resources/test.drl"), ResourceType.DRL)

        when:
        def x = 1
        if (builder.hasErrors()) {
            builder.errors.each { System.err.println it }
        }

        then:
        builder.hasErrors()
    }

}
