package org.cdsframework.ice.dto;

import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Singular;

@Builder
public record GuidanceResponse(String resourceType,
                               Narrative text,
                               String status,
                               Reference subject,
                               String occurrenceDateTime,
                               String moduleCanonical,
                               @Singular("note")
                               List<Annotation> note) implements FhirResource
{
    public GuidanceResponse
    {
        resourceType = resourceType == null ? "GuidanceResponse" : resourceType;
        note = Objects.requireNonNullElseGet(note, List::of);
    }
}
