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
 * <p>Java class for IVL_PQ complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 */

public class IVLPQ
    extends ANY
{

    protected String lowUnit;
    protected double lowValue;
    protected String highUnit;
    protected double highValue;
    protected Boolean lowIsInclusive;
    protected Boolean highIsInclusive;

	/**
	 * @return the lowUnit
	 */
	public String getLowUnit() {
		return lowUnit;
	}

	/**
	 * @param lowUnit the lowUnit to set
	 */
	public void setLowUnit(String lowUnit) {
		this.lowUnit = lowUnit;
	}

	/**
	 * @return the lowValue
	 */
	public double getLowValue() {
		return lowValue;
	}

	/**
	 * @param lowValue the lowValue to set
	 */
	public void setLowValue(double lowValue) {
		this.lowValue = lowValue;
	}

	/**
	 * @return the highUnit
	 */
	public String getHighUnit() {
		return highUnit;
	}

	/**
	 * @param highUnit the highUnit to set
	 */
	public void setHighUnit(String highUnit) {
		this.highUnit = highUnit;
	}

	/**
	 * @return the highValue
	 */
	public double getHighValue() {
		return highValue;
	}

	/**
	 * @param highValue the highValue to set
	 */
	public void setHighValue(double highValue) {
		this.highValue = highValue;
	}

	/**
	 * @return the lowIsInclusive
	 */
	public Boolean getLowIsInclusive() {
		return lowIsInclusive;
	}

	/**
	 * @param lowIsInclusive the lowIsInclusive to set
	 */
	public void setLowIsInclusive(Boolean lowIsInclusive) {
		this.lowIsInclusive = lowIsInclusive;
	}

	/**
	 * @return the highIsInclusive
	 */
	public Boolean getHighIsInclusive() {
		return highIsInclusive;
	}

	/**
	 * @param highIsInclusive the highIsInclusive to set
	 */
	public void setHighIsInclusive(Boolean highIsInclusive) {
		this.highIsInclusive = highIsInclusive;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((highIsInclusive == null) ? 0 : highIsInclusive.hashCode());
		result = prime * result
				+ ((highUnit == null) ? 0 : highUnit.hashCode());
		long temp;
		temp = Double.doubleToLongBits(highValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((lowIsInclusive == null) ? 0 : lowIsInclusive.hashCode());
		result = prime * result + ((lowUnit == null) ? 0 : lowUnit.hashCode());
		temp = Double.doubleToLongBits(lowValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IVLPQ other = (IVLPQ) obj;
		if (highIsInclusive == null) {
			if (other.highIsInclusive != null)
				return false;
		} else if (!highIsInclusive.equals(other.highIsInclusive))
			return false;
		if (highUnit == null) {
			if (other.highUnit != null)
				return false;
		} else if (!highUnit.equals(other.highUnit))
			return false;
		if (Double.doubleToLongBits(highValue) != Double
				.doubleToLongBits(other.highValue))
			return false;
		if (lowIsInclusive == null) {
			if (other.lowIsInclusive != null)
				return false;
		} else if (!lowIsInclusive.equals(other.lowIsInclusive))
			return false;
		if (lowUnit == null) {
			if (other.lowUnit != null)
				return false;
		} else if (!lowUnit.equals(other.lowUnit))
			return false;
		if (Double.doubleToLongBits(lowValue) != Double
				.doubleToLongBits(other.lowValue))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IVLPQ [lowUnit=" + lowUnit + ", lowValue=" + lowValue
				+ ", highUnit=" + highUnit + ", highValue=" + highValue
				+ ", lowIsInclusive=" + lowIsInclusive + ", highIsInclusive="
				+ highIsInclusive + "]";
	}

}
