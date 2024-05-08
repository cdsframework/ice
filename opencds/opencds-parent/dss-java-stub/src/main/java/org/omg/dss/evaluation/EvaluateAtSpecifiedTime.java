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

package org.omg.dss.evaluation;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import org.omg.dss.common.ServiceRequestBase;
import org.omg.dss.evaluation.requestresponse.EvaluationRequest;


/**
 * <p>Java class for evaluateAtSpecifiedTime complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="evaluateAtSpecifiedTime">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ServiceRequestBase">
 *       &lt;sequence>
 *         &lt;element name="specifiedTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="evaluationRequest" type="{http://www.omg.org/spec/CDSS/201105/dss}EvaluationRequest"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "evaluateAtSpecifiedTime", propOrder = {
    "specifiedTime",
    "evaluationRequest"
})
public class EvaluateAtSpecifiedTime
    extends ServiceRequestBase
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar specifiedTime;
    @XmlElement(required = true)
    protected EvaluationRequest evaluationRequest;

    /**
     * Gets the value of the specifiedTime property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getSpecifiedTime() {
        return specifiedTime;
    }

    /**
     * Sets the value of the specifiedTime property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setSpecifiedTime(XMLGregorianCalendar value) {
        this.specifiedTime = value;
    }

    /**
     * Gets the value of the evaluationRequest property.
     *
     * @return
     *     possible object is
     *     {@link EvaluationRequest }
     *
     */
    public EvaluationRequest getEvaluationRequest() {
        return evaluationRequest;
    }

    /**
     * Sets the value of the evaluationRequest property.
     *
     * @param value
     *     allowed object is
     *     {@link EvaluationRequest }
     *
     */
    public void setEvaluationRequest(EvaluationRequest value) {
        this.evaluationRequest = value;
    }

}
