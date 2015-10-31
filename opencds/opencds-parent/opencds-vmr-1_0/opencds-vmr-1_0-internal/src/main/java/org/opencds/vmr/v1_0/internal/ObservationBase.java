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
 * The abstract base class for an observation, which is the act of recognizing and noting a fact.
 */

public abstract class ObservationBase extends ClinicalStatement
{
    protected CD observationFocus;
    protected CD observationMethod;
    protected BodySite targetBodySite;

    /**
     * Gets the value of the observationFocus property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getObservationFocus() {
        return observationFocus;
    }

    /**
     * Sets the value of the observationFocus property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setObservationFocus(CD value) {
        this.observationFocus = value;
    }

    /**
     * Gets the value of the observationMethod property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getObservationMethod() {
        return observationMethod;
    }

    /**
     * Sets the value of the observationMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setObservationMethod(CD value) {
        this.observationMethod = value;
    }

    /**
     * Gets the value of the targetBodySite property.
     * 
     * @return
     *     possible object is
     *     {@link String }
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
     *     {@link String }
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
		ObservationBase other = (ObservationBase) obj;
		if (targetBodySite == null) {
			if (other.targetBodySite != null)
				return false;
		} else if (!targetBodySite.equals(other.targetBodySite))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + ", ObservationBase [targetBodySite=" + targetBodySite + "]";
	}
     

}
