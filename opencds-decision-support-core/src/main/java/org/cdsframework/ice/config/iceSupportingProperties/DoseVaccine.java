package org.cdsframework.ice.config.iceSupportingProperties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DoseVaccine(Boolean preferred,
                          @NotNull
                          @Valid
                          Vaccine vaccine,
                          String allowableMinimumAgeOfUse,
                          String allowableMaximumAgeOfUse)
{
}
