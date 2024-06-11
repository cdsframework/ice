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

package org.omg.dss.metadata.semanticrequirement;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * It specifies the list of the knowledge module traits that will or may be associated with all knowledge modules claiming conformance to the requirement. Traits are identified by the identifier of the scoping entity for the trait, the trait identifier, and the trait version. The requirement also specifies if the trait is required or optional for knowledge modules claiming conformance to the requirement.
 * 
 * 
 * <p>Java class for TraitSetRequirement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TraitSetRequirement">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}SemanticRequirement">
 *       &lt;sequence>
 *         &lt;element name="traitRequirement" type="{http://www.omg.org/spec/CDSS/201105/dss}TraitRequirement" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TraitSetRequirement", propOrder = {
    "traitRequirement"
})
public class TraitSetRequirement
    extends SemanticRequirement
{

    protected List<TraitRequirement> traitRequirement;

    /**
     * Gets the value of the traitRequirement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the traitRequirement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTraitRequirement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TraitRequirement }
     * 
     * 
     */
    public List<TraitRequirement> getTraitRequirement() {
        if (traitRequirement == null) {
            traitRequirement = new ArrayList<TraitRequirement>();
        }
        return this.traitRequirement;
    }

}
