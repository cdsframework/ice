package org.cdsframework.ice.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Singular;

@Builder
public record ImmunizationRecommendation(String resourceType,
                                         String id,
                                         @Singular("identifier")
                                         List<Identifier> identifier,
                                         Narrative text,
                                         Reference patient,
                                         LocalDate date,
                                         Reference authority,
                                         @Singular("recommendation")
                                         List<ImmunizationRecommendationRecommendation> recommendation) implements FhirResource
{
    public ImmunizationRecommendation
    {
        resourceType = resourceType == null ? "ImmunizationRecommendation" : resourceType;
        identifier = Objects.requireNonNullElseGet(identifier, List::of);
        recommendation = Objects.requireNonNullElseGet(recommendation, List::of);
    }
}
