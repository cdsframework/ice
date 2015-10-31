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

package org.opencds.vmr.v1_0.internal.cdsinputspecification;

import org.opencds.vmr.v1_0.internal.datatypes.CS;


/**
 * Specifies the clinical statements required regarding the evaluated person of interest.  Can include CodedAttributeRequirements and 
 * TimeAttributeRequirements.
 * 
 * If no CodedAttributeRequirement specified, all relevant clinical statements are required regardless of their coded attributes.  
 * If no TimeAttributeRequirement specified, all relevant clinical statements are required regardless of their time attributes.  
 * All specified CodedAttributeRequirements and TimeAttributeRequirements should be fulfilled in provided ClinicalStatements.
 */

public class ClinicalStatementInputSpecification {

    protected CS requiredGeneralClinicalStatementClass;
    protected CS requiredSpecificClinicalStatementClass;
    
	/**
	 * @return the requiredGeneralClinicalStatementClass
	 */
	public CS getRequiredGeneralClinicalStatementClass() {
		return requiredGeneralClinicalStatementClass;
	}
	/**
	 * @param requiredGeneralClinicalStatementClass the requiredGeneralClinicalStatementClass to set
	 */
	public void setRequiredGeneralClinicalStatementClass(
			CS requiredGeneralClinicalStatementClass) {
		this.requiredGeneralClinicalStatementClass = requiredGeneralClinicalStatementClass;
	}
	/**
	 * @return the requiredSpecificClinicalStatementClass
	 */
	public CS getRequiredSpecificClinicalStatementClass() {
		return requiredSpecificClinicalStatementClass;
	}
	/**
	 * @param requiredSpecificClinicalStatementClass the requiredSpecificClinicalStatementClass to set
	 */
	public void setRequiredSpecificClinicalStatementClass(
			CS requiredSpecificClinicalStatementClass) {
		this.requiredSpecificClinicalStatementClass = requiredSpecificClinicalStatementClass;
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
				+ ((requiredGeneralClinicalStatementClass == null) ? 0
						: requiredGeneralClinicalStatementClass.hashCode());
		result = prime
				* result
				+ ((requiredSpecificClinicalStatementClass == null) ? 0
						: requiredSpecificClinicalStatementClass.hashCode());
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
		ClinicalStatementInputSpecification other = (ClinicalStatementInputSpecification) obj;
		if (requiredGeneralClinicalStatementClass == null) {
			if (other.requiredGeneralClinicalStatementClass != null)
				return false;
		} else if (!requiredGeneralClinicalStatementClass
				.equals(other.requiredGeneralClinicalStatementClass))
			return false;
		if (requiredSpecificClinicalStatementClass == null) {
			if (other.requiredSpecificClinicalStatementClass != null)
				return false;
		} else if (!requiredSpecificClinicalStatementClass
				.equals(other.requiredSpecificClinicalStatementClass))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClinicalStatementInputSpecification [requiredGeneralClinicalStatementClass="
				+ requiredGeneralClinicalStatementClass
				+ ", requiredSpecificClinicalStatementClass="
				+ requiredSpecificClinicalStatementClass + "]";
	}

    
}
