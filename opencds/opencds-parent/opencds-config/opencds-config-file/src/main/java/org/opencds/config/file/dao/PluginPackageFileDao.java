package org.opencds.config.file.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.config.api.dao.PluginPackageDao;
import org.opencds.config.api.dao.util.ResourceUtil;
import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.Plugin;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PluginPackage;
import org.opencds.config.file.dao.support.RestConfigUtil;

public class PluginPackageFileDao implements PluginPackageDao {
	private static final Logger log = LogManager.getLogger();
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
