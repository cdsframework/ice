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

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * The EvaluationResponse is an object containing responses of evaluating knowledge modules in a non-iterative fashion. It contains a list of knowledge module evaluation responses reaching final conclusions as well as a list of knowledge module evaluations with intermediate conclusions due to insufficient data.
 * 
 * <p>Java class for EvaluationResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EvaluationResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}EvaluationResponseBase">
 *       &lt;sequence>
 *         &lt;element name="intermediateKMEvaluationResponse" type="{http://www.omg.org/spec/CDSS/201105/dss}IntermediateKMEvaluationResponse" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EvaluationResponse", propOrder = {
    "intermediateKMEvaluationResponse"
})
public class EvaluationResponse
    extends EvaluationResponseBase
{

    protected List<IntermediateKMEvaluationResponse> intermediateKMEvaluationResponse;

    /**
     * Gets the value of the intermediateKMEvaluationResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the intermediateKMEvaluationResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIntermediateKMEvaluationResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IntermediateKMEvaluationResponse }
     * 
     * 
     */
    public List<IntermediateKMEvaluationResponse> getIntermediateKMEvaluationResponse() {
        if (intermediateKMEvaluationResponse == null) {
            intermediateKMEvaluationResponse = new ArrayList<IntermediateKMEvaluationResponse>();
        }
        return this.intermediateKMEvaluationResponse;
    }

}
