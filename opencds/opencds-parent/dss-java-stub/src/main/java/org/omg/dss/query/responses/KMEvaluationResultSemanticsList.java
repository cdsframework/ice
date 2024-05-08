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

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.omg.dss.knowledgemodule.KMEvaluationResultSemantics;


/**
 * Specification of the semantics used to convey the evaluation result(s) of a knowledge module.
 * 
 * <p>Java class for KMEvaluationResultSemanticsList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMEvaluationResultSemanticsList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="kmEvaluationResultSemantics" type="{http://www.omg.org/spec/CDSS/201105/dss}KMEvaluationResultSemantics" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMEvaluationResultSemanticsList", propOrder = {
    "kmEvaluationResultSemantics"
})
public class KMEvaluationResultSemanticsList {

    @XmlElement(required = true)
    protected List<KMEvaluationResultSemantics> kmEvaluationResultSemantics;

    /**
     * Gets the value of the kmEvaluationResultSemantics property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the kmEvaluationResultSemantics property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKmEvaluationResultSemantics().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KMEvaluationResultSemantics }
     * 
     * 
     */
    public List<KMEvaluationResultSemantics> getKmEvaluationResultSemantics() {
        if (kmEvaluationResultSemantics == null) {
            kmEvaluationResultSemantics = new ArrayList<KMEvaluationResultSemantics>();
        }
        return this.kmEvaluationResultSemantics;
    }

}
