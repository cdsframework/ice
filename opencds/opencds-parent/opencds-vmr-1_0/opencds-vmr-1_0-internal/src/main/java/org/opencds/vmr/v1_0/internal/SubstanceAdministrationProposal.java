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
import org.opencds.vmr.v1_0.internal.datatypes.INT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;


/**
 * Proposal for a substance administration.  Used, for example, when a CDS system proposes that a medication or vaccination be given.
 */

public class SubstanceAdministrationProposal extends SubstanceAdministrationBase
{
    protected CD criticality;
    protected DoseRestriction doseRestriction;
    protected IVLDate proposedAdministrationTimeInterval;
    protected IVLDate validAdministrationTimeInterval;
    protected INT numberFillsAllowed;


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
	 * @return the proposedAdministrationTimeInterval
	 */
	public IVLDate getProposedAdministrationTimeInterval() {
		return proposedAdministrationTimeInterval;
	}


	/**
	 * @param proposedAdministrationTimeInterval the proposedAdministrationTimeInterval to set
	 */
	public void setProposedAdministrationTimeInterval(
			IVLDate proposedAdministrationTimeInterval) {
		this.proposedAdministrationTimeInterval = proposedAdministrationTimeInterval;
	}


	/**
	 * @return the validAdministrationTimeInterval
	 */
	public IVLDate getValidAdministrationTimeInterval() {
		return validAdministrationTimeInterval;
	}


	/**
	 * @param validAdministrationTimeInterval the validAdministrationTimeInterval to set
	 */
	public void setValidAdministrationTimeInterval(
			IVLDate validAdministrationTimeInterval) {
		this.validAdministrationTimeInterval = validAdministrationTimeInterval;
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


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((criticality == null) ? 0 : criticality.hashCode());
		result = prime * result
				+ ((doseRestriction == null) ? 0 : doseRestriction.hashCode());
		result = prime
				* result
				+ ((numberFillsAllowed == null) ? 0 : numberFillsAllowed
						.hashCode());
		result = prime
				* result
				+ ((proposedAdministrationTimeInterval == null) ? 0
						: proposedAdministrationTimeInterval.hashCode());
		result = prime
				* result
				+ ((validAdministrationTimeInterval == null) ? 0
						: validAdministrationTimeInterval.hashCode());
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
		SubstanceAdministrationProposal other = (SubstanceAdministrationProposal) obj;
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
		if (numberFillsAllowed == null) {
			if (other.numberFillsAllowed != null)
				return false;
		} else if (!numberFillsAllowed.equals(other.numberFillsAllowed))
			return false;
		if (proposedAdministrationTimeInterval == null) {
			if (other.proposedAdministrationTimeInterval != null)
				return false;
		} else if (!proposedAdministrationTimeInterval
				.equals(other.proposedAdministrationTimeInterval))
			return false;
		if (validAdministrationTimeInterval == null) {
			if (other.validAdministrationTimeInterval != null)
				return false;
		} else if (!validAdministrationTimeInterval
				.equals(other.validAdministrationTimeInterval))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return super.toString()
				+ ", SubstanceAdministrationProposal [criticality=" + criticality
				+ ", doseRestriction=" + doseRestriction
				+ ", proposedAdministrationTimeInterval="
				+ proposedAdministrationTimeInterval
				+ ", validAdministrationTimeInterval="
				+ validAdministrationTimeInterval + ", numberFillsAllowed="
				+ numberFillsAllowed 
				+ "]";
	}

}
