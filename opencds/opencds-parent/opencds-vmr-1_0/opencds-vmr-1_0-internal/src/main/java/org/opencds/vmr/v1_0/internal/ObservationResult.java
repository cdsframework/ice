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

import java.util.List;

import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;


/**
 * The findings from an observation.
 */

public class ObservationResult extends ObservationBase
{

    protected IVLDate observationEventTime;
//    protected ObservationResult.ObservationValue observationValue;
    protected ObservationValue observationValue;
    protected List<CD> interpretation;

	/**
	 * @return the observationEventTime
	 */
	public IVLDate getObservationEventTime() {
		return observationEventTime;
	}

	/**
	 * @param observationEventTime the observationEventTime to set
	 */
	public void setObservationEventTime(IVLDate observationEventTime) {
		this.observationEventTime = observationEventTime;
	}

	/**
	 * @return the observationValue
	 */
//	public ObservationResult.ObservationValue getObservationValue() {
	public ObservationValue getObservationValue() {
		return observationValue;
	}

	/**
	 * @param observationValue the observationValue to set
	 */
//	public void setObservationValue(ObservationResult.ObservationValue observationValue) {
	public void setObservationValue(ObservationValue observationValue) {
		this.observationValue = observationValue;
	}

	/**
	 * @return the interpretation
	 */
	public List<CD> getInterpretation() {
		return interpretation;
	}

	/**
	 * @param interpretation the interpretation to set
	 */
	public void setInterpretation(List<CD> interpretation) {
		this.interpretation = interpretation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((interpretation == null) ? 0 : interpretation.hashCode());
		result = prime
				* result
				+ ((observationEventTime == null) ? 0 : observationEventTime
						.hashCode());
		result = prime
				* result
				+ ((observationValue == null) ? 0 : observationValue.hashCode());
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
		ObservationResult other = (ObservationResult) obj;
		if (interpretation == null) {
			if (other.interpretation != null)
				return false;
		} else if (!interpretation.equals(other.interpretation))
			return false;
		if (observationEventTime == null) {
			if (other.observationEventTime != null)
				return false;
		} else if (!observationEventTime.equals(other.observationEventTime))
			return false;
		if (observationValue == null) {
			if (other.observationValue != null)
				return false;
		} else if (!observationValue.getClass().equals(other.observationValue.getClass())) {
			return false;
		} else if (!observationValue.equals(other.observationValue))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ObservationResult [" + super.toString() 
				+ ", observationEventTime=" + observationEventTime 
				+ ", observationValue=" + observationValue 
				+ ", interpretation=" + interpretation + "]";
	}

//    public static class ObservationValue {
//
//        protected AD address;
//        protected BL _boolean;
//        protected CD concept;
//        protected REAL decimal;
//        protected IVLREAL decimalRange;
//        protected String identifier;
//        protected INT integer;
//        protected IVLINT integerRange;
//        protected EN name;
//        protected PQ physicalQuantity;
//        protected IVLPQ physicalQuantityRange;
//        protected RTO ratio;
//        protected IVLRTO ratioRange;
//        protected String simpleConcept;
//        protected TEL telecom;
//        protected String text;
//        protected java.util.Date time;
//        protected IVLDate timeRange;
//		
//		/**
//		 * @return the address
//		 */
//		public AD getAddress() {
//			return address;
//		}
//
//		/**
//		 * @param address the address to set
//		 */
//		public void setAddress(AD address) {
//			this.address = address;
//		}
//
//		/**
//		 * @return the _boolean
//		 */
//		public BL get_boolean() {
//			return _boolean;
//		}
//
//		/**
//		 * @param _boolean the _boolean to set
//		 */
//		public void set_boolean(BL _boolean) {
//			this._boolean = _boolean;
//		}
//
//		/**
//		 * @return the concept
//		 */
//		public CD getConcept() {
//			return concept;
//		}
//
//		/**
//		 * @param concept the concept to set
//		 */
//		public void setConcept(CD concept) {
//			this.concept = concept;
//		}
//
//		/**
//		 * @return the decimal
//		 */
//		public REAL getDecimal() {
//			return decimal;
//		}
//
//		/**
//		 * @param decimal the decimal to set
//		 */
//		public void setDecimal(REAL decimal) {
//			this.decimal = decimal;
//		}
//
//		/**
//		 * @return the decimalRange
//		 */
//		public IVLREAL getDecimalRange() {
//			return decimalRange;
//		}
//
//		/**
//		 * @param decimalRange the decimalRange to set
//		 */
//		public void setDecimalRange(IVLREAL decimalRange) {
//			this.decimalRange = decimalRange;
//		}
//
//		/**
//		 * @return the identifier
//		 */
//		public String getIdentifier() {
//			return identifier;
//		}
//
//		/**
//		 * @param identifier the identifier to set
//		 */
//		public void setIdentifier(String identifier) {
//			this.identifier = identifier;
//		}
//
//		/**
//		 * @return the integer
//		 */
//		public INT getInteger() {
//			return integer;
//		}
//
//		/**
//		 * @param integer the integer to set
//		 */
//		public void setInteger(INT integer) {
//			this.integer = integer;
//		}
//
//		/**
//		 * @return the integerRange
//		 */
//		public IVLINT getIntegerRange() {
//			return integerRange;
//		}
//
//		/**
//		 * @param integerRange the integerRange to set
//		 */
//		public void setIntegerRange(IVLINT integerRange) {
//			this.integerRange = integerRange;
//		}
//
//		/**
//		 * @return the name
//		 */
//		public EN getName() {
//			return name;
//		}
//
//		/**
//		 * @param name the name to set
//		 */
//		public void setName(EN name) {
//			this.name = name;
//		}
//
//		/**
//		 * @return the physicalQuantity
//		 */
//		public PQ getPhysicalQuantity() {
//			return physicalQuantity;
//		}
//
//		/**
//		 * @param physicalQuantity the physicalQuantity to set
//		 */
//		public void setPhysicalQuantity(PQ physicalQuantity) {
//			this.physicalQuantity = physicalQuantity;
//		}
//
//		/**
//		 * @return the physicalQuantityRange
//		 */
//		public IVLPQ getPhysicalQuantityRange() {
//			return physicalQuantityRange;
//		}
//
//		/**
//		 * @param physicalQuantityRange the physicalQuantityRange to set
//		 */
//		public void setPhysicalQuantityRange(IVLPQ physicalQuantityRange) {
//			this.physicalQuantityRange = physicalQuantityRange;
//		}
//
//		/**
//		 * @return the ratio
//		 */
//		public RTO getRatio() {
//			return ratio;
//		}
//
//		/**
//		 * @param ratio the ratio to set
//		 */
//		public void setRatio(RTO ratio) {
//			this.ratio = ratio;
//		}
//
//		/**
//		 * @return the ratioRange
//		 */
//		public IVLRTO getRatioRange() {
//			return ratioRange;
//		}
//
//		/**
//		 * @param ratioRange the ratioRange to set
//		 */
//		public void setRatioRange(IVLRTO ratioRange) {
//			this.ratioRange = ratioRange;
//		}
//
//		/**
//		 * @return the simpleConcept
//		 */
//		public String getSimpleConcept() {
//			return simpleConcept;
//		}
//
//		/**
//		 * @param simpleConcept the simpleConcept to set
//		 */
//		public void setSimpleConcept(String simpleConcept) {
//			this.simpleConcept = simpleConcept;
//		}
//
//		/**
//		 * @return the telecom
//		 */
//		public TEL getTelecom() {
//			return telecom;
//		}
//
//		/**
//		 * @param telecom the telecom to set
//		 */
//		public void setTelecom(TEL telecom) {
//			this.telecom = telecom;
//		}
//
//		/**
//		 * @return the text
//		 */
//		public String getText() {
//			return text;
//		}
//
//		/**
//		 * @param text the text to set
//		 */
//		public void setText(String text) {
//			this.text = text;
//		}
//
//		/**
//		 * @return the time
//		 */
//		public java.util.Date getTime() {
//			return time;
//		}
//
//		/**
//		 * @param time the time to set
//		 */
//		public void setTime(java.util.Date time) {
//			this.time = time;
//		}
//
//		/**
//		 * @return the timeRange
//		 */
//		public IVLDate getTimeRange() {
//			return timeRange;
//		}
//
//		/**
//		 * @param timeRange the timeRange to set
//		 */
//		public void setTimeRange(IVLDate timeRange) {
//			this.timeRange = timeRange;
//		}
//
//		/* (non-Javadoc)
//		 * @see java.lang.Object#hashCode()
//		 */
//		@Override
//		public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result
//					+ ((_boolean == null) ? 0 : _boolean.hashCode());
//			result = prime * result
//					+ ((address == null) ? 0 : address.hashCode());
//			result = prime * result
//					+ ((concept == null) ? 0 : concept.hashCode());
//			result = prime * result
//					+ ((decimal == null) ? 0 : decimal.hashCode());
//			result = prime * result
//					+ ((decimalRange == null) ? 0 : decimalRange.hashCode());
//			result = prime * result
//					+ ((identifier == null) ? 0 : identifier.hashCode());
//			result = prime * result
//					+ ((integer == null) ? 0 : integer.hashCode());
//			result = prime * result
//					+ ((integerRange == null) ? 0 : integerRange.hashCode());
//			result = prime * result + ((name == null) ? 0 : name.hashCode());
//			result = prime
//					* result
//					+ ((physicalQuantity == null) ? 0 : physicalQuantity
//							.hashCode());
//			result = prime
//					* result
//					+ ((physicalQuantityRange == null) ? 0
//							: physicalQuantityRange.hashCode());
//			result = prime * result + ((ratio == null) ? 0 : ratio.hashCode());
//			result = prime * result
//					+ ((ratioRange == null) ? 0 : ratioRange.hashCode());
//			result = prime * result
//					+ ((simpleConcept == null) ? 0 : simpleConcept.hashCode());
//			result = prime * result
//					+ ((telecom == null) ? 0 : telecom.hashCode());
//			result = prime * result + ((text == null) ? 0 : text.hashCode());
//			result = prime * result + ((time == null) ? 0 : time.hashCode());
//			result = prime * result
//					+ ((timeRange == null) ? 0 : timeRange.hashCode());
//			return result;
//		}
//
//		/* (non-Javadoc)
//		 * @see java.lang.Object#equals(java.lang.Object)
//		 */
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (getClass() != obj.getClass())
//				return false;
//			ObservationValue other = (ObservationValue) obj;
//			if (_boolean == null) {
//				if (other._boolean != null)
//					return false;
//			} else if (!_boolean.equals(other._boolean))
//				return false;
//			if (address == null) {
//				if (other.address != null)
//					return false;
//			} else if (!address.equals(other.address))
//				return false;
//			if (concept == null) {
//				if (other.concept != null)
//					return false;
//			} else if (!concept.equals(other.concept))
//				return false;
//			if (decimal == null) {
//				if (other.decimal != null)
//					return false;
//			} else if (!decimal.equals(other.decimal))
//				return false;
//			if (decimalRange == null) {
//				if (other.decimalRange != null)
//					return false;
//			} else if (!decimalRange.equals(other.decimalRange))
//				return false;
//			if (identifier == null) {
//				if (other.identifier != null)
//					return false;
//			} else if (!identifier.equals(other.identifier))
//				return false;
//			if (integer == null) {
//				if (other.integer != null)
//					return false;
//			} else if (!integer.equals(other.integer))
//				return false;
//			if (integerRange == null) {
//				if (other.integerRange != null)
//					return false;
//			} else if (!integerRange.equals(other.integerRange))
//				return false;
//			if (name == null) {
//				if (other.name != null)
//					return false;
//			} else if (!name.equals(other.name))
//				return false;
//			if (physicalQuantity == null) {
//				if (other.physicalQuantity != null)
//					return false;
//			} else if (!physicalQuantity.equals(other.physicalQuantity))
//				return false;
//			if (physicalQuantityRange == null) {
//				if (other.physicalQuantityRange != null)
//					return false;
//			} else if (!physicalQuantityRange
//					.equals(other.physicalQuantityRange))
//				return false;
//			if (ratio == null) {
//				if (other.ratio != null)
//					return false;
//			} else if (!ratio.equals(other.ratio))
//				return false;
//			if (ratioRange == null) {
//				if (other.ratioRange != null)
//					return false;
//			} else if (!ratioRange.equals(other.ratioRange))
//				return false;
//			if (simpleConcept == null) {
//				if (other.simpleConcept != null)
//					return false;
//			} else if (!simpleConcept.equals(other.simpleConcept))
//				return false;
//			if (telecom == null) {
//				if (other.telecom != null)
//					return false;
//			} else if (!telecom.equals(other.telecom))
//				return false;
//			if (text == null) {
//				if (other.text != null)
//					return false;
//			} else if (!text.equals(other.text))
//				return false;
//			if (time == null) {
//				if (other.time != null)
//					return false;
//			} else if (!time.equals(other.time))
//				return false;
//			if (timeRange == null) {
//				if (other.timeRange != null)
//					return false;
//			} else if (!timeRange.equals(other.timeRange))
//				return false;
//			return true;
//		}
//
//		/* (non-Javadoc)
//		 * @see java.lang.Object#toString()
//		 */
//		@Override
//		public String toString() {
//			return "ObservationValue [address=" + address + ", _boolean="
//					+ _boolean + ", concept=" + concept + ", decimal="
//					+ decimal + ", decimalRange=" + decimalRange
//					+ ", identifier=" + identifier + ", integer=" + integer
//					+ ", integerRange=" + integerRange + ", name=" + name
//					+ ", physicalQuantity=" + physicalQuantity
//					+ ", physicalQuantityRange=" + physicalQuantityRange
//					+ ", ratio=" + ratio + ", ratioRange=" + ratioRange
//					+ ", simpleConcept=" + simpleConcept + ", telecom="
//					+ telecom + ", text=" + text + ", time=" + time
//					+ ", timeRange=" + timeRange + "]";
//		}        
//    }
}
