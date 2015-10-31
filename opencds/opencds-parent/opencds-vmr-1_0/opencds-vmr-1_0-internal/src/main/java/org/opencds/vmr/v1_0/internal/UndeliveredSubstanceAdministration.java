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
 * Documents the non-delivery of a substance.  E.g., documents that an influenza immunization was not given because the 
 * patient refused or had an adverse reaction to a previous flu vaccine.
 */

public class UndeliveredSubstanceAdministration extends SubstanceAdministrationBase {

    protected CD reason;
    protected IVLDate subjectEffectiveTime;
    protected IVLDate documentationTime;


	/**
	 * @return the reason
	 */
	public CD getReason() {
		return reason;
	}


	/**
	 * @param reason the reason to set
	 */
	public void setReason(CD reason) {
		this.reason = reason;
	}


	/**
	 * @return the subjectEffectiveTime
	 */
	public IVLDate getSubjectEffectiveTime() {
		return subjectEffectiveTime;
	}


	/**
	 * @param subjectEffectiveTime the subjectEffectiveTime to set
	 */
	public void setSubjectEffectiveTime(IVLDate subjectEffectiveTime) {
		this.subjectEffectiveTime = subjectEffectiveTime;
	}


	/**
	 * @return the documentationTime
	 */
	public IVLDate getDocumentationTime() {
		return documentationTime;
	}


	/**
	 * @param documentationTime the documentationTime to set
	 */
	public void setDocumentationTime(IVLDate documentationTime) {
		this.documentationTime = documentationTime;
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
				+ ((documentationTime == null) ? 0 : documentationTime
						.hashCode());
		result = prime * result + ((reason == null) ? 0 : reason.hashCode());
		result = prime
				* result
				+ ((subjectEffectiveTime == null) ? 0 : subjectEffectiveTime
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
		UndeliveredSubstanceAdministration other = (UndeliveredSubstanceAdministration) obj;
		if (documentationTime == null) {
			if (other.documentationTime != null)
				return false;
		} else if (!documentationTime.equals(other.documentationTime))
			return false;
		if (reason == null) {
			if (other.reason != null)
				return false;
		} else if (!reason.equals(other.reason))
			return false;
		if (subjectEffectiveTime == null) {
			if (other.subjectEffectiveTime != null)
				return false;
		} else if (!subjectEffectiveTime.equals(other.subjectEffectiveTime))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return super.toString()
				+ ", UndeliveredSubstanceAdministration [reason=" + reason
				+ ", subjectEffectiveTime=" + subjectEffectiveTime
				+ ", documentationTime=" + documentationTime
				+ "]";
	}

}
