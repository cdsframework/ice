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

package org.omg.dss.metadata.trait;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.omg.dss.common.DescribedDO;
import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.common.ItemIdentifier;


/**
 * The TraitCriterion object represents a criterion that can be applied to a knowledge module trait value.  The identifier of a trait criterion consists of the EntityIdentifier of the parent trait plus an itemId that is unique within the scope of the EntityIdentifier of the parent trait.
 *
 *
 * <p>Java class for TraitCriterion complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TraitCriterion">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DescribedDO">
 *       &lt;sequence>
 *         &lt;element name="traitCriterionId" type="{http://www.omg.org/spec/CDSS/201105/dss}ItemIdentifier"/>
 *         &lt;element name="criterionModelSSId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TraitCriterion", propOrder = {
    "traitCriterionId",
    "criterionModelSSId"
})
public class TraitCriterion
    extends DescribedDO
{

    @XmlElement(required = true)
    protected ItemIdentifier traitCriterionId;
    @XmlElement(required = true)
    protected EntityIdentifier criterionModelSSId;

    /**
     * Gets the value of the traitCriterionId property.
     *
     * @return
     *     possible object is
     *     {@link ItemIdentifier }
     *
     */
    public ItemIdentifier getTraitCriterionId() {
        return traitCriterionId;
    }

    /**
     * Sets the value of the traitCriterionId property.
     *
     * @param value
     *     allowed object is
     *     {@link ItemIdentifier }
     *
     */
    public void setTraitCriterionId(ItemIdentifier value) {
        this.traitCriterionId = value;
    }

    /**
     * Gets the value of the criterionModelSSId property.
     *
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *
     */
    public EntityIdentifier getCriterionModelSSId() {
        return criterionModelSSId;
    }

    /**
     * Sets the value of the criterionModelSSId property.
     *
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *
     */
    public void setCriterionModelSSId(EntityIdentifier value) {
        this.criterionModelSSId = value;
    }

}
