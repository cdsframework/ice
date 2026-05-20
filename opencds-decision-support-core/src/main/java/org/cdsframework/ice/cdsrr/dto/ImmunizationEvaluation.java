package org.cdsframework.ice.cdsrr.dto;

import java.util.List;

import org.cdsframework.fhir.CodeableConcept;

import lombok.Builder;
import lombok.Singular;

@Builder
public record ImmunizationEvaluation(String id,
                                     @Singular
                                     List<CodeableConcept> targetDiseases,
                                     CodeableConcept vaccineCode,
                                     CodeableConcept doseStatus,
                                     @Singular
                                     List<CodeableConcept> doseStatusReasons,
                                     Integer doseNumber)
{
}