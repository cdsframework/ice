package org.opencds.config.api.model;

import java.time.LocalDate;
import java.util.List;

public record PluginPackage(PPId identifier,
                            LoadContext loadContext,
                            String resourceName,
                            List<Plugin> plugins,
                            LocalDate timestamp,
                            String userId)
{
    public Plugin getPlugin(final PluginId pluginId)
    {
        if (plugins == null)
            return null;

        return plugins.stream().filter(p -> p.identifier().equals(pluginId)).findFirst().orElse(null);
    }
}
