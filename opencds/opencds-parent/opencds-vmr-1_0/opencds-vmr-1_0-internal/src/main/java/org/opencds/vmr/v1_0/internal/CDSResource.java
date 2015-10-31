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


/**
 * A resource independent of individual patients, provided to a CDS engine to facilitate patient evaluation.  Includes, for example, 
 * local antibiogram data (local susceptibility profile of microbes to different antimicrobial agents), local formulary restrictions, 
 * or CDS system user preference on which guidelines to use for health maintenance (e.g., HEDIS vs. USPSTF).
 */

public class CDSResource {

    protected CD cdsResourceType;
    protected Object resourceContents;

    /**
     * Gets the value of the cdsResourceType property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getCdsResourceType() {
        return cdsResourceType;
    }

    /**
     * Sets the value of the cdsResourceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setCdsResourceType(CD value) {
        this.cdsResourceType = value;
    }

    /**
     * Gets the value of the resourceContents property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getResourceContents() {
        return resourceContents;
    }

    /**
     * Sets the value of the resourceContents property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setResourceContents(Object value) {
        this.resourceContents = value;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((resourceContents == null) ? 0 : resourceContents.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CDSResource other = (CDSResource) obj;
		if (resourceContents == null) {
			if (other.resourceContents != null)
				return false;
		} else if (!resourceContents.equals(other.resourceContents))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CDSResource [resourceContents=" + resourceContents + "]";
	}

}
