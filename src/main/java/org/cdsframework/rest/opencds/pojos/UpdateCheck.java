package org.cdsframework.rest.opencds.pojos;

import java.util.ArrayList;
import java.util.List;

import org.omg.dss.evaluation.requestresponse.EvaluationRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UpdateCheck
{
    private String environment;
    private String instanceId;
    private List<KmIdCheck> kmIdChecks = new ArrayList<>();
    private List<CdmIdCheck> cdmIdChecks = new ArrayList<>();
    private EvaluationRequest evaluationRequest;
}