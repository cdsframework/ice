package org.cdsframework.ice.cdsrr.dto;

import java.time.LocalDate;

import org.cdsframework.fhir.CodeableConcept;

import lombok.Builder;

@Builder
public record Patient(String id,
                      LocalDate birthDate,
                      CodeableConcept sex)
{
}