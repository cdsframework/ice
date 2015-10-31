package org.opencds.config.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.service.PluginDataCacheService;
import org.opencds.plugin.PluginDataCache;
import org.opencds.plugin.support.PluginDataCacheImpl;

public class PluginDataCacheServiceImpl implements PluginDataCacheService {

    private CacheService cacheService;

    public PluginDataCacheServiceImpl(CacheService cacheService, List<PluginId> pluginIds) {
        this.cacheService = cacheService;
        this.cacheService.putAll(PluginDataCacheRegion.PLUGIN_DATA, buildPairs(pluginIds));
    }

    @Override
    public PluginDataCache getPluginDataCache(PluginId pluginId) {
        return cacheService.get(PluginDataCacheRegion.PLUGIN_DATA, pluginId);
    }
    
    private Map<?, ?> buildPairs(List<PluginId> pluginIds) {
        Map<PluginId, PluginDataCache> pluginsToCache = new HashMap<>();
        for (PluginId pluginId : pluginIds) {
            pluginsToCache.put(pluginId, new PluginDataCacheImpl());
        }
        return pluginsToCache;
    }

    private static enum PluginDataCacheRegion implements CacheRegion {
        PLUGIN_DATA(PluginDataCache.class);

        private Class<?> type;

        PluginDataCacheRegion(Class<?> type) {
            this.type = type;
        }

        @Override
        public boolean supports(Class<?> type) {
            return this.type.isAssignableFrom(type);
        }
    }

}
