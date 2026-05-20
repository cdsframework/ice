package org.cdsframework.fhir;

import java.util.List;

import lombok.Builder;
import lombok.Singular;

@Builder
public record CodeableConcept(@Singular("coding")
                              List<Coding> coding,
                              String text)
{
}
