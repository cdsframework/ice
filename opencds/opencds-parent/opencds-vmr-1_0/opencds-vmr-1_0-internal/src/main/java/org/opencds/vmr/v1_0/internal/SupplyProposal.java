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
 * Proposal, e.g., by a CDS system, for a Supply to be delivered.
 */

public class SupplyProposal extends SupplyBase {

    protected IVLDate proposedSupplyTime;
    protected INT repeatNumber;
    protected CD criticality;

	/**
	 * @return the proposedSupplyTime
	 */
	public IVLDate getProposedSupplyTime() {
		return proposedSupplyTime;
	}

	/**
	 * @param proposedSupplyTime the proposedSupplyTime to set
	 */
	public void setProposedSupplyTime(IVLDate proposedSupplyTime) {
		this.proposedSupplyTime = proposedSupplyTime;
	}

	/**
	 * @return the repeatNumber
	 */
	public INT getRepeatNumber() {
		return repeatNumber;
	}

	/**
	 * @param repeatNumber the repeatNumber to set
	 */
	public void setRepeatNumber(INT repeatNumber) {
		this.repeatNumber = repeatNumber;
	}

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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((criticality == null) ? 0 : criticality.hashCode());
		result = prime
				* result
				+ ((proposedSupplyTime == null) ? 0 : proposedSupplyTime
						.hashCode());
		result = prime * result
				+ ((repeatNumber == null) ? 0 : repeatNumber.hashCode());
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
		SupplyProposal other = (SupplyProposal) obj;
		if (criticality == null) {
			if (other.criticality != null)
				return false;
		} else if (!criticality.equals(other.criticality))
			return false;
		if (proposedSupplyTime == null) {
			if (other.proposedSupplyTime != null)
				return false;
		} else if (!proposedSupplyTime.equals(other.proposedSupplyTime))
			return false;
		if (repeatNumber == null) {
			if (other.repeatNumber != null)
				return false;
		} else if (!repeatNumber.equals(other.repeatNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString()
				+ ", SupplyProposal [proposedSupplyTime=" + proposedSupplyTime
				+ ", repeatNumber=" + repeatNumber
				+ ", criticality=" + criticality
				+ "]";
	}

}
