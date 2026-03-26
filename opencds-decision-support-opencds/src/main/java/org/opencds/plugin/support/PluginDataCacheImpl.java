package org.opencds.plugin.support;

import org.opencds.common.cache.CacheRegion;
import org.opencds.common.cache.OpencdsCache;
import org.opencds.plugin.api.PluginDataCache;
import org.opencds.plugin.api.SupportingData;

public final class PluginDataCacheImpl implements PluginDataCache
{
    private static final CacheRegion<SupportingData, Object> PLUGIN_DATA = CacheRegion.create(SupportingData.class, Object.class);

    private final OpencdsCache cache = new OpencdsCache();

    @Override
    public <V> V get(final SupportingData key)
    {
        final SupportingData prevKey = cache.getCacheKey(PLUGIN_DATA, key);
        if (prevKey != null && prevKey.getTimestamp().toInstant().toEpochMilli() != key.getTimestamp().toInstant().toEpochMilli())
            cache.evict(PLUGIN_DATA, prevKey);
        return (V) cache.get(PLUGIN_DATA, key);
    }

    @Override
    public <V> void put(final SupportingData key, final V value)
    {
        cache.put(PLUGIN_DATA, key, value);
    }
}
