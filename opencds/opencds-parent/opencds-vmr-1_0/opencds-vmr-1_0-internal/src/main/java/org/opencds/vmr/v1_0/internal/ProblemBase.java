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
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;


/**
 * Abstract base class for problems, which are clinical conditions that need to be treated or managed.
 */

public abstract class ProblemBase extends ClinicalStatement
{
    protected CD problemCode;
    protected List<BodySite> affectedBodySite;
    protected IVLDate problemEffectiveTime;
    protected IVLDate diagnosticEventTime;
	/**
	 * @return the problemCode
	 */
	public CD getProblemCode() {
		return problemCode;
	}
	/**
	 * @param problemCode the problemCode to set
	 */
	public void setProblemCode(CD problemCode) {
		this.problemCode = problemCode;
	}
	/**
	 * @return the affectedBodySite
	 */
	public List<BodySite> getAffectedBodySite() {
		return affectedBodySite;
	}
	/**
	 * @param affectedBodySite the affectedBodySite to set
	 */
	public void setAffectedBodySite(List<BodySite> affectedBodySite) {
		this.affectedBodySite = affectedBodySite;
	}
	/**
	 * @return the problemEffectiveTime
	 */
	public IVLDate getProblemEffectiveTime() {
		return problemEffectiveTime;
	}
	/**
	 * @param problemEffectiveTime the problemEffectiveTime to set
	 */
	public void setProblemEffectiveTime(IVLDate problemEffectiveTime) {
		this.problemEffectiveTime = problemEffectiveTime;
	}
	/**
	 * @return the diagnosticEventTime
	 */
	public IVLDate getDiagnosticEventTime() {
		return diagnosticEventTime;
	}
	/**
	 * @param diagnosticEventTime the diagnosticEventTime to set
	 */
	public void setDiagnosticEventTime(IVLDate diagnosticEventTime) {
		this.diagnosticEventTime = diagnosticEventTime;
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
				+ ((affectedBodySite == null) ? 0 : affectedBodySite.hashCode());
		result = prime
				* result
				+ ((diagnosticEventTime == null) ? 0 : diagnosticEventTime
						.hashCode());
		result = prime * result
				+ ((problemCode == null) ? 0 : problemCode.hashCode());
		result = prime
				* result
				+ ((problemEffectiveTime == null) ? 0 : problemEffectiveTime
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
		ProblemBase other = (ProblemBase) obj;
		if (affectedBodySite == null) {
			if (other.affectedBodySite != null)
				return false;
		} else if (!affectedBodySite.equals(other.affectedBodySite))
			return false;
		if (diagnosticEventTime == null) {
			if (other.diagnosticEventTime != null)
				return false;
		} else if (!diagnosticEventTime.equals(other.diagnosticEventTime))
			return false;
		if (problemCode == null) {
			if (other.problemCode != null)
				return false;
		} else if (!problemCode.equals(other.problemCode))
			return false;
		if (problemEffectiveTime == null) {
			if (other.problemEffectiveTime != null)
				return false;
		} else if (!problemEffectiveTime.equals(other.problemEffectiveTime))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProblemBase ["+ super.toString() + ", problemCode=" + problemCode
				+ ", affectedBodySite=" + affectedBodySite
				+ ", problemEffectiveTime=" + problemEffectiveTime
				+ ", diagnosticEventTime=" + diagnosticEventTime + "]";
	}

}
