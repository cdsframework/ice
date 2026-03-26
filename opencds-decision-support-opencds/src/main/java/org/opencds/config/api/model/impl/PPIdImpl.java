package org.opencds.config.api.model.impl;

import org.opencds.config.api.model.PPId;

public record PPIdImpl(String scopingEntityId,
                       String businessId,
                       String version) implements PPId
{
    public static PPIdImpl create(final String scopingEntityId, final String businessId, final String version)
    {
        return new PPIdImpl(scopingEntityId, businessId, version);
    }

    public static PPIdImpl create(final PPId pluginPackageid)
    {
        if (pluginPackageid == null)
            return null;
        if (pluginPackageid instanceof final PPIdImpl ppIdImpl)
            return ppIdImpl;
        return create(pluginPackageid.getScopingEntityId(), pluginPackageid.getBusinessId(), pluginPackageid.getVersion());
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
