package org.cdsframework.fhir;

import java.util.List;

import lombok.Builder;
import lombok.Singular;

@Builder
public record ImmunizationRecommendationRecommendation(@Singular("vaccineCode")
                                                       List<CodeableConcept> vaccineCode,
                                                       @Singular("targetDisease")
                                                       List<CodeableConcept> targetDisease,
                                                       @Singular("contraindicatedVaccineCode")
                                                       List<CodeableConcept> contraindicatedVaccineCode,
                                                       CodeableConcept forecastStatus,
                                                       @Singular("forecastReason")
                                                       List<CodeableConcept> forecastReason,
                                                       @Singular("dateCriterion")
                                                       List<ImmunizationRecommendationDateCriterion> dateCriterion,
                                                       String description,
                                                       String series,
                                                       CodeableConcept doseNumber,
                                                       CodeableConcept seriesDoses,
                                                       @Singular("extension")
                                                       List<Extension> extension,
                                                       @Singular("supportingImmunization")
                                                       List<Reference> supportingImmunization,
                                                       @Singular("supportingPatientInformation")
                                                       List<Reference> supportingPatientInformation)
{
}
