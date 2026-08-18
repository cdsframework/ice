package org.cdsframework.ice.cdsrr.dto;

import java.time.LocalDate;

import org.cdsframework.fhir.CodeableConcept;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Builder;

@Builder
public record Observation(String id,
                          CodeableConcept code,
                          @JsonAlias("effectiveDateTime")
                          LocalDate valueDateTime,
                          CodeableConcept valueCodeableConcept)
{
}
