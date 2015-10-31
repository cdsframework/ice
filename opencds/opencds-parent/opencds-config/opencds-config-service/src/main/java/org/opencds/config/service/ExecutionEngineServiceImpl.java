package org.opencds.config.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.common.exceptions.RequiredDataNotProvidedException;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.ExecutionEngineDao;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.service.ExecutionEngineService;

import com.google.common.collect.ImmutableList;

public class ExecutionEngineServiceImpl implements ExecutionEngineService {

    private final ExecutionEngineDao dao;
    private CacheService cacheService;

    public ExecutionEngineServiceImpl(ExecutionEngineDao dao, CacheService cacheService) {
        this.dao = dao;
        this.cacheService = cacheService;
        this.cacheService.putAll(EECacheRegion.EXECUTION_ENGINE, buildPairs(this.dao.getAll()));
    }

    @Override
    public ExecutionEngine find(String identifier) {
        return cacheService.get(EECacheRegion.EXECUTION_ENGINE, identifier);
    }

    @Override
    public List<ExecutionEngine> getAll() {
        Set<ExecutionEngine> ees = cacheService.getAll(EECacheRegion.EXECUTION_ENGINE);
        return ImmutableList.<ExecutionEngine> builder().addAll(ees).build();
    }

    @Override
    public void persist(ExecutionEngine ee) {
        dao.persist(ee);
        cacheService.put(EECacheRegion.EXECUTION_ENGINE, ee.getIdentifier(), ee);
    }

    @Override
    public void persist(List<ExecutionEngine> ees) {
        dao.persist(ees);
        cacheService.putAll(EECacheRegion.EXECUTION_ENGINE, buildPairs(ees));
    }

    @Override
    public void delete(String identifier) {
        ExecutionEngine ee = find(identifier);
        if (ee != null) {
            dao.delete(ee);
            cacheService.evict(EECacheRegion.EXECUTION_ENGINE, ee.getIdentifier());
        }
    }

    private Map<String, ExecutionEngine> buildPairs(List<ExecutionEngine> all) {
        Map<String, ExecutionEngine> cachables = new HashMap<>();
        for (ExecutionEngine ee : all) {
            cachables.put(ee.getIdentifier(), ee);
        }
        return cachables;
    }

    @Override
    public <T> T getExecutionEngineInstance(ExecutionEngine engine) {
        T instance = cacheService.get(EECacheRegion.EXECUTION_ENGINE_INSTANCE, engine);
        try {
            if (instance == null) {
                Class<T> c = (Class<T>) Class.forName(engine.getIdentifier());
                instance = c.newInstance();
                cacheService.put(EECacheRegion.EXECUTION_ENGINE_INSTANCE, engine, instance);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            throw new RequiredDataNotProvidedException(e.getClass().getSimpleName() + ": " + engine.getIdentifier()
                    + " " + e.getMessage());
        }

        return instance;
    }
    
    private enum EECacheRegion implements CacheRegion {
        EXECUTION_ENGINE(ExecutionEngine.class),
        EXECUTION_ENGINE_INSTANCE(Object.class);

        private Class<?> type;

        EECacheRegion(Class<?> type) {
            this.type = type;
        }

        @Override
        public boolean supports(Class<?> type) {
            return this.type.isAssignableFrom(type);
        }
    }

}
