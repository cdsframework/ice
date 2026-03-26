package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opencds.config.api.model.PrePostProcessPluginId;

public record PrePostProcessPluginIdImpl(String scopingEntityId,
                                         String businessId,
                                         String version,
                                         List<String> supportingDataIdentifier) implements PrePostProcessPluginId
{
    public static PrePostProcessPluginId create(final String scopingEntityId, final String businessId, final String version,
            final List<String> supportingDataIdentifier)
    {
        return new PrePostProcessPluginIdImpl(scopingEntityId, businessId, version, supportingDataIdentifier);
    }

    public static PrePostProcessPluginId create(final PrePostProcessPluginId pppid)
    {
        if (pppid == null)
            return null;
        return create(pppid.getScopingEntityId(), pppid.getBusinessId(), pppid.getVersion(), pppid.getSupportingDataIdentifiers());
    }

    public static List<PrePostProcessPluginId> create(final List<PrePostProcessPluginId> prePostProcPlugins)
    {
        if (prePostProcPlugins == null)
            return null;
        final var list = new ArrayList<PrePostProcessPluginId>();
        for (final var id : prePostProcPlugins)
            list.add(create(id));
        return list;
    }

    public PrePostProcessPluginIdImpl
    {
        assert StringUtils.isNotBlank(scopingEntityId);
        assert StringUtils.isNotBlank(businessId);
        assert StringUtils.isNotBlank(version);
        supportingDataIdentifier =
                supportingDataIdentifier == null ? Collections.emptyList() : Collections.unmodifiableList(supportingDataIdentifier);
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

    @Override
    public List<String> getSupportingDataIdentifiers()
    {
        return supportingDataIdentifier;
    }
}
