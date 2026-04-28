package org.cdsframework.ice.dto;

import lombok.Builder;

@Builder
public record Reference(String reference,
                        String type,
                        Identifier identifier,
                        String display)
{
}
