package org.opencds.vmr.v1_0.internal.datatypes;

public enum PostalAddressUse
{
    H,
    HP,
    HV,
    WP,
    DIR,
    PUB,
    BAD,
    PHYS,
    PST,
    TMP,
    ABC,
    IDE,
    SYL,
    SRCH,
    SNDX,
    PHON;

    public static PostalAddressUse fromValue(final String v)
    {
        return valueOf(v);
    }

    public String value()
    {
        return name();
    }
}
