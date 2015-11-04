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
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.VaccineComponentSD;
import org.cdsframework.ice.service.VaccineSD;
import org.cdsframework.ice.supportingdata.tmp.SupportedDiseaseConcept;
import org.cdsframework.ice.util.CollectionUtils;
import org.cdsframework.ice.util.ConceptUtils;
import org.cdsframework.util.support.data.ice.vaccine.IceVaccineSpecificationFile;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.datatypes.CD;


public class SupportedCdsVaccines {

	// CDS versions covered by this supporting datavaccine manager
	private List<String> cdsVersions;
	
	// Keep track of which vaccine items are fully specified; in order for a vaccine to be fully specified, all of its component vaccines must be fully specified as well. We
	// keep track of which Vaccines each VaccineComponet is associated so that they can be associated with the combination vaccine when/if that information comes available.
	// vaccineItemFullySpecified set. 
	private Map<CD, VaccineComponentSD> cDToVaccineComponentsMap;					// VaccineComponents reference for use by LocallyCodedVaccineItems
	private Map<CD, Set<VaccineSD>> vaccineComponentCDToVaccinesNotFullySpecified;	// Vaccine component codes which have not been specified, and the vaccines that they are a component of
	// private Set<String> vaccineConceptsNotFullySpecified;							// Set of vaccines by VaccineItemName not fully specified	

	private Map<String, LocallyCodedVaccineItem> vaccineConcepts;					// LOCAL CODE-RELATED: cdsListCode().cdsListItemKey -> LocallyCodedVaccineItem
	
	private static Log logger = LogFactory.getLog(SupportedCdsVaccines.class);	

	
	protected SupportedCdsVaccines(List<String> pCdsVersions) {
		
		if (pCdsVersions == null) {
			this.cdsVersions = new ArrayList<String>();
		}
		else {
			this.cdsVersions = pCdsVersions;
		}
		
		this.cDToVaccineComponentsMap = new HashMap<CD, VaccineComponentSD>();
		this.vaccineComponentCDToVaccinesNotFullySpecified = new HashMap<CD, Set<VaccineSD>>();
		// this.vaccineConceptsNotFullySpecified = new HashSet<String>();
		this.vaccineConcepts = new HashMap<String, LocallyCodedVaccineItem>();
	}
	
	
	/**
	 * Add the vaccine group information specified in the ice vaccine specification file to the list of supported vaccines. If the IceVaccineSpecificationFile is not 
	 * specified, then this method simply returns. If the IceVaccineSpecificationFile does not contain all of the provided information, or refers to a code system and 
	 * value that is not known a known CdsListItem, an ImproperUsageException is thrown.
	 * @param the IceVaccineSpecification file
	 * @param the SupportedCdsLists for this instance of supporting data
	 * @throws IncosistentConfigurationException if the information provided in the IceVaccineGroupSpecificationFile is not consistent
	 * @throws ImproperUsageException if this method is used improperly
	 */
	protected void addSupportedVaccineItemFromIceVaccineSpecificationFile(IceVaccineSpecificationFile pIceVaccineSpecificationFile, SupportedCdsLists pSupportedCdsLists) 
		throws ImproperUsageException {
		
		String _METHODNAME = "addSupportedVaccineItem(): ";
		
		if (pIceVaccineSpecificationFile == null) {
			return;
		}
		if (pSupportedCdsLists == null) {
			String lErrStr = "SupportedCdsLists parameter not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		
		LocallyCodedCdsListItem llccli = pSupportedCdsLists.getCdsListItem(ConceptUtils.toInternalCD(pIceVaccineSpecificationFile.getVaccine()));
		if (llccli == null) {
			String lErrStr = "Attempt to add vaccine that is not in the list of SupportedCdsLists";
			logger.warn(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);			
		}   
		String lVaccineCdsListItemName = llccli.getSupportedListConceptItemName();
		
		// If adding a code that is not one of the supported cdsVersions, then return
		Collection<String> lCdsVersions = CollectionUtils.intersectionOfStringCollections(pIceVaccineSpecificationFile.getCdsVersions(), this.cdsVersions);
		if (lCdsVersions == null) {
			return;
		}
		
		// OpenCDS concept
		org.opencds.vmr.v1_0.schema.CD lPrimaryOpenCdsConcept = pIceVaccineSpecificationFile.getPrimaryOpenCdsConcept();
		if (lPrimaryOpenCdsConcept == null) {
			String lErrStr = "OpenCDS Concept not specified for vaccine";
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		ICEConcept ic = new ICEConcept(lPrimaryOpenCdsConcept.getCode(), true, lPrimaryOpenCdsConcept.getDisplayName());	// TODO: check against OpenCDS concepts facts

		// Vaccine properties to track
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
		
		// All related diseases
		// Specify Vaccine Component with Related Diseases
		List<org.opencds.vmr.v1_0.schema.CD> lRelatedDiseases = pIceVaccineSpecificationFile.getDiseaseImmunities();
		List<String> lRelatedDiseasesCdsListItems = new ArrayList<String>();
		if (lRelatedDiseases == null || lRelatedDiseases.isEmpty()) {
			String lErrStr = "Related diseases not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		for (org.opencds.vmr.v1_0.schema.CD lRelatedDisease : lRelatedDiseases) {
			LocallyCodedCdsListItem lRelatedDiseaseCdsListItem = pSupportedCdsLists.getCdsListItem(ConceptUtils.toInternalCD(lRelatedDisease));
			if (lRelatedDiseaseCdsListItem == null) {
				String lErrStr = "Attempt to add a related vaccine to a vaccine that is not in the list of SupportedCdsLists";
				logger.warn(_METHODNAME + lErrStr);
				throw new InconsistentConfigurationException(lErrStr);
			}
			lRelatedDiseasesCdsListItems.add(lRelatedDiseaseCdsListItem.getSupportedListConceptItemName());
		}

		// Combination Vaccine
		boolean lCombinationVaccine = false;
		
		// Live virus vaccine
		boolean lLiveVirusVaccine = pIceVaccineSpecificationFile.isLiveVirusVaccine();

		// Vaccine Components
		List<VaccineComponentSD> lVaccineComponentsToAddToVaccine = new ArrayList<VaccineComponentSD>();
		List<org.opencds.vmr.v1_0.schema.CD> lVaccineComponentsCD = pIceVaccineSpecificationFile.getVaccineComponents();
		// Either the 
		if (lVaccineComponentsCD == null || lVaccineComponentsCD.isEmpty()) {
			String lErrStr = "vaccine components not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		
		// If the vaccine code of the component is the same as that of the vaccine (or there is only one vaccine component?), then add the vaccine component with the specified diseases
		if (lVaccineComponentsCD.size() == 1) {
			lCombinationVaccine = false;
			CD lVaccineComponentCD = ConceptUtils.toInternalCD(lVaccineComponentsCD.get(0));
			if (! ConceptUtils.requiredAttributesForCDSpecified(lVaccineComponentCD)) {
				String lErrStr = "VaccineComponent not fully specified: " + lVaccineComponentCD;
				logger.error(_METHODNAME + lErrStr);
				throw new InconsistentConfigurationException(lErrStr);
			}

			VaccineComponentSD lVaccineComponent = new VaccineComponentSD(ic, new ArrayList<String>());
			//////////
			// Make note that this vaccine component is a part of the vaccine
			lVaccineComponentsToAddToVaccine.add(lVaccineComponent);
			// This is a monovalent vaccine - thus, this entry is "defining" it and we therefore we make note that it has been processed by taking note of the mapping
			this.cDToVaccineComponentsMap.put(lVaccineComponentCD, lVaccineComponent);
			if (! ConceptUtils.cDElementsAreEqual(lVaccineCD, lVaccineComponentCD)) {
				lVaccineAndOnlyVaccineComponentNotEqual = true;
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
				if (this.cDToVaccineComponentsMap.containsKey(lVaccineComponentCD)) {
					// Make note that this vaccine component is a part of the vaccine
					lVaccineComponentsToAddToVaccine.add(this.cDToVaccineComponentsMap.get(lVaccineComponentCD));
				}
				else {
					// Make note that this vaccine component has not been defined previously; therefore this vaccine has vaccine components not specified
					lVaccineComponentsNotSpecified.add(lVaccineComponentCD);
					lThisVaccineNotFullySpecified = true;
				}
			}
		}
		

		//TODO:  Fix
		// VaccineSD lVaccine = new VaccineSD(ic, lVaccineComponentsToAddToVaccine);
		VaccineSD lVaccine = new VaccineSD(ic, new ArrayList<VaccineComponentSD>());

		//
		// If a vaccine component was not specified, add the vaccine we're dealing with now to the list of vaccines that contain this (unspecified) vaccine component
		//
		for (CD lVaccineComponentCD : lVaccineComponentsNotSpecified) {
			Set<VaccineSD> lVaccinesNotFullySpecifiedSet = this.vaccineComponentCDToVaccinesNotFullySpecified.get(lVaccineComponentCD);
			if (lVaccinesNotFullySpecifiedSet == null) {
				lVaccinesNotFullySpecifiedSet = new HashSet<VaccineSD>();
			}
			lVaccinesNotFullySpecifiedSet.add(lVaccine);
			this.vaccineComponentCDToVaccinesNotFullySpecified.put(lVaccineComponentCD, lVaccinesNotFullySpecifiedSet);
		}

		/////////////////////
	
		// Add the mapping from the String to reference the vaccine to LocallyCodedVaccineGroupItem
		// this.vaccineConcepts.put(lVaccineCdsListItemName, lcvi);

		
		/**
		 * 
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
			if (numberOfVGsEncompassingDiseases > 1) {
				numberOfVGsGreaterThanOne = true;
			}
			else {
				numberOfVGsGreaterThanOne = false;
			}
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
	
}
