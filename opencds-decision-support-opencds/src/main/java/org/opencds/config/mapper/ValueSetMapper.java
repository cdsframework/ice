package org.opencds.config.mapper;

import org.opencds.config.api.model.ValueSet;

public abstract class ValueSetMapper
{
    public static ValueSet internal(final org.opencds.config.schema.ValueSet external)
    {
        if (external == null)
            return null;

        return new ValueSet(external.getOid(), external.getName());
    }

    public static org.opencds.config.schema.ValueSet external(final ValueSet internal)
    {
        if (internal == null)
            return null;

        final org.opencds.config.schema.ValueSet external = new org.opencds.config.schema.ValueSet();
        external.setOid(internal.oid());
        external.setName(internal.name());

        return external;
    }
}
