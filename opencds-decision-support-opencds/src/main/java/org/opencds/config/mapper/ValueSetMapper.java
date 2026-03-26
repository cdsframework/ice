package org.opencds.config.mapper;

import org.opencds.config.api.model.ValueSet;
import org.opencds.config.api.model.impl.ValueSetImpl;

public abstract class ValueSetMapper
{
    public static ValueSet internal(final org.opencds.config.schema.ValueSet external)
    {
        if (external == null)
            return null;
        return ValueSetImpl.create(external.getOid(), external.getName());
    }

    public static org.opencds.config.schema.ValueSet external(final ValueSet internal)
    {
        if (internal == null)
            return null;

        final org.opencds.config.schema.ValueSet external = new org.opencds.config.schema.ValueSet();
        external.setOid(internal.getOid());
        external.setName(internal.getName());

        return external;
    }
}
