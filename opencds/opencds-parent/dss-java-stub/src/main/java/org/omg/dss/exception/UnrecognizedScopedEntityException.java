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

import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.common.EntityType;


/**
 * The UnrecognizedScopedEntityException is an exception that is thrown when the service receives a request regarding a scoped entity that is not recognized using the its specified entity identifier object. A scoped entity identifier consists of the triplet of business identifier, version, and scoping entity identifier. 
 * 
 * 
 * <p>Java class for UnrecognizedScopedEntityException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnrecognizedScopedEntityException">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DSSException">
 *       &lt;sequence>
 *         &lt;element name="entityId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *         &lt;element name="entityType" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnrecognizedScopedEntityException", propOrder = {
    "entityId",
    "entityType"
})
public class UnrecognizedScopedEntityException
    extends DSSException
{

    @XmlElement(required = true)
    protected EntityIdentifier entityId;
    @XmlElement(required = true)
    protected EntityType entityType;

    /**
     * Gets the value of the entityId property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getEntityId() {
        return entityId;
    }

    /**
     * Sets the value of the entityId property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setEntityId(EntityIdentifier value) {
        this.entityId = value;
    }

    /**
     * Gets the value of the entityType property.
     * 
     * @return
     *     possible object is
     *     {@link EntityType }
     *     
     */
    public EntityType getEntityType() {
        return entityType;
    }

    /**
     * Sets the value of the entityType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityType }
     *     
     */
    public void setEntityType(EntityType value) {
        this.entityType = value;
    }

}
