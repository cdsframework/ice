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
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;


/**
 * A clinical end or aim towards which effort is directed.
 */

public class Goal extends GoalBase
{

    protected IVLDate goalObserverEventTime;
    protected CD goalStatus;

 
	/**
	 * @return the goalObserverEventTime
	 */
	public IVLDate getGoalObserverEventTime() {
		return goalObserverEventTime;
	}


	/**
	 * @param goalObserverEventTime the goalObserverEventTime to set
	 */
	public void setGoalObserverEventTime(IVLDate goalObserverEventTime) {
		this.goalObserverEventTime = goalObserverEventTime;
	}


	/**
	 * @return the goalStatus
	 */
	public CD getGoalStatus() {
		return goalStatus;
	}


	/**
	 * @param goalStatus the goalStatus to set
	 */
	public void setGoalStatus(CD goalStatus) {
		this.goalStatus = goalStatus;
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
				+ ((goalObserverEventTime == null) ? 0 : goalObserverEventTime
						.hashCode());
		result = prime * result
				+ ((goalStatus == null) ? 0 : goalStatus.hashCode());
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
		Goal other = (Goal) obj;
		if (goalObserverEventTime == null) {
			if (other.goalObserverEventTime != null)
				return false;
		} else if (!goalObserverEventTime.equals(other.goalObserverEventTime))
			return false;
		if (goalStatus == null) {
			if (other.goalStatus != null)
				return false;
		} else if (!goalStatus.equals(other.goalStatus))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return super.toString()
				+ ", Goal [goalObserverEventTime=" + goalObserverEventTime
				+ ", goalStatus=" + goalStatus 
				+ "]";
	}
    
}
