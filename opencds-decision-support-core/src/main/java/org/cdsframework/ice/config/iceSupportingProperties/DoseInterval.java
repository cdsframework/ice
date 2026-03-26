package org.cdsframework.ice.config.iceSupportingProperties;

import jakarta.validation.constraints.NotNull;

public record DoseInterval(@NotNull
                           Integer fromDoseNumber,
                           @NotNull
                           Integer toDoseNumber,
                           String absoluteMinimumInterval,
                           String minimumInterval,
                           String earliestRecommendedInterval,
                           String latestRecommendedInterval)
{
}
