
package org.omg.dss.common;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EntityType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EntityType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SEMANTIC_SIGNIFIER"/>
 *     &lt;enumeration value="TRAIT"/>
 *     &lt;enumeration value="PROFILE"/>
 *     &lt;enumeration value="SEMANTIC_REQUIREMENT"/>
 *     &lt;enumeration value="KNOWLEDGE_MODULE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EntityType")
@XmlEnum
public enum EntityType {

    SEMANTIC_SIGNIFIER,
    TRAIT,
    PROFILE,
    SEMANTIC_REQUIREMENT,
    KNOWLEDGE_MODULE;

    public String value() {
        return name();
    }

    public static EntityType fromValue(String v) {
        return valueOf(v);
    }

}
