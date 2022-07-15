package org.cdsframework.rest.opencds.pojos;

import java.util.ArrayList;
import java.util.List;

import org.omg.dss.evaluation.requestresponse.EvaluationRequest;

public class UpdateCheck {
    private String environment;
    private String instanceId;
    private List<KmIdCheck> kmIdChecks = new ArrayList<>();
    private List<CdmIdCheck> cdmIdChecks = new ArrayList<>();
    private EvaluationRequest evaluationRequest;

    public EvaluationRequest getEvaluationRequest() {
        return this.evaluationRequest;
    }

    public void setEvaluationRequest(final EvaluationRequest evaluationRequest) {
        this.evaluationRequest = evaluationRequest;
    }

    public String getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(final String environment) {
        this.environment = environment;
    }

    public String getInstanceId() {
        return this.instanceId;
    }

    public void setInstanceId(final String instanceId) {
        this.instanceId = instanceId;
    }

    public List<KmIdCheck> getKmIdChecks() {
        return this.kmIdChecks;
    }

    public void setKmIdChecks(final List<KmIdCheck> kmIdChecks) {
        this.kmIdChecks = kmIdChecks;
    }

    public List<CdmIdCheck> getCdmIdChecks() {
        return this.cdmIdChecks;
    }

    public void setCdmIdChecks(final List<CdmIdCheck> cdmIdChecks) {
        this.cdmIdChecks = cdmIdChecks;
    }

    @Override
    public String toString() {
        return "{" + " environment='" + getEnvironment() + "'" + ", instanceId='" + getInstanceId() + "'"
                + ", kmIdChecks='" + getKmIdChecks() + "'" + ", cdmIdChecks='" + getCdmIdChecks() + "'" + "}";
    }
}