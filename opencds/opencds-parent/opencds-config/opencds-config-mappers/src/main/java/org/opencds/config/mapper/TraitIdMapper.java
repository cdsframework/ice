package org.opencds.config.mapper;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.TraitId;
import org.opencds.config.api.model.impl.TraitIdImpl;
import org.opencds.config.schema.EntityIdentifier;

public abstract class TraitIdMapper {

    public static TraitIdImpl internal(EntityIdentifier external) {
        if (external == null) {
            return null;
        }
        return TraitIdImpl.create(external.getScopingEntityId(), external.getBusinessId(), external.getVersion());
    }

    public static List<TraitId> internal(List<EntityIdentifier> external) {
        if (external == null) {
            return null;
        }
        List<TraitId> traitIds = new ArrayList<>();
        for (EntityIdentifier tid : external) {
            traitIds.add(internal(tid));
        }
        return traitIds;
    }
    
    public static EntityIdentifier external(TraitId internal) {
        if (internal == null) {
            return null;
        }
        EntityIdentifier external = new EntityIdentifier();
        external.setBusinessId(internal.getBusinessId());
        external.setScopingEntityId(internal.getScopingEntityId());
        external.setVersion(internal.getVersion());
        return external;
    }
    
    public static List<EntityIdentifier> external(List<TraitId> internal) {
        if (internal == null) {
            return null;
        }
        List<EntityIdentifier> traitids = new ArrayList<>();
        for (TraitId tid : internal) {
            traitids.add(external(tid));
        }
        return traitids;
    }

}
