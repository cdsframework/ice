package org.opencds.plugin.support;

import org.opencds.common.cache.OpencdsCache;
import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.plugin.PluginDataCache;

public final class PluginDataCacheImpl implements PluginDataCache {
    
    private enum PluginDataCacheRegion implements CacheRegion {
        PLUGIN_DATA(Object.class);

        private Class<?> supportedType;

        PluginDataCacheRegion(Class<?> supportedType) {
            this.supportedType = supportedType;
        }
        
        @Override
        public boolean supports(Class<?> type) {
            return supportedType.isAssignableFrom(type);
        }
        
    }
    
    private OpencdsCache cache;
    
    public PluginDataCacheImpl() {
        this.cache = new OpencdsCache();
    }
    
    @Override
    public <K, V> V get(K key) {
        return cache.get(PluginDataCacheRegion.PLUGIN_DATA, key);
    }
    
    @Override
    public <K, V> void put(K key, V value) {
        cache.put(PluginDataCacheRegion.PLUGIN_DATA, key, value);
    }
    
}
