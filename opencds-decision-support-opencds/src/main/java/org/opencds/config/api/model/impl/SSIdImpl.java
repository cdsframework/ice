package org.opencds.config.api.model.impl;

import org.opencds.config.api.model.EntityIdentifier;
import org.opencds.config.api.model.SSId;

public record SSIdImpl(String scopingEntityId,
                       String businessId,
                       String version) implements SSId
{
    public static SSIdImpl create(final String scopingEntityId, final String businessId, final String version)
    {
        return new SSIdImpl(scopingEntityId, businessId, version);
    }

    public static SSIdImpl create(final SSId ssid)
    {
        if (ssid == null)
            return null;
        if (ssid instanceof final SSIdImpl ssIdImpl)
            return ssIdImpl;
        return create(ssid.getScopingEntityId(), ssid.getBusinessId(), ssid.getVersion());
    }

    public static SSId create(final EntityIdentifier ei)
    {
        return create(ei.getScopingEntityId(), ei.getBusinessId(), ei.getVersion());
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
