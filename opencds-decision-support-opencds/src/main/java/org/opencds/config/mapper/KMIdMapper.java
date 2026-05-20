package org.opencds.config.mapper;

import org.opencds.config.api.model.KMId;

public abstract class KMIdMapper
{
    public static KMId internal(final org.opencds.config.schema.KMId external)
    {
        if (external == null)
            return null;

        return new KMId(external.getScopingEntityId(), external.getBusinessId(), external.getVersion());
    }

    public static org.opencds.config.schema.KMId external(final KMId internal)
    {
        if (internal == null)
            return null;

        final org.opencds.config.schema.KMId external = new org.opencds.config.schema.KMId();
        external.setBusinessId(internal.businessId());
        external.setScopingEntityId(internal.scopingEntityId());
        external.setVersion(internal.version());
        return external;
    }
}
