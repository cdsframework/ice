package org.opencds.config.api.model.impl;

import org.apache.commons.lang3.StringUtils;
import org.opencds.config.api.model.ValueSet;

public record ValueSetImpl(String oid,
                           String name) implements ValueSet
{
    public static ValueSetImpl create(final String oid, final String name)
    {
        return new ValueSetImpl(oid, name);
    }

    public static ValueSetImpl create(final ValueSet valueSet)
    {
        if (valueSet == null)
            return null;
        if (valueSet instanceof final ValueSetImpl valueSetImpl)
            return valueSetImpl;
        return create(valueSet.getOid(), valueSet.getName());
    }

    public ValueSetImpl
    {
        assert StringUtils.isNotBlank(oid);
    }

    @Override
    public String getOid()
    {
        return oid;
    }

    @Override
    public String getName()
    {
        return name;
    }
}
