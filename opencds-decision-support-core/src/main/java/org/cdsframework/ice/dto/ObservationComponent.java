package org.cdsframework.ice.dto;

import lombok.Builder;

@Builder
public record ObservationComponent(CodeableConcept code,
                                   CodeableConcept valueCodeableConcept,
                                   Integer valueInteger,
                                   String valueString)
{
}
