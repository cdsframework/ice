package org.opencds.config.mapper;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.Plugin;
import org.opencds.config.api.model.impl.PluginImpl;
import org.opencds.config.schema.PluginPackage.Plugins;

public class PluginMapper
{
    public static Plugin internal(final org.opencds.config.schema.Plugin external)
    {
        if (external == null)
            return null;
        return PluginImpl.create(PluginIdMapper.internal(external.getIdentifier()), external.getClassName());
    }

    public static List<Plugin> internal(final Plugins external)
    {
        if (external == null)
            return null;
        final List<Plugin> plugins = new ArrayList<>();
        for (final org.opencds.config.schema.Plugin plugin : external.getPlugin())
            plugins.add(internal(plugin));
        return plugins;
    }

    private static org.opencds.config.schema.Plugin external(final Plugin internal)
    {
        if (internal == null)
            return null;
        final org.opencds.config.schema.Plugin plugin = new org.opencds.config.schema.Plugin();
        plugin.setIdentifier(PluginIdMapper.external(internal.getIdentifier()));
        plugin.setClassName(internal.getClassName());
        return plugin;
    }

    public static Plugins external(final List<Plugin> external)
    {
        if (external == null)
            return null;
        final Plugins plugins = new Plugins();
        for (final Plugin plugin : external)
            plugins.getPlugin().add(external(plugin));
        return plugins;
    }
}
