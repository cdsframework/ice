package org.opencds.config.api.model.impl;

import org.apache.commons.lang3.StringUtils;
import org.opencds.config.api.model.EntityIdentifier;
import org.opencds.config.api.model.KMId;

public record KMIdImpl(String scopingEntityId,
                       String businessId,
                       String version) implements KMId
{
    public static KMIdImpl create(final String scopingEntityId, final String businessId, final String version)
    {
        return new KMIdImpl(scopingEntityId, businessId, version);
    }

    public static KMIdImpl create(final KMId kmid)
    {
        if (kmid == null)
            return null;
        if (kmid instanceof final KMIdImpl kmIdImpl)
            return kmIdImpl;
        return create(kmid.getScopingEntityId(), kmid.getBusinessId(), kmid.getVersion());
    }

    public static KMId create(final EntityIdentifier ei)
    {
        return create(ei.getScopingEntityId(), ei.getBusinessId(), ei.getVersion());
    }

    public KMIdImpl
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
