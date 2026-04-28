package org.cdsframework.ice.dto;

import lombok.Builder;

@Builder
public record CodeSystemConceptProperty(String code,
                                        Coding valueCoding,
                                        Boolean valueBoolean,
                                        Integer valueInteger,
                                        String valueString)
{
}
