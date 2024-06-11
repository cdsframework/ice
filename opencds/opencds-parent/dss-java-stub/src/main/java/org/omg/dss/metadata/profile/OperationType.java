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

package org.omg.dss.metadata.profile;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OperationType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="OperationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EVALUATION.EVALUATE"/>
 *     &lt;enumeration value="EVALUATION.EVALUATE_AT_SPECIFIED_TIME"/>
 *     &lt;enumeration value="EVALUATION.EVALUATE_ITERATIVELY"/>
 *     &lt;enumeration value="EVALUATION.EVALUATE_ITERATIVELY_AT_SPECIFIED_TIME"/>
 *     &lt;enumeration value="METADATA_DISCOVERY.DESCRIBE_PROFILE"/>
 *     &lt;enumeration value="METADATA_DISCOVERY.DESCRIBE_SCOPING_ENTITY"/>
 *     &lt;enumeration value="METADATA_DISCOVERY.DESCRIBE_SCOPING_ENTITY_HIERARCHY"/>
 *     &lt;enumeration value="METADATA_DISCOVERY.DESCRIBE_SEMANTIC_REQUIREMENT"/>
 *     &lt;enumeration value="METADATA_DISCOVERY.DESCRIBE_SEMANTIC_SIGNIFIER"/>
 *     &lt;enumeration value="METADATA_DISCOVERY.DESCRIBE_TRAIT"/>
 *     &lt;enumeration value="METADATA_DISCOVERY.LIST_PROFILES"/>
 *     &lt;enumeration value="QUERY.FIND_KMS"/>
 *     &lt;enumeration value="QUERY.GET_KM_DATA_REQUIREMENTS"/>
 *     &lt;enumeration value="QUERY.GET_KM_DATA_REQUIREMENTS_FOR_EVALUATION_AT_SPECIFIED_TIME"/>
 *     &lt;enumeration value="QUERY.GET_KM_DESCRIPTION"/>
 *     &lt;enumeration value="QUERY.GET_KM_EVALUATION_RESULT_SEMANTICS"/>
 *     &lt;enumeration value="QUERY.LIST_KMS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "OperationType")
@XmlEnum
public enum OperationType {

    @XmlEnumValue("EVALUATION.EVALUATE")
    EVALUATION_EVALUATE("EVALUATION.EVALUATE"),
    @XmlEnumValue("EVALUATION.EVALUATE_AT_SPECIFIED_TIME")
    EVALUATION_EVALUATE_AT_SPECIFIED_TIME("EVALUATION.EVALUATE_AT_SPECIFIED_TIME"),
    @XmlEnumValue("EVALUATION.EVALUATE_ITERATIVELY")
    EVALUATION_EVALUATE_ITERATIVELY("EVALUATION.EVALUATE_ITERATIVELY"),
    @XmlEnumValue("EVALUATION.EVALUATE_ITERATIVELY_AT_SPECIFIED_TIME")
    EVALUATION_EVALUATE_ITERATIVELY_AT_SPECIFIED_TIME("EVALUATION.EVALUATE_ITERATIVELY_AT_SPECIFIED_TIME"),
    @XmlEnumValue("METADATA_DISCOVERY.DESCRIBE_PROFILE")
    METADATA_DISCOVERY_DESCRIBE_PROFILE("METADATA_DISCOVERY.DESCRIBE_PROFILE"),
    @XmlEnumValue("METADATA_DISCOVERY.DESCRIBE_SCOPING_ENTITY")
    METADATA_DISCOVERY_DESCRIBE_SCOPING_ENTITY("METADATA_DISCOVERY.DESCRIBE_SCOPING_ENTITY"),
    @XmlEnumValue("METADATA_DISCOVERY.DESCRIBE_SCOPING_ENTITY_HIERARCHY")
    METADATA_DISCOVERY_DESCRIBE_SCOPING_ENTITY_HIERARCHY("METADATA_DISCOVERY.DESCRIBE_SCOPING_ENTITY_HIERARCHY"),
    @XmlEnumValue("METADATA_DISCOVERY.DESCRIBE_SEMANTIC_REQUIREMENT")
    METADATA_DISCOVERY_DESCRIBE_SEMANTIC_REQUIREMENT("METADATA_DISCOVERY.DESCRIBE_SEMANTIC_REQUIREMENT"),
    @XmlEnumValue("METADATA_DISCOVERY.DESCRIBE_SEMANTIC_SIGNIFIER")
    METADATA_DISCOVERY_DESCRIBE_SEMANTIC_SIGNIFIER("METADATA_DISCOVERY.DESCRIBE_SEMANTIC_SIGNIFIER"),
    @XmlEnumValue("METADATA_DISCOVERY.DESCRIBE_TRAIT")
    METADATA_DISCOVERY_DESCRIBE_TRAIT("METADATA_DISCOVERY.DESCRIBE_TRAIT"),
    @XmlEnumValue("METADATA_DISCOVERY.LIST_PROFILES")
    METADATA_DISCOVERY_LIST_PROFILES("METADATA_DISCOVERY.LIST_PROFILES"),
    @XmlEnumValue("QUERY.FIND_KMS")
    QUERY_FIND_KMS("QUERY.FIND_KMS"),
    @XmlEnumValue("QUERY.GET_KM_DATA_REQUIREMENTS")
    QUERY_GET_KM_DATA_REQUIREMENTS("QUERY.GET_KM_DATA_REQUIREMENTS"),
    @XmlEnumValue("QUERY.GET_KM_DATA_REQUIREMENTS_FOR_EVALUATION_AT_SPECIFIED_TIME")
    QUERY_GET_KM_DATA_REQUIREMENTS_FOR_EVALUATION_AT_SPECIFIED_TIME("QUERY.GET_KM_DATA_REQUIREMENTS_FOR_EVALUATION_AT_SPECIFIED_TIME"),
    @XmlEnumValue("QUERY.GET_KM_DESCRIPTION")
    QUERY_GET_KM_DESCRIPTION("QUERY.GET_KM_DESCRIPTION"),
    @XmlEnumValue("QUERY.GET_KM_EVALUATION_RESULT_SEMANTICS")
    QUERY_GET_KM_EVALUATION_RESULT_SEMANTICS("QUERY.GET_KM_EVALUATION_RESULT_SEMANTICS"),
    @XmlEnumValue("QUERY.LIST_KMS")
    QUERY_LIST_KMS("QUERY.LIST_KMS");
    private final String value;

    OperationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static OperationType fromValue(String v) {
        for (OperationType c: OperationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
