package org.opencds.service.util

import groovy.util.slurpersupport.GPathResult

class VMRUtil {
    private static final String DEFAULT_DELIMITER = ','

    static Map getResults(GPathResult vmr) {
        getResults(vmr, DEFAULT_DELIMITER);
    }

    static Map getResults(GPathResult vmr, String delimiter) {
        def results = [:]
        results.assertions = getAssertions(vmr, delimiter)
        results.concepts = getConcepts(vmr, delimiter)
        results.debugObjects = getDebugObjects(vmr, delimiter)
		results.identifiers = getIdentifiers(vmr)
        results.measure = getMeasure(vmr)
        results.measures = getMeasures(vmr)
		results.measuresList = getMeasuresList(vmr)
		results.measureFocus = getMeasureFocus(vmr)
        results
    }

    static Map getAssertions(GPathResult vmr) {
        getAssertions(vmr, DEFAULT_DELIMITER)
    }

    static Map getAssertions(GPathResult vmr, String delimiter) {
        def assertions = [:]
        vmr.vmrOutput.patient.clinicalStatements.observationResults.observationResult.list().each {or ->
            if (or.observationFocus.@code.text() == 'assertions') {
                def vmrAssertions = or.observationValue.text.@value.text()
                vmrAssertions.split(delimiter).each {vmrAssertion ->
                    def vaa = vmrAssertion.split("=")
                    if (vaa.size() > 1) {
                        assertions.put(vaa[0], vaa[1])
                    } else {
                        assertions.put(vaa[0], '')
                    }
                }
            }
        }
        return assertions
    }

    static Map getConcepts(GPathResult vmr) {
        getConcepts(vmr, DEFAULT_DELIMITER)
    }

    static Map getConcepts(GPathResult vmr, String delimiter) {
        def concepts = [:]
        vmr.vmrOutput.patient.clinicalStatements.observationResults.observationResult.list().each {or ->
            if (or.observationFocus.@code.text() == "concepts") {
                def obsvalAll = or.observationValue.text.@value.text()
                def obsvals = obsvalAll.split(delimiter)
                obsvals.each {obsval ->
                    def ov = obsval.split("=")
                    if (ov.size() > 1) {
                        concepts.put(ov[0], ov[1])
                    } else {
                        concepts.put(ov[0], '')
                    }
                }
            }
        }
        return concepts
    }

    static Map getDebugObjects(GPathResult vmr) {
        getDebugObjects(vmr, DEFAULT_DELIMITER)
    }

    static Map getDebugObjects(GPathResult vmr, String delimiter) {
        def debugObjects = [:]
        vmr.vmrOutput.patient.clinicalStatements.observationResults.observationResult.list().each {or ->
            if (or.observationFocus.@code.text() == "debugObjects") {
                def obsvalAll = or.observationValue.text.@value.text()
                def obsvals = obsvalAll.split(delimiter)
                obsvals.each {obsval ->
                    def ov = obsval.split("=")
                    if (ov.size() > 1) {
                        debugObjects.put(ov[0], ov[1])
                    } else {
                        debugObjects.put(ov[0], '')
                    }
                }
            }
        }
        return debugObjects
    }

    static Map getMeasure(GPathResult data) {
        def nd = [:]
        data.vmrOutput.patient.clinicalStatements.observationResults.observationResult.relatedClinicalStatement.observationResult.list().each {or ->
            if (or.id.@root.text() == '2.16.840.1.113883.3.795.5.1' || or.id.@root.text() == '2.16.840.1.113883.3.795.5.1.1') {
                if (or.id.@extension.text() == 'numerator') {
                    nd.numerator = or.observationValue.integer.@value.text()
                }
                if (or.id.@extension.text() == 'denominator') {
                    nd.denominator = or.observationValue.integer.@value.text()
                }
            }
        }
        return nd
    }

    static Map getMeasures(GPathResult data) {
        def measures = [:]
        data.vmrOutput.patient.clinicalStatements.observationResults.observationResult.list().each {or ->
            if ((or.observationFocus.@codeSystem.text() == "2.16.840.1.113883.3.795.12.1.1") &&
				!(or.observationFocus.@code.text() ==~ /(assertions|concepts|debugObjects)/)) {
//				def id = or.observationValue.identifier.@root.text()
                def code = or.observationFocus.@code.text()
                def numerator = -1
                def denominator = -1
                or.relatedClinicalStatement.list().each {rs ->
                    if (rs.observationResult.observationFocus.@code.text() == "C466") {
                        numerator = Integer.valueOf(rs.observationResult.observationValue.integer.@value.text())
                    }
                    if (rs.observationResult.observationFocus.@code.text() == "C467") {
                        denominator = Integer.valueOf(rs.observationResult.observationValue.integer.@value.text())
                    }
                }
                measures.put(code, [num: numerator, denom: denominator])
//                measures.put(id, [meas: code, num: numerator, denom: denominator])
            }
        }
        measures
    }

    static Map getMeasuresList(GPathResult data) {
        def measuresList = [:]
        data.vmrOutput.patient.clinicalStatements.observationResults.observationResult.list().each {or ->
            if ((or.observationFocus.@codeSystem.text() == "2.16.840.1.113883.3.795.12.1.1") &&
				!(or.observationFocus.@code.text() ==~ /(assertions|concepts|debugObjects)/)) {
				def id = or.observationValue.identifier.@root.text()
                def code = or.observationFocus.@code.text()
                def numerator = -1
                def denominator = -1
                or.relatedClinicalStatement.list().each {rs ->
                    if (rs.observationResult.observationFocus.@code.text() == "C466") {
                        numerator = Integer.valueOf(rs.observationResult.observationValue.integer.@value.text())
                    }
                    if (rs.observationResult.observationFocus.@code.text() == "C467") {
                        denominator = Integer.valueOf(rs.observationResult.observationValue.integer.@value.text())
                    }
                }
                measuresList.put(id, [meas: code, num: numerator, denom: denominator])
            }
        }
        measuresList
    }

    static List getIdentifiers(GPathResult data) {
        def identifiers = []
        data.vmrOutput.patient.clinicalStatements.observationResults.observationResult.list().each {or ->
            if ((or.observationFocus.@codeSystem.text() == "2.16.840.1.113883.3.795.12.1.1") &&
				!(or.observationFocus.@code.text() ==~ /(assertions|concepts|debugObjects)/)) {
				if (or.observationValue.identifier != null){
					def id = or.observationValue.identifier.@root.text()
					identifiers.add(id)
				}
            }
        }
        identifiers
    }

    static List getMeasureFocus(GPathResult data) {
        def measureFocus = []
        data.vmrOutput.patient.clinicalStatements.observationResults.observationResult.list().each {or ->
            if ((or.observationFocus.@codeSystem.text() == "2.16.840.1.113883.3.795.12.1.1") &&
				!(or.observationFocus.@code.text() ==~ /(assertions|concepts|debugObjects)/)) {
                def code = or.observationFocus.@code.text()
                measureFocus.add(code)
            }
        }
        measureFocus
    }
}
