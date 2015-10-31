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
