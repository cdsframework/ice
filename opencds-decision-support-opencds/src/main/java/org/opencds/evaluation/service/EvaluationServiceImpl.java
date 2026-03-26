package org.opencds.evaluation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.opencds.common.exceptions.EvaluationException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.service.ExecutionEngineService;
import org.opencds.evaluation.service.util.CallableUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EvaluationServiceImpl implements EvaluationService
{
    private final ForkJoinPool evalPool =
            new ForkJoinPool(128, ForkJoinPool.defaultForkJoinWorkerThreadFactory, new EvaluationExceptionHandler(), true);

    private final CallableUtil callableUtil;

    public EvaluationServiceImpl(final CallableUtil callableUtil)
    {
        this.callableUtil = callableUtil;
    }

    @Override
    public List<EvaluationResponseKMItem> evaluate(final KnowledgeRepository knowledgeRepository,
            final List<EvaluationRequestKMItem> evaluationRequestKMItems)
    {
        final List<EvaluationResponseKMItem> responseItems = new ArrayList<>();

        final List<ForkJoinTask<EvaluationResponseKMItem>> tasks = new ArrayList<>();
        for (final EvaluationRequestKMItem evaluationRequestKMItem : evaluationRequestKMItems)
        {
            final KnowledgeModule knowledgeModule =
                    knowledgeRepository.knowledgeModuleService().find(evaluationRequestKMItem.requestedKmId());
            final ExecutionEngineService executionEngineService = knowledgeRepository.executionEngineService();
            final ExecutionEngine engine = executionEngineService.find(knowledgeModule.getExecutionEngine());
            final Callable<EvaluationResponseKMItem> callable =
                    getCallable(knowledgeRepository, executionEngineService, engine, evaluationRequestKMItem);

            log.debug("Starting evaluation of KM: {}", evaluationRequestKMItem.requestedKmId());
            tasks.add(evalPool.submit(callable));
        }

        boolean failing = false;
        Throwable t = null;
        for (final ForkJoinTask<EvaluationResponseKMItem> task : tasks)
        {
            if (!failing)
            {
                log.debug("Joining on task : {}", task.toString());
                task.quietlyJoin();
                t = task.getException();
                if (t == null)
                    responseItems.add(task.getRawResult());
                else
                    failing = true;
            }
            else
                task.cancel(true);
        }
        if (t != null)
            throw new EvaluationException(t.getCause().getMessage(), t.getCause());

        return responseItems;
    }

    @Override
    public EvaluationResponseKMItem evaluate(final KnowledgeRepository knowledgeRepository,
            final EvaluationRequestKMItem evaluationRequestKMItem)
    {
        final KnowledgeModule knowledgeModule =
                knowledgeRepository.knowledgeModuleService().find(evaluationRequestKMItem.requestedKmId());
        if (knowledgeModule == null)
            throw new OpenCDSRuntimeException("Unable to find requested KM: " + evaluationRequestKMItem.requestedKmId());
        final ExecutionEngineService executionEngineService = knowledgeRepository.executionEngineService();
        final ExecutionEngine engine = executionEngineService.find(knowledgeModule.getExecutionEngine());
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

    @Deprecated
    private Callable<EvaluationResponseKMItem> getCallable(final KnowledgeRepository knowledgeRepository,
            final ExecutionEngineService executionEngineService, final ExecutionEngine engine,
            final EvaluationRequestKMItem evaluationRequestKMItem)
    {
        return callableUtil.getCallable(knowledgeRepository, executionEngineService, engine, evaluationRequestKMItem);
    }
}
