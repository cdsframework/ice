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

import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

/**
 * This model does allow for the presence of information belonging to related persons (such as in the case of family history, or 
 * public health infectious disease cases) for a single patient.  These related persons are modeled as EvaluatedPersons who have associated 
 * ClinicalStatements.  Note that this model is not designed to be an information model for providing CDS for a large population.
 * 
 */
public class EvaluatedPersonRelationship {

    protected String id;
	protected String sourceEntityId;
    protected String targetEntityId;
    protected CD targetRole;
    protected IVLDate relationshipTimeInterval;
    
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the sourceEntityId
	 */
	public String getSourceEntityId() {
		return sourceEntityId;
	}

	/**
	 * @param sourceEntityId the sourceEntityId to set
	 */
	public void setSourceEntityId(String sourceEntityId) {
		this.sourceEntityId = sourceEntityId;
	}

	/**
	 * @return the targetEntityId
	 */
	public String getTargetEntityId() {
		return targetEntityId;
	}

	/**
	 * @param targetEntityId the targetEntityId to set
	 */
	public void setTargetEntityId(String targetEntityId) {
		this.targetEntityId = targetEntityId;
	}

	/**
	 * @return the targetRole
	 */
	public CD getTargetRole() {
		return targetRole;
	}

	/**
	 * @param targetRole the targetRole to set
	 */
	public void setTargetRole(CD targetRole) {
		this.targetRole = targetRole;
	}

	/**
	 * @return the relationshipTimeInterval
	 */
	public IVLDate getRelationshipTimeInterval() {
		return relationshipTimeInterval;
	}

	/**
	 * @param relationshipTimeInterval the relationshipTimeInterval to set
	 */
	public void setRelationshipTimeInterval(IVLDate relationshipTimeInterval) {
		this.relationshipTimeInterval = relationshipTimeInterval;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((relationshipTimeInterval == null) ? 0
						: relationshipTimeInterval.hashCode());
		result = prime * result
				+ ((sourceEntityId == null) ? 0 : sourceEntityId.hashCode());
		result = prime * result
				+ ((targetEntityId == null) ? 0 : targetEntityId.hashCode());
		result = prime * result
				+ ((targetRole == null) ? 0 : targetRole.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EvaluatedPersonRelationship other = (EvaluatedPersonRelationship) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (relationshipTimeInterval == null) {
			if (other.relationshipTimeInterval != null)
				return false;
		} else if (!relationshipTimeInterval
				.equals(other.relationshipTimeInterval))
			return false;
		if (sourceEntityId == null) {
			if (other.sourceEntityId != null)
				return false;
		} else if (!sourceEntityId.equals(other.sourceEntityId))
			return false;
		if (targetEntityId == null) {
			if (other.targetEntityId != null)
				return false;
		} else if (!targetEntityId.equals(other.targetEntityId))
			return false;
		if (targetRole == null) {
			if (other.targetRole != null)
				return false;
		} else if (!targetRole.equals(other.targetRole))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EvaluatedPersonRelationship [id=" + id + ", sourceEntityId="
				+ sourceEntityId + ", targetEntityId=" + targetEntityId
				+ ", targetRole=" + targetRole + ", relationshipTimeInterval="
				+ relationshipTimeInterval + "]";
	}
    
}
