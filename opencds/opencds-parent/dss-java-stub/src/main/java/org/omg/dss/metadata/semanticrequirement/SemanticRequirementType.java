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

package org.omg.dss.metadata.semanticrequirement;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


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
