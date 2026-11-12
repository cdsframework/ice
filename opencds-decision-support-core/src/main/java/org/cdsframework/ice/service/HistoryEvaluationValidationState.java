package org.cdsframework.ice.service;

import java.util.Collection;
import java.util.List;

import org.kie.api.definition.type.ClassReactive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ClassReactive
public class HistoryEvaluationValidationState
{
    public enum ValidationStatus
    {
        NOT_RUN,
        PASSED,
        FAILED
    }

    // Called by the PASSED and FAILED MVEL gateway conditions in the ICE BPMN process.
    public static boolean hasValidationStatus(final Collection<?> facts, final ValidationStatus validationStatus)
    {
        if (facts == null || validationStatus == null)
            return false;

        final List<HistoryEvaluationValidationState> validationStates = facts.stream()
                .filter(HistoryEvaluationValidationState.class::isInstance)
                .map(HistoryEvaluationValidationState.class::cast)
                .toList();
        return validationStates.size() == 1 && validationStates.getFirst().getValidationStatus() == validationStatus;
    }

    private ValidationStatus validationStatus = ValidationStatus.NOT_RUN;
    private String diagnosticMessage;
    private boolean controlledErrorResponseGenerated;
}
