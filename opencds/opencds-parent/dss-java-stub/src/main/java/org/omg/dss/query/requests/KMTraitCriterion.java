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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * If the trait criterion is satisfied, then the knowledge module is considered to have satisfied the criterion.
 * 
 * 
 * <p>Java class for KMTraitCriterion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMTraitCriterion">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}KMCriterion">
 *       &lt;sequence>
 *         &lt;element name="kmTraitCriterionValue" type="{http://www.omg.org/spec/CDSS/201105/dss}KMTraitCriterionValue"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMTraitCriterion", propOrder = {
    "kmTraitCriterionValue"
})
public class KMTraitCriterion
    extends KMCriterion
{

    @XmlElement(required = true)
    protected KMTraitCriterionValue kmTraitCriterionValue;

    /**
     * Gets the value of the kmTraitCriterionValue property.
     * 
     * @return
     *     possible object is
     *     {@link KMTraitCriterionValue }
     *     
     */
    public KMTraitCriterionValue getKmTraitCriterionValue() {
        return kmTraitCriterionValue;
    }

    /**
     * Sets the value of the kmTraitCriterionValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link KMTraitCriterionValue }
     *     
     */
    public void setKmTraitCriterionValue(KMTraitCriterionValue value) {
        this.kmTraitCriterionValue = value;
    }

}
