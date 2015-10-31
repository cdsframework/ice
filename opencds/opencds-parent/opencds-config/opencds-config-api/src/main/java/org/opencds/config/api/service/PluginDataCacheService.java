package org.opencds.config.api.service;

import org.opencds.config.api.model.PluginId;
import org.opencds.plugin.PluginDataCache;

public interface PluginDataCacheService {

    PluginDataCache getPluginDataCache(PluginId pluginId);

}
