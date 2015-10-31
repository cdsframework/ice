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
import org.opencds.vmr.v1_0.internal.datatypes.PQ;


/**
 * Abstract base class for the provision of some clinical material or equipment to the subject, such as a wheelchair.
 */

public abstract class SupplyBase extends ClinicalStatement
{
    protected CD supplyCode;
    protected PQ quantity;
    protected BodySite targetBodySite;

    /**
     * Gets the value of the supplyCode property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getSupplyCode() {
        return supplyCode;
    }

    /**
     * Sets the value of the supplyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setSupplyCode(CD value) {
        this.supplyCode = value;
    }

    /**
     * Gets the value of the quantity property.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setQuantity(PQ value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the targetBodySite property.
     * 
     * @return
     *     possible object is
     *     {@link BodySite }
     *     
     */
    public BodySite getTargetBodySite() {
        return targetBodySite;
    }

    /**
     * Sets the value of the targetBodySite property.
     * 
     * @param value
     *     allowed object is
     *     {@link BodySite }
     *     
     */
    public void setTargetBodySite(BodySite value) {
        this.targetBodySite = value;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((targetBodySite == null) ? 0 : targetBodySite.hashCode());
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
		SupplyBase other = (SupplyBase) obj;
		if (targetBodySite == null) {
			if (other.targetBodySite != null)
				return false;
		} else if (!targetBodySite.equals(other.targetBodySite))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + ", SupplyBase [targetBodySite=" + targetBodySite + "]";
	}

}
