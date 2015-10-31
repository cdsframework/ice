package org.opencds.config.mapper;

import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.impl.CDMIdImpl;
import org.opencds.config.schema.ConceptDeterminationMethodBase;

public abstract class CDMIdMapper {

    public static CDMId internal(org.opencds.config.schema.ConceptDeterminationMethodBase external) {
        if (external == null) {
            return null;
        }
        return CDMIdImpl.create(external.getCodeSystem(), external.getCode(), external.getVersion());
    }

    public static ConceptDeterminationMethodBase external(CDMId internal) {
        if (internal == null) {
            return null;
        }
        ConceptDeterminationMethodBase external = new ConceptDeterminationMethodBase();
        external.setCode(internal.getCode());
        external.setCodeSystem(internal.getCodeSystem());
        external.setVersion(internal.getVersion());
        return external;
    }
}
