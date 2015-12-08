/*
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

package org.cdsframework.ice.supportingdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.ice.service.ICEConcept;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.TimePeriod;
import org.cdsframework.ice.service.VaccineComponentSD;
import org.cdsframework.ice.service.VaccineSD;
import org.cdsframework.ice.util.CollectionUtils;
import org.cdsframework.ice.util.ConceptUtils;
import org.cdsframework.util.support.data.ice.vaccine.IceVaccineSpecificationFile;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.datatypes.CD;


public class SupportedCdsVaccines {

	// CDS versions covered by this supporting datavaccine manager
	private List<String> cdsVersions;
	
	// Supporting Data Cds List from which this vaccine supporting data is built
	private SupportedCdsLists supportedCdsLists;
	
	// Keep track of which vaccine items are fully specified; in order for a vaccine to be fully specified, all of its component vaccines must be fully specified as well. We
	// keep track of which Vaccines each VaccineComponent is associated so that they can be associated with the combination vaccine when/if that information comes available.
	// vaccineItemFullySpecified set. 
	private Map<String, VaccineSD> cdsListItemNameToVaccine;						// cdsListItemName (cdsListCode.cdsListItemKey) to Vaccine 
	private Map<CD, VaccineComponentSD> cDToVaccineComponentsMap;					// VaccineComponents previously defined, keyed by CD
	private Map<CD, Set<VaccineSD>> vaccineComponentCDToVaccinesNotFullySpecified;	// VaccineComponents which have been encountered in a Vaccine object but not yet defined
	// private Set<String> vaccineConceptsNotFullySpecified;							// Set of vaccines by VaccineItemName not fully specified	

	private Map<String, LocallyCodedVaccineItem> vaccineConcepts;					// LOCAL CODE-RELATED: cdsListCode().cdsListItemKey -> LocallyCodedVaccineItem
	
	private static Log logger = LogFactory.getLog(SupportedCdsVaccines.class);	

	
	protected SupportedCdsVaccines(List<String> pCdsVersions, SupportedCdsLists pSupportedCdsLists) {
		
		// String _METHODNAME = "SupportedCdsVaccines(): ";
		
		if (pCdsVersions == null) {
			this.cdsVersions = new ArrayList<String>();
		}
		else {
			this.cdsVersions = pCdsVersions;
		}
		
		if (pSupportedCdsLists == null) {
			this.supportedCdsLists = new SupportedCdsLists(this.cdsVersions);
		}
		else {
			this.supportedCdsLists = pSupportedCdsLists;
		}

		this.cdsListItemNameToVaccine = new HashMap<String, VaccineSD>();
		this.cDToVaccineComponentsMap = new HashMap<CD, VaccineComponentSD>();
		this.vaccineComponentCDToVaccinesNotFullySpecified = new HashMap<CD, Set<VaccineSD>>();
		/////// this.vaccineConceptsNotFullySpecified = new HashSet<String>();
		this.vaccineConcepts = new HashMap<String, LocallyCodedVaccineItem>();
	}
	
	
	protected boolean isEmpty() {
		
		if (this.cdsListItemNameToVaccine.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * Add the vaccine group information specified in the ice vaccine specification file to the list of supported vaccines. If the IceVaccineSpecificationFile is not 
	 * specified, then this method simply returns. If the IceVaccineSpecificationFile does not contain all of the provided information, or refers to a code system and 
	 * value that is not known a known CdsListItem, an ImproperUsageException is thrown.
	 * @param the IceVaccineSpecification file
	 * @param the SupportedCdsLists for this instance of supporting data
	 * @return true if the vaccine data loaded to this point is consistent, false if it is not
	 * @throws IncosistentConfigurationException if the information provided in the IceVaccineGroupSpecificationFile is not consistent
	 * @throws ImproperUsageException if this method is used improperly
	 */
	protected boolean addSupportedVaccineItemFromIceVaccineSpecificationFile(IceVaccineSpecificationFile pIceVaccineSpecificationFile) 
		throws ImproperUsageException {
		
		String _METHODNAME = "addSupportedVaccineItem(): ";
		
		if (pIceVaccineSpecificationFile == null) {
			return isVaccineSupportingDataConsistent();
		}
		
		LocallyCodedCdsListItem llccli = this.supportedCdsLists.getCdsListItem(ConceptUtils.toInternalCD(pIceVaccineSpecificationFile.getVaccine()));
		if (llccli == null) {
			String lErrStr = "Attempt to add vaccine that is not in the list of SupportedCdsLists: " + 
					(pIceVaccineSpecificationFile.getVaccine() == null ? "null" : ConceptUtils.toInternalCD(pIceVaccineSpecificationFile.getVaccine()));
			logger.warn(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);			
		}
		
		// If adding a code that is not one of the supported cdsVersions, then return
		Collection<String> lCdsVersions = CollectionUtils.intersectionOfStringCollections(pIceVaccineSpecificationFile.getCdsVersions(), this.cdsVersions);
		if (lCdsVersions == null) {
			return isVaccineSupportingDataConsistent();
		}
		
		////////////// Determine Primary OpenCDS Concept START //////////////		
		///////
		// The Vaccine Concept will be stored as a Concept; doublecheck that a SupportedCdsConcept with the specified primary OpenCDS Concept for this Vaccine exists
		// That is, check to make sure that the ICEConcept associated with this Vaccine (CdsListItem) is also a OpenCDS concept
		///////
		org.opencds.vmr.v1_0.schema.CD lPrimaryOpenCdsConcept = pIceVaccineSpecificationFile.getPrimaryOpenCdsConcept();
		if (lPrimaryOpenCdsConcept == null) {
			String lErrStr = "OpenCDS Concept not specified for vaccine: " + llccli;
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		// The corresponding OpenCDS concept was specified in the file. Was the OpenCDS concept previously specified with this CdsListItem?
		ICEConcept ic = new ICEConcept(lPrimaryOpenCdsConcept.getCode(), true, lPrimaryOpenCdsConcept.getDisplayName());
		Collection<ICEConcept> lOpenCDSConcepts = this.supportedCdsLists.getAssociatedSupportedCdsConcepts().getListOfOpenCDSICEConceptsForSpecifiedCdsListItem(llccli);
		boolean lPrimaryOpenCDSConceptForVaccineIdentified = false;
		for (ICEConcept lIC : lOpenCDSConcepts) {
			if (ic.equals(lIC)) {
				lPrimaryOpenCDSConceptForVaccineIdentified = true;
				break;
			}
		}
		if (! lPrimaryOpenCDSConceptForVaccineIdentified) {
			String lErrStr = "Attempt to specify Primary OpenCDS Concept for vaccine but OpenCDS concept not associated CdsListItem; vaccine: " + llccli;
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}

		////////////// Determine Primary OpenCDS Concept END //////////////
				
		////////////// Vaccine properties to track START //////////////
		boolean lThisVaccineNotFullySpecified = false;
		boolean lVaccineAndOnlyVaccineComponentNotEqual = false;
		List<CD> lVaccineComponentsNotSpecified = new ArrayList<CD>();
		
		// Vaccine CD
		CD lVaccineCD = ConceptUtils.toInternalCD(pIceVaccineSpecificationFile.getVaccine());
		if (! ConceptUtils.requiredAttributesForCDSpecified(lVaccineCD)) {
			String lErrStr = "Vaccine Code specified is not fully populated: " + lVaccineCD;
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		
		// Live virus vaccine?
		boolean lLiveVirusVaccine = pIceVaccineSpecificationFile.isLiveVirusVaccine();

		// Combination vaccine?
		boolean lCombinationVaccine = false;

		////////
		// START RELATED DISEASES: Get all the related diseases that the vaccine targets (as specified in the configuration data).
		// Related diseases is only used for monovalent vaccines; for combination vaccines, the related diseases are determined by the vaccine components
		///////
		List<org.opencds.vmr.v1_0.schema.CD> lRelatedDiseases = pIceVaccineSpecificationFile.getDiseaseImmunities();
		List<String> lRelatedDiseasesCdsListItems = new ArrayList<String>();
		if (lRelatedDiseases == null || lRelatedDiseases.isEmpty()) {
			String lErrStr = "Related diseases not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		for (org.opencds.vmr.v1_0.schema.CD lRelatedDisease : lRelatedDiseases) {
			LocallyCodedCdsListItem lRelatedDiseaseCdsListItem = this.supportedCdsLists.getCdsListItem(ConceptUtils.toInternalCD(lRelatedDisease));
			if (lRelatedDiseaseCdsListItem == null) {
				String lErrStr = "Attempt to add a related disease to a vaccine that is not a supported disease concept specified in the list of Supported CdsLists";
				logger.warn(_METHODNAME + lErrStr);
				throw new InconsistentConfigurationException(lErrStr);
			}
			lRelatedDiseasesCdsListItems.add(lRelatedDiseaseCdsListItem.getSupportedCdsListItemName());
		}
		////////
		// END RELATED DISEASES: Get all the related diseases that the vaccine targets (as specified in the configuration data).
		///////
		
		// Vaccine Components
		List<VaccineComponentSD> lVaccineComponentsToAddToVaccine = new ArrayList<VaccineComponentSD>();
		List<org.opencds.vmr.v1_0.schema.CD> lVaccineComponentsCD = pIceVaccineSpecificationFile.getVaccineComponents();
		// Either the 
		if (lVaccineComponentsCD == null || lVaccineComponentsCD.isEmpty()) {
			String lErrStr = "vaccine components not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		
		////////////// Vaccine properties to track END //////////////
		
		/////// 
		// START to Determine which vaccine components can be incorporated now in the initialization of this vaccine
		///////
		// If the vaccine code of the component is the same as that of the vaccine (or there is only one vaccine component?), then add the vaccine component with the specified diseases
		if (lVaccineComponentsCD.size() == 1) {
			lCombinationVaccine = false;
			CD lVaccineComponentCD = ConceptUtils.toInternalCD(lVaccineComponentsCD.get(0));
			if (! ConceptUtils.requiredAttributesForCDSpecified(lVaccineComponentCD)) {
				String lErrStr = "VaccineComponent not fully specified: " + lVaccineComponentCD;
				logger.error(_METHODNAME + lErrStr);
				throw new InconsistentConfigurationException(lErrStr);
			}
			if (! lVaccineComponentCD.getCodeSystem().equals(lVaccineCD.getCodeSystem())) {
				String lErrStr = "Vaccine and Vaccine Component are specified using two different code systems. This is not permitted";
				logger.error(_METHODNAME + lErrStr);
				throw new InconsistentConfigurationException(lErrStr);
			}

			// This is a monovalent vaccine; this is the definition. Create the vaccine component and store it. It targets all of the diseases targeted by this vaccine
			if (! ConceptUtils.cDElementsAreEqual(lVaccineCD, lVaccineComponentCD)) {
				// FYI - the vaccine component and vaccine local codes are not equal, and we don't require the VaccneComponent to be previously specified
				lVaccineAndOnlyVaccineComponentNotEqual = true;
			}
			if (this.cDToVaccineComponentsMap.containsKey(lVaccineComponentCD) && lVaccineAndOnlyVaccineComponentNotEqual == false) {
				// The vaccine component should not have been defined before - throw an InconsistentConfigurationException
				String lErrStr = "Monovalent vaccine with VaccineComponent previously defined encountered. VaccineComponents cannot be defined more than once.";
				logger.error(_METHODNAME + lErrStr);
				throw new InconsistentConfigurationException(lErrStr);
			}
			else {
				///////
				// Create the new VaccineComponent, and make note of this a vaccine component; record and will be a part of this Vaccine
				VaccineComponentSD lVaccineComponent = new VaccineComponentSD(ic, lRelatedDiseasesCdsListItems);
				addPropertiesFromSDToVaccineComponent(lVaccineComponent, pIceVaccineSpecificationFile); 
				lVaccineComponentsToAddToVaccine.add(lVaccineComponent);
				// Make note that it has been processed by taking note of the mapping
				this.cDToVaccineComponentsMap.put(lVaccineComponentCD, lVaccineComponent);
			}
		}
		// More than one vaccine component was specified; include the vaccine components that have already been specified previously. If not all of them have been specified
		// before, then mark that the vaccine component specified in this combo vaccine has not been specified yet.
		else {
			lCombinationVaccine = true;
			for (org.opencds.vmr.v1_0.schema.CD lVaccineComponentCDSchema : lVaccineComponentsCD) {
				CD lVaccineComponentCD = ConceptUtils.toInternalCD(lVaccineComponentCDSchema);
				if (! ConceptUtils.requiredAttributesForCDSpecified(lVaccineComponentCD)) {
					String lErrStr = "VaccineComponent not fully specified: " + lVaccineComponentCD;
					logger.error(_METHODNAME + lErrStr);
					throw new InconsistentConfigurationException(lErrStr);					
				}
				if (! lVaccineComponentCD.getCodeSystem().equals(lVaccineCD.getCodeSystem())) {
					String lErrStr = "Vaccine and Vaccine Component are specified using two different code systems in the supporting data. This is not permitted";
					logger.error(_METHODNAME + lErrStr);
					throw new InconsistentConfigurationException(lErrStr);
				}
				if (this.cDToVaccineComponentsMap.containsKey(lVaccineComponentCD)) {
					//
					// This VaccineComponent has been defined previously. Make note that this previously encountered VaccineComponent is a part of this vaccine
					//
					VaccineComponentSD lVaccineComponent = this.cDToVaccineComponentsMap.get(lVaccineComponentCD);
					lVaccineComponentsToAddToVaccine.add(lVaccineComponent);

					// If this VaccineComponent was previously encountered by a Vaccine, add this VaccineComponent to those Vaccines as well.
					if (this.vaccineComponentCDToVaccinesNotFullySpecified.containsKey(lVaccineComponentCD)) {
						Set<VaccineSD> lPreviouslyEncounteredVaccinesWVaccineComponent = this.vaccineComponentCDToVaccinesNotFullySpecified.get(lVaccineComponentCD);
						if (lPreviouslyEncounteredVaccinesWVaccineComponent != null) {
							for (VaccineSD lPreviousVaccineEncountered : lPreviouslyEncounteredVaccinesWVaccineComponent) {
								lPreviousVaccineEncountered.addMemberVaccineComponent(lVaccineComponent);
								lPreviouslyEncounteredVaccinesWVaccineComponent.remove(lPreviousVaccineEncountered);
							}
							if (lPreviouslyEncounteredVaccinesWVaccineComponent.size() == 0) {
								// If all vaccines have been handled, remove the fact that there were previously encountered vaccines that need this (now) fully specified 
								// vaccine component to be added to it
								this.vaccineComponentCDToVaccinesNotFullySpecified.remove(lVaccineComponentCD);
							}
						}
						else {
							String lErrStr = "Error: Unaccounted for inconsistency encountered during processing of vaccine supporting data. " + 
								"(Unaccounted for vaccine component when no vaccines have been defined)";
							logger.error(_METHODNAME + lErrStr);
							throw new ICECoreError(lErrStr);
							////////////// OLD:::: This should not happen (null set of vaccines), but remove it anyway
							////////////// this.vaccineComponentCDToVaccinesNotFullySpecified.remove(lVaccineComponentCD);
						}
					}
					else {
						// TODO: 
					}

				}
				else {
					// Make note that this vaccine component has not been defined previously; therefore this vaccine has vaccine components not specified
					lVaccineComponentsNotSpecified.add(lVaccineComponentCD);
					lThisVaccineNotFullySpecified = true;
				}
			}
		}
		/////// 
		// END of Determine which vaccine components can be incorporated now in the initialization of this vaccine
		///////

		///////
		// START Create the Vaccine and store it this object
		///////
		VaccineSD lVaccine = null;
		if (lVaccineComponentsToAddToVaccine.size() == 0) {
			lVaccine = new VaccineSD(ic);
		}
		else if (lVaccineComponentsToAddToVaccine.size() == 1) {
			if (lVaccineAndOnlyVaccineComponentNotEqual) {
				// Monovalent vaccine where the VaccineComponent code is not the same as the Vaccine; this is rare but allowed if explicitly stated as such in the Vaccine constructor
				lVaccine = new VaccineSD(ic, lVaccineComponentsToAddToVaccine, true);
			}
			else {
				// Create the vaccine with the VaccineComponent code the same as the Vaccine code
				lVaccine = new VaccineSD(ic, lVaccineComponentsToAddToVaccine);
			}
		}
		else {	
			// The sized of the VaccineComponents defined is > 1. Only those VaccineComponents previously encountered in the supporting data configuration so far are included 
			lVaccine = new VaccineSD(ic, lVaccineComponentsToAddToVaccine);
		}
		// Set combination vaccine and unformulated formulations, if applicable
		lVaccine.setCombinationVaccine(lCombinationVaccine);
		if (lCombinationVaccine == false) {
			// Determine if the formulation of this vaccine is unformulated or not
			boolean lUnspecifiedFormulation = false;
			Boolean pIceVaccineSpecificationFormulation = pIceVaccineSpecificationFile.isUnspecifiedFormulation();
			if (pIceVaccineSpecificationFormulation != null && pIceVaccineSpecificationFormulation.booleanValue() == true) {
				lUnspecifiedFormulation = true;
			}
			lVaccine.setUnspecifiedFormulation(lUnspecifiedFormulation);
		}
		lVaccine.setLiveVirusVaccine(lLiveVirusVaccine);
		this.cdsListItemNameToVaccine.put(llccli.getSupportedCdsListItemName(), lVaccine);
		
		///////
		// END Creating and persisting the Vaccine
		///////
				
		///////
		// For those vaccine components not specified in this combination vaccine, add this vaccine we're dealing with now to the list of vaccines that contain this unspecified 
		// vaccine component
		//
		for (CD lVaccineComponentCD : lVaccineComponentsNotSpecified) {
			Set<VaccineSD> lVaccinesNotFullySpecifiedSet = this.vaccineComponentCDToVaccinesNotFullySpecified.get(lVaccineComponentCD);
			if (lVaccinesNotFullySpecifiedSet == null) {
				lVaccinesNotFullySpecifiedSet = new HashSet<VaccineSD>();
			}
			lVaccinesNotFullySpecifiedSet.add(lVaccine);
			this.vaccineComponentCDToVaccinesNotFullySpecified.put(lVaccineComponentCD, lVaccinesNotFullySpecifiedSet);
		}
		///////
	
		return isVaccineSupportingDataConsistent();
				
		/**
		 * Examples
		 * 		
		// _HEPA_PEDADOL_3_DOSE
			ICEConcept hepAPedAdol3Dose = new ICEConcept(SupportedVaccineConcept._HEPA_PEDADOL_3_DOSE.getConceptCodeValue(), true, SupportedVaccineConcept._HEPA_PEDADOL_3_DOSE.getConceptDisplayNameValue());
			lVaccineComponent = new VaccineComponent(hepAPedAdol3Dose, diseaseImmunityList);
			lVaccineComponent.setValidMinimumAgeForUse(new TimePeriod(0, DurationType.YEARS));
			lVaccineComponent.setValidMaximumAgeForUse(null);
			lVaccineComponent.setLiveVirusVaccine(false);
			lVaccineComponent.setUnspecifiedFormulation(false);
			lVaccineComponentList = new ArrayList<VaccineComponent>();
			lVaccineComponentList.add(lVaccineComponent);
			lVaccine = new Vaccine(hepAPedAdol3Dose, lVaccineComponentList);
			lVaccine.setValidMinimumAgeForUse(new TimePeriod(0, DurationType.DAYS));
			lVaccine.setValidMaximumAgeForUse(null);
			lVaccine.setLiveVirusVaccine(false);
			supportedVaccinesMap.put(SupportedVaccineConcept._HEPA_PEDADOL_3_DOSE, lVaccine);
	
			// _HEPA_PED_NOS
			ICEConcept hepAPedNOS = new ICEConcept(SupportedVaccineConcept._HEPA_PED_NOS.getConceptCodeValue(), true, SupportedVaccineConcept._HEPA_PED_NOS.getConceptDisplayNameValue());
			lVaccineComponent = new VaccineComponent(hepAPedNOS, diseaseImmunityList);
			lVaccineComponent.setValidMinimumAgeForUse(new TimePeriod(0, DurationType.YEARS));
			lVaccineComponent.setValidMaximumAgeForUse(null);
			lVaccineComponent.setLiveVirusVaccine(false);
			lVaccineComponent.setUnspecifiedFormulation(true);
			lVaccineComponentList = new ArrayList<VaccineComponent>();
			lVaccineComponentList.add(lVaccineComponent);
			lVaccine = new Vaccine(hepAPedNOS, lVaccineComponentList);
			lVaccine.setValidMinimumAgeForUse(new TimePeriod(0, DurationType.DAYS));
			lVaccine.setValidMaximumAgeForUse(null);
			lVaccine.setLiveVirusVaccine(false);
			supportedVaccinesMap.put(SupportedVaccineConcept._HEPA_PED_NOS, lVaccine);

			//
			// DTaP-HepB-IPV
			//
			diseaseImmunityList = new ArrayList<SupportedDiseaseConcept>();
			diseaseImmunityList.add(SupportedDiseaseConcept.HepB);
			diseaseImmunityList.add(SupportedDiseaseConcept.Diphtheria);
			diseaseImmunityList.add(SupportedDiseaseConcept.Tetanus);
			diseaseImmunityList.add(SupportedDiseaseConcept.Pertussis);
			diseaseImmunityList.add(SupportedDiseaseConcept.Polio);
			numberOfVGsEncompassingDiseases = getNumberOfVaccineGroupsEncompassingDiseases(diseaseImmunityList);
			ic = new ICEConcept(SupportedVaccineConcept._DTAP_HEPB_IPV.getConceptCodeValue(), true, SupportedVaccineConcept._DTAP_HEPB_IPV.getConceptDisplayNameValue());
			// Add HepB, DTaP, and IPV Vaccine Components START
			lVaccineComponentList = new ArrayList<VaccineComponent>();
			componentVaccine = supportedVaccinesMap.get(SupportedVaccineConcept._DTAP);
			lVaccineComponentList.add(new VaccineComponent(componentVaccine));
			componentVaccine = supportedVaccinesMap.get(SupportedVaccineConcept._HEPB_LESSTHAN_20);
			lVaccineComponentList.add(new VaccineComponent(componentVaccine));
			componentVaccine = supportedVaccinesMap.get(SupportedVaccineConcept._IPV);
			lVaccineComponentList.add(new VaccineComponent(componentVaccine));
			// Add HepB, DTaP, and IPV Vaccine Components END
			lVaccine = new Vaccine(ic, lVaccineComponentList);
			lVaccine.setLiveVirusVaccine(false);
			lVaccine.setCombinationVaccine(true);
			supportedVaccinesMap.put(SupportedVaccineConcept._DTAP_HEPB_IPV, lVaccine);
		 **/
		
		
	}

	
	private void addPropertiesFromSDToVaccineComponent(VaccineComponentSD pVaccineComponent, IceVaccineSpecificationFile pIVSF) {
		
		if (pVaccineComponent == null || pIVSF == null) {
			return;
		}
		String lAge = pIVSF.getValidMinimumAgeForUse();
		if (lAge != null) {
			pVaccineComponent.setValidMinimumAgeForUse(new TimePeriod(lAge));
		}
		lAge = pIVSF.getValidMaximumAgeForUse();
		if (lAge != null) {
			pVaccineComponent.setValidMaximumAgeForUse(new TimePeriod(lAge));
		}
	}
	
	
	public boolean isVaccineSupportingDataConsistent() {
		
		if (this.cDToVaccineComponentsMap.size() > 0) {
			return false;
		}
		else {
			return true;
		}
	}
	
}
