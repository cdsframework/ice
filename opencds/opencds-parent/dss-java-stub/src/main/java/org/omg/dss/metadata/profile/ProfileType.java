
package org.omg.dss.metadata.profile;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProfileType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ProfileType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CONFORMANCE_PROFILE"/>
 *     &lt;enumeration value="FUNCTIONAL_PROFILE"/>
 *     &lt;enumeration value="SEMANTIC_PROFILE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ProfileType")
@XmlEnum
public enum ProfileType {

    CONFORMANCE_PROFILE,
    FUNCTIONAL_PROFILE,
    SEMANTIC_PROFILE;

    public String value() {
        return name();
    }

    public static ProfileType fromValue(String v) {
        return valueOf(v);
    }

}
