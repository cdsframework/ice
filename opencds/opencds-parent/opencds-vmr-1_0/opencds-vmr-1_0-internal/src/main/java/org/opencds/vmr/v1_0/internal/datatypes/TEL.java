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

package org.opencds.vmr.v1_0.internal.datatypes;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for TEL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 */

public class TEL
    extends ANY
{

    protected String useablePeriodOriginalText;
    protected String value;
    protected List<TelecommunicationAddressUse> use;
    protected List<TelecommunicationCapability> capabilities;

    /**
     * Gets the value of the useablePeriodOriginalText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUseablePeriodOriginalText() {
        return useablePeriodOriginalText;
    }

    /**
     * Sets the value of the useablePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUseablePeriodOriginalText(String value) {
        this.useablePeriodOriginalText = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the use property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the use property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TelecommunicationAddressUse }
     * 
     * 
     */
    public List<TelecommunicationAddressUse> getUse() {
        if (use == null) {
            use = new ArrayList<TelecommunicationAddressUse>();
        }
        return this.use;
    }

    /**
     * Gets the value of the capabilities property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the capabilities property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCapabilities().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TelecommunicationCapability }
     * 
     * 
     */
    public List<TelecommunicationCapability> getCapabilities() {
        if (capabilities == null) {
            capabilities = new ArrayList<TelecommunicationCapability>();
        }
        return this.capabilities;
    }

	public void setUse(List<TelecommunicationAddressUse> use) {
		this.use = use;
	}

	public void setCapabilities(List<TelecommunicationCapability> capabilities) {
		this.capabilities = capabilities;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((capabilities == null) ? 0 : capabilities.hashCode());
		result = prime * result + ((use == null) ? 0 : use.hashCode());
		result = prime * result
				+ ((useablePeriodOriginalText == null) ? 0 : useablePeriodOriginalText.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		TEL other = (TEL) obj;
		if (capabilities == null) {
			if (other.capabilities != null)
				return false;
		} else if (!capabilities.equals(other.capabilities))
			return false;
		if (use == null) {
			if (other.use != null)
				return false;
		} else if (!use.equals(other.use))
			return false;
		if (useablePeriodOriginalText == null) {
			if (other.useablePeriodOriginalText != null)
				return false;
		} else if (!useablePeriodOriginalText.equals(other.useablePeriodOriginalText))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TEL [useablePeriodOriginalText=" + useablePeriodOriginalText + ", value=" + value
				+ ", use=" + use + ", capabilities=" + capabilities + "]";
	}
    
}
