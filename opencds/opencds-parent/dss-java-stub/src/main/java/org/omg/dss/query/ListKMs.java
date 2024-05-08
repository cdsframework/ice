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

import org.omg.dss.common.ServiceRequestBase;
import org.omg.dss.query.requests.KMTraitInclusionSpecification;


/**
 * <p>Java class for listKMs complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="listKMs">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ServiceRequestBase">
 *       &lt;sequence>
 *         &lt;element name="clientLanguage" type="{http://www.omg.org/spec/CDSS/201105/dss}Language"/>
 *         &lt;element name="kmTraitInclusionSpecification" type="{http://www.omg.org/spec/CDSS/201105/dss}KMTraitInclusionSpecification"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listKMs", propOrder = {
    "clientLanguage",
    "kmTraitInclusionSpecification"
})
public class ListKMs
    extends ServiceRequestBase
{

    @XmlElement(required = true)
    protected String clientLanguage;
    @XmlElement(required = true)
    protected KMTraitInclusionSpecification kmTraitInclusionSpecification;

    /**
     * Gets the value of the clientLanguage property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getClientLanguage() {
        return clientLanguage;
    }

    /**
     * Sets the value of the clientLanguage property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setClientLanguage(String value) {
        this.clientLanguage = value;
    }

    /**
     * Gets the value of the kmTraitInclusionSpecification property.
     *
     * @return
     *     possible object is
     *     {@link KMTraitInclusionSpecification }
     *
     */
    public KMTraitInclusionSpecification getKmTraitInclusionSpecification() {
        return kmTraitInclusionSpecification;
    }

    /**
     * Sets the value of the kmTraitInclusionSpecification property.
     *
     * @param value
     *     allowed object is
     *     {@link KMTraitInclusionSpecification }
     *
     */
    public void setKmTraitInclusionSpecification(KMTraitInclusionSpecification value) {
        this.kmTraitInclusionSpecification = value;
    }

}
