package org.cdsframework.fhir;

import java.util.List;

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
}
