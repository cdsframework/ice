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
import org.opencds.vmr.v1_0.internal.BodySite;


/**
 * Abstract base class for a procedure, which is a series of steps taken on a subject to accomplish a clinical goal.
 */

public abstract class ProcedureBase extends ClinicalStatement
{

    protected CD procedureCode;
    protected CD procedureMethod;
    protected BodySite approachBodySite;
    protected BodySite targetBodySite;

    /**
     * Gets the value of the procedureCode property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getProcedureCode() {
        return procedureCode;
    }

    /**
     * Sets the value of the procedureCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setProcedureCode(CD value) {
        this.procedureCode = value;
    }

    /**
     * Gets the value of the procedureMethod property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getProcedureMethod() {
        return procedureMethod;
    }

    /**
     * Sets the value of the procedureMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setProcedureMethod(CD value) {
        this.procedureMethod = value;
    }

    /**
     * Gets the value of the approachBodySite property.
     * 
     * @return
     *     possible object is
     *     {@link BodySite }
     *     
     */
    public BodySite getApproachBodySite() {
        return approachBodySite;
    }

    /**
     * Sets the value of the approachBodySite property.
     * 
     * @param value
     *     allowed object is
     *     {@link BodySite }
     *     
     */
    public void setApproachBodySite(BodySite value) {
        this.approachBodySite = value;
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
		result = prime
				* result
				+ ((approachBodySite == null) ? 0 : approachBodySite.hashCode());
		result = prime * result
				+ ((procedureCode == null) ? 0 : procedureCode.hashCode());
		result = prime * result
				+ ((procedureMethod == null) ? 0 : procedureMethod.hashCode());
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
		ProcedureBase other = (ProcedureBase) obj;
		if (approachBodySite == null) {
			if (other.approachBodySite != null)
				return false;
		} else if (!approachBodySite.equals(other.approachBodySite))
			return false;
		if (procedureCode == null) {
			if (other.procedureCode != null)
				return false;
		} else if (!procedureCode.equals(other.procedureCode))
			return false;
		if (procedureMethod == null) {
			if (other.procedureMethod != null)
				return false;
		} else if (!procedureMethod.equals(other.procedureMethod))
			return false;
		if (targetBodySite == null) {
			if (other.targetBodySite != null)
				return false;
		} else if (!targetBodySite.equals(other.targetBodySite))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString()
				+ ", ProcedureBase [procedureCode=" + procedureCode
				+ ", procedureMethod=" + procedureMethod
				+ ", approachBodySite=" + approachBodySite
				+ ", targetBodySite=" + targetBodySite + "]";
	}

}
