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
 * <p>Java class for EntityNameUse.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 */

public enum EntityNameUse {


    /**
     * Alphabetic : Alphabetic transcription of name (Japanese: romaji) 
     * 
     */
    ABC,

    /**
     * Syllabic : Syllabic transcription of name (e.g., Japanese kana, Korean hangul) 
     * 
     */
    SYL,

    /**
     * Ideographic : Ideographic representation of name (e.g., Japanese kanji, Chinese characters) 
     * 
     */
    IDE,

    /**
     * Customary : Known as/conventional/the one you normally use
     * 
     */
    C,

    /**
     * Official Registry Name : the formal name as registered in an official (government) registry, but which name might not be commonly used. May correspond to the concept of legal name
     * 
     */
    OR,

    /**
     * Temporary : A temporary name.  Note that a name valid time can provide more detailed information.  This may also be used for temporary names assigned at birth or in emergency situations.
     * 
     */
    T,

    /**
     * Indigenous/Tribal : e.g. Chief Red Cloud 
     * 
     */
    I,

    /**
     * Other/Pseudonym/Alias: A non-official name by which the person is sometimes known.  (This may also be used to record informal names such as a nickname)
     * 
     */
    P,

    /**
     * Anonymous : Anonymous assigned name (used to protect a person's identity for privacy reasons)
     * 
     */
    ANON,

    /**
     * Business Name : A name used in a Professional or Business context .
     *   
     * Examples: Continuing to use a maiden name in a professional context, or using a stage performing name (some of these names are also pseudonyms)
     * 
     */
    A,

    /**
     * Religious : A name assumed as part of a religious vocation. e.g. Sister Mary Francis, Brother John 
     * 
     */
    R,

    /**
     * No Longer in Use : This name is no longer in use (note: Names may also carry valid time ranges .  This code is used to cover the situations where it is known that the name is no longer valid, but no particular time range for its use is known)
     * 
     */
    OLD,

    /**
     * Do Not Use : This name should no longer be used when interacting with the person (i.e .  in addition to no longer being used, the name should not be even mentioned when interacting with the person)
     * 
     * Note: applications are not required to compare names labeled "Do Not Use" and other names in order to eliminate name parts that are common between the other name and a name labeled "Do Not Use".
     * 
     */
    DN,

    /**
     * Maiden Name : A name used prior to marriage.
     * 
     * Note that marriage naming customs vary greatly around the world.  This name use is for use by applications that collect and store "maiden" names.  Though the concept of maiden name is often gender specific, the use of this term is not gender specific.  The use of this term does not imply any particular history for a person's name, nor should the maiden name be determined algorithmically
     * 
     */
    M,

    /**
     * Search Type Uses: A name intended for use in searching or matching
     * 
     */
    SRCH,

    /**
     * Phonetic : The name as understood by the data enterer, i.e. a close approximation of a phonetic spelling of the name, not based on a phonetic algorithm.
     * 
     */
    PHON;

    public String value() {
        return name();
    }

    public static EntityNameUse fromValue(String v) {
        return valueOf(v);
    }

}
