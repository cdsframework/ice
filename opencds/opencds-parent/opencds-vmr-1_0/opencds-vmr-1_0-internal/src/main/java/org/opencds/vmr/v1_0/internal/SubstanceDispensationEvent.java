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

import org.opencds.vmr.v1_0.internal.datatypes.INT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;
import org.opencds.vmr.v1_0.internal.datatypes.PQ;


/**
 * This is the Event of a pharmacy filling a prescription.
 */

public class SubstanceDispensationEvent extends SubstanceAdministrationBase {

    protected INT daysSupply;
    protected PQ dispensationQuantity;
    protected DoseRestriction doseRestriction;
    protected IVLDate dispensationTime;
    protected INT fillNumber;
    protected INT fillsRemaining;

	/**
	 * @return the daysSupply
	 */
	public INT getDaysSupply() {
		return daysSupply;
	}

	/**
	 * @param daysSupply the daysSupply to set
	 */
	public void setDaysSupply(INT daysSupply) {
		this.daysSupply = daysSupply;
	}

	/**
	 * @return the dispensationQuantity
	 */
	public PQ getDispensationQuantity() {
		return dispensationQuantity;
	}

	/**
	 * @param dispensationQuantity the dispensationQuantity to set
	 */
	public void setDispensationQuantity(PQ dispensationQuantity) {
		this.dispensationQuantity = dispensationQuantity;
	}

	/**
	 * @return the doseRestriction
	 */
	public DoseRestriction getDoseRestriction() {
		return doseRestriction;
	}

	/**
	 * @param doseRestriction the doseRestriction to set
	 */
	public void setDoseRestriction(DoseRestriction doseRestriction) {
		this.doseRestriction = doseRestriction;
	}

	/**
	 * @return the dispensationTime
	 */
	public IVLDate getDispensationTime() {
		return dispensationTime;
	}

	/**
	 * @param dispensationTime the dispensationTime to set
	 */
	public void setDispensationTime(IVLDate dispensationTime) {
		this.dispensationTime = dispensationTime;
	}

	/**
	 * @return the fillNumber
	 */
	public INT getFillNumber() {
		return fillNumber;
	}

	/**
	 * @param fillNumber the fillNumber to set
	 */
	public void setFillNumber(INT fillNumber) {
		this.fillNumber = fillNumber;
	}

	/**
	 * @return the fillsRemaining
	 */
	public INT getFillsRemaining() {
		return fillsRemaining;
	}

	/**
	 * @param fillsRemaining the fillsRemaining to set
	 */
	public void setFillsRemaining(INT fillsRemaining) {
		this.fillsRemaining = fillsRemaining;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((daysSupply == null) ? 0 : daysSupply.hashCode());
		result = prime
				* result
				+ ((dispensationQuantity == null) ? 0 : dispensationQuantity
						.hashCode());
		result = prime
				* result
				+ ((dispensationTime == null) ? 0 : dispensationTime.hashCode());
		result = prime * result
				+ ((doseRestriction == null) ? 0 : doseRestriction.hashCode());
		result = prime * result
				+ ((fillNumber == null) ? 0 : fillNumber.hashCode());
		result = prime * result
				+ ((fillsRemaining == null) ? 0 : fillsRemaining.hashCode());
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
		SubstanceDispensationEvent other = (SubstanceDispensationEvent) obj;
		if (daysSupply == null) {
			if (other.daysSupply != null)
				return false;
		} else if (!daysSupply.equals(other.daysSupply))
			return false;
		if (dispensationQuantity == null) {
			if (other.dispensationQuantity != null)
				return false;
		} else if (!dispensationQuantity.equals(other.dispensationQuantity))
			return false;
		if (dispensationTime == null) {
			if (other.dispensationTime != null)
				return false;
		} else if (!dispensationTime.equals(other.dispensationTime))
			return false;
		if (doseRestriction == null) {
			if (other.doseRestriction != null)
				return false;
		} else if (!doseRestriction.equals(other.doseRestriction))
			return false;
		if (fillNumber == null) {
			if (other.fillNumber != null)
				return false;
		} else if (!fillNumber.equals(other.fillNumber))
			return false;
		if (fillsRemaining == null) {
			if (other.fillsRemaining != null)
				return false;
		} else if (!fillsRemaining.equals(other.fillsRemaining))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubstanceDispensationEvent [daysSupply=" + daysSupply
				+ ", dispensationQuantity=" + dispensationQuantity
				+ ", doseRestriction=" + doseRestriction
				+ ", dispensationTime=" + dispensationTime + ", fillNumber="
				+ fillNumber + ", fillsRemaining=" + fillsRemaining
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
