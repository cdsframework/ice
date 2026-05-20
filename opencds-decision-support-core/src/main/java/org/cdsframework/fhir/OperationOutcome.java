package org.cdsframework.fhir;

import java.util.List;

import lombok.Builder;
import lombok.Singular;

@Builder
public record OperationOutcome(String resourceType,
                               String id,
                               Meta meta,
                               String implicitRules,
                               String language,
                               Narrative text,
                               @Singular("contained")
                               List<FhirResource> contained,
                               @Singular("extension")
                               List<Extension> extension,
                               @Singular("modifierExtension")
                               List<Extension> modifierExtension,
                               @Singular("issue")
                               List<Issue> issue) implements FhirResource
{
    @Builder
    public record Issue(String id,
                        @Singular("extension")
                        List<Extension> extension,
                        @Singular("modifierExtension")
                        List<Extension> modifierExtension,
                        String severity,
                        String code,
                        CodeableConcept details,
                        String diagnostics,
                        @Singular("location")
                        List<String> location,
                        @Singular("expression")
                        List<String> expression)
    {
    }

    public static final String RESOURCE_TYPE = "OperationOutcome";

    public OperationOutcome
    {
        resourceType = FhirResource.normalizeResourceType(resourceType, RESOURCE_TYPE);
    }
}
