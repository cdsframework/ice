/**
 * Copyright 2011 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */

package org.opencds.vmr.v1_0.internal.datatypes;

import javax.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for TelecommunicationCapability.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 */

public enum TelecommunicationCapability {


    /**
     * Voice : This device can receive voice calls (i.e. talking to another person, or a recording device, or a voice activated computer)
     * 
     */
    @XmlEnumValue("voice")
    VOICE("voice"),

    /**
     * Fax : This device can receive faxes.
     * 
     */
    @XmlEnumValue("fax")
    FAX("fax"),

    /**
     * Data : This device can receive data calls (i.e. modem)
     * 
     */
    @XmlEnumValue("data")
    DATA("data"),

    /**
     * Text : This device is a text telephone.
     * 
     */
    @XmlEnumValue("tty")
    TTY("tty"),

    /**
     * SMS : This device can receive SMS messages
     * 
     */
    @XmlEnumValue("sms")
    SMS("sms");
    private final String value;

    TelecommunicationCapability(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TelecommunicationCapability fromValue(String v) {
        for (TelecommunicationCapability c: TelecommunicationCapability.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
