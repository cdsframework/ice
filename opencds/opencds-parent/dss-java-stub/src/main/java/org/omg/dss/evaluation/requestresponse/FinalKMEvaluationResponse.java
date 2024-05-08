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
 * The FinalKMEvaluationResponse is an object containing final conclusions of a single knowledge module evaluation response.
 *
 * <p>Java class for FinalKMEvaluationResponse complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FinalKMEvaluationResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}KMEvaluationResponse">
 *       &lt;sequence>
 *         &lt;element name="kmEvaluationResultData" type="{http://www.omg.org/spec/CDSS/201105/dss}KMEvaluationResultData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinalKMEvaluationResponse", propOrder = {
    "kmEvaluationResultData"
})
public class FinalKMEvaluationResponse
    extends KMEvaluationResponse
{

    protected List<KMEvaluationResultData> kmEvaluationResultData;

    /**
     * Gets the value of the kmEvaluationResultData property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the kmEvaluationResultData property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKmEvaluationResultData().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KMEvaluationResultData }
     *
     *
     */
    public List<KMEvaluationResultData> getKmEvaluationResultData() {
        if (kmEvaluationResultData == null) {
            kmEvaluationResultData = new ArrayList<KMEvaluationResultData>();
        }
        return this.kmEvaluationResultData;
    }

}
