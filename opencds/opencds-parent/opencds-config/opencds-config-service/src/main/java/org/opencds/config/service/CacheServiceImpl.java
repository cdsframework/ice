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
        for (Entry<?, ?> cachable : cachables.entrySet()) {
            opencdsCache.put(cacheRegion, cachable.getKey(), cachable.getValue());
        }
    }

    @Override
    public void evict(CacheRegion cacheRegion, Object key) {
        opencdsCache.evict(cacheRegion, key);
    }

}
