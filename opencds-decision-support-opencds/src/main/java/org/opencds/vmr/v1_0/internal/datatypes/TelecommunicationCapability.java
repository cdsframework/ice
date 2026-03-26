package org.opencds.vmr.v1_0.internal.datatypes;

import jakarta.xml.bind.annotation.XmlEnumValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TelecommunicationCapability
{
    @XmlEnumValue("voice")
    VOICE("voice"),
    @XmlEnumValue("fax")
    FAX("fax"),
    @XmlEnumValue("data")
    DATA("data"),
    @XmlEnumValue("tty")
    TTY("tty"),
    @XmlEnumValue("sms")
    SMS("sms");

    public static TelecommunicationCapability fromValue(final String v)
    {
        for (final TelecommunicationCapability c : TelecommunicationCapability.values())
        {
            if (c.value.equals(v))
                return c;
        }
        throw new IllegalArgumentException(v);
    }

    private final String value;

    public String value()
    {
        return value;
    }
}
