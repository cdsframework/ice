package org.cdsframework.ice.supportingdata;

import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record Dose(Integer doseNumber,
                   String absoluteMinimumAge,
                   String minimumAge,
                   String earliestRecommendedAge,
                   String latestRecommendedAge,
                   String absoluteMaximumAge,
                   @NotEmpty
                   Map<String, @Valid DoseVaccine> doseVaccines)
{
    public Dose copyWithDoseNumber(final Integer doseNumber)
    {
        return new Dose(doseNumber, absoluteMinimumAge, minimumAge, earliestRecommendedAge, latestRecommendedAge,
                absoluteMaximumAge, doseVaccines);
    }
}
