package org.opencds.config.api.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.opencds.common.exceptions.OpenCDSConfigurationException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.utilities.ClassUtil;
import org.opencds.config.api.dao.PluginPackageDao;
import org.opencds.config.api.model.LoadContext;
import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.Plugin;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PluginPackage;
import org.opencds.plugin.api.OpencdsPlugin;
import org.opencds.plugin.api.PluginContext;

public class PluginPackageService
{
    private final PluginPackageDao pluginPackageDao;
    private final Map<PPId, PluginPackage> pluginPackageByPpIdMap;
    private final Map<PluginId, PluginPackage> pluginPackageByPluginIdMap;
    private final Map<PluginId, OpencdsPlugin<? extends PluginContext>> pluginClassMap = new ConcurrentHashMap<>();

    public PluginPackageService(final PluginPackageDao pluginPackageDao)
    {
        this.pluginPackageDao = pluginPackageDao;

        this.pluginPackageByPpIdMap = pluginPackageDao.getAll()
                .stream()
                .map(pp -> Map.entry(pp.identifier(), pp))
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
        this.pluginPackageByPluginIdMap = pluginPackageByPpIdMap.values()
                .stream()
                .flatMap(pp -> pp.plugins().stream().map(Plugin::identifier).map(identifier -> Map.entry(identifier, pp)))
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
        pluginPackageByPluginIdMap.keySet().forEach(this::load);
    }

    public PluginPackage find(final PPId identifier)
    {
        return pluginPackageDao.find(identifier);
    }

    @SuppressWarnings("unchecked")
    public <CTX extends PluginContext> OpencdsPlugin<CTX> load(final PluginId pluginId)
    {
        final var pp = find(pluginId);
        if (pp == null || pp.loadContext() != LoadContext.CLASSPATH)
            throw new OpenCDSRuntimeException(
                    "Unable to load plugin '%s'; class is not on classpath (IMPORTED plugins are unsupported at this time)".formatted(
                            pluginId));

        return (OpencdsPlugin<CTX>) pluginClassMap.computeIfAbsent(pluginId, k ->
        {
            final var plugin = pp.getPlugin(k);
            if (plugin == null)
                throw new OpenCDSConfigurationException("Plugin not found in configuration: " + k);

            try
            {
                return (OpencdsPlugin<CTX>) ClassUtil.newInstance(plugin.className(), OpencdsPlugin.class);
            }
            catch (final RuntimeException e)
            {
                throw new OpenCDSConfigurationException(
                        "Unable to load plugin '%s' due to exception: %s".formatted(k, e.getMessage()), e);
            }
        });
    }

    public PluginPackage find(final PluginId pluginId)
    {
        return pluginPackageByPluginIdMap.get(pluginId);
    }

    public List<PluginPackage> getAll()
    {
        return List.copyOf(pluginPackageByPpIdMap.values());
    }

    public List<PluginId> getAllPluginIds()
    {
        return List.copyOf(pluginPackageByPluginIdMap.keySet());
    }
}
