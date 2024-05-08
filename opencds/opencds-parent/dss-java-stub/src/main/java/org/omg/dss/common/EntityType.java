/*
 * Copyright 2011-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omg.dss.common;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


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
