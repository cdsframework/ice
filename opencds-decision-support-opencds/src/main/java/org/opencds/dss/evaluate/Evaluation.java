package org.opencds.dss.evaluate;

import javax.xml.datatype.XMLGregorianCalendar;

import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.EvaluationExceptionFault;
import org.omg.dss.EvaluationRequest;
import org.omg.dss.EvaluationResponse;
import org.omg.dss.InteractionIdentifier;
import org.omg.dss.IterativeEvaluationRequest;
import org.omg.dss.IterativeEvaluationResponse;
import org.omg.dss.RequiredDataNotProvidedExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;

public interface Evaluation
{
    IterativeEvaluationResponse evaluateIteratively(InteractionIdentifier interactionId,
            IterativeEvaluationRequest iterativeEvaluationRequest)
            throws RequiredDataNotProvidedExceptionFault, DSSRuntimeExceptionFault;

    EvaluationResponse evaluate(InteractionIdentifier interactionId, EvaluationRequest evaluationRequest)
            throws UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault, DSSRuntimeExceptionFault;

    EvaluationResponse evaluateAtSpecifiedTime(InteractionIdentifier interactionId, XMLGregorianCalendar specifiedTime,
            EvaluationRequest evaluationRequest)
            throws UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault, DSSRuntimeExceptionFault;

    IterativeEvaluationResponse evaluateIterativelyAtSpecifiedTime(InteractionIdentifier interactionId,
            XMLGregorianCalendar specifiedTime, IterativeEvaluationRequest iterativeEvaluationRequest)
            throws RequiredDataNotProvidedExceptionFault, DSSRuntimeExceptionFault;
}
