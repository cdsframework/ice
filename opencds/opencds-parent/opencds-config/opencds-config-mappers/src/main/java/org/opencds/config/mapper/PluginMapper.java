package org.opencds.config.mapper;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.Plugin;
import org.opencds.config.api.model.impl.PluginImpl;
import org.opencds.config.schema.PluginPackage.Plugins;

public class PluginMapper {

    public static Plugin internal(org.opencds.config.schema.Plugin external) {
        if (external == null) {
            return null;
        }
        return PluginImpl.create(PluginIdMapper.internal(external.getIdentifier()), external.getClassName());
    }

    public static List<Plugin> internal(Plugins external) {
        if (external == null) {
            return null;
        }
        List<Plugin> plugins = new ArrayList<>();
        for (org.opencds.config.schema.Plugin plugin : external.getPlugin()) {
            plugins.add(internal(plugin));
        }
        return plugins;
    }

    private static org.opencds.config.schema.Plugin external(Plugin internal) {
        if (internal == null) {
            return null;
        }
        org.opencds.config.schema.Plugin plugin = new org.opencds.config.schema.Plugin();
        plugin.setIdentifier(PluginIdMapper.external(internal.getIdentifier()));
        plugin.setClassName(internal.getClassName());
        return plugin;
    }

    public static Plugins external(List<Plugin> external) {
        if (external == null) {
            return null;
        }
        Plugins plugins = new Plugins();
        for (Plugin plugin : external) {
            plugins.getPlugin().add(external(plugin));
        }
        return plugins;
    }

}
