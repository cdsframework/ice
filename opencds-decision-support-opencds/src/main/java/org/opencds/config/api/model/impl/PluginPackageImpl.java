package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.opencds.config.api.model.LoadContext;
import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.Plugin;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PluginPackage;

public record PluginPackageImpl(PPId identifier,
                                LoadContext loadContext,
                                String resourceName,
                                List<Plugin> plugins,
                                Date timestamp,
                                String userId) implements PluginPackage
{
    public static PluginPackageImpl create(final PPId identifier, final LoadContext loadContext, final String resourceName,
            final List<Plugin> plugins, final Date timestamp, final String userId)
    {
        return new PluginPackageImpl(PPIdImpl.create(identifier), loadContext, resourceName, PluginImpl.create(plugins), timestamp,
                userId);
    }

    public static PluginPackageImpl create(final PluginPackage pp)
    {
        if (pp == null)
            return null;
        if (pp instanceof final PluginPackageImpl pluginPackageImpl)
            return pluginPackageImpl;
        return create(pp.getIdentifier(), pp.getLoadContext(), pp.getResourceName(), pp.getPlugins(), pp.getTimestamp(),
                pp.getUserId());
    }

    public static List<PluginPackageImpl> create(final List<PluginPackage> pps)
    {
        if (pps == null)
            return null;
        final var ppis = new ArrayList<PluginPackageImpl>();
        for (final var pp : pps)
            ppis.add(create(pp));
        return ppis;
    }

    public PluginPackageImpl
    {
        assert identifier != null;
        assert loadContext != null;
        assert plugins != null && !plugins.isEmpty();
        assert timestamp != null;
        plugins = Collections.unmodifiableList(plugins);
    }

    @Override
    public PPId getIdentifier()
    {
        return identifier;
    }

    @Override
    public LoadContext getLoadContext()
    {
        return loadContext;
    }

    @Override
    public String getResourceName()
    {
        return resourceName;
    }

    @Override
    public List<Plugin> getPlugins()
    {
        return plugins;
    }

    @Override
    public Plugin getPlugin(final PluginId pluginId)
    {
        if (plugins == null)
            return null;
        for (final Plugin p : plugins)
        {
            if (p.getIdentifier().equals(pluginId))
                return p;
        }
        return null;
    }

    @Override
    public Date getTimestamp()
    {
        return timestamp;
    }

    @Override
    public String getUserId()
    {
        return userId;
    }
}
