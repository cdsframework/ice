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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A DSS claiming conformance to this requirement must fulfill the semantic restrictions defined in the narrative specification.
 *
 * <p>Java class for OtherSemanticRequirement complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OtherSemanticRequirement">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}SemanticRequirement">
 *       &lt;sequence>
 *         &lt;element name="requirementSpecification" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OtherSemanticRequirement", propOrder = {
    "requirementSpecification"
})
public class OtherSemanticRequirement
    extends SemanticRequirement
{

    @XmlElement(required = true)
    protected String requirementSpecification;

    /**
     * Gets the value of the requirementSpecification property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRequirementSpecification() {
        return requirementSpecification;
    }

    /**
     * Sets the value of the requirementSpecification property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRequirementSpecification(String value) {
        this.requirementSpecification = value;
    }

}
