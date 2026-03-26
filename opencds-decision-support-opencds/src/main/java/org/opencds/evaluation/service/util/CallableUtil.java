package org.opencds.evaluation.service.util;

import java.util.concurrent.Callable;

import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.service.ExecutionEngineService;

public interface CallableUtil
{
    Callable<EvaluationResponseKMItem> getCallable(KnowledgeRepository knowledgeRepository,
            ExecutionEngineService executionEngineService, ExecutionEngine engine, EvaluationRequestKMItem evaluationRequestKMItem);
}
