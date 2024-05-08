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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.SupportingDataDao;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.api.model.impl.SupportingDataImpl;
import org.opencds.config.api.service.SupportingDataPackageService;
import org.opencds.config.api.service.SupportingDataService;

public class SupportingDataServiceImpl implements SupportingDataService {
	private static final Log log = LogFactory.getLog(SupportingDataService.class);

	private final SupportingDataDao dao;
	private final SupportingDataPackageService supportingDataPackageService;
	private final PluginPackageServiceImpl pluginPackageService;
	private final CacheService cacheService;

	/******
	 * Need to support this like all the others---build a cache of the
	 * metadata...
	 * 
	 * @param dao
	 * @param supportingDataPackageService
	 * @param pluginPackageService
	 * @param cacheService
	 */
	public SupportingDataServiceImpl(SupportingDataDao dao, SupportingDataPackageService supportingDataPackageService,
			PluginPackageServiceImpl pluginPackageService, CacheService cacheService) {
		this.dao = dao;
		this.supportingDataPackageService = supportingDataPackageService;
		this.pluginPackageService = pluginPackageService;
		this.cacheService = cacheService;
		Map<String, SupportingData> sds = buildPairs(this.dao.getAll());
		cacheService.putAll(SDCacheRegion.SUPPORTING_DATA, sds);
	}

	@Override
	public SupportingData find(String supportingDataId) {
		Set<SupportingData> sds = cacheService.getAll(SDCacheRegion.SUPPORTING_DATA);
		for (SupportingData sd : sds) {
			if (sd.getIdentifier().equals(supportingDataId)) {
				return sd;
			}
		}
		return null;
	}

	@Override
	public SupportingData find(KMId kmId, String supportingDataId) {
		Set<SupportingData> sds = cacheService.getAll(SDCacheRegion.SUPPORTING_DATA);
		for (SupportingData sd : sds) {
			if (sd.getIdentifier().equals(supportingDataId) && kmId.equals(sd.getKMId())) {
				return sd;
			}
		}
		return null;
	}

	@Override
	public List<SupportingData> find(KMId kmid) {
		List<SupportingData> sdList = new ArrayList<>();
		Set<SupportingData> sds = cacheService.getAll(SDCacheRegion.SUPPORTING_DATA);
		for (SupportingData sd : sds) {
			if (kmid.equals(sd.getKMId())) {
				sdList.add(sd);
			}
		}
		return sdList;
	}

	@Override
	public List<SupportingData> getAll() {
		List<SupportingData> sdList = new ArrayList<>();
		Set<SupportingData> sds = cacheService.getAll(SDCacheRegion.SUPPORTING_DATA);
		log.debug("SDS: " + sds);
		for (SupportingData sd : sds) {
			sdList.add(sd);
		}
		return sdList;
	}

	@Override
	public void persist(SupportingData sd) {
		dao.persist(sd);
		cacheService.put(SDCacheRegion.SUPPORTING_DATA, sd.getIdentifier(), sd);
	}

	@Deprecated
	@Override
	public void delete(KMId kmId, String identifier) {
		SupportingData sd = dao.find(kmId, identifier);
		if (sd != null) {
			deleteInternal(kmId, sd);
		}
	}

	@Override
	public void delete(String identifier) {
		SupportingData sd = dao.find(identifier);
		if (sd != null) {
			deleteInternal(sd);
		}
	}

	@Override
	public void deleteAll(KMId kmId) {
		List<SupportingData> sds = find(kmId);
		for (SupportingData sd : sds) {
			deleteInternal(sd);
		}
	}

	@Deprecated
	private void deleteInternal(KMId kmId, SupportingData sd) {
		if (sd != null) {
			dao.delete(sd);
			deleteSupportingDataPackageInternal(sd);
			cacheService.evict(SDCacheRegion.SUPPORTING_DATA, sd);
		}
	}

	private void deleteInternal(SupportingData sd) {
		if (sd != null) {
			dao.delete(sd);
			deleteSupportingDataPackageInternal(sd);
			cacheService.evict(SDCacheRegion.SUPPORTING_DATA, sd);
		}
	}

	@Deprecated
	@Override
	public InputStream getSupportingDataPackage(KMId kmId, String supportingDataId) {
		SupportingData sd = find(kmId, supportingDataId);
		if (sd != null) {
			return supportingDataPackageService.getPackageInputStream(sd);
		}
		return null;
	}

	@Override
	public InputStream getSupportingDataPackage(String supportingDataId) {
		SupportingData sd = find(supportingDataId);
		if (sd != null) {
			return supportingDataPackageService.getPackageInputStream(sd);
		}
		return null;
	}

	@Deprecated
	@Override
	public boolean packageExists(KMId kmId, String supportingDataId) {
		SupportingData sd = find(kmId, supportingDataId);
		if (sd != null) {
			return supportingDataPackageService.exists(sd);
		}
		return false;
	}

	@Override
	public boolean packageExists(String supportingDataId) {
		SupportingData sd = find(supportingDataId);
		if (sd != null) {
			return supportingDataPackageService.exists(sd);
		}
		return false;
	}

	@Deprecated
	@Override
	public void persistSupportingDataPackage(KMId kmId, String identifier, InputStream supportingDataPackage) {
		SupportingData sd = find(kmId, identifier);
		if (sd != null) {
			sd = updateTimestamp(sd);
			supportingDataPackageService.persistPackageInputStream(sd, supportingDataPackage);
			// TODO: Anything else? Error conditions?
		}
	}

	@Override
	public void persistSupportingDataPackage(String identifier, InputStream supportingDataPackage) {
		SupportingData sd = find(identifier);
		if (sd != null) {
			sd = updateTimestamp(sd);
			supportingDataPackageService.persistPackageInputStream(sd, supportingDataPackage);
			// TODO: Anything else? Error conditions?
		}
	}

	@Deprecated
	@Override
	public void deleteSupportingDataPackage(KMId kmId, String identifier) {
		SupportingData sd = find(kmId, identifier);
		if (sd != null) {
			sd = updateTimestamp(sd);
			deleteSupportingDataPackageInternal(sd);
		}
	}

	@Override
	public void deleteSupportingDataPackage(String identifier) {
		SupportingData sd = find(identifier);
		if (sd != null) {
			sd = updateTimestamp(sd);
			deleteSupportingDataPackageInternal(sd);
		}
	}

	private void deleteSupportingDataPackageInternal(SupportingData sd) {
		if (sd != null) {
			sd = updateTimestamp(sd);
			supportingDataPackageService.deletePackage(sd);
		}
	}
	
	private SupportingData updateTimestamp(SupportingData sd) {
		SupportingData sdNew = SupportingDataImpl.create(sd.getIdentifier(), sd.getKMId(), sd.getPackageType(), sd.getPackageId(),
				sd.getLoadedBy(), new Date(), sd.getUserId());
		persist(sdNew);
		return sdNew;
	}

	private Map<String, SupportingData> buildPairs(List<SupportingData> sds) {
		Map<String, SupportingData> cachables = new HashMap<>();
		for (SupportingData sd : sds) {
			cachables.put(sd.getIdentifier(), sd);
		}
		log.debug("CACHABLE SDS: " + cachables);
		return cachables;
	}

	private enum SDCacheRegion implements CacheRegion {
		SUPPORTING_DATA(SupportingData.class);

		private Class<?> type;

		SDCacheRegion(Class<?> type) {
			this.type = type;
		}

		@Override
		public boolean supports(Class<?> type) {
			return this.type.isAssignableFrom(type);
		}

	}

}
