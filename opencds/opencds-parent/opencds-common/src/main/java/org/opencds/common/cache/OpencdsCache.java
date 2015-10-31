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
