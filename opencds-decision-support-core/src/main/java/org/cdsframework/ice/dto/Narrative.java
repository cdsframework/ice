package org.cdsframework.ice.dto;

import lombok.Builder;

@Builder
public record Narrative(String status,
                        String div)
{
}
