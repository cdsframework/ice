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
import jakarta.xml.bind.annotation.XmlType;

import org.omg.dss.common.ItemIdentifier;


/**
 * This object contains information about Consumer Provided Query Parameter objects that are used within a specific Data Requirement Item in its query instance. It contains the identifier of the Consumer Provided Query parameter and its path within the query instance. 
 * 
 * <p>Java class for CPQPInUse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CPQPInUse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="specificationPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cpqpItemId" type="{http://www.omg.org/spec/CDSS/201105/dss}ItemIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CPQPInUse", propOrder = {
    "specificationPath",
    "cpqpItemId"
})
public class CPQPInUse {

    @XmlElement(required = true)
    protected String specificationPath;
    @XmlElement(required = true)
    protected ItemIdentifier cpqpItemId;

    /**
     * Gets the value of the specificationPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecificationPath() {
        return specificationPath;
    }

    /**
     * Sets the value of the specificationPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecificationPath(String value) {
        this.specificationPath = value;
    }

    /**
     * Gets the value of the cpqpItemId property.
     * 
     * @return
     *     possible object is
     *     {@link ItemIdentifier }
     *     
     */
    public ItemIdentifier getCpqpItemId() {
        return cpqpItemId;
    }

    /**
     * Sets the value of the cpqpItemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemIdentifier }
     *     
     */
    public void setCpqpItemId(ItemIdentifier value) {
        this.cpqpItemId = value;
    }

}
