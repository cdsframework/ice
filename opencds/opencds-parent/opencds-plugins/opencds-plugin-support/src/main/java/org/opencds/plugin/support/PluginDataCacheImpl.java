/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.plugin.support;

import java.util.Set;

import org.opencds.common.cache.OpencdsCache;
import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.plugin.PluginDataCache;
import org.opencds.plugin.SupportingData;

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
        cache = new OpencdsCache();
    }
    
    @Override
    public <V> V get(SupportingData key) {
    	SupportingData prevKey = cache.getCacheKey(PluginDataCacheRegion.PLUGIN_DATA, key);
    	if (prevKey != null && prevKey.getTimestamp().toInstant().toEpochMilli() != key.getTimestamp().toInstant().toEpochMilli()) {
    		cache.evict(PluginDataCacheRegion.PLUGIN_DATA, prevKey);
    		return null;
    	}
        return cache.get(PluginDataCacheRegion.PLUGIN_DATA, key);
    }
    
    @Override
    public <V> void put(SupportingData key, V value) {
        cache.put(PluginDataCacheRegion.PLUGIN_DATA, key, value);
    }
    
}
