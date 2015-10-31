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
 * RelationshipToSource is used internally to keep track of a list of "source" statements that an individual
 * RelatedEntity, or RelatedClinicalStatement pertains to.  
 * 
 * This list is used before and after calling the inferencing engine to help populate input/output xml into 
 * the correct structure, and these elements should not be modified by rules.
 * 
 * 
 */
public class RelationshipToSource {

	protected String sourceId;					// Note: this value represents the Id of the clinicalStatement/entity
												// 		that is the immediate parent of this clinicalStatement/entity
												// 		if this clinicalStatement/entity is not a root.
	protected CD relationshipToSource;			// relationship of this clinicalStatement/entity to its parent 
												//		clinicalStatement/entity.

	/**
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * @return the relationshipToSource
	 */
	public CD getRelationshipToSource() {
		return relationshipToSource;
	}

	/**
	 * @param relationshipToSource the relationshipToSource to set
	 */
	public void setRelationshipToSource(CD relationshipToSource) {
		this.relationshipToSource = relationshipToSource;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((relationshipToSource == null) ? 0
						: relationshipToSource.hashCode());
		result = prime * result
				+ ((sourceId == null) ? 0 : sourceId.hashCode());
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
		RelationshipToSource other = (RelationshipToSource) obj;
		if (relationshipToSource == null) {
			if (other.relationshipToSource != null)
				return false;
		} else if (!relationshipToSource.equals(other.relationshipToSource))
			return false;
		if (sourceId == null) {
			if (other.sourceId != null)
				return false;
		} else if (!sourceId.equals(other.sourceId))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RelationshipToSource [sourceId=" + sourceId
				+ ", relationshipToSource=" + relationshipToSource + "]";
	}

}
