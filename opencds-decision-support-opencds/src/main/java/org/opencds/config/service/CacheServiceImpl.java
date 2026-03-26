package org.opencds.config.service;

import java.util.Map;
import java.util.Set;

import org.opencds.common.cache.CacheRegion;
import org.opencds.common.cache.OpencdsCache;
import org.opencds.config.api.cache.CacheService;

public class CacheServiceImpl implements CacheService
{
    private final OpencdsCache opencdsCache;

    public CacheServiceImpl()
    {
        this.opencdsCache = new OpencdsCache();
    }

    @Override
    public <K, V> boolean containsKey(final CacheRegion<K, V> cacheRegion, final K key)
    {
        return opencdsCache.containsKey(cacheRegion, key);
    }

    @Override
    public <K, V> V get(final CacheRegion<K, V> cacheRegion, final K key)
    {
        return opencdsCache.get(cacheRegion, key);
    }

    @Override
    public <K, V> Map<K, V> getAll(final CacheRegion<K, V> cacheRegion)
    {
        return opencdsCache.getCache(cacheRegion);
    }

    @Override
    public <K, V> Set<K> getAllKeys(final CacheRegion<K, V> cacheRegion)
    {
        return opencdsCache.getCacheKeys(cacheRegion);
    }

    @Override
    public <K, V> Set<V> getAllValues(final CacheRegion<K, V> cacheRegion)
    {
        return opencdsCache.getCacheValues(cacheRegion);
    }

    @Override
    public <K, V> void put(final CacheRegion<K, V> cacheRegion, final K key, final V cachable)
    {
        opencdsCache.put(cacheRegion, key, cachable);
    }

    @Override
    public <K, V> void putAll(final CacheRegion<K, V> cacheRegion, final Map<K, V> cacheables)
    {
        cacheables.entrySet()
                .stream()
                .filter(e -> e.getKey() != null && e.getValue() != null)
                .forEach(e -> opencdsCache.put(cacheRegion, e.getKey(), e.getValue()));
    }

    @Override
    public <K, V> void evict(final CacheRegion<K, V> cacheRegion, final K key)
    {
        opencdsCache.evict(cacheRegion, key);
    }

    @Override
    public <K, V> void evictAll(final CacheRegion<K, V> cacheRegion)
    {
        opencdsCache.evictAll(cacheRegion);
    }
}
