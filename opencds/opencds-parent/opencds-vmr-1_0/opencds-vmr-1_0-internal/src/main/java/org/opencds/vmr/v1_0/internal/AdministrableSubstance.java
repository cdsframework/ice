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
import org.opencds.vmr.v1_0.internal.datatypes.RTO;

/**
 * A material of a particular constitution that can be given to a person to enable a clinical effect.  
 * It can have component administrable substances.
 */
public class AdministrableSubstance extends EntityBase
{

    protected CD substanceCode;
    protected RTO strength;
    protected CD form;
    protected CD substanceBrandCode;
    protected CD substanceGenericCode;
    protected CD manufacturer;
    protected String lotNo;
    /** 
     * generated getters and setters, hash code and equals(), and toString() methods follow 
     */
       
	public CD getSubstanceCode() {
		return substanceCode;
	}
	public void setSubstanceCode(CD substanceCode) {
		this.substanceCode = substanceCode;
	}
	public RTO getStrength() {
		return strength;
	}
	public void setStrength(RTO strength) {
		this.strength = strength;
	}
	public CD getForm() {
		return form;
	}
	public void setForm(CD form) {
		this.form = form;
	}
	public CD getSubstanceBrandCode() {
		return substanceBrandCode;
	}
	public void setSubstanceBrandCode(CD substanceBrandCode) {
		this.substanceBrandCode = substanceBrandCode;
	}
	public CD getSubstanceGenericCode() {
		return substanceGenericCode;
	}
	public void setSubstanceGenericCode(CD substanceGenericCode) {
		this.substanceGenericCode = substanceGenericCode;
	}
	public CD getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(CD manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getLotNo() {
		return lotNo;
	}
	public void setLotNo(String lotNo) {
		this.lotNo = lotNo;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((form == null) ? 0 : form.hashCode());
		result = prime * result + ((lotNo == null) ? 0 : lotNo.hashCode());
		result = prime * result
				+ ((manufacturer == null) ? 0 : manufacturer.hashCode());
		result = prime * result
				+ ((strength == null) ? 0 : strength.hashCode());
		result = prime
				* result
				+ ((substanceBrandCode == null) ? 0 : substanceBrandCode
						.hashCode());
		result = prime * result
				+ ((substanceCode == null) ? 0 : substanceCode.hashCode());
		result = prime
				* result
				+ ((substanceGenericCode == null) ? 0 : substanceGenericCode
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
		AdministrableSubstance other = (AdministrableSubstance) obj;
		if (form == null) {
			if (other.form != null)
				return false;
		} else if (!form.equals(other.form))
			return false;
		if (lotNo == null) {
			if (other.lotNo != null)
				return false;
		} else if (!lotNo.equals(other.lotNo))
			return false;
		if (manufacturer == null) {
			if (other.manufacturer != null)
				return false;
		} else if (!manufacturer.equals(other.manufacturer))
			return false;
		if (strength == null) {
			if (other.strength != null)
				return false;
		} else if (!strength.equals(other.strength))
			return false;
		if (substanceBrandCode == null) {
			if (other.substanceBrandCode != null)
				return false;
		} else if (!substanceBrandCode.equals(other.substanceBrandCode))
			return false;
		if (substanceCode == null) {
			if (other.substanceCode != null)
				return false;
		} else if (!substanceCode.equals(other.substanceCode))
			return false;
		if (substanceGenericCode == null) {
			if (other.substanceGenericCode != null)
				return false;
		} else if (!substanceGenericCode.equals(other.substanceGenericCode))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "AdministrableSubstance [" + super.toString() + ", substanceCode=" + substanceCode
				+ ", strength=" + strength + ", form=" + form
				+ ", substanceBrandCode=" + substanceBrandCode
				+ ", substanceGenericCode=" + substanceGenericCode
				+ ", manufacturer=" + manufacturer + ", lotNo=" + lotNo + "]";
	}

}
