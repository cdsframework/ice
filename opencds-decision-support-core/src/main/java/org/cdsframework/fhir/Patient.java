package org.cdsframework.fhir;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Singular;

@Builder
public record Patient(String id,
                      String resourceType,
                      Narrative text,
                      @Singular("identifier")
                      List<Identifier> identifier,
                      LocalDate birthDate,
                      AdministrativeGender gender) implements FhirResource
{
    public static final String RESOURCE_TYPE = "Patient";

    public Patient
    {
        resourceType = FhirResource.normalizeResourceType(resourceType, RESOURCE_TYPE);
    }
}
