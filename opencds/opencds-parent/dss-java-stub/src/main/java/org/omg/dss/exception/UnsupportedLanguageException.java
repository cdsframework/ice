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

package org.omg.dss.exception;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * The UnsupportedLanguageException is thrown when the language is recognized but not supported.
 *
 *
 * <p>Java class for UnsupportedLanguageException complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="UnsupportedLanguageException">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DSSException">
 *       &lt;sequence>
 *         &lt;element name="unsupportedLanguage" type="{http://www.omg.org/spec/CDSS/201105/dss}Language"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnsupportedLanguageException", propOrder = {
    "unsupportedLanguage"
})
public class UnsupportedLanguageException
    extends DSSException
{

    @XmlElement(required = true)
    protected String unsupportedLanguage;

    /**
     * Gets the value of the unsupportedLanguage property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUnsupportedLanguage() {
        return unsupportedLanguage;
    }

    /**
     * Sets the value of the unsupportedLanguage property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUnsupportedLanguage(String value) {
        this.unsupportedLanguage = value;
    }

}
