package org.opencds.dss.evaluate;

import javax.xml.datatype.XMLGregorianCalendar;

import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.EvaluationExceptionFault;
import org.omg.dss.EvaluationRequest;
import org.omg.dss.EvaluationResponse;
import org.omg.dss.InteractionIdentifier;
import org.omg.dss.IterativeEvaluationRequest;
import org.omg.dss.IterativeEvaluationResponse;
import org.omg.dss.ObjectFactory;
import org.omg.dss.RequiredDataNotProvidedExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;

import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.soap.SOAPBinding;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@WebService(endpointInterface = "org.omg.dss.Evaluation")
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
@XmlSeeAlso({ ObjectFactory.class })
public class EvaluationSoapService implements org.omg.dss.Evaluation
{
    private final Evaluation evaluation;

    @Override
    public IterativeEvaluationResponse evaluateIteratively(final InteractionIdentifier interactionId,
            final IterativeEvaluationRequest iterativeEvaluationRequest)
            throws RequiredDataNotProvidedExceptionFault, DSSRuntimeExceptionFault
    {
        return evaluation.evaluateIteratively(interactionId, iterativeEvaluationRequest);
    }

    @Override
    public EvaluationResponse evaluate(final InteractionIdentifier interactionId, final EvaluationRequest evaluationRequest)
            throws UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault, DSSRuntimeExceptionFault
    {
        return evaluation.evaluate(interactionId, evaluationRequest);
    }

    @Override
    public EvaluationResponse evaluateAtSpecifiedTime(final InteractionIdentifier interactionId,
            final XMLGregorianCalendar specifiedTime, final EvaluationRequest evaluationRequest)
            throws UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault, DSSRuntimeExceptionFault
    {
        return evaluation.evaluateAtSpecifiedTime(interactionId, specifiedTime, evaluationRequest);
    }

    @Override
    public IterativeEvaluationResponse evaluateIterativelyAtSpecifiedTime(final InteractionIdentifier interactionId,
            final XMLGregorianCalendar specifiedTime, final IterativeEvaluationRequest iterativeEvaluationRequest)
            throws RequiredDataNotProvidedExceptionFault, DSSRuntimeExceptionFault
    {
        return evaluation.evaluateIterativelyAtSpecifiedTime(interactionId, specifiedTime, iterativeEvaluationRequest);
    }
}
