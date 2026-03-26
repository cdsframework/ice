package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opencds.config.api.model.PluginId;

public record PluginIdImpl(String scopingEntityId,
                           String businessId,
                           String version) implements PluginId
{
    public static PluginIdImpl create(final String scopingEntityId, final String businessId, final String version)
    {
        return new PluginIdImpl(scopingEntityId, businessId, version);
    }

    public static PluginIdImpl create(final PluginId pid)
    {
        if (pid == null)
            return null;
        if (pid instanceof final PluginIdImpl pluginIdImpl)
            return pluginIdImpl;
        return create(pid.getScopingEntityId(), pid.getBusinessId(), pid.getVersion());
    }

    public static List<PluginId> create(final List<PluginId> pluginIds)
    {
        if (pluginIds == null)
            return null;
        final var pidis = new ArrayList<PluginId>();
        for (final var pid : pluginIds)
            pidis.add(create(pid));
        return pidis;
    }

    public PluginIdImpl
    {
        assert StringUtils.isNotBlank(scopingEntityId);
        assert StringUtils.isNotBlank(businessId);
        assert StringUtils.isNotBlank(version);
    }

    @Override
    public String getScopingEntityId()
    {
        return scopingEntityId;
    }

    @Override
    public String getBusinessId()
    {
        return businessId;
    }

    @Override
    public String getVersion()
    {
        return version;
    }
}
