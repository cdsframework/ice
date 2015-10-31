
package org.omg.dss.metadata.semanticrequirement;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SemanticRequirementType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SemanticRequirementType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INFORMATION_MODEL_REQUIREMENT"/>
 *     &lt;enumeration value="LANGUAGE_SUPPORT_REQUIREMENT"/>
 *     &lt;enumeration value="TRAIT_SET_REQUIREMENT"/>
 *     &lt;enumeration value="OTHER_SEMANTIC_REQUIREMENT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SemanticRequirementType")
@XmlEnum
public enum SemanticRequirementType {

    INFORMATION_MODEL_REQUIREMENT,
    LANGUAGE_SUPPORT_REQUIREMENT,
    TRAIT_SET_REQUIREMENT,
    OTHER_SEMANTIC_REQUIREMENT;

    public String value() {
        return name();
    }

    public static SemanticRequirementType fromValue(String v) {
        return valueOf(v);
    }

}
