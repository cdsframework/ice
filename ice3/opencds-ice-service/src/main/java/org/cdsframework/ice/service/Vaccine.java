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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.cdsframework.cds.CdsConcept;


public class Vaccine extends AbstractVaccine {
	
	private boolean combinationVaccine;
	private List<VaccineComponent> vaccineComponents;
		
	/**
	 * private String strength;				
	 * private String lotNo;	
	 */
	
	private static final Logger logger = LogManager.getLogger();
	
	
	private Vaccine(Vaccine pVaccine) {
		super(pVaccine);
		this.vaccineComponents = new ArrayList<VaccineComponent>();
 
	}
	
	public Vaccine(CdsConcept pVaccineConcept) {
		super(pVaccineConcept);
		this.vaccineComponents = new ArrayList<VaccineComponent>();
	}

	
	/**
	 * Instantiate a vaccine object. If there is only one vaccine component, the vaccine component code must be the same as that specified by the ICEConcept if using this constructor.
	 * The Unspecified Formulation on constructed vaccine object is set to the same value as the monovalent vaccine, which is true by default if not otherwise specified; 
	 * it is set to false if any component vaccine is not an unspecified formulation; otherwise set to true. Be sure to set unspecified formulation flag 
	 * for each vaccine component appropriately to ensure proper behavior of rules, or update all components and this object appropriately if changed.
	 * @param pVaccineConcept ICEConcept representing the vaccine
	 * @param pVaccineComponents At least one vaccine component is required; composite vaccines will contain more than one VaccineComponent;
	 *     monovalent vaccines must contain the antigen for itself (and therefore the name concept code). This is necessary to specify the valid
	 *     ages for the antigens
	 * @throws IllegalArgumentException If parameters are not correctly populated (or either are null) with valid values; monovalent vaccines must have a vaccine component
	 * with the same ICEConcept ID
	 */
	public Vaccine(CdsConcept pVaccineConcept, List<VaccineComponent> pVaccineComponents) {		
		this(pVaccineConcept, pVaccineComponents, false);
	}


	/**
	 * Instantiate a Vaccine object. Both parameters are mandatory.
	 * @param pVaccineConcept ICEConcept representing the vaccine
	 * @param pVaccineComponents At least one vaccine component is required; composite vaccines will contain more than one VaccineComponent;
	 *     monovalent vaccines must contain the antigen for itself (and therefore the name concept code). This is necessary to specify the valid
	 *     ages for the antigens. If more than one vaccine component is specified, than this is set to a combinationVaccine: otherwise, it is not. It is recommended that the
	 *     caller set the combinationVaccine property manually if this behavior is not desired.
	 * @param permitUnequalVaccineComponentCodeValueInMonovalentVaccine If true and monovalent vaccine, permit the vaccine component code to be a different value from the
	 * 		encompassing vaccine's code values. This is a rare circumstance and by default is set to false by other constructors.
	 * @throws IllegalArgumentException If parameters are not correctly populated (or either are null) with valid values; monovalent vaccines must have a vaccine component
	 * with the same ICEConcept ID if permitUnequalVacconeComponentValueInMonovalentVaccine is false (which by default it is).
	 */
	public Vaccine(CdsConcept pVaccineConcept, List<VaccineComponent> pVaccineComponents, boolean permitUnequalVaccineComponentCodeValueInMonovalentVaccine) {
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

		// Usually the monovalent vaccine must have a component vaccine with the same code as the encompassing vaccine object. Check this condition
		int lVaccineComponentsSize = pVaccineComponents.size();
		if (! permitUnequalVaccineComponentCodeValueInMonovalentVaccine && lVaccineComponentsSize == 1) {
			VaccineComponent vc = pVaccineComponents.iterator().next();
			// if (! vc.getVaccineConcept().equals(pVaccineConcept)) {
			if (! vc.getCdsConceptName().equals(pVaccineConcept.getOpenCdsConceptCode())) {
				String errStr = "vaccine component supplied for this monovalent vaccine has an unequal concept code value";
				logger.warn(_METHODNAME + errStr);
				throw new IllegalArgumentException(errStr);
			}
		}

		this.vaccineComponents = pVaccineComponents;
		this.combinationVaccine = false;
		boolean lUnspecifiedFormulation = true;
		if (lVaccineComponentsSize > 1) {
			this.combinationVaccine = true;
			for (VaccineComponent lVC : pVaccineComponents) {
				if (lVC.isUnspecifiedFormulation() == false) {
					lUnspecifiedFormulation = false;
					break;
				}
			}
		}
		else if (lVaccineComponentsSize == 1) {
			VaccineComponent lVC = pVaccineComponents.get(0);
			lUnspecifiedFormulation = lVC.isUnspecifiedFormulation();
		}
		else {
			lUnspecifiedFormulation = true;
		}

		this.setUnspecifiedFormulation(lUnspecifiedFormulation);
	}
	
	
	/**
	 * Add the specified VaccineComponent as a VaccineComponent of this Vaccine.
	 * @param pVaccineComponent
	 */
	public void addMemberVaccineComponent(VaccineComponent pVaccineComponent) {

		String _METHODNAME = "addMemberVaccineComponent(): ";
		
		if (pVaccineComponent == null) {
			return;
		}
		
		// Set the unspecified formulation boolean
		int lVaccineComponentsSize = getVaccineComponents().size();
		if (lVaccineComponentsSize > 1 && pVaccineComponent.isUnspecifiedFormulation() == false) {
			this.setUnspecifiedFormulation(false);
		}
		else if (lVaccineComponentsSize == 1 || lVaccineComponentsSize == 0) {
			this.setUnspecifiedFormulation(pVaccineComponent.isUnspecifiedFormulation());
		}
		else {
			this.setUnspecifiedFormulation(true);
		}

		if (!this.vaccineComponents.contains(pVaccineComponent)) {
			this.vaccineComponents.add(pVaccineComponent);
		}
		else {
			logger.warn(_METHODNAME + "Attempt to add duplicate vaccine component ignored. Vaccine Component : " + pVaccineComponent.toString() + "; Vaccine: " + this.toString());
		}
	}
	
	
	public static Vaccine constructDeepCopyOfVaccineObject(Vaccine pV) {
		
		if (pV == null) {
			return null;
		}
		
		Vaccine lVaccine = new Vaccine(pV);
		lVaccine.combinationVaccine = pV.combinationVaccine;
		for (VaccineComponent pVC : pV.vaccineComponents) {
			lVaccine.getVaccineComponents().add(VaccineComponent.constructDeepCopyOfVaccineComponentObject(pVC));
		}
		
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
	 * @return List<String> of diseases targeted by this vaccine; empty list if none
	 */
	public Collection<String> getAllDiseasesTargetedForImmunity() {
		
		Set<String> targetedDiseases = new HashSet<String>();
		if (this.vaccineComponents == null) {
			return targetedDiseases;
		}
		
		for (VaccineComponent vc : this.vaccineComponents) {
			Collection<String> lImmunityList = vc.getDiseaseImmunityList();
			targetedDiseases.addAll(lImmunityList);
		}
		
		return targetedDiseases;
	}
	
	/**
	 * Get all VaccineComponent member objects of this vaccine that targets the specified list of diseases
	 * @param pTargetedDiseases Collection of diseases from which to ascertain targeting VaccineComponents
	 */
	public Collection<VaccineComponent> getVaccineComponentsTargetingSpecifiedDiseases(Collection<String> pTargetedDiseases) {
		
		Set<VaccineComponent> lVCsContainingSpecifiedDiseases = new HashSet<VaccineComponent>(); // Ensure no duplicate VCs in returned Collection
		
		if (pTargetedDiseases == null || pTargetedDiseases.isEmpty()) {
			return lVCsContainingSpecifiedDiseases;
		}
		
		for (VaccineComponent vc : this.vaccineComponents) {
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
	 */
	public List<VaccineComponent> getVaccineComponents() {
		return vaccineComponents;
	}

	
	@Override
	public String toString() {
		
		String toStr = "Vaccine [getCdsListItemName()=" + getCdsConceptName()		// "Vaccine [getVaccineConcept()=" + getVaccineConcept()
			+ ", isLiveVirusVaccine()="	+ isLiveVirusVaccine() + ", getValidMinimumAgeForUse()="
			+ getValidMinimumAgeForUse() + ", getValidMaximumAgeForUse()="
			+ getValidMaximumAgeForUse() + ", getTradeName()="
			+ getTradeName() + ", getManufacturerCode()="
			+ getManufacturerCode() + ", getLicensedMinimumAgeForUse()="
			+ getLicensedMinimumAgeForUse()
			+ ", getLicensedMaximumAgeForUse()=" + getLicensedMaximumAgeForUse()
			+ ", isUnspecifiedFormulation()=" + isUnspecifiedFormulation()
			+ ", isCombinationVaccine()=" + isCombinationVaccine()
			+ ", isSelectAdjuvantProduct()=" + isSelectAdjuvantProduct()
			+ ", VaccineComponent [[ ";
		
		int i=1;
		for (VaccineComponent lVaccineComponent: this.getVaccineComponents()) {
			toStr += "\n(" + i + ") " + lVaccineComponent;
			i++;
		}
		toStr += "\n]]\n]";
		
		return toStr;
	}
	
}
