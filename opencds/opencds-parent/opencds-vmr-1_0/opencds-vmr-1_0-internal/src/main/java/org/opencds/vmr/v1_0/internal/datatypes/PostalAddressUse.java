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
 * <p>Java class for PostalAddressUse.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 */

public enum PostalAddressUse {


    /**
     * Home address : A communication address at a home, attempted contacts for business purposes might intrude privacy and chances are one will contact family or other household members instead of the person one wishes to call. Typically used with urgent cases, or if no other contacts are available
     * 
     */
    H,

    /**
     * Primary home : The primary home, to reach a person after business hours 
     * 
     */
    HP,

    /**
     * Vacation home : A vacation home, to reach a person while on vacation 
     * 
     */
    HV,

    /**
     * Work place : An office address. First choice for business related contacts during business hours 
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
     * Physical Visit Address : Used primarily to visit an address
     * 
     */
    PHYS,

    /**
     * Postal Address : Used to send mail
     * 
     */
    PST,

    /**
     * Temporary Address : A temporary address, may be good for visit or mailing. Note that an address history can provide more detailed information.
     * 
     */
    TMP,

    /**
     * Alphabetic : Alphabetic transcription of name (Japanese: romaji) 
     * 
     */
    ABC,

    /**
     * Ideographic : Ideographic representation of name (e.g., Japanese kanji, Chinese characters) 
     * 
     */
    IDE,

    /**
     * Syllabic : Syllabic transcription of name (e.g., Japanese kana, Korean hangul) 
     * 
     */
    SYL,

    /**
     * Search Type Uses : A name intended for use in searching or matching.
     * 
     */
    SRCH,

    /**
     * Soundex : An address spelled according to the SoundEx algorithm
     * 
     */
    SNDX,

    /**
     * Phonetic : The address as understood by the data enterer, i.e. a close approximation of a phonetic spelling of the address, not based on a phonetic algorithm
     * 
     */
    PHON;

    public String value() {
        return name();
    }

    public static PostalAddressUse fromValue(String v) {
        return valueOf(v);
    }

}
