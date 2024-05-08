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


/**
 * The IntermediateState contains contextual information regarding previous evaluation iterations for a knowledge module. The class implementations are vendor-specific.
 *
 *
 * <p>Java class for IntermediateState complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="IntermediateState">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="base64EncodedStateData" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntermediateState", propOrder = {
    "base64EncodedStateData"
})
public class IntermediateState {

    @XmlElement(required = true)
    protected byte[] base64EncodedStateData;

    /**
     * Gets the value of the base64EncodedStateData property.
     *
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getBase64EncodedStateData() {
        return base64EncodedStateData;
    }

    /**
     * Sets the value of the base64EncodedStateData property.
     *
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setBase64EncodedStateData(byte[] value) {
        this.base64EncodedStateData = ((byte[]) value);
    }

}
