/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.config.service;

import com.google.common.collect.ImmutableList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.common.exceptions.RequiredDataNotProvidedException;
import org.opencds.config.api.ExecutionEngineAdapter;
import org.opencds.config.api.ExecutionEngineContext;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.ExecutionEngineDao;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.service.ExecutionEngineService;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExecutionEngineServiceImpl implements ExecutionEngineService {
    private static final Log log = LogFactory.getLog(ExecutionEngineServiceImpl.class);

    private final ExecutionEngineDao dao;
    private final CacheService cacheService;

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
        return ImmutableList.<ExecutionEngine>builder().addAll(ees).build();
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
        Map<String, ExecutionEngine> cacheables = new HashMap<>();
        for (ExecutionEngine ee : all) {
            cacheables.put(ee.getIdentifier(), ee);
        }
        return cacheables;
    }

    @Override
    @Deprecated(forRemoval = true)
    public <T> T getExecutionEngineInstance(ExecutionEngine engine) {
        T instance = cacheService.get(EECacheRegion.EXECUTION_ENGINE_INSTANCE, engine);
        if (instance == null) {
            try {
                instance = (T) Class.forName(engine.getIdentifier()).getDeclaredConstructor().newInstance();
                cacheService.put(EECacheRegion.EXECUTION_ENGINE_INSTANCE, engine, instance);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                throw new RequiredDataNotProvidedException(e.getClass().getSimpleName() + ": " + engine.getIdentifier()
                        + " " + e.getMessage());
            } catch (ClassNotFoundException e) {
                log.warn("EE Identifier is not a class (might reference an Adapter): " + e.getMessage());
            } catch (ClassCastException e) {
                log.warn("Class is not an instance of Evaluater; will return null. " + e.getMessage());
            } catch (InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        return instance;
    }

    @Override
    public <I, O, P, E extends ExecutionEngineAdapter<I, O, P>> E getExecutionEngineAdapter(ExecutionEngine engine) {
        E instance = cacheService.get(EECacheRegion.EXECUTION_ENGINE_ADAPTER, engine);
        if (instance == null) {
            try {
                if (engine.getAdapter() == null) {
                    return null;
                }
                instance = (E) Class.forName(engine.getAdapter()).getDeclaredConstructor().newInstance();
                cacheService.put(EECacheRegion.EXECUTION_ENGINE_ADAPTER, engine, instance);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                throw new RequiredDataNotProvidedException(e.getClass().getSimpleName() + ": " + engine.getIdentifier()
                        + " " + e.getMessage());
            } catch (ClassNotFoundException e) {
                log.warn("EE Adapter is not a class (might reference an old-style Evaluater): " + e.getMessage());
            } catch (ClassCastException e) {
                log.warn("Class is not an instance of ExecutionEngineAdapter; will return null");
            } catch (InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        return instance;
    }

    @Override
    public <I, O, C extends ExecutionEngineContext<I, O>> C createContext(ExecutionEngine engine) {
        try {
            return (C) Class.forName(engine.getContext()).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RequiredDataNotProvidedException(e.getClass().getSimpleName() + ": " + engine.getIdentifier()
                    + " " + e.getMessage());
        } catch (ClassCastException e) {
            String message = "Class is not an instance of ExecutionEngineContext; will return null";
            log.warn(message);
            throw new RuntimeException(message, e);
        } catch (InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <I, O, KL extends KnowledgeLoader<I, O>> KL getKnowledgeLoader(ExecutionEngine engine) {
        KL instance = cacheService.get(EECacheRegion.KNOWLEDGE_LOADER, engine);
        if (instance == null) {
            try {
                String kLoader = engine.getKnowledgeLoader();
                if (kLoader == null) {
                    kLoader = engine.getIdentifier();
                }
                instance = (KL) Class.forName(kLoader).getDeclaredConstructor().newInstance();
                cacheService.put(EECacheRegion.KNOWLEDGE_LOADER, engine, instance);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                throw new RequiredDataNotProvidedException(e.getClass().getSimpleName() + ": " + engine.getIdentifier()
                        + " " + e.getMessage());
            } catch (ClassCastException e) {
                log.warn("Class is not an instance of KnowledgeLoader; will return null");
            } catch (InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    private enum EECacheRegion implements CacheRegion {
        EXECUTION_ENGINE(ExecutionEngine.class),
        EXECUTION_ENGINE_ADAPTER(ExecutionEngineAdapter.class),
        EXECUTION_ENGINE_INSTANCE(Object.class),
        KNOWLEDGE_LOADER(KnowledgeLoader.class);

        private final Class<?> type;

        EECacheRegion(Class<?> type) {
            this.type = type;
        }

        @Override
        public boolean supports(Class<?> type) {
            return this.type.isAssignableFrom(type);
        }
    }

}
