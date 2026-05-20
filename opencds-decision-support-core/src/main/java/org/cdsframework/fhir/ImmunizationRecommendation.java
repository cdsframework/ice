package org.cdsframework.fhir;

import java.time.LocalDate;
import java.util.List;

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
    public static final String RESOURCE_TYPE = "ImmunizationRecommendation";

    public ImmunizationRecommendation
    {
        resourceType = FhirResource.normalizeResourceType(resourceType, RESOURCE_TYPE);
    }
}
