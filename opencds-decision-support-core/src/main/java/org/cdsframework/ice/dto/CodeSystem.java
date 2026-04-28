package org.cdsframework.ice.dto;

import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Singular;

@Builder
public record CodeSystem(String name,
                         @Singular("identifier")
                         List<Identifier> identifier,
                         String description,
                         String url,
                         String title,
                         String version,
                         PublicationStatusEnum status,
                         CodeSystemContentModeEnum content,
                         @Singular("property")
                         List<CodeSystemResourceProperty> property,
                         @Singular("concept")
                         List<CodeSystemConcept> concept)
{
    public CodeSystem
    {
        identifier = Objects.requireNonNullElseGet(identifier, List::of);
        property = Objects.requireNonNullElseGet(property, List::of);
        concept = Objects.requireNonNullElseGet(concept, List::of);
    }
}
