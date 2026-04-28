package org.cdsframework.ice.dto;

import lombok.Builder;

@Builder
public record ParametersParameter(String name,
                                  String valueDate,
                                  String valueDateTime,
                                  String valueCanonical,
                                  String valueCode,
                                  String valueString,
                                  Integer valueInteger,
                                  Boolean valueBoolean,
                                  FhirResource resource)
{
}
