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

import java.util.Arrays;


/**
 * A person who is the subject of evaluation by a CDS system.  May be the focal patient or some other relevant person 
 * (e.g., a relative or a sexual contact). Includes demographic attributes, clinical statements, and related entities.
 * 			
 * EntityBase-entity, clinical statement-clinical statement, and entity-clinical statement relationships may be represented through direct 
 * nesting of content and/or through the use of the relationship entities directly attached to the EvaluatedPerson.
 */

public class EvaluatedPerson extends EntityBase {

    protected Demographics demographics;
	protected boolean focalPerson;			// Note: this value is populated from the external VMR through context conduction
											//		when the evaluatedPersonId represents focal person (root) of entire VMR  

	/**
	 * @return the demographics
	 */
	public Demographics getDemographics() {
		return demographics;
	}
	
	/**
	 * @param demographics the demographics to set
	 */
	public void setDemographics(Demographics demographics) {
		this.demographics = demographics;
	}
	
	/**
	 * @return the focalPerson
	 */
	public boolean isFocalPerson() {
		return focalPerson;
	}
	
	/**
	 * @param focalPerson the focalPerson to set
	 */
	public void setFocalPerson(boolean focalPerson) {
		this.focalPerson = focalPerson;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((demographics == null) ? 0 : demographics.hashCode());
		result = prime * result + (focalPerson ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		EvaluatedPerson other = (EvaluatedPerson) obj;
		if (demographics == null) {
			if (other.demographics != null)
				return false;
		} else if (!demographics.equals(other.demographics))
			return false;
		if (focalPerson != other.focalPerson)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EvaluatedPerson [id=" + id + ", demographics=" + demographics
				+ ", focalPerson=" + focalPerson + ", templateId="
				+ Arrays.toString(templateId) + ", entityType=" + entityType
				+ ", evaluatedPersonId=" + evaluatedPersonId
				+ ", toBeReturned=" + toBeReturned + ", relationshipToSources="
				+ relationshipToSources + "]";
	}
    
}
