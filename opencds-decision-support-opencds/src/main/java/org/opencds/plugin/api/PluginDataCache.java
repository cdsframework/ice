package org.opencds.plugin.api;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class PluginDataCache
{
    private final Map<SupportingData, Object> pluginDataMap = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <V> V get(final SupportingData key)
    {
        //noinspection ConstantValue
        return (V) pluginDataMap.computeIfPresent(key, (k, v) -> Objects.equals(k.timestamp(), key.timestamp()) ? v : null);
    }

    public <V> void put(final SupportingData key, final V value)
    {
        pluginDataMap.put(key, value);
    }
}
