package org.opencds.common.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpencdsCache
{
    private final ConcurrentMap<CacheRegion<?, ?>, ConcurrentMap<Object, Object>> cache = new ConcurrentHashMap<>();

    public <V, K> boolean containsKey(final CacheRegion<K, V> cacheRegion, final K key)
    {
        return cache.get(cacheRegion).containsKey(key);
    }

    public <K, V> Map<K, V> getCache(final CacheRegion<K, V> cacheRegion)
    {
        ensureRegionExists(cacheRegion);
        return copy(cache.get(cacheRegion));
    }

    public <K, V> K getCacheKey(final CacheRegion<K, V> cacheRegion, final K key)
    {
        ensureRegionExists(cacheRegion);
        return cache.get(cacheRegion).keySet().stream().filter(k -> k.equals(key)).map(k -> (K) k).findFirst().orElse(null);
    }

    public <K, V> Set<K> getCacheKeys(final CacheRegion<K, V> cacheRegion)
    {
        ensureRegionExists(cacheRegion);
        return copy(cache.get(cacheRegion).keySet());
    }

    public <K, V> Set<V> getCacheValues(final CacheRegion<K, V> cacheRegion)
    {
        ensureRegionExists(cacheRegion);
        return copy(cache.get(cacheRegion).values());
    }

    public <K, V> V get(final CacheRegion<K, V> cacheRegion, final K key)
    {
        ensureRegionExists(cacheRegion);
        return (V) cache.get(cacheRegion).get(key);
    }

    public <K, V> void put(final CacheRegion<K, V> cacheRegion, final K key, final V instance)
    {
        if (cacheRegion.supportsValueType(instance.getClass()))
        {
            ensureRegionExists(cacheRegion);
            cache.get(cacheRegion).put(key, instance);
        }
        else
        {
            final var message = "This CacheRegion (" + cacheRegion.getClass().getSimpleName() + "." + cacheRegion
                    + ") should not support instance or subclass of type: " + instance.getClass();
            log.warn(message);
            throw new RuntimeException(message);
        }
    }

    private <V> Set<V> copy(final Collection<Object> coll)
    {
        return coll.stream().map(this::<V>cast).filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
    }

    private <K, V> Map<K, V> copy(final Map<Object, Object> map)
    {
        return map.entrySet()
                .stream()
                .map(e -> Map.entry((K) e.getKey(), (V) e.getValue()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private <V> V cast(final Object o)
    {
        try
        {
            return (V) o;
        }
        catch (final ClassCastException e)
        {
            final var message = "Object is not expected type T: " + o.getClass();
            log.warn(message);
            throw new RuntimeException(message);
        }
    }

    private <K, V> void ensureRegionExists(final CacheRegion<K, V> cacheRegion)
    {
        cache.putIfAbsent(cacheRegion, new ConcurrentHashMap<>());
    }

    public <K, V> void evict(final CacheRegion<K, V> cacheRegion, final K key)
    {
        ensureRegionExists(cacheRegion);
        cache.get(cacheRegion).remove(key);
    }

    public <K, V> void evictAll(final CacheRegion<K, V> cacheRegion)
    {
        ensureRegionExists(cacheRegion);
        cache.get(cacheRegion).clear();
    }
}
