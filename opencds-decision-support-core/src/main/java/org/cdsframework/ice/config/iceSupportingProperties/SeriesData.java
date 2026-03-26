package org.cdsframework.ice.config.iceSupportingProperties;

import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SeriesData(@NotEmpty
                         String seriesId,
                         @NotNull
                         Series series,
                         @NotEmpty
                         Map<String, @NotEmpty String> cdsVersion,
                         @NotNull
                         Integer numberOfDosesInSeries,
                         Boolean recurringDosesAfterSeriesComplete,
                         Map<String, Season> seasons,
                         @NotEmpty
                         Map<String, @Valid VaccineGroup> vaccineGroup,
                         Integer seriesGroup,
                         String patientStartAge,
                         String patientEndAge,
                         Boolean doseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered,
                         @NotEmpty
                         Map<String, @Valid Dose> doses,
                         Map<String, @Valid DoseInterval> doseIntervals)
{
}
