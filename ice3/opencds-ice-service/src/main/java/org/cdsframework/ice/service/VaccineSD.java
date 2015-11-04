/**
 * Copyright (C) 2015 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.ice.supportingdata.tmp.SupportedDiseaseConcept;


public class VaccineSD extends AbstractVaccine {
	
	private boolean combinationVaccine;
	private List<VaccineComponentSD> vaccineComponents;
		
	/**
	 * private String strength;				
	 * private String lotNo;	
	 */
	
	private static Log logger = LogFactory.getLog(Vaccine.class);
	
	
	private VaccineSD(VaccineSD pVaccine) {
		super(pVaccine);
	}
	
	/**
	 * Instantiate a vaccine object. If there is only one vaccine component, the vaccine component code must be the same as that specified by the ICEConcept.
	 * Unspecified Formulation on constructed vaccine object is set to the same value as the monovalent vaccine, which is true by default if not otherwise specified; 
	 * it is set to false if any component vaccine is not an unspecified formulation; otherwise set to true. Be sure to set unspecified formulation flag 
	 * for each vaccine component appropriately to ensure proper behavior of rules, or update all components and this object appropriately if changed.
	 * @param pVaccineConcept ICEConcept representing the vaccine
	 * @param pVaccineComponents At least one vaccine component is required; composite vaccines will contain more than one VaccineComponent;
	 *     monovalent vaccines must contain the antigen for itself (and therefore the name concept code). This is necessary to specify the valid
	 *     ages for the antigens
	 * @throws IllegalArgumentException If parameters are not correctly populated (or either are null) with valid values; monovalent vaccines must have a vaccine component
	 * with the same ICEConcept ID
	 */
	public VaccineSD(ICEConcept pVaccineConcept, List<VaccineComponentSD> pVaccineComponents) {
	
		this(pVaccineConcept, pVaccineComponents, false);
	}
	
	/**
	 * Instantiate a Vaccine object. Both parameters are mandatory.
	 * @param pVaccineConcept ICEConcept representing the vaccine
	 * @param pVaccineComponents At least one vaccine component is required; composite vaccines will contain more than one VaccineComponent;
	 *     monovalent vaccines must contain the antigen for itself (and therefore the name concept code). This is necessary to specify the valid
	 *     ages for the antigens
	 * @param permitUnequalVaccineComponentCodeValueInMonovalentVaccine If true and monovalent vaccine, permit the vaccine component code to be a different value from the
	 * 		encompassing vaccine's code values. This is a rare circumstance and by default is set to false by other constructors.
	 * @throws IllegalArgumentException If parameters are not correctly populated (or either are null) with valid values; monovalent vaccines must have a vaccine component
	 * with the same ICEConcept ID if permitUnequalVacconeComponentValueInMonovalentVaccine is false (which by default it is).
	 */
	public VaccineSD(ICEConcept pVaccineConcept, List<VaccineComponentSD> pVaccineComponents, boolean permitUnequalVaccineComponentCodeValueInMonovalentVaccine) {
		super(pVaccineConcept);
		
		String _METHODNAME = "Vaccine(): ";
		if (pVaccineComponents == null || pVaccineComponents.size() == 0) {
			String errStr = "vaccine component not populated";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		if (pVaccineComponents.contains(null)) {
			String errStr = "one or more vaccine components is null; not permitted";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}

		int lVaccineComponentsSize = pVaccineComponents.size();
		if (! permitUnequalVaccineComponentCodeValueInMonovalentVaccine && lVaccineComponentsSize == 1) {
			VaccineComponentSD vc = pVaccineComponents.iterator().next();
			if (! vc.getVaccineConcept().equals(pVaccineConcept)) {
				String errStr = "vaccine component supplied for this monovalent vaccine has an unequal concept code value";
				logger.warn(_METHODNAME + errStr);
				throw new IllegalArgumentException(errStr);
			}
		}
		this.combinationVaccine = false;
		this.vaccineComponents = pVaccineComponents;

		boolean lUnspecifiedFormulation = true;
		if (lVaccineComponentsSize > 1) {
			for (VaccineComponentSD lVC : pVaccineComponents) {
				if (lVC.isUnspecifiedFormulation() == false) {
					lUnspecifiedFormulation = false;
					break;
				}
			}
		}
		else if (lVaccineComponentsSize == 1) {
			VaccineComponentSD lVC = pVaccineComponents.get(0);
			lUnspecifiedFormulation = lVC.isUnspecifiedFormulation();
		}
		else {
			lUnspecifiedFormulation = true;
		}

		this.setUnspecifiedFormulation(lUnspecifiedFormulation);
	}
	
	
	public static VaccineSD constructDeepCopyOfVaccineObject(VaccineSD pV) {
		
		if (pV == null) {
			return null;
		}
		
		VaccineSD lVaccine = new VaccineSD(pV);
		lVaccine.combinationVaccine = pV.combinationVaccine;
		List<VaccineComponentSD> lVCList = new ArrayList<VaccineComponentSD>();
		for (VaccineComponentSD pVC : pV.vaccineComponents) {
			lVCList.add(VaccineComponentSD.constructDeepCopyOfVaccineComponentObject(pVC));
		}
		lVaccine.vaccineComponents = lVCList;
		
		return lVaccine;
	}
		
	
	public boolean isCombinationVaccine() {
		return combinationVaccine;
	}

	
	public void setCombinationVaccine(boolean combinationVaccine) {
		this.combinationVaccine = combinationVaccine;
	}

	
	/**
	 * Get list of diseases targeted for immunity by this vaccine
	 * @return List<SupportedDiseaseConcept> of diseases targeted by this vaccine; empty list if none
	 */
	public Collection<String> getAllDiseasesTargetedForImmunity() {
		
		Set<String> targetedDiseases = new HashSet<String>();
		if (this.vaccineComponents == null) {
			return targetedDiseases;
		}
		
		for (VaccineComponentSD vc : this.vaccineComponents) {
			Collection<String> lImmunityList = vc.getDiseaseImmunityList();
			targetedDiseases.addAll(lImmunityList);
		}
		
		return targetedDiseases;
	}
	
	/**
	 * Get all VaccineComponent member objects of this vaccine that are a member of the SupportedDiseaseConcept
	 * @param pTargetedDiseases SupportedDiseaseConcept
	 * @return
	 */
	public Collection<VaccineComponentSD> getVaccineComponentsTargetingSpecifiedDiseases(Collection<SupportedDiseaseConcept> pTargetedDiseases) {
		
		Set<VaccineComponentSD> lVCsContainingSpecifiedDiseases = new HashSet<VaccineComponentSD>(); // Ensure no duplicate VCs in returned Collection
		
		if (pTargetedDiseases == null || pTargetedDiseases.isEmpty()) {
			return lVCsContainingSpecifiedDiseases;
		}
		
		for (VaccineComponentSD vc : this.vaccineComponents) {
			Collection<String> sdcs = vc.getAllDiseasesTargetedForImmunity();
			for (String sdc : sdcs) {
				if (pTargetedDiseases.contains(sdc)) {
					lVCsContainingSpecifiedDiseases.add(vc);
				}
			}
		}
		
		return lVCsContainingSpecifiedDiseases;
	}
	
	
	/**
	 * Return vaccine components associated with this vaccine. There will always be at least one.
	 * @return
	 */
	public List<VaccineComponentSD> getVaccineComponents() {
		return vaccineComponents;
	}

	
	@Override
	public String toString() {
		return "Vaccine [getVaccineConcept()=" + getVaccineConcept()
				// + ", isMemberOfMultipleVaccineGroups()=" + isMemberOfMultipleVaccineGroups() 
				+ ", isLiveVirusVaccine()="	+ isLiveVirusVaccine() + ", getValidMinimumAgeForUse()="
				+ getValidMinimumAgeForUse() + ", getValidMaximumAgeForUse()="
				+ getValidMaximumAgeForUse() + ", getTradeName()="
				+ getTradeName() + ", getManufacturerCode()="
				+ getManufacturerCode() + ", getLicensedMinimumAgeForUse()="
				+ getLicensedMinimumAgeForUse()
				+ ", getLicensedMaximumAgeForUse()=" + getLicensedMaximumAgeForUse()
				+ ", isUnspecifiedFormulation()=" + isUnspecifiedFormulation() 
				+ ", isCombinationVaccine()=" + isCombinationVaccine()
				+ "]";
	}
	

	/**
	 * Given a vaccine concept code, get the minimum age that the specified vaccine can be used
	 * @param pVaccineConceptCode
	 * @return Minimum age that the specified vaccine can be used
	 * @throws IllegalArgumentException If the vaccine code supplied is not 
	public TimePeriod getMinimumAgeOfUseForVaccineComponent(String pVaccineConceptCode) {
		
		String _METHODNAME = "getValidMinimumAgeForUse(): ";
		
		if (pVaccineConceptCode == null) {
			String errStr = "concept code parameter not supplied";
			logger.warn(_METHODNAME +errStr);
			throw new IllegalArgumentException(errStr);
		}
		
		for (VaccineComponent vc : vaccineComponents) {
			if (pVaccineConceptCode.equals(vc.getVaccineConcept().getOpenCdsConceptCode())) {
				return vc.getValidMinimumAgeForUse();
			}
		}
		
		String errStr = "invalid concept code parameter supplied - concept code does not refer to a vaccine component represented by this vaccine";
		logger.warn(_METHODNAME + errStr);
		throw new IllegalArgumentException(errStr);
	}
	
	**
	 * Given a vaccine concept code, get the minimum age that the specified vaccine can be used
	 * @param pVaccineConceptCode
	 * @return Minimum age that the specified vaccine can be used
	 * @throws IllegalArgumentException If the vaccine code supplied is not 
	 *
	public TimePeriod getMaximumAgeOfUseForVaccineComponent(String pVaccineConceptCode) {
		
		String _METHODNAME = "getValidMaximumAgeForUse(): ";
		
		if (pVaccineConceptCode == null) {
			String errStr = "concept code parameter not supplied";
			logger.warn(_METHODNAME +errStr);
			throw new IllegalArgumentException(errStr);
		}
		
		for (VaccineComponent vc : vaccineComponents) {
			if (pVaccineConceptCode.equals(vc.getVaccineConcept().getOpenCdsConceptCode())) {
				return vc.getValidMaximumAgeForUse();
			}
		}
		
		String errStr = "invalid concept code parameter supplied - concept code does not refer to a vaccine component represented by this vaccine";
		logger.warn(_METHODNAME + errStr);
		throw new IllegalArgumentException(errStr);
	}
	*/
	
	/**
	 * Get the minimum age for this vaccine
	 * @return
	 */

}
