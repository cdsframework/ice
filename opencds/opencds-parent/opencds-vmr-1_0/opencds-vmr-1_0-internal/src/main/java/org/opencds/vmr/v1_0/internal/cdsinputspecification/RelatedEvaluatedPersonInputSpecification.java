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

import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.PQ;


/**
 * The data required for evaluated persons related to the patient.  Is a specialization of the EvaluatedPersonInputSpecification class.  
 * Includes a specification of the scope of evaluated persons that are required.
 */

public class RelatedEvaluatedPersonInputSpecification
    extends EvaluatedPersonInputSpecification
{

    protected CD inclusionScope;
    protected Integer inclusionScopeChainDepth;
    protected PQ inclusionScopeDistance;

    /**
     * Gets the value of the inclusionScope property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getInclusionScope() {
        return inclusionScope;
    }

    /**
     * Sets the value of the inclusionScope property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setInclusionScope(CD value) {
        this.inclusionScope = value;
    }

    /**
     * Gets the value of the inclusionScopeChainDepth property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getInclusionScopeChainDepth() {
        return inclusionScopeChainDepth;
    }

    /**
     * Sets the value of the inclusionScopeChainDepth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setInclusionScopeChainDepth(Integer value) {
        this.inclusionScopeChainDepth = value;
    }

    /**
     * Gets the value of the inclusionScopeDistance property.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getInclusionScopeDistance() {
        return inclusionScopeDistance;
    }

    /**
     * Sets the value of the inclusionScopeDistance property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setInclusionScopeDistance(PQ value) {
        this.inclusionScopeDistance = value;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((inclusionScope == null) ? 0 : inclusionScope.hashCode());
		result = prime
				* result
				+ ((inclusionScopeChainDepth == null) ? 0
						: inclusionScopeChainDepth.hashCode());
		result = prime
				* result
				+ ((inclusionScopeDistance == null) ? 0
						: inclusionScopeDistance.hashCode());
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
		RelatedEvaluatedPersonInputSpecification other = (RelatedEvaluatedPersonInputSpecification) obj;
		if (inclusionScope == null) {
			if (other.inclusionScope != null)
				return false;
		} else if (!inclusionScope.equals(other.inclusionScope))
			return false;
		if (inclusionScopeChainDepth == null) {
			if (other.inclusionScopeChainDepth != null)
				return false;
		} else if (!inclusionScopeChainDepth
				.equals(other.inclusionScopeChainDepth))
			return false;
		if (inclusionScopeDistance == null) {
			if (other.inclusionScopeDistance != null)
				return false;
		} else if (!inclusionScopeDistance.equals(other.inclusionScopeDistance))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() 
				+ ", RelatedEvaluatedPersonInputSpecification [inclusionScope="
				+ inclusionScope + ", inclusionScopeChainDepth="
				+ inclusionScopeChainDepth + ", inclusionScopeDistance="
				+ inclusionScopeDistance + "]";
	}
   
}
