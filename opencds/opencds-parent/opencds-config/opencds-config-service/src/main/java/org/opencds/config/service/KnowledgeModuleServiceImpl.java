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

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.KnowledgeModuleDao;
import org.opencds.config.api.model.EntityIdentifier;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.impl.KMIdImpl;
import org.opencds.config.api.service.KnowledgeModuleService;
import org.opencds.config.api.service.KnowledgePackageService;
import org.opencds.config.api.service.SupportingDataService;
import org.opencds.config.api.util.EntityIdentifierUtil;

import com.google.common.collect.ImmutableList;

public class KnowledgeModuleServiceImpl extends Observable implements KnowledgeModuleService {
	private final KnowledgeModuleDao dao;
	private final KnowledgePackageService knowledgePackageService;
	private final SupportingDataService supportingDataService;
	private CacheService cacheService;

	public KnowledgeModuleServiceImpl(KnowledgeModuleDao dao, KnowledgePackageService knowledgePackageService,
			SupportingDataService supportingDataService, CacheService cacheService) {
		this.dao = dao;
		this.knowledgePackageService = knowledgePackageService;
		this.supportingDataService = supportingDataService;
		this.cacheService = cacheService;
		Map<KMId, KnowledgeModule> knowledgeModules = buildPairs(this.dao.getAll());
		cacheService.putAll(KMCacheRegion.KNOWLEDGE_MODULE, knowledgeModules);
	}

	@Override
	public KnowledgeModule find(KMId kmId) {
		return cacheService.get(KMCacheRegion.KNOWLEDGE_MODULE, kmId);
	}

	@Override
	public KnowledgeModule find(String stringKmId) {
		EntityIdentifier ei = EntityIdentifierUtil.makeEI(stringKmId);
		return find(KMIdImpl.create(ei));
	}

	/**
	 * Retrieve the first KnowledgeModule that matches the predicate.
	 */
	@Override
	public KnowledgeModule find(Predicate<? super KnowledgeModule> predicate) {
		return cacheService.<KnowledgeModule>getAll(KMCacheRegion.KNOWLEDGE_MODULE).stream()
				.filter(predicate)
				.findFirst()
				.orElse(null);
	}

	public List<KnowledgeModule> getAll(Predicate<? super KnowledgeModule> predicate) {
		return cacheService.<KnowledgeModule>getAll(KMCacheRegion.KNOWLEDGE_MODULE).stream()
				.filter(predicate)
				.collect(Collectors.toList());
	}

	@Override
	public List<KnowledgeModule> getAll() {
		Set<KnowledgeModule> kms = cacheService.getAll(KMCacheRegion.KNOWLEDGE_MODULE);
		return ImmutableList.<KnowledgeModule>builder().addAll(kms).build();
	}

	@Override
	public void persist(KnowledgeModule km) {
		dao.persist(km);
		cacheService.put(KMCacheRegion.KNOWLEDGE_MODULE, km.getKMId(), km);
		// will create or update the ConceptService for this KM
		tellObservers(km);
	}

	@Override
	public void persist(List<KnowledgeModule> internal) {
		for (KnowledgeModule km : internal) {
			persist(km);
		}
	}

	@Override
	public void delete(KMId kmid) {
		KnowledgeModule km = find(kmid);
		if (km != null) {
			dao.delete(km);
			cacheService.evict(KMCacheRegion.KNOWLEDGE_MODULE, km.getKMId());
			knowledgePackageService.deletePackage(km);
			supportingDataService.deleteAll(km.getKMId());
			// will delete the cached ConceptService associated with this KM
			tellObservers(km);
		}
	}

	@Override
	public InputStream getKnowledgePackage(KMId kmId) {
		return Optional.ofNullable(find(kmId))
				.map(knowledgePackageService::getPackageInputStream)
				.orElse(null);
	}

	@Override
	public void deleteKnowledgePackage(KMId kmId) {
		KnowledgeModule km = find(kmId);
		if (km != null) {
			knowledgePackageService.deletePackage(km);
		}
	}

	@Override
	public void persistKnowledgePackage(KMId kmId, InputStream knowledgePackage) {
		KnowledgeModule km = find(kmId);
		if (km != null) {
			knowledgePackageService.persistPackageInputStream(km, knowledgePackage);
		}
	}

	private void tellObservers(KnowledgeModule km) {
		setChanged();
		notifyObservers(km);
	}

	private Map<KMId, KnowledgeModule> buildPairs(List<KnowledgeModule> all) {
		Map<KMId, KnowledgeModule> cachables = new HashMap<>();
		for (KnowledgeModule km : all) {
			cachables.put(km.getKMId(), km);
		}
		return cachables;
	}

	private enum KMCacheRegion implements CacheRegion {
		KNOWLEDGE_MODULE(KnowledgeModule.class);

		private Class<?> type;

		KMCacheRegion(Class<?> type) {
			this.type = type;
		}

		@Override
		public boolean supports(Class<?> type) {
			return this.type.isAssignableFrom(type);
		}
	}

}
