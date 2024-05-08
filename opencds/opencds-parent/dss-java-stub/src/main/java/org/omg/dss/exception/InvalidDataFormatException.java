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
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;


/**
 * The InvalidDataFormatException is thrown when data are not consistent with the information model defined by a semantic signifier.
 *
 * <p>Java class for InvalidDataFormatException complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="InvalidDataFormatException">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DSSException">
 *       &lt;sequence>
 *         &lt;element name="informationModelSSId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InvalidDataFormatException", propOrder = {
    "informationModelSSId"
})
@XmlSeeAlso({
    InvalidDriDataFormatException.class,
    InvalidTraitCriterionDataFormatException.class
})
public class InvalidDataFormatException
    extends DSSException
{

    @XmlElement(required = true)
    protected EntityIdentifier informationModelSSId;

    /**
     * Gets the value of the informationModelSSId property.
     *
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *
     */
    public EntityIdentifier getInformationModelSSId() {
        return informationModelSSId;
    }

    /**
     * Sets the value of the informationModelSSId property.
     *
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *
     */
    public void setInformationModelSSId(EntityIdentifier value) {
        this.informationModelSSId = value;
    }

}
