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
 * Abstract base class for adverse events, which are unfavorable healthcare events (e.g., death, rash, difficulty breathing) 
 * that are thought to have been caused by some agent (e.g., a medication, immunization, food, or environmental agent). 
 * If a given agent is thought to cause multiple reactions, these reactions should be represented using multiple adverse events.
 * 
 * <p>Java class for AdverseEventBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * 
 */
public abstract class AdverseEventBase extends ClinicalStatement
{

    protected CD adverseEventCode;
    protected CD adverseEventAgent;
    protected IVLDate adverseEventTime;
    protected IVLDate documentationTime;
    protected List<BodySite> affectedBodySite;
    
    /** 
     * generated getters and setters, hash code and equals(), and toString() methods follow 
     */
    
	public CD getAdverseEventCode() {
		return adverseEventCode;
	}
	public void setAdverseEventCode(CD adverseEventCode) {
		this.adverseEventCode = adverseEventCode;
	}
	public CD getAdverseEventAgent() {
		return adverseEventAgent;
	}
	public void setAdverseEventAgent(CD adverseEventAgent) {
		this.adverseEventAgent = adverseEventAgent;
	}
	public IVLDate getAdverseEventTime() {
		return adverseEventTime;
	}
	public void setAdverseEventTime(IVLDate adverseEventTime) {
		this.adverseEventTime = adverseEventTime;
	}
	public IVLDate getDocumentationTime() {
		return documentationTime;
	}
	public void setDocumentationTime(IVLDate documentationTime) {
		this.documentationTime = documentationTime;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((adverseEventAgent == null) ? 0 : adverseEventAgent
						.hashCode());
		result = prime
				* result
				+ ((adverseEventCode == null) ? 0 : adverseEventCode.hashCode());
		result = prime
				* result
				+ ((adverseEventTime == null) ? 0 : adverseEventTime.hashCode());
		result = prime
				* result
				+ ((documentationTime == null) ? 0 : documentationTime
						.hashCode());
		result = prime
				* result
				+ ((affectedBodySite == null) ? 0 : affectedBodySite.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AdverseEventBase other = (AdverseEventBase) obj;
		if (adverseEventAgent == null) {
			if (other.adverseEventAgent != null)
				return false;
		} else if (!adverseEventAgent.equals(other.adverseEventAgent))
			return false;
		if (adverseEventCode == null) {
			if (other.adverseEventCode != null)
				return false;
		} else if (!adverseEventCode.equals(other.adverseEventCode))
			return false;
		if (adverseEventTime == null) {
			if (other.adverseEventTime != null)
				return false;
		} else if (!adverseEventTime.equals(other.adverseEventTime))
			return false;
		if (documentationTime == null) {
			if (other.documentationTime != null)
				return false;
		} else if (!documentationTime.equals(other.documentationTime))
			return false;
		if (affectedBodySite == null) {
			if (other.affectedBodySite != null)
				return false;
		} else if (!affectedBodySite.equals(other.affectedBodySite))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return super.toString() 
				+ ", AdverseEventBase [adverseEventCode=" + adverseEventCode
				+ ", adverseEventAgent=" + adverseEventAgent
				+ ", adverseEventTime=" + adverseEventTime
				+ ", documentationTime=" + documentationTime 
				+ ", affectedBodySite=" + affectedBodySite
				+ "]";
	}
       
}
