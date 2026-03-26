package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opencds.config.api.model.Plugin;
import org.opencds.config.api.model.PluginId;

public record PluginImpl(PluginId identifier,
                         String className) implements Plugin
{
    public static PluginImpl create(final PluginId identifier, final String className)
    {
        return new PluginImpl(PluginIdImpl.create(identifier), className);
    }

    public static PluginImpl create(final Plugin pp)
    {
        if (pp == null)
            return null;
        if (pp instanceof final PluginImpl pluginImpl)
            return pluginImpl;
        return create(pp.getIdentifier(), pp.getClassName());
    }

    public static List<Plugin> create(final List<Plugin> plugins)
    {
        if (plugins == null)
            return null;
        final var pis = new ArrayList<Plugin>();
        for (final var p : plugins)
            pis.add(create(p));
        return pis;
    }

    public PluginImpl
    {
        assert identifier != null;
        assert StringUtils.isNotBlank(className);
    }

    @Override
    public PluginId getIdentifier()
    {
        return identifier;
    }

    @Override
    public String getClassName()
    {
        return className;
    }
}
