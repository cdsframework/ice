package org.cdsframework.fhir;

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
                                     @Singular("authority")
                                     List<Reference> authority,
                                     @Singular("targetDisease")
                                     List<CodeableConcept> targetDisease,
                                     Reference immunizationEvent,
                                     CodeableConcept doseStatus,
                                     @Singular("doseStatusReason")
                                     List<CodeableConcept> doseStatusReason,
                                     @Singular("extension")
                                     List<Extension> extension,
                                     String description,
                                     CodeableConcept series,
                                     CodeableConcept season,
                                     CodeableConcept doseNumber,
                                     CodeableConcept seriesDoses) implements FhirResource
{
    public static final String RESOURCE_TYPE = "ImmunizationEvaluation";

    public ImmunizationEvaluation
    {
        resourceType = FhirResource.normalizeResourceType(resourceType, RESOURCE_TYPE);
        targetDisease = List.copyOf(Objects.requireNonNull(targetDisease, "targetDisease is required"));
        if (targetDisease.isEmpty())
            throw new IllegalArgumentException("targetDisease must contain at least one disease");
    }
}
