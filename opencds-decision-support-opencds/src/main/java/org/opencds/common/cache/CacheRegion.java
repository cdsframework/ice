package org.opencds.common.cache;

import java.util.UUID;

public record CacheRegion<K, V>(Class<K> keyType,
                                Class<V> valueType,
                                UUID uuid)
{
    public static <K, V> CacheRegion<K, V> create(final Class<K> keyType, final Class<V> valueType)
    {
        return new CacheRegion<>(keyType, valueType, UUID.randomUUID());
    }

    public CacheRegion
    {
        uuid = UUID.randomUUID();
    }

    boolean supportsValueType(final Class<?> type)
    {
        return valueType().isAssignableFrom(type);
    }
}
