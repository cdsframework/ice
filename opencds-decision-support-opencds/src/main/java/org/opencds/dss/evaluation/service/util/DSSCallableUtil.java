package org.opencds.dss.evaluation.service.util;

import java.util.concurrent.Callable;

import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.config.api.ExecutionEngineAdapter;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.service.ExecutionEngineService;
import org.opencds.dss.evaluation.service.Evaluater;
import org.opencds.dss.evaluation.service.EvaluaterCallable;
import org.opencds.evaluation.service.ExecutionEngineAdapterCallable;
import org.opencds.evaluation.service.util.CallableUtil;

public class DSSCallableUtil implements CallableUtil
{
    @Override
    public Callable<EvaluationResponseKMItem> getCallable(final KnowledgeRepository knowledgeRepository,
            final ExecutionEngineService executionEngineService, final ExecutionEngine engine,
            final EvaluationRequestKMItem evaluationRequestKMItem)
    {
        ExecutionEngineAdapter<?, ?, ?> adapter = executionEngineService.getExecutionEngineAdapter(engine);
        if (adapter != null)
            return ExecutionEngineAdapterCallable.create(adapter, engine, knowledgeRepository, evaluationRequestKMItem);
        adapter = executionEngineService.getExecutionEngineInstance(engine);
        if (adapter instanceof final Evaluater<?, ?> evaluater)
            return new EvaluaterCallable(evaluater, knowledgeRepository, evaluationRequestKMItem);
        return null;
    }
}
