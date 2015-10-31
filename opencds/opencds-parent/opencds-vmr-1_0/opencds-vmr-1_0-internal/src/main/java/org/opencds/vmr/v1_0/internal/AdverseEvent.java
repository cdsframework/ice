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


/**
 * unfavorable healthcare event (e.g., death, rash, difficulty breathing) that is thought to have been caused 
 * 		by some agent (e.g., a medication, immunization, food, or environmental agent).
 * 
 * 
 */

public class AdverseEvent extends AdverseEventBase
{

    protected CD criticality;
    protected CD severity;
//    protected List<BodySite> affectedBodySite;  //moved to AdverseEventBase
    protected CD adverseEventStatus;
    
    /** 
     * generated getters and setters, hash code and equals(), and toString() methods follow 
     */
    
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
//	/**
//	 * @return the affectedBodySite
//	 */
//	public List<BodySite> getAffectedBodySite() {
//		return affectedBodySite;
//	}
//	/**
//	 * @param affectedBodySite the affectedBodySite to set
//	 */
//	public void setAffectedBodySite(List<BodySite> affectedBodySite) {
//		this.affectedBodySite = affectedBodySite;
//	}
	/**
	 * @return the adverseEventStatus
	 */
	public CD getAdverseEventStatus() {
		return adverseEventStatus;
	}
	/**
	 * @param adverseEventStatus the adverseEventStatus to set
	 */
	public void setAdverseEventStatus(CD adverseEventStatus) {
		this.adverseEventStatus = adverseEventStatus;
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
				+ ((adverseEventStatus == null) ? 0 : adverseEventStatus
						.hashCode());
//		result = prime
//				* result
//				+ ((affectedBodySite == null) ? 0 : affectedBodySite.hashCode());
		result = prime * result
				+ ((criticality == null) ? 0 : criticality.hashCode());
		result = prime * result
				+ ((severity == null) ? 0 : severity.hashCode());
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
		AdverseEvent other = (AdverseEvent) obj;
		if (adverseEventStatus == null) {
			if (other.adverseEventStatus != null)
				return false;
		} else if (!adverseEventStatus.equals(other.adverseEventStatus))
			return false;
//		if (affectedBodySite == null) {
//			if (other.affectedBodySite != null)
//				return false;
//		} else if (!affectedBodySite.equals(other.affectedBodySite))
//			return false;
		if (criticality == null) {
			if (other.criticality != null)
				return false;
		} else if (!criticality.equals(other.criticality))
			return false;
		if (severity == null) {
			if (other.severity != null)
				return false;
		} else if (!severity.equals(other.severity))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return super.toString()
				+ ", AdverseEvent [criticality=" + criticality 
				+ ", severity=" + severity 
//				+ ", affectedBodySite=" + affectedBodySite
				+ ", adverseEventStatus=" + adverseEventStatus
				+ "]";
	}
    
}
