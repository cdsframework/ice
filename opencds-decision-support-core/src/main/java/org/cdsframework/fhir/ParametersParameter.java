package org.cdsframework.fhir;

import java.util.List;

import lombok.Builder;
import lombok.Singular;

@Builder
public record ParametersParameter(String name,
                                  String valueDate,
                                  String valueDateTime,
                                  String valueCanonical,
                                  String valueCode,
                                  String valueString,
                                  Integer valueInteger,
                                  Boolean valueBoolean,
                                  FhirResource resource,
                                  @Singular("part")
                                  List<ParametersParameter> part)
{
}
