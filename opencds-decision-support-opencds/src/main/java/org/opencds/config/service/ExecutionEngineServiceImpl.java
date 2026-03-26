package org.opencds.config.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.opencds.common.cache.CacheRegion;
import org.opencds.common.utilities.ClassUtil;
import org.opencds.config.api.ExecutionEngineAdapter;
import org.opencds.config.api.ExecutionEngineContext;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.ExecutionEngineDao;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.service.ExecutionEngineService;

public class ExecutionEngineServiceImpl implements ExecutionEngineService
{
    private static final CacheRegion<String, ExecutionEngine> EXECUTION_ENGINE =
            CacheRegion.create(String.class, ExecutionEngine.class);
    private static final CacheRegion<ExecutionEngine, ExecutionEngineAdapter> EXECUTION_ENGINE_ADAPTER =
            CacheRegion.create(ExecutionEngine.class, ExecutionEngineAdapter.class);
    private static final CacheRegion<ExecutionEngine, Object> EXECUTION_ENGINE_INSTANCE =
            CacheRegion.create(ExecutionEngine.class, Object.class);
    private static final CacheRegion<ExecutionEngine, KnowledgeLoader> KNOWLEDGE_LOADER =
            CacheRegion.create(ExecutionEngine.class, KnowledgeLoader.class);

    private final ExecutionEngineDao dao;
    private final CacheService cacheService;

    public ExecutionEngineServiceImpl(final ExecutionEngineDao dao, final CacheService cacheService)
    {
        this.dao = dao;
        this.cacheService = cacheService;
        this.cacheService.putAll(EXECUTION_ENGINE, buildPairs(this.dao.getAll()));
    }

    @Override
    public ExecutionEngine find(final String identifier)
    {
        return cacheService.get(EXECUTION_ENGINE, identifier);
    }

    @Override
    public List<ExecutionEngine> getAll()
    {
        return List.copyOf(cacheService.getAllValues(EXECUTION_ENGINE));
    }

    @Override
    public void persist(final ExecutionEngine ee)
    {
        dao.persist(ee);
        cacheService.put(EXECUTION_ENGINE, ee.getIdentifier(), ee);
    }

    @Override
    public void persist(final List<ExecutionEngine> ees)
    {
        dao.persist(ees);
        cacheService.putAll(EXECUTION_ENGINE, buildPairs(ees));
    }

    @Override
    public void delete(final String identifier)
    {
        final ExecutionEngine ee = find(identifier);
        if (ee != null)
        {
            dao.delete(ee);
            cacheService.evict(EXECUTION_ENGINE, ee.getIdentifier());
        }
    }

    private Map<String, ExecutionEngine> buildPairs(final List<ExecutionEngine> all)
    {
        final Map<String, ExecutionEngine> cacheables = new HashMap<>();
        for (final ExecutionEngine ee : all)
            cacheables.put(ee.getIdentifier(), ee);
        return cacheables;
    }

    @Override
    @Deprecated(forRemoval = true)
    public <T> T getExecutionEngineInstance(final ExecutionEngine engine)
    {
        return Optional.ofNullable((T) cacheService.get(EXECUTION_ENGINE_INSTANCE, engine)).orElseGet(() ->
        {
            final T instance = ClassUtil.newInstance(engine.getIdentifier());
            cacheService.put(EXECUTION_ENGINE_INSTANCE, engine, instance);
            return instance;
        });
    }

    @Override
    public <I, O, P, E extends ExecutionEngineAdapter<I, O, P>> E getExecutionEngineAdapter(final ExecutionEngine engine)
    {
        return Optional.ofNullable((E) cacheService.get(EXECUTION_ENGINE_ADAPTER, engine)).orElseGet(() ->
        {
            if (engine.getAdapter() == null)
                return null;
            final E instance = ClassUtil.newInstance(engine.getAdapter());
            cacheService.put(EXECUTION_ENGINE_ADAPTER, engine, instance);
            return instance;
        });
    }

    @Override
    public <I, O, C extends ExecutionEngineContext<I, O>> C createContext(final ExecutionEngine engine)
    {
        return ClassUtil.newInstance(engine.getContext());
    }

    @Override
    public <I, O, KL extends KnowledgeLoader<I, O>> KL getKnowledgeLoader(final ExecutionEngine engine)
    {
        return Optional.ofNullable((KL) cacheService.get(KNOWLEDGE_LOADER, engine)).orElseGet(() ->
        {
            final var kLoader = Optional.ofNullable(engine.getKnowledgeLoader()).orElseGet(engine::getIdentifier);
            final KL instance = ClassUtil.newInstance(kLoader);
            cacheService.put(KNOWLEDGE_LOADER, engine, instance);
            return instance;
        });
    }
}
