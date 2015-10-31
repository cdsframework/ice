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



/**
 * <p>Java class for IVL_INT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 */

public class IVLINT
    extends ANY
{

    protected int low;
    protected int high;
    protected Boolean lowIsInclusive;
    protected Boolean highIsInclusive;

    /**
     * Gets the value of the low property.
     * 
     * @return
     *     possible object is
     *     {@link INT }
     *     
     */
    public int getLow() {
        return low;
    }

    /**
     * Sets the value of the low property.
     * 
     * @param value
     *     allowed object is
     *     {@link INT }
     *     
     */
    public void setLow(int value) {
        this.low = value;
    }

    /**
     * Gets the value of the high property.
     * 
     * @return
     *     possible object is
     *     {@link INT }
     *     
     */
    public int getHigh() {
        return high;
    }

    /**
     * Sets the value of the high property.
     * 
     * @param value
     *     allowed object is
     *     {@link INT }
     *     
     */
    public void setHigh(int value) {
        this.high = value;
    }

    /**
     * Gets the value of the lowIsInclusive property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLowIsInclusive() {
        return lowIsInclusive;
    }

    /**
     * Sets the value of the lowIsInclusive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLowIsInclusive(Boolean value) {
        this.lowIsInclusive = value;
    }

    /**
     * Gets the value of the highIsInclusive property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHighIsInclusive() {
        return highIsInclusive;
    }

    /**
     * Sets the value of the highIsInclusive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHighIsInclusive(Boolean value) {
        this.highIsInclusive = value;
    }

	public Boolean getLowIsInclusive() {
		return lowIsInclusive;
	}

	public Boolean getHighIsInclusive() {
		return highIsInclusive;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + high;
		result = prime * result
				+ ((highIsInclusive == null) ? 0 : highIsInclusive.hashCode());
		result = prime * result + low;
		result = prime * result
				+ ((lowIsInclusive == null) ? 0 : lowIsInclusive.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		IVLINT other = (IVLINT) obj;
		if (high != other.high)
			return false;
		if (highIsInclusive == null) {
			if (other.highIsInclusive != null)
				return false;
		} else if (!highIsInclusive.equals(other.highIsInclusive))
			return false;
		if (low != other.low)
			return false;
		if (lowIsInclusive == null) {
			if (other.lowIsInclusive != null)
				return false;
		} else if (!lowIsInclusive.equals(other.lowIsInclusive))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IVLINT [low=" + low + ", high=" + high + ", lowIsInclusive="
				+ lowIsInclusive + ", highIsInclusive=" + highIsInclusive + "]";
	}

    
}
