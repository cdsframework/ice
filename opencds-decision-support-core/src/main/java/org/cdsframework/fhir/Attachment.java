package org.cdsframework.fhir;

import lombok.Builder;

@Builder
public record Attachment(String contentType,
                         String url)
{
}
