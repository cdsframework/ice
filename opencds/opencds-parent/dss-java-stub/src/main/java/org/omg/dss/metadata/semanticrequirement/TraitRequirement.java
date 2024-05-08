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

import org.omg.dss.common.EntityIdentifier;


/**
 * It specifies the a specific knowledge module trait requirement. Trait is identified by a scoping entity, the trait identifier (unique within the scoping entity), and the trait version. The requirement also specifies if the trait is required or optional for knowledge modules claiming conformance to the requirement.
 *
 *
 * <p>Java class for TraitRequirement complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TraitRequirement">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="isMandatory" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="traitId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TraitRequirement", propOrder = {
    "isMandatory",
    "traitId"
})
public class TraitRequirement {

    protected boolean isMandatory;
    @XmlElement(required = true)
    protected EntityIdentifier traitId;

    /**
     * Gets the value of the isMandatory property.
     *
     */
    public boolean isIsMandatory() {
        return isMandatory;
    }

    /**
     * Sets the value of the isMandatory property.
     *
     */
    public void setIsMandatory(boolean value) {
        this.isMandatory = value;
    }

    /**
     * Gets the value of the traitId property.
     *
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *
     */
    public EntityIdentifier getTraitId() {
        return traitId;
    }

    /**
     * Sets the value of the traitId property.
     *
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *
     */
    public void setTraitId(EntityIdentifier value) {
        this.traitId = value;
    }

}
