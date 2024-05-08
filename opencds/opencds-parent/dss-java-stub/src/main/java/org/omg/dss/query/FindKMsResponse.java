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

package org.omg.dss.query;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.omg.dss.query.responses.RankedKMList;


/**
 * <p>Java class for findKMsResponse complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="findKMsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="rankedKMList" type="{http://www.omg.org/spec/CDSS/201105/dss}RankedKMList"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findKMsResponse", propOrder = {
    "rankedKMList"
})
public class FindKMsResponse {

    @XmlElement(required = true)
    protected RankedKMList rankedKMList;

    /**
     * Gets the value of the rankedKMList property.
     *
     * @return
     *     possible object is
     *     {@link RankedKMList }
     *
     */
    public RankedKMList getRankedKMList() {
        return rankedKMList;
    }

    /**
     * Sets the value of the rankedKMList property.
     *
     * @param value
     *     allowed object is
     *     {@link RankedKMList }
     *
     */
    public void setRankedKMList(RankedKMList value) {
        this.rankedKMList = value;
    }

}
