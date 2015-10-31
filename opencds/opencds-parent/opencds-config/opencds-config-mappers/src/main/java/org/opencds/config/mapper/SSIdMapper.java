package org.opencds.config.mapper;

import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.impl.SSIdImpl;
import org.opencds.config.schema.SemanticSignifierId;

public abstract class SSIdMapper {

    public static SSId internal(SemanticSignifierId external) {
        if (external == null) {
            return null;
        }
        return SSIdImpl.create(external.getScopingEntityId(), external.getBusinessId(), external.getVersion());
    }

    public static SemanticSignifierId external(SSId internal) {
        if (internal == null) {
            return null;
        }
        SemanticSignifierId external = new SemanticSignifierId();
        external.setBusinessId(internal.getBusinessId());
        external.setScopingEntityId(internal.getScopingEntityId());
        external.setVersion(internal.getVersion());
        return external;
    }

}
