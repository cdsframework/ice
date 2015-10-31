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
 * The abstract base class for an encounter of an EvaluatedPerson with the healthcare system.  If an encounter or appointment has been 
 * canceled, it should simply not be provided using this model.  This allows the encounter and appointment classes to be used without an 
 * explicit encounter status check.
 */

public abstract class EncounterBase extends ClinicalStatement {

    protected CD encounterType;

    /**
     * Gets the value of the encounterType property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getEncounterType() {
        return encounterType;
    }

    /**
     * Sets the value of the encounterType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setEncounterType(CD value) {
        this.encounterType = value;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((encounterType == null) ? 0 : encounterType.hashCode());
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
		EncounterBase other = (EncounterBase) obj;
		if (encounterType == null) {
			if (other.encounterType != null)
				return false;
		} else if (!encounterType.equals(other.encounterType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + ", EncounterBase [encounterType=" + encounterType + "]";
	}

}
