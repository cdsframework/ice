package org.cdsframework.fhir;

import java.util.List;

import lombok.Builder;
import lombok.Singular;

@Builder
public record PlanDefinition(String id,
                             String resourceType,
                             Meta meta,
                             String url,
                             @Singular("identifier")
                             List<Identifier> identifier,
                             String version,
                             String name,
                             String title,
                             CodeableConcept type,
                             String status,
                             @Singular("action")
                             List<Action> action,
                             @Singular("extension")
                             List<Extension> extension) implements FhirResource
{
    @Builder
    public record Action(String id,
                         String title,
                         @Singular("relatedAction")
                         List<RelatedAction> relatedAction,
                         @Singular("extension")
                         List<Extension> extension)
    {
    }

    @Builder
    public record RelatedAction(String actionId,
                                String targetId,
                                String relationship,
                                @Singular("extension")
                                List<Extension> extension)
    {
    }

    public static final String RESOURCE_TYPE = "PlanDefinition";

    public PlanDefinition
    {
        resourceType = FhirResource.normalizeResourceType(resourceType, RESOURCE_TYPE);
    }
}
