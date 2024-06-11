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
import jakarta.xml.bind.annotation.XmlType;

import org.omg.dss.evaluation.requestresponse.IterativeEvaluationResponse;


/**
 * <p>Java class for evaluateIterativelyAtSpecifiedTimeResponse complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="evaluateIterativelyAtSpecifiedTimeResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="iterativeEvaluationResponse" type="{http://www.omg.org/spec/CDSS/201105/dss}IterativeEvaluationResponse"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "evaluateIterativelyAtSpecifiedTimeResponse", propOrder = {
    "iterativeEvaluationResponse"
})
public class EvaluateIterativelyAtSpecifiedTimeResponse {

    @XmlElement(required = true)
    protected IterativeEvaluationResponse iterativeEvaluationResponse;

    /**
     * Gets the value of the iterativeEvaluationResponse property.
     *
     * @return
     *     possible object is
     *     {@link IterativeEvaluationResponse }
     *
     */
    public IterativeEvaluationResponse getIterativeEvaluationResponse() {
        return iterativeEvaluationResponse;
    }

    /**
     * Sets the value of the iterativeEvaluationResponse property.
     *
     * @param value
     *     allowed object is
     *     {@link IterativeEvaluationResponse }
     *
     */
    public void setIterativeEvaluationResponse(IterativeEvaluationResponse value) {
        this.iterativeEvaluationResponse = value;
    }

}
