package org.cdsframework.fhir;

import lombok.Builder;

@Builder
public record RelatedArtifact(String type,
                              String label,
                              String display,
                              String url,
                              Attachment document)
{
}
