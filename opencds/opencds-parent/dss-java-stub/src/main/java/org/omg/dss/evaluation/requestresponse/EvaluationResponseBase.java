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
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * The EvaluationResponseBase is the superclass of all evaluation responses. It contains a list of knowledge module evaluation responses reaching final conclusions.
 *
 * <p>Java class for EvaluationResponseBase complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="EvaluationResponseBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="finalKMEvaluationResponse" type="{http://www.omg.org/spec/CDSS/201105/dss}FinalKMEvaluationResponse" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EvaluationResponseBase", propOrder = {
    "finalKMEvaluationResponse"
})
@XmlSeeAlso({
    EvaluationResponse.class,
    IterativeEvaluationResponse.class
})
public abstract class EvaluationResponseBase {

    protected List<FinalKMEvaluationResponse> finalKMEvaluationResponse;

    /**
     * Gets the value of the finalKMEvaluationResponse property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the finalKMEvaluationResponse property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFinalKMEvaluationResponse().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FinalKMEvaluationResponse }
     *
     *
     */
    public List<FinalKMEvaluationResponse> getFinalKMEvaluationResponse() {
        if (finalKMEvaluationResponse == null) {
            finalKMEvaluationResponse = new ArrayList<FinalKMEvaluationResponse>();
        }
        return this.finalKMEvaluationResponse;
    }

}
