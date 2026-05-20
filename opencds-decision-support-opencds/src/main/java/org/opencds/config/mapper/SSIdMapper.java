package org.opencds.config.mapper;

import org.opencds.config.api.model.SSId;
import org.opencds.config.schema.SemanticSignifierId;

public abstract class SSIdMapper
{
    public static SSId internal(final SemanticSignifierId external)
    {
        if (external == null)
            return null;

        return new SSId(external.getScopingEntityId(), external.getBusinessId(), external.getVersion());
    }

    public static SemanticSignifierId external(final SSId internal)
    {
        if (internal == null)
            return null;

        final SemanticSignifierId external = new SemanticSignifierId();
        external.setBusinessId(internal.businessId());
        external.setScopingEntityId(internal.scopingEntityId());
        external.setVersion(internal.version());
        return external;
    }
}
