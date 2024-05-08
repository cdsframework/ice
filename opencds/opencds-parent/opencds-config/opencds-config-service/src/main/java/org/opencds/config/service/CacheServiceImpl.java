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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.opencds.common.cache.OpencdsCache;
import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.config.api.cache.CacheService;

public class CacheServiceImpl implements CacheService {

    private OpencdsCache opencdsCache;

    public CacheServiceImpl() {
        this.opencdsCache = new OpencdsCache();
    }
    
    @Override
    public <T> T get(CacheRegion cacheRegion, Object key) {
        return opencdsCache.get(cacheRegion, key);
    }
    
    @Override
    public <T> Set<T> getAll(CacheRegion cacheRegion) {
        return opencdsCache.getCacheValues(cacheRegion);
    }

    @Override
    public <T> Set<T> getAllKeys(CacheRegion cacheRegion) {
        return opencdsCache.getCacheKeys(cacheRegion);
    }

    @Override
    public void put(CacheRegion cacheRegion, Object key, Object cachable) {
        opencdsCache.put(cacheRegion, key, cachable);
    }

    @Override
    public void putAll(CacheRegion cacheRegion, Map<?, ?> cachables) {
        cachables.entrySet().stream().filter(e -> e.getKey() != null && e.getValue() != null)
                .forEach(e -> opencdsCache.put(cacheRegion, e.getKey(), e.getValue()));
    }

    @Override
    public void evict(CacheRegion cacheRegion, Object key) {
        opencdsCache.evict(cacheRegion, key);
    }

}
