package org.opencds.config.mapper;

import org.opencds.config.api.model.CDMId;
import org.opencds.config.schema.ConceptDeterminationMethodBase;

public abstract class CDMIdMapper
{
    public static CDMId internal(final ConceptDeterminationMethodBase external)
    {
        if (external == null)
            return null;

        return new CDMId(external.getCodeSystem(), external.getCode(), external.getVersion());
    }

    public static ConceptDeterminationMethodBase external(final CDMId internal)
    {
        if (internal == null)
            return null;

        final ConceptDeterminationMethodBase external = new ConceptDeterminationMethodBase();
        external.setCode(internal.code());
        external.setCodeSystem(internal.codeSystem());
        external.setVersion(internal.version());
        return external;
    }
}
