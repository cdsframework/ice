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

import java.util.Arrays;
import java.util.List;

import org.opencds.vmr.v1_0.internal.datatypes.CD;


/**
 * A physical thing, group of physical things or an organization. 
 */

public class EntityBase {

	protected String[] templateId;
	protected String id;						//this is used in code as the subjectPersonId, and it may or may not be the same as the focalPersonId
    protected CD entityType;
    
	protected String evaluatedPersonId;			// Note: this value is populated from the external VMR through context conduction, 
												//		and represents the root patient for one VMR.  It is also called subjectPersonId in some contexts.
//	protected boolean isFocalPerson;			// Note: this value is populated from the external VMR through context conduction
//isFocalPerson does not belong here, moved to EvaluatedPerson	//		when the evaluatedPersonId represents focal person (root) of entire VMR    

	protected boolean toBeReturned;				//flags whether this particular entity is to be returned as part of output data
	protected List<RelationshipToSource> relationshipToSources;	//Note: list of ClinicalStatement or EntityBase sourceIds and
												//		targetRelationshipToSource values that this Entity is related to.  This list
												//		should NOT be updated or modified by rules.
   
	/**
	 * @return the templateId
	 */
	public String[] getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(String[] templateId) {
		this.templateId = templateId;
	}

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
	 * @return the entityType
	 */
	public CD getEntityType() {
		return entityType;
	}

	/**
	 * @param entityType the entityType to set
	 */
	public void setEntityType(CD entityType) {
		this.entityType = entityType;
	}

	/**
	 * @return the evaluatedPersonId
	 */
	public String getEvaluatedPersonId() {
		return evaluatedPersonId;
	}

	/**
	 * @param evaluatedPersonId the evaluatedPersonId to set
	 */
	public void setEvaluatedPersonId(String evaluatedPersonId) {
		this.evaluatedPersonId = evaluatedPersonId;
	}

	/**
	 * @return the toBeReturnData
	 */
	public boolean isToBeReturned() {
		return toBeReturned;
	}

	/**
	 * @param toBeReturnData the toBeReturnData to set
	 */
	public void setToBeReturned(boolean toBeReturned) {
		this.toBeReturned = toBeReturned;
	}

	/**
	 * @return the relationshipToSources
	 */
	public List<RelationshipToSource> getRelationshipToSources() {
		return relationshipToSources;
	}

	/**
	 * @param relationshipToSources the relationshipToSources to set
	 */
	public void setRelationshipToSources(
			List<RelationshipToSource> relationshipToSources) {
		this.relationshipToSources = relationshipToSources;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((entityType == null) ? 0 : entityType.hashCode());
		result = prime
				* result
				+ ((evaluatedPersonId == null) ? 0 : evaluatedPersonId
						.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((relationshipToSources == null) ? 0 : relationshipToSources
						.hashCode());
		result = prime * result + Arrays.hashCode(templateId);
		result = prime * result + (toBeReturned ? 1231 : 1237);
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
		EntityBase other = (EntityBase) obj;
		if (entityType == null) {
			if (other.entityType != null)
				return false;
		} else if (!entityType.equals(other.entityType))
			return false;
		if (evaluatedPersonId == null) {
			if (other.evaluatedPersonId != null)
				return false;
		} else if (!evaluatedPersonId.equals(other.evaluatedPersonId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (relationshipToSources == null) {
			if (other.relationshipToSources != null)
				return false;
		} else if (!relationshipToSources.equals(other.relationshipToSources))
			return false;
		if (!Arrays.equals(templateId, other.templateId))
			return false;
		if (toBeReturned != other.toBeReturned)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EntityBase [templateId=" + Arrays.toString(templateId)
				+ ", id=" + id + ", entityType=" + entityType
				+ ", evaluatedPersonId=" + evaluatedPersonId
				+ ", toBeReturnData=" + toBeReturned
				+ ", relationshipToSources=" + relationshipToSources + "]";
	}

}
