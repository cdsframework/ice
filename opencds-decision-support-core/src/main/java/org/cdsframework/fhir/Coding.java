package org.cdsframework.fhir;

import lombok.Builder;

@Builder
public record Coding(String system,
                     String version,
                     String code,
                     String display)
{
}
