package org.opencds.terminology.apelon;


import org.opencds.terminology.apelon.util.XmlRequestUtil

import spock.lang.Specification

class OpenCDSApelonDtsServiceFunctionalSpec extends Specification {
    private static final BASE_URL = "http://localhost:48080/opencds-apelon/apelonDtsService"
    
    def "find concept by name"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findConceptByName', name: 'Concept Determination Method'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)
       
        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.OpenCdsConcept.Name == 'Concept Determination Method'
        xml.OpenCdsConcept.OpenCDS_Concept_Type == 'conceptDeterminationMethod'
        xml.OpenCdsConcept.Code_in_Source == 'C35'
}
    
    def "find concept by name - unknown"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findConceptByName', name: 'Don\'t find me'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)
       
        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.text() == 'No results found!'
}

    def "find concept by name - no results found on null name"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findConceptByName', name: null])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)
       
        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.text() == 'No results found!'
}
    
    def "find concept by code"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findConceptByCode', code: 'C35'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)
       
        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.OpenCdsConcept.Name == 'Concept Determination Method'
        xml.OpenCdsConcept.OpenCDS_Concept_Type == 'conceptDeterminationMethod'
        xml.OpenCdsConcept.Code_in_Source == 'C35'
}

    def "find concept by code - unknown"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findConceptByCode', code: 'Don\'t find me'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)
       
        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.text() == 'No results found!'
}

    def "find concept by code - no results on null code"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findConceptByCode', code: null])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)
       
        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.text() == 'No results found!'
}
    
    def "findDirectChildrenOfConcept with parent"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findDirectChildrenOfConcept', parentConcept: 'Concept Determination Method', includeParent: 'true'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)

        then:
        response
        def xml = new XmlSlurper().parseText(response)
        def nameList = xml.OpenCdsConcept.Name.list().inject([]) {l, n -> l << n.text() }
        [
            'AHRQ v4.3',
            'AHRQ v4.4',
            'Best Available',
            'Concept Determination Method',
            'HEDIS 2011',
            'HEDIS 2012',
            'HEDIS 2014',
            'HEDIS 2015',
            'NHIQM 2013',
            'NQF',
            'OpenCDS',
            'Reportable Disease'
        ].each {name ->
                assert nameList.contains(name)
        }
        def cisList = xml.OpenCdsConcept.Code_in_Source.list().inject([]) {l, n -> l << n.text() }
        [
            'C267',
            'C464',
            'C265',
            'C35',
            'C263',
            'C264',
            'C2511',
            'C3391',
            'C494',
            'C45',
            'C36',
            'C74'
        ].each {c ->
                assert cisList.contains(c)
        }
    }
    
    def "findDirectChildrenOfConcept without (excluding) parent"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findDirectChildrenOfConcept', parentConcept: 'Concept Determination Method', includeParent: 'false'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)

        then:
        response
        def xml = new XmlSlurper().parseText(response)
        def nameList = xml.OpenCdsConcept.Name.list().inject([]) {l, n -> l << n.text() }
        [
            'AHRQ v4.3',
            'AHRQ v4.4',
            'Best Available',
            'HEDIS 2011',
            'HEDIS 2012',
            'HEDIS 2014',
            'HEDIS 2015',
            'NHIQM 2013',
            'NQF',
            'OpenCDS',
            'Reportable Disease'
        ].each {name ->
                assert nameList.contains(name)
        }
        def cisList = xml.OpenCdsConcept.Code_in_Source.list().inject([]) {l, n -> l << n.text() }
        [
            'C267',
            'C464',
            'C265',
            'C263',
            'C264',
            'C2511',
            'C3391',
            'C494',
            'C45',
            'C36',
            'C74'
        ].each {c ->
                assert cisList.contains(c)
        }
    }
    
    def "findDirectChildrenOfConcept with no children"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findDirectChildrenOfConcept', parentConcept: 'NQF', includeParent: 'false'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)

        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.text() == 'No results found!'
    }

    def "findDirectChildrenOfConcept with null parent"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findDirectChildrenOfConcept', parentConcept: null, includeParent: 'false'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)

        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.text() == 'No results found!'
    }

    def "findDescendantOfConcept including root max levels 1"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findDescendantsOfConcept', rootConcept: 'Concept Determination Method', maxLevelsDeep: '1', includeRoot: 'true'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)

        then:
        response
        def xml = new XmlSlurper().parseText(response)
        def nameList = xml.OpenCdsConcept.Name.list().inject([]) {l, n -> l << n.text() }
        [
            'AHRQ v4.3',
            'AHRQ v4.4',
            'Best Available',
            'Concept Determination Method',
            'HEDIS 2011',
            'HEDIS 2012',
            'HEDIS 2014',
            'HEDIS 2015',
            'NHIQM 2013',
            'NQF',
            'OpenCDS',
            'Reportable Disease'
        ].each {name ->
                assert nameList.contains(name)
        }
        def cisList = xml.OpenCdsConcept.Code_in_Source.list().inject([]) {l, n -> l << n.text() }
        [
            'C267',
            'C464',
            'C265',
            'C35',
            'C263',
            'C264',
            'C2511',
            'C3391',
            'C494',
            'C45',
            'C36',
            'C74'
        ].each {c ->
                assert cisList.contains(c)
        }
    }
    
    def "findDescendantOfConcept excluding root max levels 1"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findDescendantsOfConcept', rootConcept: 'Concept Determination Method', maxLevelsDeep: '1', includeRoot: 'false'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)

        then:
        response
        def xml = new XmlSlurper().parseText(response)
        def nameList = xml.OpenCdsConcept.Name.list().inject([]) {l, n -> l << n.text() }
        [
            'AHRQ v4.3',
            'AHRQ v4.4',
            'Best Available',
            'HEDIS 2011',
            'HEDIS 2012',
            'HEDIS 2014',
            'HEDIS 2015',
            'NHIQM 2013',
            'NQF',
            'OpenCDS',
            'Reportable Disease'
        ].each {name ->
                assert nameList.contains(name)
        }
        def cisList = xml.OpenCdsConcept.Code_in_Source.list().inject([]) {l, n -> l << n.text() }
        [
            'C267',
            'C464',
            'C265',
            'C263',
            'C264',
            'C2511',
            'C3391',
            'C494',
            'C45',
            'C36',
            'C74'
        ].each {c ->
                assert cisList.contains(c)
        }
    }
    
    def "findDescendantOfConcept including root max levels 2"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findDescendantsOfConcept', rootConcept: 'OpenCDS Templates (node)', maxLevelsDeep: '2', includeRoot: 'true'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)

        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.OpenCdsConcept.Name.list().size() > 0
        xml.OpenCdsConcept.Code_in_Source.list().size() > 0
    }
    
    def "findDescendantOfConcept excluding root max levels 2"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findDescendantsOfConcept', rootConcept: 'OpenCDS Templates (node)', maxLevelsDeep: '2', includeRoot: 'false'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)

        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.OpenCdsConcept.Name.list().size() > 0
        xml.OpenCdsConcept.Code_in_Source.list().size() > 0
    }

    def "findDescendantOfConcept invalid depth == 0"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findDescendantsOfConcept', rootConcept: 'OpenCDS Templates (node)', maxLevelsDeep: '0', includeRoot: 'false'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)

        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.Error == '0 is invalid for maxLevelsDeep (1-50)'
    }

    def "findDescendantOfConcept invalid depth == 51"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findDescendantsOfConcept', rootConcept: 'OpenCDS Templates (node)', maxLevelsDeep: '51', includeRoot: 'false'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)

        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.Error == '51 is invalid for maxLevelsDeep (1-50)'
    }

    def "findDescendantOfConcept null root concept"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findDescendantsOfConcept', rootConcept: null, maxLevelsDeep: '1', includeRoot: 'false'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)

        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.text() == 'No results found!'
    }

    def "find by property"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findConceptsWithPropertyMatching', propertyTypeName: 'Description', propertyValuePattern: '*'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)

        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.OpenCdsConcept.Name.list().size() > 0
        xml.OpenCdsConcept.Code_in_Source.list().size() > 0
    }

    def "find by property - unknown"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findConceptsWithPropertyMatching', propertyTypeName: 'Description', propertyValuePattern: 'Don\'t find me'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)

        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.text() == 'No results found!'
    }

    def "find by property - unknown property name--error returned"() {
        when:
        def request = XmlRequestUtil.createRequest([queryType: 'findConceptsWithPropertyMatching', propertyTypeName: 'Don\'t find me', propertyValuePattern: '*'])
        def response = XmlRequestUtil.sendRequestGetResponse(BASE_URL, request)

        then:
        response
        def xml = new XmlSlurper().parseText(response)
        xml.Error.text() == '\'Don\'t find me\' could not be found as a property type'
    }

}
