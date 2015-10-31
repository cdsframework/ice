package org.opencds.config.migrate.cache;

import java.util.List;

import org.opencds.common.cache.OpencdsCache.CacheRegion;

public enum ConfigCacheRegion implements CacheRegion {
    METADATA(List.class),
    DATA(List.class);

    private Class<?> supportedType;

    private ConfigCacheRegion(Class<?> supportedType) {
        this.supportedType = supportedType;
    }

    @Override
    public boolean supports(Class<?> type) {
        return supportedType.isAssignableFrom(type);
    }
}