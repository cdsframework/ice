package org.cdsframework.ice.cdsrr.dto;

import java.time.LocalDate;

import org.cdsframework.fhir.CodeableConcept;

import lombok.Builder;

@Builder
public record Observation(String id,
                          CodeableConcept code,
                          LocalDate effectiveDateTime,
                          CodeableConcept valueCodeableConcept)
{
}