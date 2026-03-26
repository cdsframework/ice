package org.opencds.config.mapper;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.TraitId;
import org.opencds.config.api.model.impl.TraitIdImpl;
import org.opencds.config.schema.EntityIdentifier;

public abstract class TraitIdMapper
{
    public static TraitIdImpl internal(final EntityIdentifier external)
    {
        if (external == null)
            return null;
        return TraitIdImpl.create(external.getScopingEntityId(), external.getBusinessId(), external.getVersion());
    }

    public static List<TraitId> internal(final List<EntityIdentifier> external)
    {
        if (external == null)
            return null;
        final List<TraitId> traitIds = new ArrayList<>();
        for (final EntityIdentifier tid : external)
            traitIds.add(internal(tid));
        return traitIds;
    }

    public static EntityIdentifier external(final TraitId internal)
    {
        if (internal == null)
            return null;
        final EntityIdentifier external = new EntityIdentifier();
        external.setBusinessId(internal.getBusinessId());
        external.setScopingEntityId(internal.getScopingEntityId());
        external.setVersion(internal.getVersion());
        return external;
    }

    public static List<EntityIdentifier> external(final List<TraitId> internal)
    {
        if (internal == null)
            return null;
        final List<EntityIdentifier> traitids = new ArrayList<>();
        for (final TraitId tid : internal)
            traitids.add(external(tid));
        return traitids;
    }
}
