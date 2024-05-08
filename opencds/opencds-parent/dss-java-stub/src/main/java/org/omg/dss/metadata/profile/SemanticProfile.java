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

package org.omg.dss.metadata.profile;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.omg.dss.metadata.semanticrequirement.SemanticRequirement;


/**
 * A SemanticProfile represents a list of the semantic requirements to which the service conforms.
 *
 * <p>Java class for SemanticProfile complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SemanticProfile">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ServiceProfile">
 *       &lt;sequence>
 *         &lt;element name="fulfilledSemanticRequirement" type="{http://www.omg.org/spec/CDSS/201105/dss}SemanticRequirement" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SemanticProfile", propOrder = {
    "fulfilledSemanticRequirement"
})
public class SemanticProfile
    extends ServiceProfile
{

    @XmlElement(required = true)
    protected List<SemanticRequirement> fulfilledSemanticRequirement;

    /**
     * Gets the value of the fulfilledSemanticRequirement property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fulfilledSemanticRequirement property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFulfilledSemanticRequirement().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SemanticRequirement }
     *
     *
     */
    public List<SemanticRequirement> getFulfilledSemanticRequirement() {
        if (fulfilledSemanticRequirement == null) {
            fulfilledSemanticRequirement = new ArrayList<SemanticRequirement>();
        }
        return this.fulfilledSemanticRequirement;
    }

}
