package org.cdsframework.ice.dto;

import lombok.Builder;

@Builder
public record CodeSystemResourceProperty(String code,
                                         String type)
{
}
