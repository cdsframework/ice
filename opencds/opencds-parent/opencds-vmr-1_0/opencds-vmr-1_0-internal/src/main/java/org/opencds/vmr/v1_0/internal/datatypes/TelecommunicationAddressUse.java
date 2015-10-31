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



/**
 * <p>Java class for TelecommunicationAddressUse.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 */

public enum TelecommunicationAddressUse {


    /**
     * Home address : A communication address at a home, attempted contacts for business purposes might intrude privacy and chances are one will contact family or other household members instead of the person one wishes to call. Typically used with urgent cases, or if no other contacts are available
     * 
     */
    H,

    /**
     * Primary Home : The primary home, to reach a person after business hours 
     * 
     */
    HP,

    /**
     * Vacation Home : A vacation home, to reach a person while on vacation 
     * 
     */
    HV,

    /**
     * Work Place : An office address. First choice for business related contacts during business hours 
     * 
     */
    WP,

    /**
     * Direct : Indicates a work place address or telecommunication address that reaches the individual or organization directly without intermediaries. For phones, often referred to as a 'private line' 
     * 
     */
    DIR,

    /**
     * Public : Indicates a work place address or telecommunication address that is a 'standard' address which may reach a reception service, mail-room, or other intermediary prior to the target entity 
     * 
     */
    PUB,

    /**
     * Bad address : A flag indicating that the address is bad, in fact, useless 
     * 
     */
    BAD,

    /**
     * Temporary address : A temporary address, may be good for visit or mailing. Note that an address history can provide more detailed information 
     * 
     */
    TMP,

    /**
     * Answering Service : An automated answering machine used for less urgent cases and if the main purpose of contact is to leave a message or access an automated announcement
     * 
     */
    AS,

    /**
     * Emergency Contact : A contact specifically designated to be used for emergencies. This is the first choice in emergencies, independent of any other use codes 
     * 
     */
    EC,

    /**
     * Mobile Contact : A telecommunication device that moves and stays with its owner. May have characteristics of all other use codes, suitable for urgent matters, not the first choice for routine business 
     * 
     */
    MC,

    /**
     * Pager: A paging device suitable to solicit a callback or to leave a very short message 
     * 
     */
    PG;

    public String value() {
        return name();
    }

    public static TelecommunicationAddressUse fromValue(String v) {
        return valueOf(v);
    }

}
