package org.opencds.vmr.v1_0.internal.datatypes;

public enum EntityNamePartType
{
    FAM,
    GIV,
    TITLE,
    DEL;

    public static EntityNamePartType fromValue(final String v)
    {
        return valueOf(v);
    }

    public String value()
    {
        return name();
    }
}
