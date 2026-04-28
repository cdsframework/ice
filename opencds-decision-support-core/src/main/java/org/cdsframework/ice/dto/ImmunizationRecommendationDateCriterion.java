package org.cdsframework.ice.dto;

import lombok.Builder;

@Builder
public record ImmunizationRecommendationDateCriterion(CodeableConcept code,
                                                      String value)
{
}
