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

package org.omg.dss.query.requests;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for KMRelationshipType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="KMRelationshipType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PROVIDES_EVALUATION_RESULT_FOR_USE_BY"/>
 *     &lt;enumeration value="PROVIDES_EVALUATION_RESULT_FOR_PASS_THROUGH_BY"/>
 *     &lt;enumeration value="USES_EVALUATION_RESULT_FROM"/>
 *     &lt;enumeration value="PASSES_THROUGH_EVALUATION_RESULT_FROM"/>
 *     &lt;enumeration value="SUPERCEDED_BY"/>
 *     &lt;enumeration value="SUPERCEDES"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "KMRelationshipType")
@XmlEnum
public enum KMRelationshipType {


    /**
     * The source KM provides one or more of its evaluation results to the target KM for usage as an evaluation input.
     *
     */
    PROVIDES_EVALUATION_RESULT_FOR_USE_BY,

    /**
     * The source KM provides one or more of its evaluation results to the target KM for passing the evaluation result through to the consumer.
     *
     */
    PROVIDES_EVALUATION_RESULT_FOR_PASS_THROUGH_BY,

    /**
     * The source KM uses one or more of the evaluation results from the traget KM as an evaluation input.
     *
     */
    USES_EVALUATION_RESULT_FROM,

    /**
     * The source KM passes through to the consumer one or more of the evaluation results obtained from the target KM.
     *
     */
    PASSES_THROUGH_EVALUATION_RESULT_FROM,

    /**
     * The source KM was superceded by the target KM. That is, the target KM should be used instead of the source KM if possible.
     *
     */
    SUPERCEDED_BY,

    /**
     * The source KM supercedes the target KM. That is, the source KM should be used instead of the target KM if possible.
     *
     */
    SUPERCEDES;

    public String value() {
        return name();
    }

    public static KMRelationshipType fromValue(String v) {
        return valueOf(v);
    }

}
