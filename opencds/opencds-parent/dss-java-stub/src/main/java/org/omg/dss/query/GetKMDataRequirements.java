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

package org.omg.dss.query;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.common.ServiceRequestBase;


/**
 * <p>Java class for getKMDataRequirements complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getKMDataRequirements">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ServiceRequestBase">
 *       &lt;sequence>
 *         &lt;element name="kmId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getKMDataRequirements", propOrder = {
    "kmId"
})
public class GetKMDataRequirements
    extends ServiceRequestBase
{

    @XmlElement(required = true)
    protected EntityIdentifier kmId;

    /**
     * Gets the value of the kmId property.
     *
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *
     */
    public EntityIdentifier getKmId() {
        return kmId;
    }

    /**
     * Sets the value of the kmId property.
     *
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *
     */
    public void setKmId(EntityIdentifier value) {
        this.kmId = value;
    }

}
