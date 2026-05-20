package org.cdsframework.fhir;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Singular;

@Builder
public record Immunization(String id,
                           String resourceType,
                           Narrative text,
                           Status status,
                           @Singular("identifier")
                           List<Identifier> identifier,
                           CodeableConcept vaccineCode,
                           Reference patient,
                           String occurrenceDateTime) implements FhirResource
{
    public enum Status
    {
        @JsonProperty("completed")
        COMPLETED,
        @JsonProperty("entered-in-error")
        ENTERED_IN_ERROR,
        @JsonProperty("not-done")
        NOT_DONE
    }

    public static final String RESOURCE_TYPE = "Immunization";

    public Immunization
    {
        resourceType = FhirResource.normalizeResourceType(resourceType, RESOURCE_TYPE);
        status = Objects.requireNonNullElse(status, Status.COMPLETED);
    }
}
