package org.opencds.config.api.cache;

import java.util.Map;
import java.util.Set;

import org.opencds.common.cache.CacheRegion;

public interface CacheService
{
    <K, V> boolean containsKey(CacheRegion<K, V> cacheRegion, K key);

    <K, V> V get(CacheRegion<K, V> cacheRegion, K key);

    <K, V> Map<K, V> getAll(CacheRegion<K, V> cacheRegion);

    <K, V> Set<K> getAllKeys(CacheRegion<K, V> cacheRegion);

    <K, V> Set<V> getAllValues(CacheRegion<K, V> cacheRegion);

    <K, V> void put(CacheRegion<K, V> cacheRegion, K key, V cachable);

    <K, V> void putAll(CacheRegion<K, V> cacheRegion, Map<K, V> cachables);

    <K, V> void evict(CacheRegion<K, V> cacheRegion, K key);

    <K, V> void evictAll(CacheRegion<K, V> cacheRegion);
}
