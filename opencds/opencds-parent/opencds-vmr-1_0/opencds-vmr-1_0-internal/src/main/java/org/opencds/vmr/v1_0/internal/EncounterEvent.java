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
 */

package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;


/**
 * EncounterEvent is the record of an interaction between an EvaluatedPerson and the healthcare system.  It can be used to group 
 * observations and interventions performed during that interaction, through the use of relatedClinicalStatements.
 */

public class EncounterEvent extends EncounterBase {

    protected IVLDate encounterEventTime;


	/**
	 * @return the encounterEventTime
	 */
	public IVLDate getEncounterEventTime() {
		return encounterEventTime;
	}


	/**
	 * @param encounterEventTime the encounterEventTime to set
	 */
	public void setEncounterEventTime(IVLDate encounterEventTime) {
		this.encounterEventTime = encounterEventTime;
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
				+ ((encounterEventTime == null) ? 0 : encounterEventTime
						.hashCode());
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
		EncounterEvent other = (EncounterEvent) obj;
		if (encounterEventTime == null) {
			if (other.encounterEventTime != null)
				return false;
		} else if (!encounterEventTime.equals(other.encounterEventTime))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return super.toString() 
				+ ", EncounterEvent [encounterEventTime=" + encounterEventTime
				+ "]";
	}

}
