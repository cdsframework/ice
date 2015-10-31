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

import org.opencds.vmr.v1_0.internal.datatypes.BL;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.PQ;


/**
 * An assertion regarding a clinical condition of the subject that needs to be treated or managed.
 */

public class Problem extends ProblemBase
{

    protected CD importance;
    protected CD severity;
    protected CD problemStatus;
    protected PQ ageAtOnset;
    protected BL wasCauseOfDeath;

 
	/**
	 * @return the importance
	 */
	public CD getImportance() {
		return importance;
	}


	/**
	 * @param importance the importance to set
	 */
	public void setImportance(CD importance) {
		this.importance = importance;
	}


	/**
	 * @return the severity
	 */
	public CD getSeverity() {
		return severity;
	}


	/**
	 * @param severity the severity to set
	 */
	public void setSeverity(CD severity) {
		this.severity = severity;
	}


	/**
	 * @return the problemStatus
	 */
	public CD getProblemStatus() {
		return problemStatus;
	}


	/**
	 * @param problemStatus the problemStatus to set
	 */
	public void setProblemStatus(CD problemStatus) {
		this.problemStatus = problemStatus;
	}


	/**
	 * @return the ageAtOnset
	 */
	public PQ getAgeAtOnset() {
		return ageAtOnset;
	}


	/**
	 * @param ageAtOnset the ageAtOnset to set
	 */
	public void setAgeAtOnset(PQ ageAtOnset) {
		this.ageAtOnset = ageAtOnset;
	}


	/**
	 * @return the wasCauseOfDeath
	 */
	public BL getWasCauseOfDeath() {
		return wasCauseOfDeath;
	}


	/**
	 * @param wasCauseOfDeath the wasCauseOfDeath to set
	 */
	public void setWasCauseOfDeath(BL wasCauseOfDeath) {
		this.wasCauseOfDeath = wasCauseOfDeath;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((ageAtOnset == null) ? 0 : ageAtOnset.hashCode());
		result = prime * result
				+ ((importance == null) ? 0 : importance.hashCode());
		result = prime * result
				+ ((problemStatus == null) ? 0 : problemStatus.hashCode());
		result = prime * result
				+ ((severity == null) ? 0 : severity.hashCode());
		result = prime * result
				+ ((wasCauseOfDeath == null) ? 0 : wasCauseOfDeath.hashCode());
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
		Problem other = (Problem) obj;
		if (ageAtOnset == null) {
			if (other.ageAtOnset != null)
				return false;
		} else if (!ageAtOnset.equals(other.ageAtOnset))
			return false;
		if (importance == null) {
			if (other.importance != null)
				return false;
		} else if (!importance.equals(other.importance))
			return false;
		if (problemStatus == null) {
			if (other.problemStatus != null)
				return false;
		} else if (!problemStatus.equals(other.problemStatus))
			return false;
		if (severity == null) {
			if (other.severity != null)
				return false;
		} else if (!severity.equals(other.severity))
			return false;
		if (wasCauseOfDeath == null) {
			if (other.wasCauseOfDeath != null)
				return false;
		} else if (!wasCauseOfDeath.equals(other.wasCauseOfDeath))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString() 
				+ ", Problem [importance=" + importance 
				+ ", severity=" + severity
				+ ", problemStatus=" + problemStatus 
				+ ", ageAtOnset=" + ageAtOnset 
				+ ", wasCauseOfDeath=" + wasCauseOfDeath
				+ "]";
	}

}
