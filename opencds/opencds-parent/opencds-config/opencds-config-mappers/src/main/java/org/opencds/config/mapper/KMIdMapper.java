package org.opencds.config.mapper;

import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.impl.KMIdImpl;

public abstract class KMIdMapper {

    public static KMId internal(org.opencds.config.schema.KMId external) {
        if (external == null) {
            return null;
        }
        return KMIdImpl.create(external.getScopingEntityId(), external.getBusinessId(), external.getVersion());
    }

    public static org.opencds.config.schema.KMId external(KMId internal) {
        if (internal == null) {
            return null;
        }
        org.opencds.config.schema.KMId external = new org.opencds.config.schema.KMId();
        external.setBusinessId(internal.getBusinessId());
        external.setScopingEntityId(internal.getScopingEntityId());
        external.setVersion(internal.getVersion());
        return external;
    }
}
