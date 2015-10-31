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
 *	
 */

package org.opencds.vmr.v1_0.internal.datatypes;


/**
 * <p>Java class for AddressPartType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 */

public enum AddressPartType {


    /**
     * Address Line : An address line is for either an additional locator, a delivery address or a street address. An address generally has only a delivery address line or a street address line, but not both
     * 
     */
    AL,

    /**
     * Additional Locator : This can be a unit designator, such as apartment number, suite number, or floor. There may be several unit designators in an address (e.g., "3rd floor, Appt. 342"). This can also be a designator pointing away from the location, rather than specifying a smaller location within some larger one (e.g., Dutch "t.o." means "opposite to" for house boats located across the street facing houses)
     * 
     */
    ADL,

    /**
     * Unit Identifier : The number or name of a specific unit contained within a building or complex, as assigned by that building or complex
     * 
     */
    UNID,

    /**
     * Unit Designator : Indicates the type of specific unit contained within a building or complex. E.g. Apartment, Floor 
     * 
     */
    UNIT,

    /**
     * Delivery Address Line : A delivery address line is frequently used instead of breaking out delivery mode, delivery installation, etc. An address generally has only a delivery address line or a street address line, but not both.
     * 
     */
    DAL,

    /**
     * Delivery Installation Type : Indicates the type of delivery installation (the facility to which the mail will be delivered prior to final shipping via the delivery mode.) Example: post office, letter carrier depot, community mail center, station, etc.
     * 
     */
    DINST,

    /**
     * Delivery Installation Area : The location of the delivery installation, usually a town or city, and is only required if the area is different from the municipality. Area to which mail delivery service is provided from any postal facility or service such as an individual letter carrier, rural route, or postal route
     * 
     */
    DINSTA,

    /**
     * Delivery Installation Qualifier : A number, letter or name identifying a delivery installation. E.g., for Station A, the delivery installation qualifier would be 'A'.
     * 
     */
    DINSTQ,

    /**
     * Delivery Mode : Indicates the type of service offered, method of delivery. For example: post office box, rural route, general delivery, etc.
     * 
     */
    DMOD,

    /**
     * Delivery Mode Identifier: Represents the routing information such as a letter carrier route number. It is the identifying number of the designator (the box number or rural route number)
     * 
     */
    DMODID,

    /**
     * Street Address Line : A street address line is frequently used instead of breaking out build number, street name, street type, etc. An address generally has only a delivery address line or a street address line, but not both.
     * 
     */
    SAL,

    /**
     * Building Number : The number of a building, house or lot alongside the street. Also known as "primary street number". This does not number the street but rather the building
     * 
     */
    BNR,

    /**
     * Building Number Numeric : The numeric portion of a building number 
     * 
     */
    BNN,

    /**
     * Building Number Suffix : Any alphabetic character, fraction or other text that may appear after the numeric portion of a building number 
     * 
     */
    BNS,

    /**
     * Street Name : The name of the street, including the type
     * 
     */
    STR,

    /**
     * Street Name Base : The base name of a roadway or artery recognized by a municipality (excluding street type and direction) 
     * 
     */
    STB,

    /**
     * Street Type : The designation given to the street. (e.g. Street, Avenue, Crescent, etc.) 
     * 
     */
    STTYP,

    /**
     * Direction : Direction (e.g., N, S, W, E) 
     * 
     */
    DIR,

    /**
     * Intersection : An intersection denotes that the actual address is located at or close to the intersection of two or more streets
     * 
     */
    INT,

    /**
     * Care of : The name of the party who will take receipt at the specified address, and will take on responsibility for ensuring delivery to the target recipient.
     * 
     * Note: This is included only to support the convention of writing c/- address lines. This item is not appropriate for use when information is entrusted to one party on behalf of another in some significant way.
     * 
     */
    CAR,

    /**
     * Census Tract : A geographic sub-unit delineated for demographic purposes 
     * 
     */
    CEN,

    /**
     * Country : Country
     * 
     */
    CNT,

    /**
     * County or Parish : A sub-unit of a state or province. (49 of the United States of America use the term "county;" Louisiana uses the term "parish") 
     * 
     */
    CPA,

    /**
     * Municipality : The name of the city, town, village, or other community or delivery center 
     * 
     */
    CTY,

    /**
     * Delimiter : Delimiters are printed without framing white space. If no value component is provided, the delimiter appears as a line break
     * 
     */
    DEL,

    /**
     * Post box : A numbered box located in a post station 
     * 
     */
    POB,

    /**
     * Precinct : A subsection of a municipality 
     * 
     */
    PRE,

    /**
     * State or Province : A sub-unit of a country with limited sovereignty in a federally organized country
     * 
     */
    STA,

    /**
     * Postal Code : A postal code designating a region defined by the postal service
     * 
     */
    ZIP,

    /**
     * Delivery Point Identifier : A value that uniquely identifies the postal address
     * 
     */
    DPID;

    public String value() {
        return name();
    }

    public static AddressPartType fromValue(String v) {
        return valueOf(v);
    }

}
