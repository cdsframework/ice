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

package org.opencds.config.file.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.config.api.dao.PluginPackageDao;
import org.opencds.config.api.dao.util.ResourceUtil;
import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.Plugin;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PluginPackage;
import org.opencds.config.mapper.util.RestConfigUtil;

public class PluginPackageFileDao implements PluginPackageDao {
    private static final Log log = LogFactory.getLog(PluginPackageFileDao.class);
    private final Map<PPId, PluginPackage> cache;
    private final Map<PluginId, PluginPackage> pluginToPackages;
    private RestConfigUtil restConfigUtil;

    public PluginPackageFileDao(ResourceUtil resourceUtil, String path) {
        cache = new HashMap<>();
        restConfigUtil = new RestConfigUtil(); // TODO replace with an injected instance
        log.info("Finding plugin resources in path: " + path);
        pluginToPackages = new HashMap<>();
        List<String> resources = resourceUtil.findFiles(path, false);

        for (String resource : resources) {
            log.info("Loading resource: " + resource);
            PluginPackage pp = restConfigUtil.unmarshalPluginPackage(resourceUtil.getResourceAsStream(resource));
            if (pp != null) {
                cachePlugin(pp);
            } else {
                log.info("Loading resource as PluginPackages (resource was not a PluginPackage instance)");
                List<PluginPackage> ppkgs = restConfigUtil.unmarshalPluginPackages(resourceUtil
                        .getResourceAsStream(resource));
                if (ppkgs != null) {
                    for (PluginPackage ppkg : ppkgs) {
                        cachePlugin(ppkg);
                    }
                }
            }
        }
    }

    private void cachePlugin(PluginPackage pp) {
        cache.put(pp.getIdentifier(), pp);
        for (Plugin p : pp.getPlugins()) {
            pluginToPackages.put(p.getIdentifier(), pp);
        }
    }

    @Override
    public PluginPackage find(PPId ppId) {
        return cache.get(ppId);
    }

    @Override
    public List<PluginPackage> getAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void persist(PluginPackage pluginPackage) {
        throw new UnsupportedOperationException("Cannot persist to file store through dao API");
    }

    @Override
    public void persist(List<PluginPackage> pluginPackages) {
        throw new UnsupportedOperationException("Cannot persist to file store through dao API");
    }

    @Override
    public void delete(PluginPackage pluginPackage) {
        throw new UnsupportedOperationException("Cannot delete from file store through dao API");
    }

}
