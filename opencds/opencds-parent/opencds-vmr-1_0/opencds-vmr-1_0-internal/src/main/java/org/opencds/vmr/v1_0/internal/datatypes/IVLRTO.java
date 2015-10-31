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
 * <p>Java class for IVL_RTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 */

public class IVLRTO
    extends ANY
{

    protected double lowNumerator;
    protected double lowDenominator;
    protected double highNumerator;
    protected double highDenominator;
    protected Boolean lowIsInclusive;
    protected Boolean highIsInclusive;

	/**
	 * @return the lowNumerator
	 */
	public double getLowNumerator() {
		return lowNumerator;
	}

	/**
	 * @param lowNumerator the lowNumerator to set
	 */
	public void setLowNumerator(double lowNumerator) {
		this.lowNumerator = lowNumerator;
	}

	/**
	 * @return the lowDenominator
	 */
	public double getLowDenominator() {
		return lowDenominator;
	}

	/**
	 * @param lowDenominator the lowDenominator to set
	 */
	public void setLowDenominator(double lowDenominator) {
		this.lowDenominator = lowDenominator;
	}

	/**
	 * @return the highNumerator
	 */
	public double getHighNumerator() {
		return highNumerator;
	}

	/**
	 * @param highNumerator the highNumerator to set
	 */
	public void setHighNumerator(double highNumerator) {
		this.highNumerator = highNumerator;
	}

	/**
	 * @return the highDenominator
	 */
	public double getHighDenominator() {
		return highDenominator;
	}

	/**
	 * @param highDenominator the highDenominator to set
	 */
	public void setHighDenominator(double highDenominator) {
		this.highDenominator = highDenominator;
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
		long temp;
		temp = Double.doubleToLongBits(highDenominator);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((highIsInclusive == null) ? 0 : highIsInclusive.hashCode());
		temp = Double.doubleToLongBits(highNumerator);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lowDenominator);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((lowIsInclusive == null) ? 0 : lowIsInclusive.hashCode());
		temp = Double.doubleToLongBits(lowNumerator);
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
		IVLRTO other = (IVLRTO) obj;
		if (Double.doubleToLongBits(highDenominator) != Double
				.doubleToLongBits(other.highDenominator))
			return false;
		if (highIsInclusive == null) {
			if (other.highIsInclusive != null)
				return false;
		} else if (!highIsInclusive.equals(other.highIsInclusive))
			return false;
		if (Double.doubleToLongBits(highNumerator) != Double
				.doubleToLongBits(other.highNumerator))
			return false;
		if (Double.doubleToLongBits(lowDenominator) != Double
				.doubleToLongBits(other.lowDenominator))
			return false;
		if (lowIsInclusive == null) {
			if (other.lowIsInclusive != null)
				return false;
		} else if (!lowIsInclusive.equals(other.lowIsInclusive))
			return false;
		if (Double.doubleToLongBits(lowNumerator) != Double
				.doubleToLongBits(other.lowNumerator))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IVLRTO [lowNumerator=" + lowNumerator 
				+ ", lowDenominator=" + lowDenominator 
				+ ", highNumerator=" + highNumerator
				+ ", highDenominator=" + highDenominator 
				+ ", lowIsInclusive=" + lowIsInclusive 
				+ ", highIsInclusive=" + highIsInclusive 
				+ "]";
	}

}
