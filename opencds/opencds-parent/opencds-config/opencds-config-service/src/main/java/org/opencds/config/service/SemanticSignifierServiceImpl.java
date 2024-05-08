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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.common.interfaces.ResultSetBuilder;
import org.opencds.config.api.FactListsBuilder;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.SemanticSignifierDao;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.service.SemanticSignifierService;
import org.opencds.config.api.ss.EntryPoint;
import org.opencds.config.api.ss.ExitPoint;

import com.google.common.collect.ImmutableList;

public class SemanticSignifierServiceImpl implements SemanticSignifierService {
    private static final Log log = LogFactory.getLog(SemanticSignifierServiceImpl.class);

    private final SemanticSignifierDao dao;
    private CacheService cacheService;

    public SemanticSignifierServiceImpl(SemanticSignifierDao dao, CacheService cacheService) {
        this.dao = dao;
        this.cacheService = cacheService;
        List<SemanticSignifier> allSS = this.dao.getAll();
        init(allSS);
    }
    
    private void init(List<SemanticSignifier> allSS) {
        cacheService.putAll(SSCacheRegion.SEMANTIC_SIGNIFIER, buildPairs(allSS));
        cacheService.putAll(SSCacheRegion.ENTRY_POINT,  buildEntryPointPairs(allSS));
        cacheService.putAll(SSCacheRegion.EXIT_POINT,  buildExitPointPairs(allSS));
        cacheService.putAll(SSCacheRegion.FACT_LISTS_BUILDER, buildFactListsBuilderPairs(allSS));
        cacheService.putAll(SSCacheRegion.RESULT_SET_BUILDER, buildResultSetBuilderPairs(allSS));
        
    }

	@Override
    public SemanticSignifier find(SSId ssId) {
        return cacheService.get(SSCacheRegion.SEMANTIC_SIGNIFIER, ssId);
    }

    @Override
    public List<SemanticSignifier> getAll() {
        Set<SemanticSignifier> sses = cacheService.getAll(SSCacheRegion.SEMANTIC_SIGNIFIER);
        return ImmutableList.<SemanticSignifier> builder().addAll(sses).build();
    }

    @Override
    public void persist(SemanticSignifier ss) {
        dao.persist(ss);
        cacheService.put(SSCacheRegion.SEMANTIC_SIGNIFIER, ss.getSSId(), ss);
        init(this.dao.getAll());
    }

    @Override
    public void persist(List<SemanticSignifier> sses) {
        dao.persist(sses);
        cacheService.putAll(SSCacheRegion.SEMANTIC_SIGNIFIER, buildPairs(sses));
        init(this.dao.getAll());
    }

    @Override
    public void delete(SSId ssId) {
        SemanticSignifier ss = find(ssId);
        if (ss != null) {
            dao.delete(ss);
            cacheService.evict(SSCacheRegion.SEMANTIC_SIGNIFIER, ss.getSSId());
            cacheService.evict(SSCacheRegion.FACT_LISTS_BUILDER, ss.getSSId());
            cacheService.evict(SSCacheRegion.RESULT_SET_BUILDER, ss.getSSId());
        }
    }
    
    @Override
    public <T> EntryPoint<T> getEntryPoint(SSId ssId) {
    	return cacheService.get(SSCacheRegion.ENTRY_POINT, ssId);
    }

    @Override
    public ExitPoint getExitPoint(SSId ssId) {
    	return cacheService.get(SSCacheRegion.EXIT_POINT, ssId);
    }

    @Override
    public FactListsBuilder getFactListsBuilder(SSId ssId) {
        return cacheService.get(SSCacheRegion.FACT_LISTS_BUILDER, ssId);
    }
    
    @Override
    public <T> ResultSetBuilder<T> getResultSetBuilder(SSId ssId) {
        return cacheService.get(SSCacheRegion.RESULT_SET_BUILDER, ssId);
    }

    private Map<SSId, SemanticSignifier> buildPairs(List<SemanticSignifier> all) {
    	Map<SSId, SemanticSignifier> cachables = new HashMap<>();
    	for (SemanticSignifier ss : all) {
    		cachables.put(ss.getSSId(), ss);
    	}
    	return cachables;
    }
    
    private Map<SSId, EntryPoint<?>> buildEntryPointPairs(List<SemanticSignifier> allSS) {
    	Map<SSId, EntryPoint<?>> cachables = new HashMap<>();
    	for (SemanticSignifier ss : allSS) {
    		Class<EntryPoint<?>> epClass;
    		try {
    			epClass = (Class<EntryPoint<?>>) Class.forName(ss.getEntryPoint());
    			EntryPoint<?> entryPoint = epClass.newInstance();
    			cachables.put(ss.getSSId(), entryPoint);
    		} catch (NullPointerException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            	log.error("Problem loading EntryPoint from configuration: " + e.getMessage(),e);
    		}
    	}
    	return cachables;
    }
    
	private Map<?, ?> buildExitPointPairs(List<SemanticSignifier> allSS) {
    	Map<SSId, ExitPoint> cachables = new HashMap<>();
    	for (SemanticSignifier ss : allSS) {
    		Class<ExitPoint> exClass;
    		try {
    			exClass = (Class<ExitPoint>) Class.forName(ss.getExitPoint());
    			ExitPoint exitPoint = exClass.newInstance();
    			cachables.put(ss.getSSId(), exitPoint);
    		} catch (NullPointerException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            	log.error("Problem loading ExitPoint from configuration: " + e.getMessage(),e);
    		}
    	}
    	return cachables;
	}

    @SuppressWarnings("unchecked")
    private Map<SSId, FactListsBuilder> buildFactListsBuilderPairs(List<SemanticSignifier> all) {
        Map<SSId, FactListsBuilder> cachables = new HashMap<>();
        for (SemanticSignifier ss : all) {
            Class<FactListsBuilder> flbClass;
            try {
                flbClass = (Class<FactListsBuilder>) Class.forName(ss.getFactListsBuilder());
                FactListsBuilder flbInstance = flbClass.newInstance();
                cachables.put(ss.getSSId(), flbInstance);
            } catch (NullPointerException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            	log.error("Problem loading FactListsBuilder from configuration: " + e.getMessage(),e);
            }
        }
        return cachables;
    }

    private Map<?, ?> buildResultSetBuilderPairs(List<SemanticSignifier> all) {
        Map<SSId, ResultSetBuilder<?>> cachables = new HashMap<>();
        for (SemanticSignifier ss : all) {
            Class<ResultSetBuilder<?>> rsbClass;
            try {
                rsbClass = (Class<ResultSetBuilder<?>>) Class.forName(ss.getResultSetBuilder());
                ResultSetBuilder<?> rsbInstance = rsbClass.newInstance();
                cachables.put(ss.getSSId(), rsbInstance);
            } catch (NullPointerException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            	log.error("Problem loading ResultSetBuilder from configuration: " + e.getMessage(),e);
            }
        }
        return cachables;
    }

    private enum SSCacheRegion implements CacheRegion {
        SEMANTIC_SIGNIFIER(SemanticSignifier.class),
        ENTRY_POINT(SemanticSignifier.class),
        EXIT_POINT(SemanticSignifier.class),
        FACT_LISTS_BUILDER(FactListsBuilder.class),
        RESULT_SET_BUILDER(ResultSetBuilder.class);

        private Class<?> type;

        SSCacheRegion(Class<?> type) {
            this.type = type;
        }

        @Override
        public boolean supports(Class<?> type) {
            return this.type.isAssignableFrom(type);
        }
    }

}
