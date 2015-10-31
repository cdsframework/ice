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

import org.opencds.vmr.v1_0.internal.datatypes.BL;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.INT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;
import org.opencds.vmr.v1_0.internal.datatypes.IVLINT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLPQ;
import org.opencds.vmr.v1_0.internal.datatypes.IVLREAL;
import org.opencds.vmr.v1_0.internal.datatypes.IVLRTO;
import org.opencds.vmr.v1_0.internal.datatypes.PQ;
import org.opencds.vmr.v1_0.internal.datatypes.REAL;
import org.opencds.vmr.v1_0.internal.datatypes.RTO;


/**
 * The data structure for defining a goal, the clinical end or aim towards which effort is directed.
 * 
 * Only one of the elements represented may be populated in one particular instance of a GoalValue.
 */
public class GoalValue
{

    protected BL _boolean;
    protected CD concept;
    protected REAL decimal;
    protected IVLREAL decimalRange;
    protected INT integer;
    protected IVLINT integerRange;
    protected PQ physicalQuantity;
    protected IVLPQ physicalQuantityRange;
    protected RTO ratio;
    protected IVLRTO ratioRange;
    protected String simpleConcept;
    protected String text;
    protected java.util.Date time;
    protected IVLDate timeRange;
	
	/**
	 * @return the _boolean
	 */
	public BL get_boolean() {
		return _boolean;
	}

	/**
	 * @param _boolean the _boolean to set
	 */
	public void set_boolean(BL _boolean) {
		this._boolean = _boolean;
	}

	/**
	 * @return the concept
	 */
	public CD getConcept() {
		return concept;
	}

	/**
	 * @param concept the concept to set
	 */
	public void setConcept(CD concept) {
		this.concept = concept;
	}

	/**
	 * @return the decimal
	 */
	public REAL getDecimal() {
		return decimal;
	}

	/**
	 * @param decimal the decimal to set
	 */
	public void setDecimal(REAL decimal) {
		this.decimal = decimal;
	}

	/**
	 * @return the decimalRange
	 */
	public IVLREAL getDecimalRange() {
		return decimalRange;
	}

	/**
	 * @param decimalRange the decimalRange to set
	 */
	public void setDecimalRange(IVLREAL decimalRange) {
		this.decimalRange = decimalRange;
	}

	/**
	 * @return the integer
	 */
	public INT getInteger() {
		return integer;
	}

	/**
	 * @param integer the integer to set
	 */
	public void setInteger(INT integer) {
		this.integer = integer;
	}

	/**
	 * @return the integerRange
	 */
	public IVLINT getIntegerRange() {
		return integerRange;
	}

	/**
	 * @param integerRange the integerRange to set
	 */
	public void setIntegerRange(IVLINT integerRange) {
		this.integerRange = integerRange;
	}

	/**
	 * @return the physicalQuantity
	 */
	public PQ getPhysicalQuantity() {
		return physicalQuantity;
	}

	/**
	 * @param physicalQuantity the physicalQuantity to set
	 */
	public void setPhysicalQuantity(PQ physicalQuantity) {
		this.physicalQuantity = physicalQuantity;
	}

	/**
	 * @return the physicalQuantityRange
	 */
	public IVLPQ getPhysicalQuantityRange() {
		return physicalQuantityRange;
	}

	/**
	 * @param physicalQuantityRange the physicalQuantityRange to set
	 */
	public void setPhysicalQuantityRange(IVLPQ physicalQuantityRange) {
		this.physicalQuantityRange = physicalQuantityRange;
	}

	/**
	 * @return the ratio
	 */
	public RTO getRatio() {
		return ratio;
	}

	/**
	 * @param ratio the ratio to set
	 */
	public void setRatio(RTO ratio) {
		this.ratio = ratio;
	}

	/**
	 * @return the ratioRange
	 */
	public IVLRTO getRatioRange() {
		return ratioRange;
	}

	/**
	 * @param ratioRange the ratioRange to set
	 */
	public void setRatioRange(IVLRTO ratioRange) {
		this.ratioRange = ratioRange;
	}

	/**
	 * @return the simpleConcept
	 */
	public String getSimpleConcept() {
		return simpleConcept;
	}

	/**
	 * @param simpleConcept the simpleConcept to set
	 */
	public void setSimpleConcept(String simpleConcept) {
		this.simpleConcept = simpleConcept;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the time
	 */
	public java.util.Date getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(java.util.Date time) {
		this.time = time;
	}

	/**
	 * @return the timeRange
	 */
	public IVLDate getTimeRange() {
		return timeRange;
	}

	/**
	 * @param timeRange the timeRange to set
	 */
	public void setTimeRange(IVLDate timeRange) {
		this.timeRange = timeRange;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_boolean == null) ? 0 : _boolean.hashCode());
		result = prime * result
				+ ((concept == null) ? 0 : concept.hashCode());
		result = prime * result
				+ ((decimal == null) ? 0 : decimal.hashCode());
		result = prime * result
				+ ((decimalRange == null) ? 0 : decimalRange.hashCode());
		result = prime * result
				+ ((integer == null) ? 0 : integer.hashCode());
		result = prime * result
				+ ((integerRange == null) ? 0 : integerRange.hashCode());
		result = prime
				* result
				+ ((physicalQuantity == null) ? 0 : physicalQuantity
						.hashCode());
		result = prime
				* result
				+ ((physicalQuantityRange == null) ? 0
						: physicalQuantityRange.hashCode());
		result = prime * result + ((ratio == null) ? 0 : ratio.hashCode());
		result = prime * result
				+ ((ratioRange == null) ? 0 : ratioRange.hashCode());
		result = prime * result
				+ ((simpleConcept == null) ? 0 : simpleConcept.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result
				+ ((timeRange == null) ? 0 : timeRange.hashCode());
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
		GoalValue other = (GoalValue) obj;
		if (_boolean == null) {
			if (other._boolean != null)
				return false;
		} else if (!_boolean.equals(other._boolean))
			return false;
		if (concept == null) {
			if (other.concept != null)
				return false;
		} else if (!concept.equals(other.concept))
			return false;
		if (decimal == null) {
			if (other.decimal != null)
				return false;
		} else if (!decimal.equals(other.decimal))
			return false;
		if (decimalRange == null) {
			if (other.decimalRange != null)
				return false;
		} else if (!decimalRange.equals(other.decimalRange))
			return false;
		if (integer == null) {
			if (other.integer != null)
				return false;
		} else if (!integer.equals(other.integer))
			return false;
		if (integerRange == null) {
			if (other.integerRange != null)
				return false;
		} else if (!integerRange.equals(other.integerRange))
			return false;
		if (physicalQuantity == null) {
			if (other.physicalQuantity != null)
				return false;
		} else if (!physicalQuantity.equals(other.physicalQuantity))
			return false;
		if (physicalQuantityRange == null) {
			if (other.physicalQuantityRange != null)
				return false;
		} else if (!physicalQuantityRange
				.equals(other.physicalQuantityRange))
			return false;
		if (ratio == null) {
			if (other.ratio != null)
				return false;
		} else if (!ratio.equals(other.ratio))
			return false;
		if (ratioRange == null) {
			if (other.ratioRange != null)
				return false;
		} else if (!ratioRange.equals(other.ratioRange))
			return false;
		if (simpleConcept == null) {
			if (other.simpleConcept != null)
				return false;
		} else if (!simpleConcept.equals(other.simpleConcept))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (timeRange == null) {
			if (other.timeRange != null)
				return false;
		} else if (!timeRange.equals(other.timeRange))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GoalValue [_boolean=" + _boolean + ", concept="
				+ concept + ", decimal=" + decimal + ", decimalRange="
				+ decimalRange + ", integer=" + integer + ", integerRange="
				+ integerRange + ", physicalQuantity=" + physicalQuantity
				+ ", physicalQuantityRange=" + physicalQuantityRange
				+ ", ratio=" + ratio + ", ratioRange=" + ratioRange
				+ ", simpleConcept=" + simpleConcept + ", text=" + text
				+ ", time=" + time + ", timeRange=" + timeRange + "]";
		}
   
}
