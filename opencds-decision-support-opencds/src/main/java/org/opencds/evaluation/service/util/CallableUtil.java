package org.opencds.evaluation.service.util;

import java.util.Optional;
import java.util.concurrent.Callable;

import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.config.api.ExecutionEngineAdapter;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.service.ExecutionEngineService;
import org.opencds.evaluation.service.ExecutionEngineAdapterCallable;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CallableUtil
{
    public static Callable<EvaluationResponseKMItem> getCallable(final KnowledgeRepository knowledgeRepository,
            final ExecutionEngineService executionEngineService, final ExecutionEngine engine,
            final EvaluationRequestKMItem evaluationRequestKMItem)
    {
        return Optional.ofNullable(executionEngineService.getExecutionEngineAdapter(engine))
                .map(adapter -> ExecutionEngineAdapterCallable.create((ExecutionEngineAdapter<?, ?, ?>) adapter, engine,
                        knowledgeRepository, evaluationRequestKMItem))
                .orElse(null);
    }
}
