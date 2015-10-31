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


package org.opencds.vmr.v1_0.internal.cdsinputspecification;

import java.util.ArrayList;
import java.util.List;

import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.II;
import org.opencds.vmr.v1_0.internal.datatypes.PQ;


/**
 * Specifies the data required regarding entities related to the evaluated person of interest.
 */

public class RelatedEntityInputSpecification {

    protected CD requiredRelationshipType;
    protected PQ requiredRelationshipSearchBackTimePeriod;
    protected PQ requiredRelationshipSearchFowardTimePeriod;
    protected List<II> requiredEntityTemplate;

    /**
     * Gets the value of the requiredRelationshipType property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getRequiredRelationshipType() {
        return requiredRelationshipType;
    }

    /**
     * Sets the value of the requiredRelationshipType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setRequiredRelationshipType(CD value) {
        this.requiredRelationshipType = value;
    }

    /**
     * Gets the value of the requiredRelationshipSearchBackTimePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getRequiredRelationshipSearchBackTimePeriod() {
        return requiredRelationshipSearchBackTimePeriod;
    }

    /**
     * Sets the value of the requiredRelationshipSearchBackTimePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setRequiredRelationshipSearchBackTimePeriod(PQ value) {
        this.requiredRelationshipSearchBackTimePeriod = value;
    }

    /**
     * Gets the value of the requiredRelationshipSearchFowardTimePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getRequiredRelationshipSearchFowardTimePeriod() {
        return requiredRelationshipSearchFowardTimePeriod;
    }

    /**
     * Sets the value of the requiredRelationshipSearchFowardTimePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setRequiredRelationshipSearchFowardTimePeriod(PQ value) {
        this.requiredRelationshipSearchFowardTimePeriod = value;
    }

    public List<II> getRequiredEntityTemplate() {
        if (requiredEntityTemplate == null) {
            requiredEntityTemplate = new ArrayList<II>();
        }
        return this.requiredEntityTemplate;
    }

	public void setRequiredEntityTemplate(List<II> requiredEntityTemplate) {
		this.requiredEntityTemplate = requiredEntityTemplate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((requiredEntityTemplate == null) ? 0
						: requiredEntityTemplate.hashCode());
		result = prime
				* result
				+ ((requiredRelationshipSearchBackTimePeriod == null) ? 0
						: requiredRelationshipSearchBackTimePeriod.hashCode());
		result = prime
				* result
				+ ((requiredRelationshipSearchFowardTimePeriod == null) ? 0
						: requiredRelationshipSearchFowardTimePeriod.hashCode());
		result = prime
				* result
				+ ((requiredRelationshipType == null) ? 0
						: requiredRelationshipType.hashCode());
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
		RelatedEntityInputSpecification other = (RelatedEntityInputSpecification) obj;
		if (requiredEntityTemplate == null) {
			if (other.requiredEntityTemplate != null)
				return false;
		} else if (!requiredEntityTemplate.equals(other.requiredEntityTemplate))
			return false;
		if (requiredRelationshipSearchBackTimePeriod == null) {
			if (other.requiredRelationshipSearchBackTimePeriod != null)
				return false;
		} else if (!requiredRelationshipSearchBackTimePeriod
				.equals(other.requiredRelationshipSearchBackTimePeriod))
			return false;
		if (requiredRelationshipSearchFowardTimePeriod == null) {
			if (other.requiredRelationshipSearchFowardTimePeriod != null)
				return false;
		} else if (!requiredRelationshipSearchFowardTimePeriod
				.equals(other.requiredRelationshipSearchFowardTimePeriod))
			return false;
		if (requiredRelationshipType == null) {
			if (other.requiredRelationshipType != null)
				return false;
		} else if (!requiredRelationshipType
				.equals(other.requiredRelationshipType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString()
				+ ", RelatedEntityInputSpecification [requiredRelationshipType="
				+ requiredRelationshipType
				+ ", requiredRelationshipSearchBackTimePeriod="
				+ requiredRelationshipSearchBackTimePeriod
				+ ", requiredRelationshipSearchFowardTimePeriod="
				+ requiredRelationshipSearchFowardTimePeriod
				+ ", requiredEntityTemplate=" + requiredEntityTemplate + "]";
	}
    

}
