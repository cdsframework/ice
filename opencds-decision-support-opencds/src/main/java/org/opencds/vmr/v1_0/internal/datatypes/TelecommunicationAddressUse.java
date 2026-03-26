package org.opencds.vmr.v1_0.internal.datatypes;

public enum TelecommunicationAddressUse
{
    H,
    HP,
    HV,
    WP,
    DIR,
    PUB,
    BAD,
    TMP,
    AS,
    EC,
    MC,
    PG;

    public static TelecommunicationAddressUse fromValue(final String v)
    {
        return valueOf(v);
    }

    public String value()
    {
        return name();
    }
}
