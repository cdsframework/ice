package org.cdsframework.fhir;

import lombok.Builder;

@Builder
public record Identifier(String use,
                         CodeableConcept type,
                         String system,
                         String value)
{
}
