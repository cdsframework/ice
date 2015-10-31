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
 * The parent class containing the data used by a CDS system to generate inferences.  Includes an input vMR and optionally CDS context 
 * and/or CDS resource data.
 */

public class CDSInput {

    protected List<String> templateId;
    protected List<CDSResource> cdsResource;
//    protected VMR vmrInput;  -- just using the focalPersonId to substitute for the VMR
    protected String focalPersonId;		//in place of the full VMR
    protected CDSContext cdsContext;
    
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
	 * @return the cdsResource
	 */
	public List<CDSResource> getCdsResource() {
		return cdsResource;
	}

	/**
	 * @param cdsResource the cdsResource to set
	 */
	public void setCdsResource(List<CDSResource> cdsResource) {
		this.cdsResource = cdsResource;
	}

	/**
	 * @return the cdsContext
	 */
	public CDSContext getCdsContext() {
		return cdsContext;
	}

	/**
	 * @param cdsContext the cdsContext to set
	 */
	public void setCdsContext(CDSContext cdsContext) {
		this.cdsContext = cdsContext;
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
				+ ((cdsContext == null) ? 0 : cdsContext.hashCode());
		result = prime * result
				+ ((cdsResource == null) ? 0 : cdsResource.hashCode());
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
		CDSInput other = (CDSInput) obj;
		if (cdsContext == null) {
			if (other.cdsContext != null)
				return false;
		} else if (!cdsContext.equals(other.cdsContext))
			return false;
		if (cdsResource == null) {
			if (other.cdsResource != null)
				return false;
		} else if (!cdsResource.equals(other.cdsResource))
			return false;
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
		String templateIdList = "";
		if (templateId != null) {
			for (String eachTemplateId : templateId) {
				if (eachTemplateId != null) {
					templateIdList += eachTemplateId + " ";
				}
			}
		}
		return "CDSInput [templateId=" + templateIdList + ", focalPersonId="
				+ focalPersonId + ", cdsContext=" + cdsContext + "]";
	}

}
