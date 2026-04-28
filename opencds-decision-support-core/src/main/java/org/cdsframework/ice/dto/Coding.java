package org.cdsframework.ice.dto;

import lombok.Builder;

@Builder
public record Coding(String system,
                     String version,
                     String code,
                     String display)
{
}
