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

package org.omg.dss.metadata.semanticsignifier;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.omg.dss.common.ScopedDO;


/**
 * A SemanticSignifier uniquely specifies an information model
 *
 * <p>Java class for SemanticSignifier complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SemanticSignifier">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ScopedDO">
 *       &lt;sequence>
 *         &lt;element name="xsdComputableDefinition" type="{http://www.omg.org/spec/CDSS/201105/dss}XSDComputableDefinition"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SemanticSignifier", propOrder = {
    "xsdComputableDefinition"
})
public class SemanticSignifier
    extends ScopedDO
{

    @XmlElement(required = true)
    protected XSDComputableDefinition xsdComputableDefinition;

    /**
     * Gets the value of the xsdComputableDefinition property.
     *
     * @return
     *     possible object is
     *     {@link XSDComputableDefinition }
     *
     */
    public XSDComputableDefinition getXsdComputableDefinition() {
        return xsdComputableDefinition;
    }

    /**
     * Sets the value of the xsdComputableDefinition property.
     *
     * @param value
     *     allowed object is
     *     {@link XSDComputableDefinition }
     *
     */
    public void setXsdComputableDefinition(XSDComputableDefinition value) {
        this.xsdComputableDefinition = value;
    }

}
