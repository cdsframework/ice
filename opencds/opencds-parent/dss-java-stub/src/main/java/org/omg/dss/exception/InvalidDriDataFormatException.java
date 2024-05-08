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

package org.omg.dss.exception;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.omg.dss.common.ItemIdentifier;


/**
 * A InvalidDriDataFormatException is thrown when trying to evaluate a KM but data provided for one of its Data Requirement Items has an invalid format.
 *
 * <p>Java class for InvalidDriDataFormatException complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="InvalidDriDataFormatException">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}InvalidDataFormatException">
 *       &lt;sequence>
 *         &lt;element name="driId" type="{http://www.omg.org/spec/CDSS/201105/dss}ItemIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InvalidDriDataFormatException", propOrder = {
    "driId"
})
public class InvalidDriDataFormatException
    extends InvalidDataFormatException
{

    @XmlElement(required = true)
    protected ItemIdentifier driId;

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

}
