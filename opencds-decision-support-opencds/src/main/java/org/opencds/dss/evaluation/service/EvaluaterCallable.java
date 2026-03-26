package org.opencds.dss.evaluation.service;

import java.util.concurrent.Callable;

import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.config.api.KnowledgeRepository;

import lombok.RequiredArgsConstructor;

@Deprecated(forRemoval = true)
@RequiredArgsConstructor
public class EvaluaterCallable implements Callable<EvaluationResponseKMItem>
{
    private final Evaluater evaluater;
    private final KnowledgeRepository knowledgeRepository;
    private final EvaluationRequestKMItem evaluationRequestKMItem;

    @Override
    public EvaluationResponseKMItem call()
    {
        return new EvaluationResponseKMItem(evaluater.getOneResponse(knowledgeRepository, evaluationRequestKMItem),
                evaluationRequestKMItem);
    }
}
