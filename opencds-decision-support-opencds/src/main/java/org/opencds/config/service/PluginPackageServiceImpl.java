package org.opencds.config.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.opencds.common.cache.CacheRegion;
import org.opencds.common.exceptions.OpenCDSConfigurationException;
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
import org.opencds.plugin.api.OpencdsPlugin;
import org.opencds.plugin.api.PluginContext;

public class PluginPackageServiceImpl implements PluginPackageService
{
    private static final CacheRegion<PPId, PluginPackage> PLUGIN_PACKAGE_BY_PP_ID =
            CacheRegion.create(PPId.class, PluginPackage.class);
    private static final CacheRegion<PluginId, PluginPackage> PLUGIN_PACKAGE_BY_PLUGIN_ID =
            CacheRegion.create(PluginId.class, PluginPackage.class);
    private static final CacheRegion<PluginId, OpencdsPlugin> PLUGIN_CLASS =
            CacheRegion.create(PluginId.class, OpencdsPlugin.class);

    private final PluginPackageDao pluginPackageDao;
    private final CacheService cacheService;

    public PluginPackageServiceImpl(final PluginPackageDao pluginPackageDao, final FileDao fileDao, final CacheService cacheService)
    {
        this.pluginPackageDao = pluginPackageDao;
        this.cacheService = cacheService;
        cacheService.putAll(PLUGIN_PACKAGE_BY_PP_ID, buildPairs(pluginPackageDao.getAll()));
        cacheService.putAll(PLUGIN_PACKAGE_BY_PLUGIN_ID, buildPairs(cacheService.getAllValues(PLUGIN_PACKAGE_BY_PP_ID)));
        cacheService.putAll(PLUGIN_CLASS, loadClasses(cacheService.getAllKeys(PLUGIN_PACKAGE_BY_PLUGIN_ID)));
    }

    @Override
    public PluginPackage find(final PPId identifier)
    {
        return pluginPackageDao.find(identifier);
    }

    @Override
    public <CTX extends PluginContext, OP extends OpencdsPlugin<CTX>> OP load(final PluginId pluginId)
    {
        OP opencdsPlugin;
        final var pp = find(pluginId);
        if (pp != null && pp.getLoadContext() == LoadContext.CLASSPATH)
        {
            opencdsPlugin = (OP) cacheService.get(PLUGIN_CLASS, pluginId);
            if (opencdsPlugin == null)
            {
                try
                {
                    final var plugin = pp.getPlugin(pluginId);
                    if (plugin == null)
                        throw new OpenCDSConfigurationException("Plugin not found in configuration: " + pluginId);
                    opencdsPlugin = (OP) Class.forName(plugin.getClassName()).getDeclaredConstructor().newInstance();
                }
                catch (final InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException |
                             InvocationTargetException e)
                {
                    throw new OpenCDSConfigurationException(
                            "Unable to load plugin '" + pluginId + "' due to exception: " + e.getMessage(), e);
                }
            }
        }
        else
        {
            throw new OpenCDSRuntimeException("Unable to load plugin '" + pluginId
                    + "'; class is not on classpath (IMPORTED plugins are unsupported at this time)");
        }
        return opencdsPlugin;
    }

    @Override
    public PluginPackage find(final PluginId pluginId)
    {
        return cacheService.get(PLUGIN_PACKAGE_BY_PLUGIN_ID, pluginId);
    }

    @Override
    public List<PluginPackage> getAll()
    {
        return List.copyOf(cacheService.getAllValues(PLUGIN_PACKAGE_BY_PP_ID));
    }

    @Override
    public void persist(final PluginPackage pp)
    {
        pluginPackageDao.persist(pp);
        cacheService.put(PLUGIN_PACKAGE_BY_PP_ID, pp.getIdentifier(), pp);

    }

    @Override
    public void persist(final List<PluginPackage> pluginPackages)
    {
        pluginPackageDao.persist(pluginPackages);
        cacheService.putAll(PLUGIN_PACKAGE_BY_PP_ID, buildPairs(pluginPackages));
    }

    @Override
    public void delete(final PPId ppId)
    {
        final var pp = find(ppId);
        if (pp != null)
        {
            pluginPackageDao.delete(pp);
            cacheService.evict(PLUGIN_PACKAGE_BY_PP_ID, pp.getIdentifier());
        }
    }

    @Override
    public List<PluginId> getAllPluginIds()
    {
        return List.copyOf(cacheService.getAllKeys(PLUGIN_PACKAGE_BY_PLUGIN_ID));
    }

    private Map<PPId, PluginPackage> buildPairs(final List<PluginPackage> pluginPackages)
    {
        return pluginPackages.stream()
                .map(pp -> Map.entry(pp.getIdentifier(), pp))
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<PluginId, PluginPackage> buildPairs(final Set<PluginPackage> pluginPackages)
    {
        return pluginPackages.stream()
                .flatMap(pp -> pp.getPlugins().stream().map(Plugin::getIdentifier).map(identifier -> Map.entry(identifier, pp)))
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private <CTX extends PluginContext, OP extends OpencdsPlugin<CTX>> Map<PluginId, OP> loadClasses(final Set<PluginId> set)
    {
        return set.stream()
                .map(pluginId -> Map.<PluginId, OP>entry(pluginId, load(pluginId)))
                .collect(Collectors.<Map.Entry<PluginId, OP>, PluginId, OP>toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
