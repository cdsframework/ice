package org.cdsframework.fhir;

import lombok.Builder;

@Builder
public record CodeSystemConceptProperty(String code,
                                        Coding valueCoding,
                                        Boolean valueBoolean,
                                        Integer valueInteger,
                                        String valueString)
{
}
