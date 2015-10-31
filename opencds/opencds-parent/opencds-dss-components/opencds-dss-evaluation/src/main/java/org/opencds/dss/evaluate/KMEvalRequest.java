package org.opencds.dss.evaluate;

import java.util.Date;

import org.omg.dss.evaluation.requestresponse.EvaluationRequest;

/**
 * A simple wrapper around EvaluationRequest that also contains timing data.
 * 
 * @author phillip
 *
 */
public class KMEvalRequest {
    private final EvaluationRequest evaluationRequest;
    private final Date evalTime;
    
    public KMEvalRequest(EvaluationRequest evaluationRequest, Date evalTime) {
        this.evaluationRequest = evaluationRequest;
        this.evalTime = evalTime;
    }

    public EvaluationRequest getEvaluationRequest() {
        return evaluationRequest;
    }

    public Date getEvalTime() {
        return evalTime;
    }

}
