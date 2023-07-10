/**
 * Copyright (C) 2023 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdsframework.cds.CdsConcept;
import org.cdsframework.ice.util.TimePeriod;


public abstract class AbstractVaccine {

	private CdsConcept cdsConcept;
	/////// private String cdsListItemName;
	private String tradeName;
	private String manufacturerCode;
	private boolean unspecifiedFormulation;
	private boolean liveVirusVaccine;
	private boolean selectAdjuvantProduct;
	private TimePeriod validMinimumAgeOfUse;
	private TimePeriod validMaximumAgeOfUse;
	private TimePeriod licensedForUseMinimumAge;
	private TimePeriod licensedForUseMaximumAge;

	private static final Logger logger = LogManager.getLogger();
	

	public AbstractVaccine(AbstractVaccine pAbstractVaccine) {
		
		String _METHODNAME = "AbstractVaccine(AbstractVaccine): ";
		if (pAbstractVaccine == null) {
			String errStr = "Vaccine instance not specified";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		
		// String lCdsListItemName = pAbstractVaccine.getCdsListItemName();
		// if (lCdsListItemName == null) {
		// 	String errStr = "cdsListItem code not supplied in Vaccine instance";
		// 	logger.warn(_METHODNAME + errStr);
		// 	throw new IllegalArgumentException(errStr);
		// }
		// this.cdsListItemName = lCdsListItemName;
		this.cdsConcept = CdsConcept.constructDeepCopyOfCdsConceptObject(pAbstractVaccine.getCdsConcept());
		this.tradeName = pAbstractVaccine.getTradeName();
		this.manufacturerCode = pAbstractVaccine.getManufacturerCode();
		this.liveVirusVaccine = pAbstractVaccine.isLiveVirusVaccine();
		this.selectAdjuvantProduct = pAbstractVaccine.isSelectAdjuvantProduct();
		this.unspecifiedFormulation = pAbstractVaccine.isUnspecifiedFormulation();
		this.validMinimumAgeOfUse = TimePeriod.constructDeepCopyOfTimePeriodObject(pAbstractVaccine.getValidMinimumAgeForUse());
		this.validMaximumAgeOfUse = TimePeriod.constructDeepCopyOfTimePeriodObject(pAbstractVaccine.getValidMaximumAgeForUse());
		this.licensedForUseMinimumAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pAbstractVaccine.getLicensedMinimumAgeForUse());
		this.licensedForUseMaximumAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pAbstractVaccine.getLicensedMaximumAgeForUse());
	}
	
	/**
	 * Constructor for AbstractVaccine object
	 * @param pCdsConceptName cdsListItemName to be associated with this Vaccine must be supplied
	 */
	public AbstractVaccine(CdsConcept pCC) {
		
		String _METHODNAME = "AbstractVaccine(String): ";
		if (pCC == null || pCC.getOpenCdsConceptCode() == null) {			// The latter condition should never occur
			String errStr = "cdsConceptName code not supplied";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		/////// this.cdsListItemName = pCdsListItemName;
		this.cdsConcept = pCC;
		this.tradeName = null;
		this.manufacturerCode = null;
		this.liveVirusVaccine = false;
		this.unspecifiedFormulation = false;
		this.selectAdjuvantProduct = false;
		this.validMinimumAgeOfUse = null;
		this.validMaximumAgeOfUse = null;
		this.licensedForUseMinimumAge = null;
		this.licensedForUseMaximumAge = null;
	}

	/*
	public String getCdsListItemName() {
		return this.cdsListItemName;
	}
	*/

	public CdsConcept getCdsConcept() {
		return this.cdsConcept;
	}
	
	public String getCdsConceptName() {
		return this.cdsConcept.getOpenCdsConceptCode();
	}

	public boolean isLiveVirusVaccine() {
		return liveVirusVaccine;
	}

	public void setLiveVirusVaccine(boolean liveVirusVaccine) {
		this.liveVirusVaccine = liveVirusVaccine;
	}

	/**
	 * Return the minimum age for the vaccine. If not specified previously, null is returned
	 * @return TimePeriod representing the minimum age for the vaccine
	 */
	public TimePeriod getValidMinimumAgeForUse() {
		return validMinimumAgeOfUse;
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

	public boolean isSelectAdjuvantProduct() {
		return selectAdjuvantProduct;
	}
	
	public void setSelectAdjuvantProduct(boolean selectAdjuvantProduct) {
		this.selectAdjuvantProduct = selectAdjuvantProduct;
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
				+ ((this.cdsConcept == null) ? 0 : this.cdsConcept.hashCode());
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
		if (this.cdsConcept == null) {
			if (other.cdsConcept != null)
				return false;
		} else if (!this.cdsConcept.equals(other.cdsConcept))
			return false;
		return true;
	}

}

