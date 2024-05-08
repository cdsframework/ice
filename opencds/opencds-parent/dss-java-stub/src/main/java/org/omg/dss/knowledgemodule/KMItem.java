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

package org.omg.dss.knowledgemodule;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

import org.omg.dss.common.DescribedDO;
import org.omg.dss.common.ItemIdentifier;


/**
 * The superclass of all knowledge module sub-items. For example, Data Requirement Group or Item. It contains the item identifier, which consists of the identifier of the scoping entity as well as a unique identifier within the scoping entity.  The scoping entity may be the knowledge module within which the KMItem resides, a different knowledge module, or an entity other than the knowledge module.  This approach enables items such as data requirements to be decoupled from specific knowledge modules and reused across knowledge modules.  This class inherits its name and description information from DescribedDO.
 *
 * <p>Java class for KMItem complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="KMItem">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DescribedDO">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.omg.org/spec/CDSS/201105/dss}ItemIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMItem", propOrder = {
    "id"
})
@XmlSeeAlso({
    KMEvaluationResultSemantics.class,
    KMConsumerProvidedQueryParameter.class,
    KMDataRequirementItem.class,
    KMDataRequirementGroup.class
})
public abstract class KMItem
    extends DescribedDO
{

    @XmlElement(required = true)
    protected ItemIdentifier id;

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link ItemIdentifier }
     *
     */
    public ItemIdentifier getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link ItemIdentifier }
     *
     */
    public void setId(ItemIdentifier value) {
        this.id = value;
    }

}
