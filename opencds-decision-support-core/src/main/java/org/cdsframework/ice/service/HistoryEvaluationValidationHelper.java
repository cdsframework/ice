package org.cdsframework.ice.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.cdsframework.ice.supportingdatatmp.SupportedFactConcept;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HistoryEvaluationValidationHelper
{
    public record ValidationResult(List<Violation> violations)
    {
        public String diagnosticMessage()
        {
            return violations.stream().map(Violation::diagnosticMessage).collect(Collectors.joining("; "));
        }
    }

    public record Violation(TargetDose targetDose,
                            Set<String> vaccineGroups,
                            Set<String> targetSeriesNames)
    {
        public ICEFactTypeFinding finding()
        {
            return new ICEFactTypeFinding(
                    SupportedFactConcept._MULTIPLE_VACCINE_GROUP_TARGET_DOSE_ASSOCIATION.getConceptCodeValue(), targetDose,
                    targetDose.getAssociatedTargetSeries(), diagnosticMessage());
        }

        public String diagnosticMessage()
        {
            return "doseId=%s, cvx=%s, vaccineGroups=%s, targetSeries=%s".formatted(targetDose.getDoseId(),
                    targetDose.getAdministeredVaccine().getCdsConceptName(), vaccineGroups, targetSeriesNames);
        }
    }

    public static ValidationResult validate(final Collection<TargetDose> targetDoses)
    {
        return new ValidationResult(targetDoses.stream()
                .filter((final TargetDose targetDose) -> targetDose.getAdministeredVaccine().isValidInMultipleVaccineGroups())
                .collect(Collectors.groupingBy((final TargetDose targetDose) -> Arrays.asList(targetDose.getDoseId(),
                        targetDose.getVaccineComponent().getCdsConceptName())))
                .values()
                .stream()
                .map((final List<TargetDose> doses) ->
                {
                    final Set<String> vaccineGroups = doses.stream()
                            .map(TargetDose::getAssociatedVaccineGroup)
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                    final Set<String> targetSeriesNames = doses.stream()
                            .map(TargetDose::getAssociatedTargetSeries)
                            .map(TargetSeries::getSeriesName)
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    return vaccineGroups.size() > 1 ? new Violation(doses.getFirst(), vaccineGroups, targetSeriesNames) : null;
                })
                .filter(Objects::nonNull)
                .toList());
    }
}
