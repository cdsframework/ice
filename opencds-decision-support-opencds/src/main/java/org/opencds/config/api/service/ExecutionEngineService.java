package org.opencds.config.api.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.opencds.common.utilities.ClassUtil;
import org.opencds.config.api.ExecutionEngineAdapter;
import org.opencds.config.api.ExecutionEngineContext;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.dao.ExecutionEngineDao;
import org.opencds.config.api.model.ExecutionEngine;

public class ExecutionEngineService
{
    private final Map<String, ExecutionEngine> executionEngineMap;
    private final Map<ExecutionEngine, ExecutionEngineAdapter<?, ?, ?>> executionEngineAdapterMap = new ConcurrentHashMap<>();
    private final Map<ExecutionEngine, KnowledgeLoader<?, ?>> knowledgeLoaderMap = new ConcurrentHashMap<>();

    public ExecutionEngineService(final ExecutionEngineDao dao)
    {
        executionEngineMap =
                dao.getAll().stream().collect(Collectors.toConcurrentMap(ExecutionEngine::identifier, Function.identity()));
    }

    public ExecutionEngine find(final String identifier)
    {
        return executionEngineMap.get(identifier);
    }

    public List<ExecutionEngine> getAll()
    {
        return List.copyOf(executionEngineMap.values());
    }

    @SuppressWarnings("unchecked")
    public <I, O, P, E extends ExecutionEngineAdapter<I, O, P>> E getExecutionEngineAdapter(final ExecutionEngine engine)
    {
        return (E) executionEngineAdapterMap.computeIfAbsent(engine,
                _ -> ClassUtil.newInstance(engine.adapter(), ExecutionEngineAdapter.class));
    }

    public <I, O, C extends ExecutionEngineContext<I, O>> C createContext(final ExecutionEngine engine)
    {
        return (C) ClassUtil.newInstance(engine.context(), ExecutionEngineContext.class);
    }

    @SuppressWarnings("unchecked")
    public <I, O, KL extends KnowledgeLoader<I, O>> KL getKnowledgeLoader(final ExecutionEngine engine)
    {
        return (KL) knowledgeLoaderMap.computeIfAbsent(engine,
                _ -> ClassUtil.newInstance(Optional.ofNullable(engine.knowledgeLoader()).orElseGet(engine::identifier),
                        KnowledgeLoader.class));
    }
}
