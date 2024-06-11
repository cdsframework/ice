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

package org.omg.dss.common;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlList;
import jakarta.xml.bind.annotation.XmlType;


/**
 * SemanticPayload is identified by a semantic signifier and has a value that is expressed using the semantics defined by the semantic signifier.
 *
 *
 * <p>Java class for SemanticPayload complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SemanticPayload">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="informationModelSSId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *         &lt;element name="base64EncodedPayload" type="{http://www.omg.org/spec/CDSS/201105/dss}base64EncodedContent"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SemanticPayload", propOrder = {
    "informationModelSSId",
    "base64EncodedPayload"
})
public class SemanticPayload {

    @XmlElement(required = true)
    protected EntityIdentifier informationModelSSId;
    @XmlList
    @XmlElement(required = true)
    protected List<byte[]> base64EncodedPayload;

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

    /**
     * Gets the value of the base64EncodedPayload property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the base64EncodedPayload property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBase64EncodedPayload().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * byte[]
     *
     */
    public List<byte[]> getBase64EncodedPayload() {
        if (base64EncodedPayload == null) {
            base64EncodedPayload = new ArrayList<byte[]>();
        }
        return this.base64EncodedPayload;
    }

}
