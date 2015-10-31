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

import java.util.ArrayList;
import java.util.List;

import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.CS;


/**
 * A requirement for a coded attribute of a clinical statement.  Specified in terms of the target coded attribute and the code(s) for 
 * that attribute that allow the requirement to be fulfilled.
 */

public class CodedAttributeRequirement {

    protected CS targetCodedAttribute;
    protected List<CD> targetCode;

    /**
     * Gets the value of the targetCodedAttribute property.
     * 
     * @return
     *     possible object is
     *     {@link CS }
     *     
     */
    public CS getTargetCodedAttribute() {
        return targetCodedAttribute;
    }

    /**
     * Sets the value of the targetCodedAttribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link CS }
     *     
     */
    public void setTargetCodedAttribute(CS value) {
        this.targetCodedAttribute = value;
    }

    /**
     * Gets the value of the targetCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the targetCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTargetCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CD }
     * 
     * 
     */
    public List<CD> getTargetCode() {
        if (targetCode == null) {
            targetCode = new ArrayList<CD>();
        }
        return this.targetCode;
    }

	public void setTargetCode(List<CD> targetCode) {
		this.targetCode = targetCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((targetCode == null) ? 0 : targetCode.hashCode());
		result = prime
				* result
				+ ((targetCodedAttribute == null) ? 0 : targetCodedAttribute
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
		CodedAttributeRequirement other = (CodedAttributeRequirement) obj;
		if (targetCode == null) {
			if (other.targetCode != null)
				return false;
		} else if (!targetCode.equals(other.targetCode))
			return false;
		if (targetCodedAttribute == null) {
			if (other.targetCodedAttribute != null)
				return false;
		} else if (!targetCodedAttribute.equals(other.targetCodedAttribute))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CodedAttributeRequirement [targetCodedAttribute="
				+ targetCodedAttribute + ", targetCode=" + targetCode + "]";
	}

}
