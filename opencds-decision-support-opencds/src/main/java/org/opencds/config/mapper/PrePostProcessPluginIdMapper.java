package org.opencds.config.mapper;

import java.util.List;

import org.opencds.config.api.model.PrePostProcessPluginId;

public class PrePostProcessPluginIdMapper
{
    public static PrePostProcessPluginId internal(final org.opencds.config.schema.PrePostProcessPluginId external)
    {
        if (external == null)
            return null;

        return new PrePostProcessPluginId(external.getScopingEntityId(), external.getBusinessId(), external.getVersion(),
                external.getSupportingDataIdentifier());
    }

    public static List<PrePostProcessPluginId> internal(final List<org.opencds.config.schema.PrePostProcessPluginId> plugins)
    {
        if (plugins == null)
            return null;

        return plugins.stream().map(PrePostProcessPluginIdMapper::internal).toList();
    }

    public static org.opencds.config.schema.PrePostProcessPluginId external(final PrePostProcessPluginId internal)
    {
        if (internal == null)
            return null;

        final org.opencds.config.schema.PrePostProcessPluginId pid = new org.opencds.config.schema.PrePostProcessPluginId();
        pid.setScopingEntityId(internal.scopingEntityId());
        pid.setBusinessId(internal.businessId());
        pid.setVersion(internal.version());
        pid.getSupportingDataIdentifier().addAll(internal.supportingDataIdentifiers());
        return pid;
    }
}
