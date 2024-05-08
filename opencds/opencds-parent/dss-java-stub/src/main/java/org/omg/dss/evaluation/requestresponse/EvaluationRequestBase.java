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
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * The EvaluationRequestBase is the superclass of all knowledge modules evaluation requests. It contains the data passed as input for evaluating one or more knowledge modules.
 * 
 * 
 * <p>Java class for EvaluationRequestBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EvaluationRequestBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="clientLanguage" use="required" type="{http://www.omg.org/spec/CDSS/201105/dss}Language" />
 *       &lt;attribute name="clientTimeZoneOffset" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EvaluationRequestBase")
@XmlSeeAlso({
    IterativeEvaluationRequest.class,
    EvaluationRequest.class
})
public abstract class EvaluationRequestBase {

    @XmlAttribute(name = "clientLanguage", required = true)
    protected String clientLanguage;
    @XmlAttribute(name = "clientTimeZoneOffset", required = true)
    protected String clientTimeZoneOffset;

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
     * Gets the value of the clientTimeZoneOffset property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientTimeZoneOffset() {
        return clientTimeZoneOffset;
    }

    /**
     * Sets the value of the clientTimeZoneOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientTimeZoneOffset(String value) {
        this.clientTimeZoneOffset = value;
    }

}
