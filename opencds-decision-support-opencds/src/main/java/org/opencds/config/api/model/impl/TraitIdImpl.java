package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.TraitId;

public record TraitIdImpl(String scopingEntityId,
                          String businessId,
                          String version) implements TraitId
{
    public static TraitIdImpl create(final String scopingEntityId, final String businessId, final String version)
    {
        return new TraitIdImpl(scopingEntityId, businessId, version);
    }

    public static TraitIdImpl create(final TraitId traitId)
    {
        if (traitId == null)
            return null;
        if (traitId instanceof final TraitIdImpl traitIdImpl)
            return traitIdImpl;
        return create(traitId.getScopingEntityId(), traitId.getBusinessId(), traitId.getVersion());
    }

    public static List<TraitId> create(final List<TraitId> traitIds)
    {
        if (traitIds == null)
            return null;
        final var tids = new ArrayList<TraitId>();
        for (final var tid : traitIds)
            tids.add(create(tid));
        return tids;
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
