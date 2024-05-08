/*
 * Copyright 2013-2020 OpenCDS.org
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

package org.opencds.common.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OpencdsCache {
	private static final Log log = LogFactory.getLog(OpencdsCache.class);

	public interface CacheRegion {
		public boolean supports(Class<?> type);
	}

	private ConcurrentMap<CacheRegion, ConcurrentMap<Object, Object>> cache = new ConcurrentHashMap<>();

	public <K> K getCacheKey(CacheRegion cacheRegion, K key){
    	ensureRegionExists(cacheRegion);
    	K existingKey = null;
    	for (Object k : cache.get(cacheRegion).keySet()) {
    		if (k.equals(key)) {
    			existingKey = (K)k;
    		}
    	}
		return existingKey;
    }

	public <T> Set<T> getCacheKeys(CacheRegion cacheRegion) {
		ensureRegionExists(cacheRegion);
		ConcurrentMap<Object, Object> regionCache = cache.get(cacheRegion);
		Set<T> keys = new HashSet<>();
		// make a copy then return the copied set.
		Set<?> keySet = regionCache.keySet();
		if (keySet != null) {
			for (Object o : keySet) {
				keys.add((T) o);
			}
		}
		return keys;
	}

	public <T> Set<T> getCacheValues(CacheRegion cacheRegion) {
		ensureRegionExists(cacheRegion);
		ConcurrentMap<Object, Object> regionCache = cache.get(cacheRegion);
		Set<T> values = new HashSet<>();
		// make a copy then return the copied set.
		Collection<Object> keySet = regionCache.values();
		if (keySet != null) {
			for (Object o : keySet) {
				values.add((T) o);
			}
		}
		return values;
	}

	public <T> T get(CacheRegion cacheRegion, Object key) {
		ensureRegionExists(cacheRegion);
		ConcurrentMap<Object, Object> region = cache.get(cacheRegion);
		return (T) region.get(key);
	}

	public void put(CacheRegion cacheRegion, Object key, Object instance) {
		if (!cacheRegion.supports(instance.getClass())) {
			log.warn("This CacheRegion (" + cacheRegion + ") should not support instance type: " + instance.getClass());
		}
		ensureRegionExists(cacheRegion);
		cache.get(cacheRegion).put(key, instance);
	}

	private void ensureRegionExists(CacheRegion cacheRegion) {
		cache.putIfAbsent(cacheRegion, new ConcurrentHashMap<Object, Object>());
	}

	public void evict(CacheRegion cacheRegion, Object key) {
		ensureRegionExists(cacheRegion);
		cache.get(cacheRegion).remove(key);
	}

}
