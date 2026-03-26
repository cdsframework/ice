package org.opencds.vmr.v1_0.internal.datatypes;

public enum AddressPartType
{
    AL,
    ADL,
    UNID,
    UNIT,
    DAL,
    DINST,
    DINSTA,
    DINSTQ,
    DMOD,
    DMODID,
    SAL,
    BNR,
    BNN,
    BNS,
    STR,
    STB,
    STTYP,
    DIR,
    INT,
    CAR,
    CEN,
    CNT,
    CPA,
    CTY,
    DEL,
    POB,
    PRE,
    STA,
    ZIP,
    DPID;

    public static AddressPartType fromValue(final String v)
    {
        return valueOf(v);
    }

    public String value()
    {
        return name();
    }
}
