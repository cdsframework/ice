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

import java.util.Arrays;

import org.opencds.vmr.v1_0.internal.datatypes.BL;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.INT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;


/**
 * The actual administration of the substance.
 * 
 * Handling of entries in "current medication list" with no other information than current medications would be as follows:
 * - SubstanceAdministrationEvent with documentationTime = time when snapshot was taken of current medication list, 
 * administrationEventTime = null if no information provided on when medication was started or stopped, administrationTime with 
 * specified Low but null High if information only provided on when medication was started.
 * 
 * To specify "patient takes an unknown drug", use a code for substance that represents "unknown medication".
 */

public class SubstanceAdministrationEvent extends SubstanceAdministrationBase
{

    protected INT doseNumber;
    protected IVLDate administrationTimeInterval;
    protected IVLDate documentationTime;
    protected CD informationAttestationType;
    protected BL isValid;

	/**
	 * @return the doseNumber
	 */
	public INT getDoseNumber() {
		return doseNumber;
	}

	/**
	 * @param doseNumber the doseNumber to set
	 */
	public void setDoseNumber(INT doseNumber) {
		this.doseNumber = doseNumber;
	}

	/**
	 * @return the administrationTimeInterval
	 */
	public IVLDate getAdministrationTimeInterval() {
		return administrationTimeInterval;
	}

	/**
	 * @param administrationTimeInterval the administrationTimeInterval to set
	 */
	public void setAdministrationTimeInterval(IVLDate administrationTimeInterval) {
		this.administrationTimeInterval = administrationTimeInterval;
	}

	/**
	 * @return the documentationTime
	 */
	public IVLDate getDocumentationTime() {
		return documentationTime;
	}

	/**
	 * @param documentationTime the documentationTime to set
	 */
	public void setDocumentationTime(IVLDate documentationTime) {
		this.documentationTime = documentationTime;
	}

	/**
	 * @return the informationAttestationType
	 */
	public CD getInformationAttestationType() {
		return informationAttestationType;
	}

	/**
	 * @param informationAttestationType the informationAttestationType to set
	 */
	public void setInformationAttestationType(CD informationAttestationType) {
		this.informationAttestationType = informationAttestationType;
	}

	/**
	 * @return the isValid
	 */
	public BL getIsValid() {
		return isValid;
	}

	/**
	 * @param isValid the isValid to set
	 */
	public void setIsValid(BL isValid) {
		this.isValid = isValid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((administrationTimeInterval == null) ? 0
						: administrationTimeInterval.hashCode());
		result = prime
				* result
				+ ((documentationTime == null) ? 0 : documentationTime
						.hashCode());
		result = prime * result
				+ ((doseNumber == null) ? 0 : doseNumber.hashCode());
		result = prime
				* result
				+ ((informationAttestationType == null) ? 0
						: informationAttestationType.hashCode());
		result = prime * result + ((isValid == null) ? 0 : isValid.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubstanceAdministrationEvent other = (SubstanceAdministrationEvent) obj;
		if (administrationTimeInterval == null) {
			if (other.administrationTimeInterval != null)
				return false;
		} else if (!administrationTimeInterval
				.equals(other.administrationTimeInterval))
			return false;
		if (documentationTime == null) {
			if (other.documentationTime != null)
				return false;
		} else if (!documentationTime.equals(other.documentationTime))
			return false;
		if (doseNumber == null) {
			if (other.doseNumber != null)
				return false;
		} else if (!doseNumber.equals(other.doseNumber))
			return false;
		if (informationAttestationType == null) {
			if (other.informationAttestationType != null)
				return false;
		} else if (!informationAttestationType
				.equals(other.informationAttestationType))
			return false;
		if (isValid == null) {
			if (other.isValid != null)
				return false;
		} else if (!isValid.equals(other.isValid))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubstanceAdministrationEvent [doseNumber=" + doseNumber
				+ ", administrationTimeInterval=" + administrationTimeInterval
				+ ", documentationTime=" + documentationTime
				+ ", informationAttestationType=" + informationAttestationType
				+ ", isValid=" + isValid
				+ ", substanceAdministrationGeneralPurpose="
				+ substanceAdministrationGeneralPurpose + ", substance="
				+ substance + ", deliveryMethod=" + deliveryMethod
				+ ", doseQuantity=" + doseQuantity + ", deliveryRoute="
				+ deliveryRoute + ", approachBodySite=" + approachBodySite
				+ ", targetBodySite=" + targetBodySite + ", dosingPeriod="
				+ dosingPeriod + ", dosingPeriodIntervalIsImportant="
				+ dosingPeriodIntervalIsImportant + ", deliveryRate="
				+ deliveryRate + ", doseType=" + doseType + ", templateId="
				+ Arrays.toString(templateId) + ", id=" + id
				+ ", dataSourceType=" + dataSourceType + ", evaluatedPersonId="
				+ evaluatedPersonId + ", subjectIsFocalPerson="
				+ subjectIsFocalPerson + ", clinicalStatementToBeRoot="
				+ clinicalStatementToBeRoot + ", toBeReturned=" + toBeReturned
				+ ", relationshipToSources=" + relationshipToSources + "]";
	}
}
