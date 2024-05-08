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

package org.omg.dss.query.responses;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.query.requests.KMRelationshipType;


/**
 * This object contains information about the identifier of the related knowledge module and the relation type.  The direction of the relationship uses the following pattern: [this KM] [relationship type] [related KM].  E.g., if this KM = KM A, related KM = KM B, and relationship type = USES_EVALUATION_RESULTS_FROM, then KM A uses evaluation results from KM B.
 * 
 * <p>Java class for RelatedKM complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RelatedKM">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="relationshipType" type="{http://www.omg.org/spec/CDSS/201105/dss}KMRelationshipType"/>
 *         &lt;element name="relatedKmId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RelatedKM", propOrder = {
    "relationshipType",
    "relatedKmId"
})
public class RelatedKM {

    @XmlElement(required = true)
    protected KMRelationshipType relationshipType;
    @XmlElement(required = true)
    protected EntityIdentifier relatedKmId;

    /**
     * Gets the value of the relationshipType property.
     * 
     * @return
     *     possible object is
     *     {@link KMRelationshipType }
     *     
     */
    public KMRelationshipType getRelationshipType() {
        return relationshipType;
    }

    /**
     * Sets the value of the relationshipType property.
     * 
     * @param value
     *     allowed object is
     *     {@link KMRelationshipType }
     *     
     */
    public void setRelationshipType(KMRelationshipType value) {
        this.relationshipType = value;
    }

    /**
     * Gets the value of the relatedKmId property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getRelatedKmId() {
        return relatedKmId;
    }

    /**
     * Sets the value of the relatedKmId property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setRelatedKmId(EntityIdentifier value) {
        this.relatedKmId = value;
    }

}
