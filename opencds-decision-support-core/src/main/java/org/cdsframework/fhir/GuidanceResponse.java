package org.cdsframework.fhir;

import java.util.List;

import lombok.Builder;
import lombok.Singular;

@Builder
public record GuidanceResponse(String id,
                               String resourceType,
                               Narrative text,
                               String status,
                               Reference subject,
                               String occurrenceDateTime,
                               String moduleCanonical,
                               @Singular("note")
                               List<Annotation> note) implements FhirResource
{
    public static final String RESOURCE_TYPE = "GuidanceResponse";

    public GuidanceResponse
    {
        resourceType = FhirResource.normalizeResourceType(resourceType, RESOURCE_TYPE);
    }
}
