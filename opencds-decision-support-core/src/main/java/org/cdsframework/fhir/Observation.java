package org.cdsframework.fhir;

import java.util.List;

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
    public static final String RESOURCE_TYPE = "Observation";

    public Observation
    {
        resourceType = FhirResource.normalizeResourceType(resourceType, RESOURCE_TYPE);
    }
}
