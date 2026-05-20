package org.cdsframework.ice.cdsrr.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Singular;

public record CdsRequest(Patient patient,
                         LocalDate assessmentDate,
                         Module module,
                         @Singular
                         List<Immunization> immunizations,
                         @Singular
                         List<Observation> observations)
{
}