/**
 * Copyright 2011 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.CD;


/**
 * The situation or context within which a CDS evaluation is made.  Included in CDS inputs for HL7 Context-Aware Knowledge Retrieval 
 * (Infobutton) Knowledge Request standard.  Used, for example, to generate human-readable care guidance in the end-user's preferred language.
 */

public class CDSContext {

    protected CD cdsSystemUserType;
    protected CD cdsSystemUserPreferredLanguage;
    protected CD cdsInformationRecipientType;
    protected CD cdsInformationRecipientPreferredLanguage;
    protected CD cdsSystemUserTaskContext;

    /**
     * Gets the value of the cdsSystemUserType property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getCdsSystemUserType() {
        return cdsSystemUserType;
    }

    /**
     * Sets the value of the cdsSystemUserType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setCdsSystemUserType(CD value) {
        this.cdsSystemUserType = value;
    }

    /**
     * Gets the value of the cdsSystemUserPreferredLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getCdsSystemUserPreferredLanguage() {
        return cdsSystemUserPreferredLanguage;
    }

    /**
     * Sets the value of the cdsSystemUserPreferredLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setCdsSystemUserPreferredLanguage(CD value) {
        this.cdsSystemUserPreferredLanguage = value;
    }

    /**
     * Gets the value of the cdsInformationRecipientType property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getCdsInformationRecipientType() {
        return cdsInformationRecipientType;
    }

    /**
     * Sets the value of the cdsInformationRecipientType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setCdsInformationRecipientType(CD value) {
        this.cdsInformationRecipientType = value;
    }

    /**
     * Gets the value of the cdsInformationRecipientPreferredLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getCdsInformationRecipientPreferredLanguage() {
        return cdsInformationRecipientPreferredLanguage;
    }

    /**
     * Sets the value of the cdsInformationRecipientPreferredLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setCdsInformationRecipientPreferredLanguage(CD value) {
        this.cdsInformationRecipientPreferredLanguage = value;
    }

    /**
     * Gets the value of the cdsSystemUserTaskContext property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getCdsSystemUserTaskContext() {
        return cdsSystemUserTaskContext;
    }

    /**
     * Sets the value of the cdsSystemUserTaskContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setCdsSystemUserTaskContext(CD value) {
        this.cdsSystemUserTaskContext = value;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((cdsInformationRecipientPreferredLanguage == null) ? 0
						: cdsInformationRecipientPreferredLanguage.hashCode());
		result = prime
				* result
				+ ((cdsInformationRecipientType == null) ? 0
						: cdsInformationRecipientType.hashCode());
		result = prime
				* result
				+ ((cdsSystemUserPreferredLanguage == null) ? 0
						: cdsSystemUserPreferredLanguage.hashCode());
		result = prime
				* result
				+ ((cdsSystemUserTaskContext == null) ? 0
						: cdsSystemUserTaskContext.hashCode());
		result = prime
				* result
				+ ((cdsSystemUserType == null) ? 0 : cdsSystemUserType
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CDSContext other = (CDSContext) obj;
		if (cdsInformationRecipientPreferredLanguage == null) {
			if (other.cdsInformationRecipientPreferredLanguage != null)
				return false;
		} else if (!cdsInformationRecipientPreferredLanguage
				.equals(other.cdsInformationRecipientPreferredLanguage))
			return false;
		if (cdsInformationRecipientType == null) {
			if (other.cdsInformationRecipientType != null)
				return false;
		} else if (!cdsInformationRecipientType
				.equals(other.cdsInformationRecipientType))
			return false;
		if (cdsSystemUserPreferredLanguage == null) {
			if (other.cdsSystemUserPreferredLanguage != null)
				return false;
		} else if (!cdsSystemUserPreferredLanguage
				.equals(other.cdsSystemUserPreferredLanguage))
			return false;
		if (cdsSystemUserTaskContext == null) {
			if (other.cdsSystemUserTaskContext != null)
				return false;
		} else if (!cdsSystemUserTaskContext
				.equals(other.cdsSystemUserTaskContext))
			return false;
		if (cdsSystemUserType == null) {
			if (other.cdsSystemUserType != null)
				return false;
		} else if (!cdsSystemUserType.equals(other.cdsSystemUserType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CDSContext [cdsSystemUserType=" + cdsSystemUserType
				+ ", cdsSystemUserPreferredLanguage="
				+ cdsSystemUserPreferredLanguage
				+ ", cdsInformationRecipientType="
				+ cdsInformationRecipientType
				+ ", cdsInformationRecipientPreferredLanguage="
				+ cdsInformationRecipientPreferredLanguage
				+ ", cdsSystemUserTaskContext=" + cdsSystemUserTaskContext
				+ "]";
	}

}
