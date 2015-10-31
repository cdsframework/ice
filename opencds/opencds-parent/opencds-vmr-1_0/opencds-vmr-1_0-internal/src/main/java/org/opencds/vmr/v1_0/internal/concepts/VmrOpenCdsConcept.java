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

package org.opencds.vmr.v1_0.internal.concepts;



/**
 * A VmrOpenCdsConcept is an OpenCDS concept associated with a class within the VMR.
 * 
 * Note that a given class may have multiple attachment points for concept (e.g., problem code,
 * problem status code).  Also note that a given attachment point may have multiple concepts
 * (e.g., diabetes mellitus and type 1 diabetes mellitus for problem code).
 * 
 * Also note that otherwise same concepts may have different determinationMethods.
 * 
 * The targeted attachment point is indicated by the concrete implementation classes' names.
 * 
 * @author kawam001
 *
 */
public abstract class VmrOpenCdsConcept {
	protected String Id;				//Note this is a unique internal Id, generated from a UUID
	protected String conceptTargetId; // id of the concept target (e.g., ClinicalStatement, Entity)
	protected String openCdsConceptCode; 
	protected String determinationMethodCode;
	protected String displayName;
	/**
	 * @return the id
	 */
	public String getId() {
		return Id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		Id = id;
	}
	/**
	 * @return the conceptTargetId
	 */
	public String getConceptTargetId() {
		return conceptTargetId;
	}
	/**
	 * @param conceptTargetId the conceptTargetId to set
	 */
	public void setConceptTargetId(String conceptTargetId) {
		this.conceptTargetId = conceptTargetId;
	}
	/**
	 * @return the openCdsConceptCode
	 */
	public String getOpenCdsConceptCode() {
		return openCdsConceptCode;
	}
	/**
	 * @param openCdsConceptCode the openCdsConceptCode to set
	 */
	public void setOpenCdsConceptCode(String openCdsConceptCode) {
		this.openCdsConceptCode = openCdsConceptCode;
	}
	/**
	 * @return the determinationMethodCode
	 */
	public String getDeterminationMethodCode() {
		return determinationMethodCode;
	}
	/**
	 * @param determinationMethodCode the determinationMethodCode to set
	 */
	public void setDeterminationMethodCode(String determinationMethodCode) {
		this.determinationMethodCode = determinationMethodCode;
	}
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Id == null) ? 0 : Id.hashCode());
		result = prime * result
				+ ((conceptTargetId == null) ? 0 : conceptTargetId.hashCode());
		result = prime
				* result
				+ ((determinationMethodCode == null) ? 0
						: determinationMethodCode.hashCode());
		result = prime
				* result
				+ ((openCdsConceptCode == null) ? 0 : openCdsConceptCode
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VmrOpenCdsConcept other = (VmrOpenCdsConcept) obj;
		if (Id == null) {
			if (other.Id != null)
				return false;
		} else if (!Id.equals(other.Id))
			return false;
		if (conceptTargetId == null) {
			if (other.conceptTargetId != null)
				return false;
		} else if (!conceptTargetId.equals(other.conceptTargetId))
			return false;
		if (determinationMethodCode == null) {
			if (other.determinationMethodCode != null)
				return false;
		} else if (!determinationMethodCode
				.equals(other.determinationMethodCode))
			return false;
		if (openCdsConceptCode == null) {
			if (other.openCdsConceptCode != null)
				return false;
		} else if (!openCdsConceptCode.equals(other.openCdsConceptCode))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VmrOpenCdsConcept [Id=" + Id + ", conceptTargetId="
				+ conceptTargetId + ", openCdsConceptCode="
				+ openCdsConceptCode + ", determinationMethodCode="
				+ determinationMethodCode + ", displayName=" + displayName
				+ "]";
	}
	
	
}