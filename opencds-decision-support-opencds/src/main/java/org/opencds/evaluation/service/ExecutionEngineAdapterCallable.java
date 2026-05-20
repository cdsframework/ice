package org.opencds.evaluation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.config.api.EvaluationContext;
import org.opencds.config.api.ExecutionEngineAdapter;
import org.opencds.config.api.ExecutionEngineContext;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.evaluation.service.util.SupportingDataUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ExecutionEngineAdapterCallable<I, O, KP> implements Callable<EvaluationResponseKMItem>
{
    public static <I, O, KP> Callable<EvaluationResponseKMItem> create(final ExecutionEngineAdapter<I, O, KP> adapter,
            final ExecutionEngine engine, final KnowledgeRepository knowledgeRepository,
            final EvaluationRequestKMItem evaluationRequestKMItem)
    {
        return new ExecutionEngineAdapterCallable<>(adapter, engine, knowledgeRepository, evaluationRequestKMItem);
    }

    private final ExecutionEngineAdapter<I, O, KP> adapter;
    private final ExecutionEngine engine;
    private final KnowledgeRepository knowledgeRepository;
    private final EvaluationRequestKMItem evaluationRequestKMItem;

    @Override
    public EvaluationResponseKMItem call()
    {
        final var knowledgeModule = knowledgeRepository.knowledgeModuleService().find(evaluationRequestKMItem.requestedKmId());

        final var evaluationCtx = EvaluationContext.create(evaluationRequestKMItem, knowledgeModule.primaryProcess());

        final var supportingData = SupportingDataUtil.getSupportingData(knowledgeRepository, knowledgeModule);

        PluginProcessor.preProcess(knowledgeRepository, knowledgeModule, supportingData, evaluationCtx);

        ExecutionEngineContext<I, O> eeContext = knowledgeRepository.executionEngineService()
                .<I, O, ExecutionEngineContext<I, O>>createContext(engine)
                .setEvaluationContext(evaluationCtx);

        final KP knowledgePackage = knowledgeRepository.knowledgePackageService().getKnowledgePackage(knowledgeModule);
        log.debug("Package found.");

        try
        {
            eeContext = evaluate(knowledgePackage, eeContext);
        }
        catch (final Exception e)
        {
            log.error(e.getMessage(), e);
            throw e;
        }
        final Map<String, List<?>> resultFactLists = eeContext.getResults();

        final Map<String, ?> namedObjects = evaluationCtx.namedObjects();
        if (namedObjects != null)
        {
            //noinspection unchecked
            namedObjects.values()
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(oneNamedObject -> ((List<Object>) resultFactLists.computeIfAbsent(
                            oneNamedObject.getClass().getSimpleName(), _ -> new ArrayList<>())).add(oneNamedObject));
        }
        PluginProcessor.postProcess(knowledgeRepository, knowledgeModule, supportingData, evaluationCtx);

        return new EvaluationResponseKMItem(resultFactLists, evaluationRequestKMItem);
    }

    private ExecutionEngineContext<I, O> evaluate(final KP knowledgePackage, final ExecutionEngineContext<I, O> context)
    {
        try
        {
            return adapter.execute(knowledgePackage, context);
        }
        catch (final Exception e)
        {
            throw new OpenCDSRuntimeException(e.getMessage(), e);
        }
    }
}
