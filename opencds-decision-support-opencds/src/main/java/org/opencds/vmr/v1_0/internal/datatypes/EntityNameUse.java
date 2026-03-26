package org.opencds.vmr.v1_0.internal.datatypes;

public enum EntityNameUse
{
    ABC,
    SYL,
    IDE,
    C,
    OR,
    T,
    I,
    P,
    ANON,
    A,
    R,
    OLD,
    DN,
    M,
    SRCH,
    PHON;

    public static EntityNameUse fromValue(final String v)
    {
        return valueOf(v);
    }

    public String value()
    {
        return name();
    }
}
