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
 * A request by a provider to schedule an appointment.
 * 
 */

public class AppointmentRequest extends EncounterBase
{

    protected CD criticality;
    protected IVLDate requestedAppointmentTime;
    protected IVLDate requestIssuanceTime;
    protected INT repeatNumber;


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
	 * @return the requestedAppointmentTime
	 */
	public IVLDate getRequestedAppointmentTime() {
		return requestedAppointmentTime;
	}


	/**
	 * @param requestedAppointmentTime the requestedAppointmentTime to set
	 */
	public void setRequestedAppointmentTime(IVLDate requestedAppointmentTime) {
		this.requestedAppointmentTime = requestedAppointmentTime;
	}


	/**
	 * @return the requestIssuanceTime
	 */
	public IVLDate getRequestIssuanceTime() {
		return requestIssuanceTime;
	}


	/**
	 * @param requestIssuanceTime the requestIssuanceTime to set
	 */
	public void setRequestIssuanceTime(IVLDate requestIssuanceTime) {
		this.requestIssuanceTime = requestIssuanceTime;
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
				+ ((repeatNumber == null) ? 0 : repeatNumber.hashCode());
		result = prime
				* result
				+ ((requestIssuanceTime == null) ? 0 : requestIssuanceTime
						.hashCode());
		result = prime
				* result
				+ ((requestedAppointmentTime == null) ? 0
						: requestedAppointmentTime.hashCode());
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
		AppointmentRequest other = (AppointmentRequest) obj;
		if (criticality == null) {
			if (other.criticality != null)
				return false;
		} else if (!criticality.equals(other.criticality))
			return false;
		if (repeatNumber == null) {
			if (other.repeatNumber != null)
				return false;
		} else if (!repeatNumber.equals(other.repeatNumber))
			return false;
		if (requestIssuanceTime == null) {
			if (other.requestIssuanceTime != null)
				return false;
		} else if (!requestIssuanceTime.equals(other.requestIssuanceTime))
			return false;
		if (requestedAppointmentTime == null) {
			if (other.requestedAppointmentTime != null)
				return false;
		} else if (!requestedAppointmentTime
				.equals(other.requestedAppointmentTime))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return super.toString() + ", AppointmentRequest [criticality=" + criticality
				+ ", requestedAppointmentTime=" + requestedAppointmentTime
				+ ", requestIssuanceTime=" + requestIssuanceTime
				+ ", repeatNumber=" + repeatNumber + "]";
	}
	
}
