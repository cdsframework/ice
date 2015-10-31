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

/**
 * The parent class containing the data required by a specific CDS use case.  For example, this class can be used to specify that 
 * the evaluation of a patient for the need for a mammogram requires the following data: (i) gender; (ii) age; (iii) past mastectomy history; 
 * and (iv) past mammogram history.
 * 
 * Can include a detailed input specification for the focal patient as well as for related evaluated persons.  Note that it is assumed 
 * that the superset of data required for related evaluated persons are the same for each of the related evaluated persons (e.g., relatives).
 * If input specifications are not provided regarding patients or other evaluated persons, then this signifies that no further constraints 
 * are being placed on required data other than what is expressed through the input data model and its existing template(s).
 */

public class CDSInputSpecification {

    protected PatientInputSpecification patientInputSpecification;
    protected RelatedEvaluatedPersonInputSpecification relatedEvaluatedPersonInputSpecification;
    
	/**
	 * @return the patientInputSpecification
	 */
	public PatientInputSpecification getPatientInputSpecification() {
		return patientInputSpecification;
	}
	/**
	 * @param patientInputSpecification the patientInputSpecification to set
	 */
	public void setPatientInputSpecification(
			PatientInputSpecification patientInputSpecification) {
		this.patientInputSpecification = patientInputSpecification;
	}
	/**
	 * @return the relatedEvaluatedPersonInputSpecification
	 */
	public RelatedEvaluatedPersonInputSpecification getRelatedEvaluatedPersonInputSpecification() {
		return relatedEvaluatedPersonInputSpecification;
	}
	/**
	 * @param relatedEvaluatedPersonInputSpecification the relatedEvaluatedPersonInputSpecification to set
	 */
	public void setRelatedEvaluatedPersonInputSpecification(
			RelatedEvaluatedPersonInputSpecification relatedEvaluatedPersonInputSpecification) {
		this.relatedEvaluatedPersonInputSpecification = relatedEvaluatedPersonInputSpecification;
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
				+ ((patientInputSpecification == null) ? 0
						: patientInputSpecification.hashCode());
		result = prime
				* result
				+ ((relatedEvaluatedPersonInputSpecification == null) ? 0
						: relatedEvaluatedPersonInputSpecification.hashCode());
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
		CDSInputSpecification other = (CDSInputSpecification) obj;
		if (patientInputSpecification == null) {
			if (other.patientInputSpecification != null)
				return false;
		} else if (!patientInputSpecification
				.equals(other.patientInputSpecification))
			return false;
		if (relatedEvaluatedPersonInputSpecification == null) {
			if (other.relatedEvaluatedPersonInputSpecification != null)
				return false;
		} else if (!relatedEvaluatedPersonInputSpecification
				.equals(other.relatedEvaluatedPersonInputSpecification))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CDSInputSpecification [patientInputSpecification="
				+ patientInputSpecification
				+ ", relatedEvaluatedPersonInputSpecification="
				+ relatedEvaluatedPersonInputSpecification + "]";
	}

    
}
