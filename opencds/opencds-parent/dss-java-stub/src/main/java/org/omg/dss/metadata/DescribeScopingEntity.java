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

package org.omg.dss.metadata;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.omg.dss.common.ServiceRequestBase;


/**
 * <p>Java class for describeScopingEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="describeScopingEntity">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ServiceRequestBase">
 *       &lt;sequence>
 *         &lt;element name="scopingEntityId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "describeScopingEntity", propOrder = {
    "scopingEntityId"
})
public class DescribeScopingEntity
    extends ServiceRequestBase
{

    @XmlElement(required = true)
    protected String scopingEntityId;

    /**
     * Gets the value of the scopingEntityId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScopingEntityId() {
        return scopingEntityId;
    }

    /**
     * Sets the value of the scopingEntityId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScopingEntityId(String value) {
        this.scopingEntityId = value;
    }

}
