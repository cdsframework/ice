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
 * The actual event of performing a procedure.
 */

public class ProcedureEvent extends ProcedureBase
{

    protected IVLDate procedureTime;


	/**
	 * @return the procedureTime
	 */
	public IVLDate getProcedureTime() {
		return procedureTime;
	}


	/**
	 * @param procedureTime the procedureTime to set
	 */
	public void setProcedureTime(IVLDate procedureTime) {
		this.procedureTime = procedureTime;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((procedureTime == null) ? 0 : procedureTime.hashCode());
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
		ProcedureEvent other = (ProcedureEvent) obj;
		if (procedureTime == null) {
			if (other.procedureTime != null)
				return false;
		} else if (!procedureTime.equals(other.procedureTime))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return super.toString()
				+ ", ProcedureEvent [procedureTime=" + procedureTime
				+ "]";
	}
    
}
