/**
 * Copyright (C) 2016 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 *
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.ice.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.ice.util.TimePeriod;


public abstract class AbstractVaccine {

	// private CdsConcept vaccineConcept;
	private String cdsListItemName;
	private String tradeName;
	private String manufacturerCode;
	private boolean unspecifiedFormulation;
	private boolean liveVirusVaccine;
	private TimePeriod validMinimumAgeOfUse;
	private TimePeriod validMaximumAgeOfUse;
	private TimePeriod licensedForUseMinimumAge;
	private TimePeriod licensedForUseMaximumAge;
	
	private static Log logger = LogFactory.getLog(AbstractVaccine.class);
	

	public AbstractVaccine(AbstractVaccine pAbstractVaccine) {
		
		String _METHODNAME = "AbstractVaccine(AbstractVaccine): ";
		if (pAbstractVaccine == null) {
			String errStr = "Vaccine instance not specified";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		
		/*
		CdsConcept ic = pAbstractVaccine.getVaccineConcept();
		if (ic == null) {
			String errStr = "Concept code not supplied in Vaccine instance";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		// this.vaccineConcept = CdsConcept.constructDeepCopyOfCdsConceptObject(ic);
		*/
		
		String lCdsListItemName = pAbstractVaccine.getCdsListItemName();
		if (lCdsListItemName == null) {
			String errStr = "Concept code not supplied in Vaccine instance";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		this.cdsListItemName = lCdsListItemName;
		this.tradeName = pAbstractVaccine.getTradeName();
		this.manufacturerCode = pAbstractVaccine.getManufacturerCode();
		this.liveVirusVaccine = pAbstractVaccine.isLiveVirusVaccine();
		this.unspecifiedFormulation = pAbstractVaccine.isUnspecifiedFormulation();
		this.validMinimumAgeOfUse = TimePeriod.constructDeepCopyOfTimePeriodObject(pAbstractVaccine.getValidMinimumAgeForUse());
		this.validMaximumAgeOfUse = TimePeriod.constructDeepCopyOfTimePeriodObject(pAbstractVaccine.getValidMaximumAgeForUse());
		this.licensedForUseMinimumAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pAbstractVaccine.getLicensedMinimumAgeForUse());
		this.licensedForUseMaximumAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pAbstractVaccine.getLicensedMaximumAgeForUse());
	}

	
	/**
	 * Constructor for AbstractVaccine object
	 * @param pCdsListItemName cdsListItemName to be associated with this Vaccine must be supplied
	 */
	public AbstractVaccine(String pCdsListItemName) {
		
		String _METHODNAME = "AbstractVaccine(String): ";
		if (pCdsListItemName == null) {
			String errStr = "cdsListItemName code not supplied";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		this.cdsListItemName = pCdsListItemName;
		this.tradeName = null;
		this.manufacturerCode = null;
		this.liveVirusVaccine = false;
		this.unspecifiedFormulation = false;
		this.validMinimumAgeOfUse = null;
		this.validMaximumAgeOfUse = null;
		this.licensedForUseMinimumAge = null;
		this.licensedForUseMaximumAge = null;
	}

	
	/**
	 * Constructor for AbstractVaccine object
	 * @param pConceptCode conceptCode associated with this Vaccine; must be supplied
	 */
	/*
	public AbstractVaccine(CdsConcept pVaccineConcept) {
		
		String _METHODNAME = "AbstractVaccine(ICEConcept): ";
		if (pVaccineConcept == null) {
			String errStr = "Concept code not supplied";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		
		// this.vaccineConcept = pVaccineConcept;
		
		this.tradeName = null;
		this.manufacturerCode = null;
		this.liveVirusVaccine = false;
		this.unspecifiedFormulation = false;
		this.validMinimumAgeOfUse = null;
		this.validMaximumAgeOfUse = null;
		this.licensedForUseMinimumAge = null;
		this.licensedForUseMaximumAge = null;
	}

	public CdsConcept getVaccineConcept() {
		return vaccineConcept;
	}

	public void setVaccineConcept(CdsConcept vaccineConcept) {
		this.vaccineConcept = vaccineConcept;
	}
	*/
	
	public String getCdsListItemName() {
		return this.cdsListItemName;
	}

	public boolean isLiveVirusVaccine() {
		return liveVirusVaccine;
	}

	public void setLiveVirusVaccine(boolean liveVirusVaccine) {
		this.liveVirusVaccine = liveVirusVaccine;
	}

	/**
	 * Return the minimum age for the vaccine. If not specified previously, 0 days is returned
	 * @return
	 */
	public TimePeriod getValidMinimumAgeForUse() {
		if (validMinimumAgeOfUse != null) {
			return validMinimumAgeOfUse;
		}
		else {
			return new TimePeriod("0d");
		}
	}

	public void setValidMinimumAgeForUse(TimePeriod validMinimumAgeForUse) {
		if (validMinimumAgeForUse == null) {
			this.validMinimumAgeOfUse = null;
		}
		else {
			this.validMinimumAgeOfUse = validMinimumAgeForUse;
		}
	}

	/**
	 * Return the maximum age for the vaccine. If not specified previously, null is returned.
	 */
	public TimePeriod getValidMaximumAgeForUse() {
		return validMaximumAgeOfUse;
	}

	public void setValidMaximumAgeForUse(TimePeriod validMaximumAgeForUse) {
		if (validMaximumAgeForUse == null) {
			this.validMaximumAgeOfUse = null;
		}
		else {
			this.validMaximumAgeOfUse = validMaximumAgeForUse;
		}
	}
	
	public boolean isUnspecifiedFormulation() {
		return unspecifiedFormulation;
	}


	/**
	 * Sets the unspecified formulation flag to whatever is specified by the parameter, overriding any previous (including calculated) unspecified formulation 
	 * @param unspecifiedFormulation
	 */
	public void setUnspecifiedFormulation(boolean unspecifiedFormulation) {
		this.unspecifiedFormulation = unspecifiedFormulation;
	}


	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public String getManufacturerCode() {
		return manufacturerCode;
	}

	public void setManufacturerCode(String manufacturerCode) {
		this.manufacturerCode = manufacturerCode;
	}

	public TimePeriod getLicensedMinimumAgeForUse() {
		return licensedForUseMinimumAge;
	}

	public void setLicensedMinimumAgeForUse(TimePeriod licensedForUseMinimumAge) {
		this.licensedForUseMinimumAge = licensedForUseMinimumAge;
	}

	public TimePeriod getLicensedMaximumAgeForUse() {
		return licensedForUseMaximumAge;
	}

	public void setLicensedMaximumAgeForUse(TimePeriod licensedForUseMaximumAge) {
		this.licensedForUseMaximumAge = licensedForUseMaximumAge;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.cdsListItemName == null) ? 0 : this.cdsListItemName.hashCode());
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
		AbstractVaccine other = (AbstractVaccine) obj;
		if (this.cdsListItemName == null) {
			if (other.cdsListItemName != null)
				return false;
		} else if (!this.cdsListItemName.equals(other.cdsListItemName))
			return false;
		return true;
	}
	
	/*
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((vaccineConcept == null) ? 0 : vaccineConcept.hashCode());
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
		AbstractVaccine other = (AbstractVaccine) obj;
		if (vaccineConcept == null) {
			if (other.vaccineConcept != null)
				return false;
		} else if (!vaccineConcept.equals(other.vaccineConcept))
			return false;
		return true;
	}
	*/
	
}

