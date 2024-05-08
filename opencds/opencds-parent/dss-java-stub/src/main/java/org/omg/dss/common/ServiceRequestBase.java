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

package org.omg.dss.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

import org.omg.dss.evaluation.Evaluate;
import org.omg.dss.evaluation.EvaluateAtSpecifiedTime;
import org.omg.dss.evaluation.EvaluateIteratively;
import org.omg.dss.evaluation.EvaluateIterativelyAtSpecifiedTime;
import org.omg.dss.metadata.DescribeProfile;
import org.omg.dss.metadata.DescribeScopingEntity;
import org.omg.dss.metadata.DescribeScopingEntityHierarchy;
import org.omg.dss.metadata.DescribeSemanticRequirement;
import org.omg.dss.metadata.DescribeSemanticSignifier;
import org.omg.dss.metadata.DescribeTrait;
import org.omg.dss.metadata.ListProfiles;
import org.omg.dss.query.FindKMs;
import org.omg.dss.query.GetKMDataRequirements;
import org.omg.dss.query.GetKMDataRequirementsForEvaluationAtSpecifiedTime;
import org.omg.dss.query.GetKMDescription;
import org.omg.dss.query.GetKMEvaluationResultSemantics;
import org.omg.dss.query.ListKMs;


/**
 * This is the base class for all service requests and contains an InteractionIdentifier.
 *
 * <p>Java class for ServiceRequestBase complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ServiceRequestBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="interactionId" type="{http://www.omg.org/spec/CDSS/201105/dss}InteractionIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceRequestBase", propOrder = {
    "interactionId"
})
@XmlSeeAlso({
    GetKMDataRequirements.class,
    GetKMDataRequirementsForEvaluationAtSpecifiedTime.class,
    FindKMs.class,
    DescribeScopingEntity.class,
    DescribeProfile.class,
    EvaluateAtSpecifiedTime.class,
    Evaluate.class,
    GetKMEvaluationResultSemantics.class,
    DescribeSemanticSignifier.class,
    DescribeSemanticRequirement.class,
    ListKMs.class,
    EvaluateIteratively.class,
    DescribeTrait.class,
    EvaluateIterativelyAtSpecifiedTime.class,
    DescribeScopingEntityHierarchy.class,
    GetKMDescription.class,
    ListProfiles.class
})
public class ServiceRequestBase {

    @XmlElement(required = true)
    protected InteractionIdentifier interactionId;

    /**
     * Gets the value of the interactionId property.
     *
     * @return
     *     possible object is
     *     {@link InteractionIdentifier }
     *
     */
    public InteractionIdentifier getInteractionId() {
        return interactionId;
    }

    /**
     * Sets the value of the interactionId property.
     *
     * @param value
     *     allowed object is
     *     {@link InteractionIdentifier }
     *
     */
    public void setInteractionId(InteractionIdentifier value) {
        this.interactionId = value;
    }

}
