package org.cdsframework.ice.cdsrr.dto;

import java.time.LocalDate;

import org.cdsframework.fhir.CodeableConcept;

public record Immunization(String id,
                           CodeableConcept vaccineCode,
                           LocalDate occurrenceDateTime)
{
}