package org.opencds.dss.evaluate;

import javax.xml.datatype.XMLGregorianCalendar;

import org.omg.dss.InteractionIdentifier;
import org.omg.dss.IterativeEvaluationRequest;
import org.omg.dss.IterativeEvaluationResponse;

@Deprecated(forRemoval = true)
public abstract class IterativeEvaluater
{
    public abstract IterativeEvaluationResponse getResponse(InteractionIdentifier ii, XMLGregorianCalendar et,
            IterativeEvaluationRequest e);
}
