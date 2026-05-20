package org.opencds.config.mapper;

import java.util.List;

import org.opencds.config.api.model.TraitId;
import org.opencds.config.schema.EntityIdentifier;

public abstract class TraitIdMapper
{
    public static TraitId internal(final EntityIdentifier external)
    {
        if (external == null)
            return null;

        return new TraitId(external.getScopingEntityId(), external.getBusinessId(), external.getVersion());
    }

    public static List<TraitId> internal(final List<EntityIdentifier> external)
    {
        if (external == null)
            return null;

        return external.stream().map(TraitIdMapper::internal).toList();
    }

    public static EntityIdentifier external(final TraitId internal)
    {
        if (internal == null)
            return null;

        final EntityIdentifier external = new EntityIdentifier();
        external.setBusinessId(internal.businessId());
        external.setScopingEntityId(internal.scopingEntityId());
        external.setVersion(internal.version());
        return external;
    }

    public static List<EntityIdentifier> external(final List<TraitId> internal)
    {
        if (internal == null)
            return null;

        return internal.stream().map(TraitIdMapper::external).toList();
    }
}
