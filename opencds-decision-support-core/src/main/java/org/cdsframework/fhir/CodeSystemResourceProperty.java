package org.cdsframework.fhir;

import lombok.Builder;

@Builder
public record CodeSystemResourceProperty(String code,
                                         String type)
{
}
