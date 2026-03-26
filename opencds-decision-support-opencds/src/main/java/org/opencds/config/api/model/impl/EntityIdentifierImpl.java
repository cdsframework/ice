package org.opencds.config.api.model.impl;

import org.apache.commons.lang3.StringUtils;
import org.opencds.config.api.model.EntityIdentifier;

public record EntityIdentifierImpl(String scopingEntityId,
                                   String businessId,
                                   String version) implements EntityIdentifier
{
    public static EntityIdentifierImpl create(final String scopingEntityId, final String businessId, final String version)
    {
        return new EntityIdentifierImpl(scopingEntityId, businessId, version);
    }

    public static EntityIdentifierImpl create(final EntityIdentifier kmid)
    {
        if (kmid == null)
            return null;
        if (kmid instanceof final EntityIdentifierImpl entityIdentifierImpl)
            return entityIdentifierImpl;
        return create(kmid.getScopingEntityId(), kmid.getBusinessId(), kmid.getVersion());
    }

    public EntityIdentifierImpl
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
