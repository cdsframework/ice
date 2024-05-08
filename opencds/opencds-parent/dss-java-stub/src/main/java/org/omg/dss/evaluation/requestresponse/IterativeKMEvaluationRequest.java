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
 * The IterativeKMEvaluationRequest is an object containing information of a single knowledge module evaluation request in an iterative fashion. It contains information from a previous evaluation iteration.
 *
 * <p>Java class for IterativeKMEvaluationRequest complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="IterativeKMEvaluationRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}KMEvaluationRequestBase">
 *       &lt;sequence>
 *         &lt;element name="previousState" type="{http://www.omg.org/spec/CDSS/201105/dss}IntermediateState"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IterativeKMEvaluationRequest", propOrder = {
    "previousState"
})
public class IterativeKMEvaluationRequest
    extends KMEvaluationRequestBase
{

    @XmlElement(required = true)
    protected IntermediateState previousState;

    /**
     * Gets the value of the previousState property.
     *
     * @return
     *     possible object is
     *     {@link IntermediateState }
     *
     */
    public IntermediateState getPreviousState() {
        return previousState;
    }

    /**
     * Sets the value of the previousState property.
     *
     * @param value
     *     allowed object is
     *     {@link IntermediateState }
     *
     */
    public void setPreviousState(IntermediateState value) {
        this.previousState = value;
    }

}
