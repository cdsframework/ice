package org.opencds.config.mapper;

import java.util.List;

import org.opencds.config.api.model.Plugin;
import org.opencds.config.schema.PluginPackage.Plugins;

public class PluginMapper
{
    public static Plugin internal(final org.opencds.config.schema.Plugin external)
    {
        if (external == null)
            return null;

        return new Plugin(PluginIdMapper.internal(external.getIdentifier()), external.getClassName());
    }

    public static List<Plugin> internal(final Plugins external)
    {
        if (external == null)
            return null;

        return external.getPlugin().stream().map(PluginMapper::internal).toList();
    }

    private static org.opencds.config.schema.Plugin external(final Plugin internal)
    {
        if (internal == null)
            return null;

        final org.opencds.config.schema.Plugin plugin = new org.opencds.config.schema.Plugin();
        plugin.setIdentifier(PluginIdMapper.external(internal.identifier()));
        plugin.setClassName(internal.className());
        return plugin;
    }

    public static Plugins external(final List<Plugin> external)
    {
        if (external == null)
            return null;

        final Plugins plugins = new Plugins();

        external.stream().map(PluginMapper::external).forEach(plugins.getPlugin()::add);

        return plugins;
    }
}
