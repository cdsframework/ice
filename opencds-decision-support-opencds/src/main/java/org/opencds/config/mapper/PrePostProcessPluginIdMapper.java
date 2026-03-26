package org.opencds.config.mapper;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.PrePostProcessPluginId;
import org.opencds.config.api.model.impl.PrePostProcessPluginIdImpl;

public class PrePostProcessPluginIdMapper
{
    public static PrePostProcessPluginId internal(final org.opencds.config.schema.PrePostProcessPluginId external)
    {
        if (external == null)
            return null;
        return PrePostProcessPluginIdImpl.create(external.getScopingEntityId(), external.getBusinessId(), external.getVersion(),
                external.getSupportingDataIdentifier());
    }

    public static List<PrePostProcessPluginId> internal(final List<org.opencds.config.schema.PrePostProcessPluginId> plugins)
    {
        if (plugins == null)
            return null;
        final List<PrePostProcessPluginId> pids = new ArrayList<>();
        for (final org.opencds.config.schema.PrePostProcessPluginId pid : plugins)
            pids.add(internal(pid));
        return pids;
    }

    public static org.opencds.config.schema.PrePostProcessPluginId external(final PrePostProcessPluginId internal)
    {
        if (internal == null)
            return null;
        final org.opencds.config.schema.PrePostProcessPluginId pid = new org.opencds.config.schema.PrePostProcessPluginId();
        pid.setScopingEntityId(internal.getScopingEntityId());
        pid.setBusinessId(internal.getBusinessId());
        pid.setVersion(internal.getVersion());
        pid.getSupportingDataIdentifier().addAll(internal.getSupportingDataIdentifiers());
        return pid;
    }
}
