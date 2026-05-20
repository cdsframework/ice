package org.cdsframework.fhir;

import lombok.Builder;

@Builder
public record ImmunizationRecommendationDateCriterion(CodeableConcept code,
                                                      String value)
{
}
