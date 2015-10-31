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

import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;


/**
 * An appointment that was (i) scheduled, (ii) not rescheduled or canceled, and (iii) for which the EvaluatedPerson did not show up.
 */

public class MissedAppointment extends EncounterBase
{

    protected IVLDate appointmentTime;


	/**
	 * @return the appointmentTime
	 */
	public IVLDate getAppointmentTime() {
		return appointmentTime;
	}


	/**
	 * @param appointmentTime the appointmentTime to set
	 */
	public void setAppointmentTime(IVLDate appointmentTime) {
		this.appointmentTime = appointmentTime;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((appointmentTime == null) ? 0 : appointmentTime.hashCode());
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
		MissedAppointment other = (MissedAppointment) obj;
		if (appointmentTime == null) {
			if (other.appointmentTime != null)
				return false;
		} else if (!appointmentTime.equals(other.appointmentTime))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return super.toString()
				+ ", MissedAppointment [appointmentTime=" + appointmentTime
				+ "]";
	}
    
}
