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

package org.omg.dss.evaluation.requestresponse;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.omg.dss.common.ItemIdentifier;
import org.omg.dss.common.SemanticPayload;


/**
 * The DataRequirementItemData is an object containing data for a particular data requirement item passed as input for a KM evaluation request.
 *
 * <p>Java class for DataRequirementItemData complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DataRequirementItemData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="driId" type="{http://www.omg.org/spec/CDSS/201105/dss}ItemIdentifier"/>
 *         &lt;element name="data" type="{http://www.omg.org/spec/CDSS/201105/dss}SemanticPayload"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataRequirementItemData", propOrder = {
    "driId",
    "data"
})
public class DataRequirementItemData {

    @XmlElement(required = true)
    protected ItemIdentifier driId;
    @XmlElement(required = true)
    protected SemanticPayload data;

    /**
     * Gets the value of the driId property.
     *
     * @return
     *     possible object is
     *     {@link ItemIdentifier }
     *
     */
    public ItemIdentifier getDriId() {
        return driId;
    }

    /**
     * Sets the value of the driId property.
     *
     * @param value
     *     allowed object is
     *     {@link ItemIdentifier }
     *
     */
    public void setDriId(ItemIdentifier value) {
        this.driId = value;
    }

    /**
     * Gets the value of the data property.
     *
     * @return
     *     possible object is
     *     {@link SemanticPayload }
     *
     */
    public SemanticPayload getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     *
     * @param value
     *     allowed object is
     *     {@link SemanticPayload }
     *
     */
    public void setData(SemanticPayload value) {
        this.data = value;
    }

}
