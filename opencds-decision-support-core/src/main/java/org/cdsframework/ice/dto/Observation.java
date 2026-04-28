package org.cdsframework.ice.dto;

import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Singular;

@Builder
public record Observation(String resourceType,
                          Narrative text,
                          String id,
                          @Singular("identifier")
                          List<Identifier> identifier,
                          String status,
                          CodeableConcept code,
                          Reference subject,
                          @Singular("performer")
                          List<Reference> performer,
                          String effectiveDateTime,
                          String issued,
                          CodeableConcept valueCodeableConcept,
                          @Singular("component")
                          List<ObservationComponent> component,
                          @Singular("interpretation")
                          List<CodeableConcept> interpretation) implements FhirResource
{
    public Observation
    {
        resourceType = resourceType == null ? "Observation" : resourceType;
        identifier = Objects.requireNonNullElseGet(identifier, List::of);
        performer = Objects.requireNonNullElseGet(performer, List::of);
        component = Objects.requireNonNullElseGet(component, List::of);
        interpretation = Objects.requireNonNullElseGet(interpretation, List::of);
    }
}
