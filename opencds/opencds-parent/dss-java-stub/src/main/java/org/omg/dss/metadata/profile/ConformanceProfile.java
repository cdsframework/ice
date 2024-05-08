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


/**
 * A ConformanceProfile consists of one or more supported functional profiles and one or more supported semantic profiles.
 * 
 * 
 * <p>Java class for ConformanceProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConformanceProfile">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ServiceProfile">
 *       &lt;sequence>
 *         &lt;element name="supportedFunctionalProfile" type="{http://www.omg.org/spec/CDSS/201105/dss}FunctionalProfile" maxOccurs="unbounded"/>
 *         &lt;element name="supportedSemanticProfile" type="{http://www.omg.org/spec/CDSS/201105/dss}SemanticProfile" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConformanceProfile", propOrder = {
    "supportedFunctionalProfile",
    "supportedSemanticProfile"
})
public class ConformanceProfile
    extends ServiceProfile
{

    @XmlElement(required = true)
    protected List<FunctionalProfile> supportedFunctionalProfile;
    @XmlElement(required = true)
    protected List<SemanticProfile> supportedSemanticProfile;

    /**
     * Gets the value of the supportedFunctionalProfile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportedFunctionalProfile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportedFunctionalProfile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FunctionalProfile }
     * 
     * 
     */
    public List<FunctionalProfile> getSupportedFunctionalProfile() {
        if (supportedFunctionalProfile == null) {
            supportedFunctionalProfile = new ArrayList<FunctionalProfile>();
        }
        return this.supportedFunctionalProfile;
    }

    /**
     * Gets the value of the supportedSemanticProfile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportedSemanticProfile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportedSemanticProfile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SemanticProfile }
     * 
     * 
     */
    public List<SemanticProfile> getSupportedSemanticProfile() {
        if (supportedSemanticProfile == null) {
            supportedSemanticProfile = new ArrayList<SemanticProfile>();
        }
        return this.supportedSemanticProfile;
    }

}
