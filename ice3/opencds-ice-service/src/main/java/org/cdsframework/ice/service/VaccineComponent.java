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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.cdsframework.cds.CdsConcept;

public class VaccineComponent extends AbstractVaccine {

	private Collection<String> diseaseImmunityList;
	
	private static final Logger logger = LogManager.getLogger();
	

	private VaccineComponent(VaccineComponent pVaccineComponent) {
		super(pVaccineComponent);
	}
	
	
	/**
	 * Instantiate a VaccineComponent object. Both arguments are mandatory. User setters to specify the valid minimum and maximum ages 
	 * for this vaccine component (if any)
	 * @param pVaccineConcept Concept that represents this vaccine component
	 * @param pDisease Disease that this vaccine component induces immunity to
	 * @throws IllegalArgumentException If either parameter is not supplied
	 */
	public VaccineComponent(CdsConcept pVaccineConcept, List<String> pDiseaseImmunityList) {
		
		super(pVaccineConcept);
		
		String _METHODNAME = "VaccineComponent(): ";
		
		if (pDiseaseImmunityList == null) {
			String errStr = "disease not specified";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		
		this.diseaseImmunityList = pDiseaseImmunityList;
	}

	
	/**
	 * Instantiate a VaccineComponent object. Both arguments are mandatory. User setters to specify the valid minimum and maximum ages 
	 * for this vaccine component (if any)
	 * @param pVaccine Populated vaccine instance that represents this vaccine component
	 * @throws IllegalArgumentException If vaccine object or its ICEConcept is not supplied
	 */
	public VaccineComponent(Vaccine pVaccine) {
	
		super(pVaccine);
		
		// String _METHODNAME = "VaccineComponent(): ";
		this.diseaseImmunityList = pVaccine.getAllDiseasesTargetedForImmunity();
	}
	
	
	/**
	 * Construct a deep copy of the supplied VaccineComponent object and return the newly created object to the caller
	 */
	public static VaccineComponent constructDeepCopyOfVaccineComponentObject(VaccineComponent pV) { 
	
		if (pV == null) {
			return null;
		}
		
		VaccineComponent lVC = new VaccineComponent(pV);
		List<String> lSDCList = new ArrayList<String>();
		for (String pSD : pV.diseaseImmunityList) {
			lSDCList.add(pSD);
		}
		lVC.diseaseImmunityList = lSDCList;
		
		return lVC;
	}
	
	
	public Collection<String> getDiseaseImmunityList() {
		return diseaseImmunityList;
	}
	
	
	/**
	 * Get list of diseases targeted for immunity by this vaccine
	 * @return List<SupportedDiseaseConcept> of diseases targeted by this vaccine; empty list if none
	 */
	public Collection<String> getAllDiseasesTargetedForImmunity() {
		
		Collection<String> lImmunityList = getDiseaseImmunityList();
		if (lImmunityList != null) {
			return lImmunityList;
		}
		else {
			return new ArrayList<String>();
		}
	}


	@Override
	public String toString() {
		return "VaccineComponent [diseaseImmunityList=" + diseaseImmunityList
				+ ", getCdsListItemName()=" + getCdsConceptName()
				+ ", isLiveVirusVaccine()=" + isLiveVirusVaccine()
				+ ", isSelectAdjuvantProduct()=" + isSelectAdjuvantProduct()
				+ ", getValidMinimumAgeForUse()=" + getValidMinimumAgeForUse()
				+ ", getValidMaximumAgeForUse()=" + getValidMaximumAgeForUse()
				+ ", getTradeName()=" + getTradeName()
				+ ", getManufacturerCode()=" + getManufacturerCode()
				+ ", getLicensedMinimumAgeForUse()="
				+ getLicensedMinimumAgeForUse()
				+ ", getLicensedMaximumAgeForUse()="
				+ getLicensedMaximumAgeForUse()
				+ ", isUnspecifiedFormulation()=" + isUnspecifiedFormulation()
				+ "]";
		}

}
