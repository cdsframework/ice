
package org.omg.dss.knowledgemodule;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for KMStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="KMStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="APPROVED"/>
 *     &lt;enumeration value="DEFINED"/>
 *     &lt;enumeration value="DRAFT"/>
 *     &lt;enumeration value="PROMOTED"/>
 *     &lt;enumeration value="REJECTED"/>
 *     &lt;enumeration value="RETIRED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "KMStatus")
@XmlEnum
public enum KMStatus {


    /**
     * The KM successfully passed the QA tests.
     * 
     */
    APPROVED,

    /**
     * Can be executed. Precondition: the KM is syntactically valid. When a defined KM changes, a new version should be created.
     * 
     */
    DEFINED,

    /**
     * Draft KM.
     * 
     */
    DRAFT,

    /**
     * The KM can be used in the production environment.
     * 
     */
    PROMOTED,

    /**
     * The KM definition doesn't match the KM specification.
     * 
     */
    REJECTED,

    /**
     * The KM was historically promoted, but should not be used any more.
     * 
     */
    RETIRED;

    public String value() {
        return name();
    }

    public static KMStatus fromValue(String v) {
        return valueOf(v);
    }

}
