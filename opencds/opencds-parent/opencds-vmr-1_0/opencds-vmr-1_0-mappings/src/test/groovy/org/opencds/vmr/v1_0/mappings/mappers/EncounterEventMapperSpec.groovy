package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.InvalidDataException
import org.opencds.vmr.v1_0.internal.EncounterEvent
import org.opencds.vmr.v1_0.mappings.in.FactLists
import org.opencds.vmr.v1_0.schema.II
import org.opencds.vmr.v1_0.schema.IVLTS

import spock.lang.Specification

class EncounterEventMapperSpec extends Specification {

    def "test pullIn with null source"() {
        given:
        def source = null
        def target = [:] as EncounterEvent
        def subjectPersonId = '1234'
        def focalPersonId = '1234'
        def factLists = new FactLists()
        
        when:
        EncounterEventMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists)
        
        then:
        target.encounterEventTime == null
    }
    
    def "test pullIn with source containing null values"() {
        given:
        def source = [:] as org.opencds.vmr.v1_0.schema.EncounterEvent
        def target = [:] as EncounterEvent
        def subjectPersonId = '1234'
        def focalPersonId = '1234'
        def factLists = new FactLists()
        
        when:
        EncounterEventMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists)
        
        then:
        target.encounterEventTime == null
        thrown(RuntimeException) // because source.id is null
    }
    
    def "test pullIn with source containing null values except id"() {
        given:
        def source = [id: [root: 'ii1234'] as II] as org.opencds.vmr.v1_0.schema.EncounterEvent
        def target = [:] as EncounterEvent
        def subjectPersonId = '1234'
        def focalPersonId = '1234'
        def factLists = new FactLists()
        
        when:
        EncounterEventMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists)
        
        then:
        target.encounterEventTime == null
        thrown(InvalidDataException)
    }
    
    def "test pullIn with encountereventtime with null values"() {
        given:
        def source = [
            id: [root: 'ii1234'] as II,
            encounterEventTime: [:] as IVLTS
        ] as org.opencds.vmr.v1_0.schema.EncounterEvent
        def target = [:] as EncounterEvent
        def subjectPersonId = '1234'
        def focalPersonId = '1234'
        def factLists = new FactLists()

        when:
        EncounterEventMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists)

        then:"is this even valid? The schema requires encounterEventTime, and requires a date value. low and high must not be null"
        thrown(InvalidDataException)
    }

    def "test pullIn with encountereventtime with non-null low and high values"() {
        given:
        def source = [
            id: [root: 'ii1234'] as II,
            encounterEventTime: [low: '20140117', high: '20140117'] as IVLTS
        ] as org.opencds.vmr.v1_0.schema.EncounterEvent
        def target = [:] as EncounterEvent
        def subjectPersonId = '1234'
        def focalPersonId = '1234'
        def factLists = new FactLists()

        when:
        EncounterEventMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists)

        then:"neither low nor high are null"
        target.encounterEventTime != null
        target.encounterEventTime.lowIsInclusive == null
        target.encounterEventTime.highIsInclusive == null
        target.encounterEventTime.low != null
        target.encounterEventTime.high != null
    }

    def "test pullIn with encountereventtime with null low value"() {
        given:
        def source = [
            id: [root: 'ii1234'] as II,
            encounterEventTime: [high: '20140117'] as IVLTS
        ] as org.opencds.vmr.v1_0.schema.EncounterEvent
        def target = [:] as EncounterEvent
        def subjectPersonId = '1234'
        def focalPersonId = '1234'
        def factLists = new FactLists()

        when:
        EncounterEventMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists)

        then:"low is null, high is non-null"
        target.encounterEventTime != null
        target.encounterEventTime.lowIsInclusive == null
        target.encounterEventTime.highIsInclusive == null
        target.encounterEventTime.low == null
        target.encounterEventTime.high != null
    }

    def "test pullIn with encountereventtime with null high value"() {
        given:
        def source = [
            id: [root: 'ii1234'] as II,
            encounterEventTime: [low: '20140117'] as IVLTS
        ] as org.opencds.vmr.v1_0.schema.EncounterEvent
        def target = [:] as EncounterEvent
        def subjectPersonId = '1234'
        def focalPersonId = '1234'
        def factLists = new FactLists()

        when:
        EncounterEventMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists)

        then:"low is non-null, high is null"
        target.encounterEventTime != null
        target.encounterEventTime.lowIsInclusive == null
        target.encounterEventTime.highIsInclusive == null
        target.encounterEventTime.low != null
        target.encounterEventTime.high == null
    }


    
}
