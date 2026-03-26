package org.opencds.config.api.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PrePostProcessPluginId;

public class PluginIdComparator
{
    public static List<PluginIdTuple> intersect(final List<PrePostProcessPluginId> prePostProcessPlugins,
            final List<PluginId> plugins)
    {
        return prePostProcessPlugins.stream()
                .map(pppid -> Optional.of(match(plugins, pppid))
                        .map(pluginId -> PluginIdTuple.create(pluginId, pppid))
                        .orElse(null))
                .collect(Collectors.toList());
    }

    public static PluginId match(final List<PluginId> pluginIds, final PrePostProcessPluginId prePostProcessPluginId)
    {
        return pluginIds.stream().filter(pluginId -> compare(prePostProcessPluginId, pluginId)).findFirst().orElse(null);
    }

    public static boolean compare(final PrePostProcessPluginId prePostProcessPlugin, final PluginId plugin)
    {
        return prePostProcessPlugin.getScopingEntityId().equals(plugin.getScopingEntityId()) && prePostProcessPlugin.getBusinessId()
                .equals(plugin.getBusinessId()) && prePostProcessPlugin.getVersion().equals(plugin.getVersion());
    }
}
