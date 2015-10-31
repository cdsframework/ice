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

import org.opencds.vmr.v1_0.internal.datatypes.PQ;


/**
 * Referred to in CDA release 2 as maxDoseQuantity.  Specifies the maximum dose that can be given in a specified time interval.
 */

public class DoseRestriction {

    protected PQ maxDoseForInterval;
    protected PQ timeInterval;

    /**
     * Gets the value of the maxDoseForInterval property.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getMaxDoseForInterval() {
        return maxDoseForInterval;
    }

    /**
     * Sets the value of the maxDoseForInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setMaxDoseForInterval(PQ value) {
        this.maxDoseForInterval = value;
    }

    /**
     * Gets the value of the timeInterval property.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getTimeInterval() {
        return timeInterval;
    }

    /**
     * Sets the value of the timeInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setTimeInterval(PQ value) {
        this.timeInterval = value;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((maxDoseForInterval == null) ? 0 : maxDoseForInterval
						.hashCode());
		result = prime * result
				+ ((timeInterval == null) ? 0 : timeInterval.hashCode());
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
		DoseRestriction other = (DoseRestriction) obj;
		if (maxDoseForInterval == null) {
			if (other.maxDoseForInterval != null)
				return false;
		} else if (!maxDoseForInterval.equals(other.maxDoseForInterval))
			return false;
		if (timeInterval == null) {
			if (other.timeInterval != null)
				return false;
		} else if (!timeInterval.equals(other.timeInterval))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DoseRestriction [maxDoseForInterval=" + maxDoseForInterval
				+ ", timeInterval=" + timeInterval + "]";
	}
}
