package org.cdsframework.ice.dto;

import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Singular;

@Builder
public record ImmunizationEvaluation(String resourceType,
                                     Narrative text,
                                     String id,
                                     @Singular("identifier")
                                     List<Identifier> identifier,
                                     String status,
                                     Reference patient,
                                     String date,
                                     Reference authority,
                                     CodeableConcept targetDisease,
                                     Reference immunizationEvent,
                                     CodeableConcept doseStatus,
                                     @Singular("doseStatusReason")
                                     List<CodeableConcept> doseStatusReason,
                                     @Singular("extension")
                                     List<Extension> extension,
                                     String description,
                                     String series,
                                     CodeableConcept doseNumber,
                                     CodeableConcept seriesDoses) implements FhirResource
{
    public ImmunizationEvaluation
    {
        resourceType = resourceType == null ? "ImmunizationEvaluation" : resourceType;
        identifier = Objects.requireNonNullElseGet(identifier, List::of);
        doseStatusReason = Objects.requireNonNullElseGet(doseStatusReason, List::of);
        extension = Objects.requireNonNullElseGet(extension, List::of);
    }
}
