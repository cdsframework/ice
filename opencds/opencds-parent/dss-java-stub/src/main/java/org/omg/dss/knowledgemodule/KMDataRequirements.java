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

package org.omg.dss.knowledgemodule;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * The response object of the getKMDataRequirements operation in the Query interface. This object specifies the data requirement groups initially required for an iterative evaluation interaction, as well as any additional data requirement groups that may be needed depending on the results of the initial interaction.  In addition, it extends its superclass for a list of Consumer Provided Query Parameter objects defined for the data requirement items of the knowledge module.
 *
 * <p>Java class for KMDataRequirements complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="KMDataRequirements">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="initialDataRequirementGroup" type="{http://www.omg.org/spec/CDSS/201105/dss}KMDataRequirementGroup" maxOccurs="unbounded"/>
 *         &lt;element name="additionalDataRequirementGroup" type="{http://www.omg.org/spec/CDSS/201105/dss}KMDataRequirementGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="consumerProvidedQueryParameter" type="{http://www.omg.org/spec/CDSS/201105/dss}KMConsumerProvidedQueryParameter" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMDataRequirements", propOrder = {
    "initialDataRequirementGroup",
    "additionalDataRequirementGroup",
    "consumerProvidedQueryParameter"
})
public class KMDataRequirements {

    @XmlElement(required = true)
    protected List<KMDataRequirementGroup> initialDataRequirementGroup;
    protected List<KMDataRequirementGroup> additionalDataRequirementGroup;
    protected List<KMConsumerProvidedQueryParameter> consumerProvidedQueryParameter;

    /**
     * Gets the value of the initialDataRequirementGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the initialDataRequirementGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInitialDataRequirementGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KMDataRequirementGroup }
     *
     *
     */
    public List<KMDataRequirementGroup> getInitialDataRequirementGroup() {
        if (initialDataRequirementGroup == null) {
            initialDataRequirementGroup = new ArrayList<KMDataRequirementGroup>();
        }
        return this.initialDataRequirementGroup;
    }

    /**
     * Gets the value of the additionalDataRequirementGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalDataRequirementGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalDataRequirementGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KMDataRequirementGroup }
     *
     *
     */
    public List<KMDataRequirementGroup> getAdditionalDataRequirementGroup() {
        if (additionalDataRequirementGroup == null) {
            additionalDataRequirementGroup = new ArrayList<KMDataRequirementGroup>();
        }
        return this.additionalDataRequirementGroup;
    }

    /**
     * Gets the value of the consumerProvidedQueryParameter property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the consumerProvidedQueryParameter property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConsumerProvidedQueryParameter().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KMConsumerProvidedQueryParameter }
     *
     *
     */
    public List<KMConsumerProvidedQueryParameter> getConsumerProvidedQueryParameter() {
        if (consumerProvidedQueryParameter == null) {
            consumerProvidedQueryParameter = new ArrayList<KMConsumerProvidedQueryParameter>();
        }
        return this.consumerProvidedQueryParameter;
    }

}
