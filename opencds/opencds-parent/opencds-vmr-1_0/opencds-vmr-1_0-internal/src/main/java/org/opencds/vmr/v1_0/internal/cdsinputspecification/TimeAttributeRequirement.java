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

import org.opencds.vmr.v1_0.internal.datatypes.CS;
import org.opencds.vmr.v1_0.internal.datatypes.PQ;


/**
 * A requirement for a time attribute of a clinical statement.  Specified in terms of the target time attribute and the required time 
 * interval for that attribute in related to the index evaluation time.  A searchBackTimePeriod and/or a searchForwardTimePeriod 
 * must be provided.
 */

public class TimeAttributeRequirement {

    protected CS targetTimeAttribute;
    protected PQ searchBackTimePeriod;
    protected PQ searchForwardTimePeriod;

    /**
     * Gets the value of the targetTimeAttribute property.
     * 
     * @return
     *     possible object is
     *     {@link CS }
     *     
     */
    public CS getTargetTimeAttribute() {
        return targetTimeAttribute;
    }

    /**
     * Sets the value of the targetTimeAttribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link CS }
     *     
     */
    public void setTargetTimeAttribute(CS value) {
        this.targetTimeAttribute = value;
    }

    /**
     * Gets the value of the searchBackTimePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getSearchBackTimePeriod() {
        return searchBackTimePeriod;
    }

    /**
     * Sets the value of the searchBackTimePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setSearchBackTimePeriod(PQ value) {
        this.searchBackTimePeriod = value;
    }

    /**
     * Gets the value of the searchForwardTimePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getSearchForwardTimePeriod() {
        return searchForwardTimePeriod;
    }

    /**
     * Sets the value of the searchForwardTimePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setSearchForwardTimePeriod(PQ value) {
        this.searchForwardTimePeriod = value;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((searchBackTimePeriod == null) ? 0 : searchBackTimePeriod
						.hashCode());
		result = prime
				* result
				+ ((searchForwardTimePeriod == null) ? 0
						: searchForwardTimePeriod.hashCode());
		result = prime
				* result
				+ ((targetTimeAttribute == null) ? 0 : targetTimeAttribute
						.hashCode());
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
		TimeAttributeRequirement other = (TimeAttributeRequirement) obj;
		if (searchBackTimePeriod == null) {
			if (other.searchBackTimePeriod != null)
				return false;
		} else if (!searchBackTimePeriod.equals(other.searchBackTimePeriod))
			return false;
		if (searchForwardTimePeriod == null) {
			if (other.searchForwardTimePeriod != null)
				return false;
		} else if (!searchForwardTimePeriod
				.equals(other.searchForwardTimePeriod))
			return false;
		if (targetTimeAttribute == null) {
			if (other.targetTimeAttribute != null)
				return false;
		} else if (!targetTimeAttribute.equals(other.targetTimeAttribute))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimeAttributeRequirement [targetTimeAttribute="
				+ targetTimeAttribute + ", searchBackTimePeriod="
				+ searchBackTimePeriod + ", searchForwardTimePeriod="
				+ searchForwardTimePeriod + "]";
	}

}
