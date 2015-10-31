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

import java.util.List;

import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.INT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;


/**
 * A clinical order for a substance administration.  Includes medication prescriptions.
 */

public class SubstanceAdministrationOrder extends SubstanceAdministrationBase
{

    protected CD criticality;
    protected DoseRestriction doseRestriction;
    protected IVLDate administrationTimeInterval;
    protected List<CD> dosingSig;
    protected INT numberFillsAllowed;
    protected IVLDate orderEventTime;
	/**
	 * @return the criticality
	 */
	public CD getCriticality() {
		return criticality;
	}
	/**
	 * @param criticality the criticality to set
	 */
	public void setCriticality(CD criticality) {
		this.criticality = criticality;
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
	 * @return the dosingSig
	 */
	public List<CD> getDosingSig() {
		return dosingSig;
	}
	/**
	 * @param dosingSig the dosingSig to set
	 */
	public void setDosingSig(List<CD> dosingSig) {
		this.dosingSig = dosingSig;
	}
	/**
	 * @return the numberFillsAllowed
	 */
	public INT getNumberFillsAllowed() {
		return numberFillsAllowed;
	}
	/**
	 * @param numberFillsAllowed the numberFillsAllowed to set
	 */
	public void setNumberFillsAllowed(INT numberFillsAllowed) {
		this.numberFillsAllowed = numberFillsAllowed;
	}
	/**
	 * @return the orderEventTime
	 */
	public IVLDate getOrderEventTime() {
		return orderEventTime;
	}
	/**
	 * @param orderEventTime the orderEventTime to set
	 */
	public void setOrderEventTime(IVLDate orderEventTime) {
		this.orderEventTime = orderEventTime;
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
		result = prime * result
				+ ((criticality == null) ? 0 : criticality.hashCode());
		result = prime * result
				+ ((doseRestriction == null) ? 0 : doseRestriction.hashCode());
		result = prime * result
				+ ((dosingSig == null) ? 0 : dosingSig.hashCode());
		result = prime
				* result
				+ ((numberFillsAllowed == null) ? 0 : numberFillsAllowed
						.hashCode());
		result = prime * result
				+ ((orderEventTime == null) ? 0 : orderEventTime.hashCode());
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
		SubstanceAdministrationOrder other = (SubstanceAdministrationOrder) obj;
		if (administrationTimeInterval == null) {
			if (other.administrationTimeInterval != null)
				return false;
		} else if (!administrationTimeInterval
				.equals(other.administrationTimeInterval))
			return false;
		if (criticality == null) {
			if (other.criticality != null)
				return false;
		} else if (!criticality.equals(other.criticality))
			return false;
		if (doseRestriction == null) {
			if (other.doseRestriction != null)
				return false;
		} else if (!doseRestriction.equals(other.doseRestriction))
			return false;
		if (dosingSig == null) {
			if (other.dosingSig != null)
				return false;
		} else if (!dosingSig.equals(other.dosingSig))
			return false;
		if (numberFillsAllowed == null) {
			if (other.numberFillsAllowed != null)
				return false;
		} else if (!numberFillsAllowed.equals(other.numberFillsAllowed))
			return false;
		if (orderEventTime == null) {
			if (other.orderEventTime != null)
				return false;
		} else if (!orderEventTime.equals(other.orderEventTime))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubstanceAdministrationOrder [" + super.toString() +"criticality=" + criticality
				+ ", doseRestriction=" + doseRestriction
				+ ", administrationTimeInterval=" + administrationTimeInterval
				+ ", dosingSig=" + dosingSig + ", numberFillsAllowed="
				+ numberFillsAllowed + ", orderEventTime=" + orderEventTime
				+ "]";
	}



}
