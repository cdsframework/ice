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

package org.opencds.config.api.cache;

import java.util.Map;
import java.util.Set;

import org.opencds.common.cache.OpencdsCache.CacheRegion;

public interface CacheService {

    // TODO: need the ability to refresh the cache
    <T> T get(CacheRegion cacheRegion, Object key);

    <T> Set<T> getAll(CacheRegion cacheRegion);
    
    <T> Set<T> getAllKeys(CacheRegion cacheRegion);
    
    void put(CacheRegion cacheRegion, Object key, Object cachable);

    void putAll(CacheRegion cacheRegion, Map<?, ?> cachables);

    void evict(CacheRegion cacheRegion, Object key);
    
}
