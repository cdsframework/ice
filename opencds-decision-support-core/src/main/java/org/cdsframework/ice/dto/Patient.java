package org.cdsframework.ice.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Singular;

@Builder
public record Patient(String resourceType,
                      Narrative text,
                      @Singular("identifier")
                      List<Identifier> identifier,
                      LocalDate birthDate,
                      AdministrativeGender gender) implements FhirResource
{
    public Patient
    {
        resourceType = resourceType == null ? "Patient" : resourceType;
        identifier = Objects.requireNonNullElseGet(identifier, List::of);
    }
}
