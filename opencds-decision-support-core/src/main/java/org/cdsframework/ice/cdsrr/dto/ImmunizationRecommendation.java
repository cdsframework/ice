package org.cdsframework.ice.cdsrr.dto;

import java.time.LocalDate;
import java.util.List;

import org.cdsframework.fhir.CodeableConcept;

import lombok.Builder;
import lombok.Singular;

@Builder
public record ImmunizationRecommendation(LocalDate recommendationDate,
                                         LocalDate earliestDate,
                                         LocalDate latestDate,
                                         LocalDate overdueDate,
                                         @Singular
                                         List<CodeableConcept> targetDiseases,
                                         @Singular
                                         List<CodeableConcept> vaccineCodes,
                                         CodeableConcept forecastStatus,
                                         @Singular
                                         List<CodeableConcept> forecastStatusReasons,
                                         Integer doseNumber)
{
}