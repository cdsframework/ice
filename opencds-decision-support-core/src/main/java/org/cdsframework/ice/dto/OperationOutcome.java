package org.cdsframework.ice.dto;

import java.util.List;
import java.util.Objects;

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
        public Issue
        {
            extension = Objects.requireNonNullElseGet(extension, List::of);
            modifierExtension = Objects.requireNonNullElseGet(modifierExtension, List::of);
            location = Objects.requireNonNullElseGet(location, List::of);
            expression = Objects.requireNonNullElseGet(expression, List::of);
        }
    }

    public OperationOutcome
    {
        resourceType = resourceType == null ? "OperationOutcome" : resourceType;
        contained = Objects.requireNonNullElseGet(contained, List::of);
        extension = Objects.requireNonNullElseGet(extension, List::of);
        modifierExtension = Objects.requireNonNullElseGet(modifierExtension, List::of);
        issue = Objects.requireNonNullElseGet(issue, List::of);
    }
}
