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

package org.opencds.config.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.FileDao;
import org.opencds.config.api.dao.PluginPackageDao;
import org.opencds.config.api.model.LoadContext;
import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.Plugin;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PluginPackage;
import org.opencds.config.api.service.PluginPackageService;
import org.opencds.plugin.OpencdsPlugin;
import org.opencds.plugin.PluginContext;

import com.google.common.collect.ImmutableList;

public class PluginPackageServiceImpl implements PluginPackageService {
    private static final Log log = LogFactory.getLog(PluginPackageServiceImpl.class);

    private final PluginPackageDao pluginPackageDao;
    private final FileDao fileDao;
    private final CacheService cacheService;

    public PluginPackageServiceImpl(PluginPackageDao pluginPackageDao, FileDao fileDao, CacheService cacheService) {
        this.pluginPackageDao = pluginPackageDao;
        this.fileDao = fileDao;
        this.cacheService = cacheService;
        this.cacheService.putAll(PPCacheRegion.PLUGIN_PACKAGE, buildPairs(this.pluginPackageDao.getAll()));
        this.cacheService.putAll(PPCacheRegion.PLUGIN, buildPairs(cacheService.getAll(PPCacheRegion.PLUGIN_PACKAGE)));
        this.cacheService.putAll(PPCacheRegion.PLUGIN_CLASS, loadClasses(cacheService.getAllKeys(PPCacheRegion.PLUGIN)));
    }

    @Override
    public PluginPackage find(PPId identifier) {
        return pluginPackageDao.find(identifier);
    }

    @Override
    public <PC extends PluginContext> OpencdsPlugin<PC> load(PluginId pluginId) {
        OpencdsPlugin<PC> op = null;
        PluginPackage pp = find(pluginId);
        if (pp.getLoadContext() == LoadContext.CLASSPATH) {
            // already on classpath, we'll return a new instance
            op = cacheService.get(PPCacheRegion.PLUGIN_CLASS, pluginId);
            if (op == null) {
                try {
                    Plugin plugin = pp.getPlugin(pluginId);
                    if (plugin == null) {
                        log.error("Plugin not found in configuration: " + pluginId);
                    }
                    op = OpencdsPlugin.class.cast(Class.forName(plugin.getClassName()).newInstance());
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    log.error("Unable to load plugin '" + pluginId + "' due to exception: "
                            + e.getMessage(), e);
                }
            }
        } else {
            throw new OpenCDSRuntimeException("Unable to load plugin '" + pluginId
                    + "'; class is not on classpath (IMPORTED plugins are unsupported at this time)");
        }
        return op;
    }

    @Override
    public PluginPackage find(PluginId pluginId) {
        return cacheService.get(PPCacheRegion.PLUGIN, pluginId);
    }

    @Override
    public List<PluginPackage> getAll() {
        Set<PluginPackage> pps = cacheService.getAll(PPCacheRegion.PLUGIN_PACKAGE);
        return ImmutableList.<PluginPackage> builder().addAll(pps).build();
    }

    @Override
    public void persist(PluginPackage pp) {
        pluginPackageDao.persist(pp);
        cacheService.put(PPCacheRegion.PLUGIN_PACKAGE, pp.getIdentifier(), pp);

    }

    @Override
    public void persist(List<PluginPackage> pluginPackages) {
        pluginPackageDao.persist(pluginPackages);
        cacheService.putAll(PPCacheRegion.PLUGIN_PACKAGE, buildPairs(pluginPackages));
    }

    @Override
    public void delete(PPId ppId) {
        PluginPackage pp = find(ppId);
        if (pp != null) {
            pluginPackageDao.delete(pp);
            cacheService.evict(PPCacheRegion.PLUGIN_PACKAGE, pp);
        }
    }

    @Override
    public InputStream getJar(PPId ppId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void persistJar(PPId ppId, InputStream jar) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteJar(PPId ppId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PluginId> getAllPluginIds() {
        Set<PluginId> pluginIds = cacheService.getAllKeys(PPCacheRegion.PLUGIN);
        return ImmutableList.<PluginId> builder().addAll(pluginIds).build();
    }

    private Map<PPId, PluginPackage> buildPairs(List<PluginPackage> pluginPackages) {
        Map<PPId, PluginPackage> cachables = new HashMap<>();
        for (PluginPackage pp : pluginPackages) {
            cachables.put(pp.getIdentifier(), pp);
        }
        return cachables;
    }

    private Map<PluginId, PluginPackage> buildPairs(Set<Object> pluginPacakges) {
        Map<PluginId, PluginPackage> pluginsToPackage = new HashMap<>();
        for (Object obj : pluginPacakges) {
            PluginPackage pp = PluginPackage.class.cast(obj);
            for (Plugin plugin : pp.getPlugins()) {
                pluginsToPackage.put(plugin.getIdentifier(), pp);
            }
        }
        return pluginsToPackage;
    }

    private Map<PluginId, ?> loadClasses(Set<Object> set) {
        Map<PluginId, Object> instances = new HashMap<>();
        for (Object obj : set) {
            PluginId pluginId = PluginId.class.cast(obj);
            instances.put(pluginId, load(pluginId));
        }
        return instances;
    }

    private enum PPCacheRegion implements CacheRegion {
        PLUGIN_PACKAGE(PluginPackage.class),
        PLUGIN(Plugin.class),
        PLUGIN_CLASS(Object.class);

        private Class<?> type;

        PPCacheRegion(Class<?> type) {
            this.type = type;
        }

        @Override
        public boolean supports(Class<?> type) {
            return this.type.isAssignableFrom(type);
        }
    }

}
