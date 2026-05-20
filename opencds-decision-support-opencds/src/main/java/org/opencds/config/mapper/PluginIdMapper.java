package org.opencds.config.mapper;

import java.util.List;

import org.opencds.config.api.model.PluginId;

public class PluginIdMapper
{
    public static PluginId internal(final org.opencds.config.schema.PluginId external)
    {
        if (external == null)
            return null;

        return new PluginId(external.getScopingEntityId(), external.getBusinessId(), external.getVersion());
    }

    public static List<PluginId> internal(final List<org.opencds.config.schema.PluginId> plugins)
    {
        if (plugins == null)
            return null;

        return plugins.stream().map(PluginIdMapper::internal).toList();
    }

    public static org.opencds.config.schema.PluginId external(final PluginId internal)
    {
        if (internal == null)
            return null;

        final org.opencds.config.schema.PluginId pid = new org.opencds.config.schema.PluginId();
        pid.setScopingEntityId(internal.scopingEntityId());
        pid.setBusinessId(internal.businessId());
        pid.setVersion(internal.version());
        return pid;
    }
}
