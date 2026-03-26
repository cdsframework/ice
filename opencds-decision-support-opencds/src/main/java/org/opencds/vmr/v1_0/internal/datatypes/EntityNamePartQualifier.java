package org.opencds.vmr.v1_0.internal.datatypes;

public enum EntityNamePartQualifier
{
    LS,
    AC,
    NB,
    PR,
    HON,
    BR,
    AD,
    SP,
    MID,
    CL,
    IN,
    PFX,
    SFX;

    public static EntityNamePartQualifier fromValue(final String v)
    {
        return valueOf(v);
    }

    public String value()
    {
        return name();
    }
}
