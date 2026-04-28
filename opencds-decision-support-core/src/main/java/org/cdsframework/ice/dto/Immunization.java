package org.cdsframework.ice.dto;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Singular;

@Builder
public record Immunization(String resourceType,
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

    public Immunization
    {
        resourceType = resourceType == null ? "Immunization" : resourceType;
        status = status == null ? Status.COMPLETED : status;
        identifier = Objects.requireNonNullElseGet(identifier, List::of);
    }
}
