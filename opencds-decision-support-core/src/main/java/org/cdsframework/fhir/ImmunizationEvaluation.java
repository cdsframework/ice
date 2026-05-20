package org.cdsframework.fhir;

import java.util.List;

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
    public static final String RESOURCE_TYPE = "ImmunizationEvaluation";

    public ImmunizationEvaluation
    {
        resourceType = FhirResource.normalizeResourceType(resourceType, RESOURCE_TYPE);
    }
}
