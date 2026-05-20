package org.cdsframework.fhir;

import lombok.Builder;

@Builder
public record Narrative(String status,
                        String div)
{
}
