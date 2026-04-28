package org.cdsframework.ice.dto;

import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Singular;

@Builder
public record ImmunizationRecommendationRecommendation(List<CodeableConcept> vaccineCode,
                                                       List<CodeableConcept> targetDisease,
                                                       List<CodeableConcept> contraindicatedVaccineCode,
                                                       CodeableConcept forecastStatus,
                                                       List<CodeableConcept> forecastReason,
                                                       List<ImmunizationRecommendationDateCriterion> dateCriterion,
                                                       String description,
                                                       String series,
                                                       CodeableConcept doseNumber,
                                                       CodeableConcept seriesDoses,
                                                       @Singular("extension")
                                                       List<Extension> extension,
                                                       List<Reference> supportingImmunization,
                                                       List<Reference> supportingPatientInformation)
{
    public ImmunizationRecommendationRecommendation
    {
        vaccineCode = Objects.requireNonNullElseGet(vaccineCode, List::of);
        targetDisease = Objects.requireNonNullElseGet(targetDisease, List::of);
        contraindicatedVaccineCode = Objects.requireNonNullElseGet(contraindicatedVaccineCode, List::of);
        forecastReason = Objects.requireNonNullElseGet(forecastReason, List::of);
        dateCriterion = Objects.requireNonNullElseGet(dateCriterion, List::of);
        extension = Objects.requireNonNullElseGet(extension, List::of);
        supportingImmunization = Objects.requireNonNullElseGet(supportingImmunization, List::of);
        supportingPatientInformation = Objects.requireNonNullElseGet(supportingPatientInformation, List::of);
    }
}
