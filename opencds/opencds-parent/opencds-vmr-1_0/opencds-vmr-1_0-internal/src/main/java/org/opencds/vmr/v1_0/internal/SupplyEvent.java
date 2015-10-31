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

import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;


/**
 * The provision of some clinical material or equipment to the subject, such as a wheelchair.
 */
public class SupplyEvent extends SupplyBase {

    protected IVLDate supplyTime;

    /**
     * Gets the value of the supplyTime property.
     * 
     * @return
     *     possible object is
     *     {@link IVLDate }
     *     
     */
    public IVLDate getSupplyTime() {
        return supplyTime;
    }

    /**
     * Sets the value of the supplyTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLDate }
     *     
     */
    public void setSupplyTime(IVLDate value) {
        this.supplyTime = value;
    }


    @Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((supplyTime == null) ? 0 : supplyTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplyEvent other = (SupplyEvent) obj;
		if (supplyTime == null) {
			if (other.supplyTime != null)
				return false;
		} else if (!supplyTime.equals(other.supplyTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() 
				+ ", SupplyEvent [supplyTime=" + supplyTime
				+ "]";
	}

}
