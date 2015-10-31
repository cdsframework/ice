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

import java.util.List;

/**
 * A virtual medical record (vMR) contains information about a patient relevant for CDS, either with regard to the data used for 
 * generating inferences (input) or the conclusions reached as a result of analyzing the data (output).  A vMR may contain, for example, 
 * problems and medications or CDS-generated assessments and recommended actions.  Note that CDS-generated assessments and recommended actions 
 * would typically be considered a CDS output but could also be used as a CDS input as well (e.g., prior CDS system recommendations could 
 * influence current CDS system recommendations).
 * 
 * This model does allow for the presence of information belonging to related persons (such as in the case of family history, or 
 * public health infectious disease cases) for a single patient.  These related persons are modeled as EvaluatedPersons who have associated 
 * ClinicalStatements.  Note that this model is not designed to be an information model for providing CDS for a large population.
 * 
 * Note that enumerations and value domains are anticipated to be specified in profiles in additional ballots.
 */

public class VMR {

	protected List<String> templateId;
//    protected EvaluatedPerson patient;
	protected String focalPersonId;

	/**
	 * @return the templateId
	 */
	public List<String> getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(List<String> templateId) {
		this.templateId = templateId;
	}

	/**
	 * @return the focalPersonId
	 */
	public String getFocalPersonId() {
		return focalPersonId;
	}

	/**
	 * @param focalPersonId the focalPersonId to set
	 */
	public void setFocalPersonId(String focalPersonId) {
		this.focalPersonId = focalPersonId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((focalPersonId == null) ? 0 : focalPersonId.hashCode());
		result = prime * result
				+ ((templateId == null) ? 0 : templateId.hashCode());
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
		VMR other = (VMR) obj;
		if (focalPersonId == null) {
			if (other.focalPersonId != null)
				return false;
		} else if (!focalPersonId.equals(other.focalPersonId))
			return false;
		if (templateId == null) {
			if (other.templateId != null)
				return false;
		} else if (!templateId.equals(other.templateId))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VMR [templateId=" + templateId + ", focalPersonId="
				+ focalPersonId + "]";
	}


}
