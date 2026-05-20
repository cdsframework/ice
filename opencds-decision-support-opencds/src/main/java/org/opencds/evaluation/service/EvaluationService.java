package org.opencds.evaluation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.opencds.common.exceptions.EvaluationException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.service.ExecutionEngineService;
import org.opencds.evaluation.service.util.CallableUtil;
import org.springframework.core.task.AsyncTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record EvaluationService(AsyncTaskExecutor evalPool)
{
    public List<EvaluationResponseKMItem> evaluate(final KnowledgeRepository knowledgeRepository,
            final List<EvaluationRequestKMItem> evaluationRequestKMItems)
    {
        final ExecutionEngineService executionEngineService = knowledgeRepository.executionEngineService();

        final List<Future<EvaluationResponseKMItem>> tasks = evaluationRequestKMItems.stream().map(evaluationRequestKMItem ->
        {
            final Callable<EvaluationResponseKMItem> callable = getCallable(knowledgeRepository, executionEngineService,
                    executionEngineService.find(knowledgeRepository.knowledgeModuleService()
                            .find(evaluationRequestKMItem.requestedKmId())
                            .executionEngine()), evaluationRequestKMItem);

            log.debug("Starting evaluation of KM: {}", evaluationRequestKMItem.requestedKmId());
            return evalPool.submit(callable);
        }).toList();

        final List<EvaluationResponseKMItem> responseItems = new ArrayList<>();
        boolean failing = false;
        Throwable t = null;

        for (final Future<EvaluationResponseKMItem> task : tasks)
        {
            if (!failing)
            {
                log.debug("Joining on task : {}", task.toString());

                try
                {
                    responseItems.add(task.get());
                }
                catch (final Exception e)
                {
                    t = e instanceof ExecutionException ? e.getCause() : e;
                    failing = true;
                }
            }
            else
                task.cancel(true);
        }

        if (t != null)
            throw new EvaluationException(t.getMessage(), t);

        return responseItems;
    }

    public EvaluationResponseKMItem evaluate(final KnowledgeRepository knowledgeRepository,
            final EvaluationRequestKMItem evaluationRequestKMItem)
    {
        final KnowledgeModule knowledgeModule =
                knowledgeRepository.knowledgeModuleService().find(evaluationRequestKMItem.requestedKmId());
        if (knowledgeModule == null)
            throw new OpenCDSRuntimeException("Unable to find requested KM: " + evaluationRequestKMItem.requestedKmId());

        final ExecutionEngineService executionEngineService = knowledgeRepository.executionEngineService();
        final ExecutionEngine engine = executionEngineService.find(knowledgeModule.executionEngine());
        final Callable<EvaluationResponseKMItem> callable =
                getCallable(knowledgeRepository, executionEngineService, engine, evaluationRequestKMItem);
        final EvaluationResponseKMItem evaluationResponseKMItem;
        try
        {
            evaluationResponseKMItem = callable.call();
        }
        catch (final Exception e)
        {
            log.error("==========================================================", e);
            throw new OpenCDSRuntimeException(e);
        }

        return evaluationResponseKMItem;
    }

    private Callable<EvaluationResponseKMItem> getCallable(final KnowledgeRepository knowledgeRepository,
            final ExecutionEngineService executionEngineService, final ExecutionEngine engine,
            final EvaluationRequestKMItem evaluationRequestKMItem)
    {
        return CallableUtil.getCallable(knowledgeRepository, executionEngineService, engine, evaluationRequestKMItem);
    }
}
