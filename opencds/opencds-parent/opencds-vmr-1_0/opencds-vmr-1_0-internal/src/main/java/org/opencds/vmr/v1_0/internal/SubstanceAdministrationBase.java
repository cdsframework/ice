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
import org.opencds.vmr.v1_0.internal.datatypes.IVLPQ;


/**
 * Abstract base class for giving a material of a particular constitution to a person to enable a clinical effect.
 */

public abstract class SubstanceAdministrationBase extends ClinicalStatement {

    protected CD substanceAdministrationGeneralPurpose;
    protected AdministrableSubstance substance;
    protected CD deliveryMethod;
    protected IVLPQ doseQuantity;
    protected CD deliveryRoute;
    protected BodySite approachBodySite;
    protected BodySite targetBodySite;
    protected IVLPQ dosingPeriod;
    protected BL dosingPeriodIntervalIsImportant;
    protected IVLPQ deliveryRate;
    protected CD doseType;

    /**
     * Gets the value of the substanceAdministrationGeneralPurpose property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getSubstanceAdministrationGeneralPurpose() {
        return substanceAdministrationGeneralPurpose;
    }

    /**
     * Sets the value of the substanceAdministrationGeneralPurpose property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setSubstanceAdministrationGeneralPurpose(CD value) {
        this.substanceAdministrationGeneralPurpose = value;
    }

    /**
     * Gets the value of the substance property.
     * 
     * @return
     *     possible object is
     *     {@link AdministrableSubstance }
     *     
     */
    public AdministrableSubstance getSubstance() {
        return substance;
    }

    /**
     * Sets the value of the substance property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdministrableSubstance }
     *     
     */
    public void setSubstance(AdministrableSubstance value) {
        this.substance = value;
    }

    /**
     * Gets the value of the deliveryMethod property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getDeliveryMethod() {
        return deliveryMethod;
    }

    /**
     * Sets the value of the deliveryMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setDeliveryMethod(CD value) {
        this.deliveryMethod = value;
    }

    /**
     * Gets the value of the doseQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link IVLPQ }
     *     
     */
    public IVLPQ getDoseQuantity() {
        return doseQuantity;
    }

    /**
     * Sets the value of the doseQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLPQ }
     *     
     */
    public void setDoseQuantity(IVLPQ value) {
        this.doseQuantity = value;
    }

    /**
     * Gets the value of the deliveryRoute property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getDeliveryRoute() {
        return deliveryRoute;
    }

    /**
     * Sets the value of the deliveryRoute property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setDeliveryRoute(CD value) {
        this.deliveryRoute = value;
    }

    /**
     * Gets the value of the approachBodySite property.
     * 
     * @return
     *     possible object is
     *     {@link String }
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
     *     {@link String }
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
     *     {@link String }
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
     *     {@link String }
     *     
     */
    public void setTargetBodySite(BodySite value) {
        this.targetBodySite = value;
    }

    /**
     * Gets the value of the dosingPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link IVLPQ }
     *     
     */
    public IVLPQ getDosingPeriod() {
        return dosingPeriod;
    }

    /**
     * Sets the value of the dosingPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLPQ }
     *     
     */
    public void setDosingPeriod(IVLPQ value) {
        this.dosingPeriod = value;
    }

    /**
     * Gets the value of the dosingPeriodIntervalIsImportant property.
     * 
     * @return
     *     possible object is
     *     {@link BL }
     *     
     */
    public BL getDosingPeriodIntervalIsImportant() {
        return dosingPeriodIntervalIsImportant;
    }

    /**
     * Sets the value of the dosingPeriodIntervalIsImportant property.
     * 
     * @param value
     *     allowed object is
     *     {@link BL }
     *     
     */
    public void setDosingPeriodIntervalIsImportant(BL value) {
        this.dosingPeriodIntervalIsImportant = value;
    }

    /**
     * Gets the value of the deliveryRate property.
     * 
     * @return
     *     possible object is
     *     {@link IVLPQ }
     *     
     */
    public IVLPQ getDeliveryRate() {
        return deliveryRate;
    }

    /**
     * Sets the value of the deliveryRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLPQ }
     *     
     */
    public void setDeliveryRate(IVLPQ value) {
        this.deliveryRate = value;
    }

    /**
     * Gets the value of the doseType property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getDoseType() {
        return doseType;
    }

    /**
     * Sets the value of the doseType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setDoseType(CD value) {
        this.doseType = value;
    }

	@Override 
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((approachBodySite == null) ? 0 : approachBodySite.hashCode());
		result = prime * result
				+ ((deliveryMethod == null) ? 0 : deliveryMethod.hashCode());
		result = prime * result
				+ ((deliveryRate == null) ? 0 : deliveryRate.hashCode());
		result = prime * result
				+ ((deliveryRoute == null) ? 0 : deliveryRoute.hashCode());
		result = prime * result
				+ ((doseQuantity == null) ? 0 : doseQuantity.hashCode());
		result = prime * result
				+ ((dosingPeriod == null) ? 0 : dosingPeriod.hashCode());
		result = prime
				* result
				+ ((dosingPeriodIntervalIsImportant == null) ? 0
						: dosingPeriodIntervalIsImportant.hashCode());
		result = prime * result
				+ ((substance == null) ? 0 : substance.hashCode());
		result = prime
				* result
				+ ((substanceAdministrationGeneralPurpose == null) ? 0
						: substanceAdministrationGeneralPurpose.hashCode());
		result = prime * result
				+ ((targetBodySite == null) ? 0 : targetBodySite.hashCode());
		result = prime * result
				+ ((doseType == null) ? 0 : doseType.hashCode());
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
		SubstanceAdministrationBase other = (SubstanceAdministrationBase) obj;
		if (approachBodySite == null) {
			if (other.approachBodySite != null)
				return false;
		} else if (!approachBodySite.equals(other.approachBodySite))
			return false;
		if (deliveryMethod == null) {
			if (other.deliveryMethod != null)
				return false;
		} else if (!deliveryMethod.equals(other.deliveryMethod))
			return false;
		if (deliveryRate == null) {
			if (other.deliveryRate != null)
				return false;
		} else if (!deliveryRate.equals(other.deliveryRate))
			return false;
		if (deliveryRoute == null) {
			if (other.deliveryRoute != null)
				return false;
		} else if (!deliveryRoute.equals(other.deliveryRoute))
			return false;
		if (doseQuantity == null) {
			if (other.doseQuantity != null)
				return false;
		} else if (!doseQuantity.equals(other.doseQuantity))
			return false;
		if (dosingPeriod == null) {
			if (other.dosingPeriod != null)
				return false;
		} else if (!dosingPeriod.equals(other.dosingPeriod))
			return false;
		if (dosingPeriodIntervalIsImportant == null) {
			if (other.dosingPeriodIntervalIsImportant != null)
				return false;
		} else if (!dosingPeriodIntervalIsImportant
				.equals(other.dosingPeriodIntervalIsImportant))
			return false;
		if (substance == null) {
			if (other.substance != null)
				return false;
		} else if (!substance.equals(other.substance))
			return false;
		if (substanceAdministrationGeneralPurpose == null) {
			if (other.substanceAdministrationGeneralPurpose != null)
				return false;
		} else if (!substanceAdministrationGeneralPurpose
				.equals(other.substanceAdministrationGeneralPurpose))
			return false;
		if (targetBodySite == null) {
			if (other.targetBodySite != null)
				return false;
		} else if (!targetBodySite.equals(other.targetBodySite))
			return false;
		if (doseType == null) {
			if (other.doseType != null)
				return false;
		} else if (!doseType.equals(other.doseType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString()
				+ ", SubstanceAdministrationBase [substanceAdministrationGeneralPurpose="
				+ substanceAdministrationGeneralPurpose
				+ ", substance="
				+ substance
				+ ", deliveryMethod="
				+ deliveryMethod
				+ ", doseQuantity="
				+ doseQuantity
				+ ", deliveryRoute="
				+ deliveryRoute
				+ ", approachBodySite="
				+ approachBodySite
				+ ", targetBodySite="
				+ targetBodySite
				+ ", dosingPeriod="
				+ dosingPeriod
				+ ", dosingPeriodIntervalIsImportant="
				+ dosingPeriodIntervalIsImportant
				+ ", deliveryRate="
				+ deliveryRate 
				+ ", doseType="
				+ doseType 
				+ "]";
	}

}
