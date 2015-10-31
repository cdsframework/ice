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
 * BodySite, used in ClinicalStatement
 * 
 */

public class BodySite {

//	protected II id;				//Note: this is an assigned unique id for internal purposes
//	protected II clinicalStatementId;	
    protected CD bodySiteCode;
    protected CD laterality;
    
	/**
	 * @return the bodySiteCode
	 */
	public CD getBodySiteCode() {
		return bodySiteCode;
	}

	/**
	 * @param bodySiteCode the bodySiteCode to set
	 */
	public void setBodySiteCode(CD bodySiteCode) {
		this.bodySiteCode = bodySiteCode;
	}

	/**
	 * @return the laterality
	 */
	public CD getLaterality() {
		return laterality;
	}

	/**
	 * @param laterality the laterality to set
	 */
	public void setLaterality(CD laterality) {
		this.laterality = laterality;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bodySiteCode == null) ? 0 : bodySiteCode.hashCode());
		result = prime * result
				+ ((laterality == null) ? 0 : laterality.hashCode());
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
		BodySite other = (BodySite) obj;
		if (bodySiteCode == null) {
			if (other.bodySiteCode != null)
				return false;
		} else if (!bodySiteCode.equals(other.bodySiteCode))
			return false;
		if (laterality == null) {
			if (other.laterality != null)
				return false;
		} else if (!laterality.equals(other.laterality))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BodySite [bodySiteCode=" + bodySiteCode
				+ ", laterality=" + laterality + "]";
	}

}