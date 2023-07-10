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
 
package org.cdsframework.ice.supportingdata;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.ConceptUtils;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.util.CollectionUtils;
import org.cdsframework.util.support.data.ice.vaccinegroup.IceVaccineGroupSpecificationFile;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.datatypes.CD;

public class SupportedVaccineGroups implements SupportingData {
	
	private SupportedCdsLists supportedCdsLists;	// Supporting Data CdsLists from which this vaccine group supporting data is built
	private Map<String, LocallyCodedVaccineGroupItem> cdsListItemNameToVaccineGroupItem;		// LOCAL CODE-RELATED: cdsListCode().cdsListItemKey -> LocallyCodedVaccineGroupItem
	private boolean isSupportingDataConsistent;
	
	private static final Logger logger = LogManager.getLogger();

	
	/**
	 * Create a SupportedCdsVaccineGroups object. If the SupportedCdsLists argument is null, an IllegalArgumentException is thrown.
	 * @param pSupportedCdsLists
	 */
	protected SupportedVaccineGroups(ICESupportingDataConfiguration isdc) 
		throws ImproperUsageException, IllegalArgumentException {
	
		String _METHODNAME = "SupportedCdsVaccineGroups(): ";
		
		if (isdc == null) {
			String lErrStr = "ICESupportingDataConfiguration argument is null; a valid argument must be provided.";
			logger.error(_METHODNAME + lErrStr);
			throw new IllegalArgumentException(lErrStr);
		}

		this.supportedCdsLists = isdc.getSupportedCdsLists();
		if (this.supportedCdsLists == null) {
			String lErrStr = "Supporting cds list data not set in ICESupportingDataConfiguration; cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		this.cdsListItemNameToVaccineGroupItem = new HashMap<String, LocallyCodedVaccineGroupItem>();
		this.isSupportingDataConsistent = true;
	}
	
	
	public boolean isEmpty() {
		
		if (this.cdsListItemNameToVaccineGroupItem.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public boolean isSupportingDataConsistent() {
		return this.isSupportingDataConsistent;
	}

	
	protected SupportedCdsLists getAssociatedSupportedCdsLists() {
		
		return this.supportedCdsLists;
	}
	
	
	/**
	 * Add the vaccine group information specified in the ice vaccine group specification file to the list of supported vaccine groups. If the IceVaccineGroupSpecificationFile is 
	 * not specified, then this method simply returns. If the IceVaccineGroupSpecificationFile does not contain all of the provided information, or refers to a code system and 
	 * value that is not known a known CdsListItem, an ImproperUsageException is thrown.
	 * @param the IceVaccineGroupSpecification file
	 * @param the 
	 * @throws InconsistentConfigurationException if the information provided in the IceVaccineGroupSpecificationFile is not consistent
	 * @throws ImproperUsageException if this operation is used incorrectly
	 * @throws IllegalArgumentException if data supplied in the supporting data file is not permitted
	 */
	protected void addVaccineGroupItemFromIceVaccineGroupSpecificationFile(IceVaccineGroupSpecificationFile pIceVaccineGroupSpecificationFile) 
		throws InconsistentConfigurationException, IllegalArgumentException {
		
		String _METHODNAME = "addSupportedVaccineGroupItem(): ";
		
		if (pIceVaccineGroupSpecificationFile == null || this.supportedCdsLists == null) {
			return;
		}

		// If adding a code that is not one of the supported cdsVersions, then return
		Collection<String> lCdsVersions = CollectionUtils.intersectionOfStringCollections(pIceVaccineGroupSpecificationFile.getCdsVersions(), this.supportedCdsLists.getCdsVersions());
		if (lCdsVersions == null || lCdsVersions.size() == 0) {
			return;
		}

		// Verify that there is a primary opencds concept code
		if (pIceVaccineGroupSpecificationFile.getPrimaryOpenCdsConcept() == null || pIceVaccineGroupSpecificationFile.getPrimaryOpenCdsConcept().getCode() == null) {
			String lErrStr = "Attempt to add the following vaccine group which has no specified corresponding primary OpenCDS concept: " +
					(pIceVaccineGroupSpecificationFile.getVaccineGroup() == null ? "null" : ConceptUtils.toInternalCD(pIceVaccineGroupSpecificationFile.getVaccineGroup()));
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}

		///////
		// Determine the vaccine group name. No duplicates allowed; no vaccine groups that weren't previously defined LocallyCodedCdsListItem allowed.
		///////
		LocallyCodedCdsListItem llccli = this.supportedCdsLists.getCdsListItem(ConceptUtils.toInternalCD(pIceVaccineGroupSpecificationFile.getVaccineGroup()));
		// Now verify that there is a CdsListItem for this vaccine group (i.e. - we are tracking the codes and code systems in SupportedCdsLists - it must be there too).
		if (llccli == null) {
			String lErrStr = "Attempt to add the following vaccine group which is not in the list of SupportedCdsLists: " + 
					(pIceVaccineGroupSpecificationFile.getVaccineGroup() == null ? "null" : ConceptUtils.toInternalCD(pIceVaccineGroupSpecificationFile.getVaccineGroup()));
			logger.warn(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);			
		}
		if (ICEConceptType.VACCINE_GROUP != ICEConceptType.getSupportedIceConceptType(llccli.getCdsListCode())) {
			String lErrStr = "Attempt to add an item as a vaccine group which is not a VACCINE_GROUP ICEConceptType";
			logger.warn(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		String lVaccineGroupCdsListItemName = llccli.getCdsListItemName();
		if (this.cdsListItemNameToVaccineGroupItem.containsKey(lVaccineGroupCdsListItemName)) {
			String lErrStr = "Attempt to add vaccine group that was already specified previously: " + 
					(pIceVaccineGroupSpecificationFile.getVaccineGroup() == null ? "null" : ConceptUtils.toInternalCD(pIceVaccineGroupSpecificationFile.getVaccineGroup()));
			logger.warn(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		
		/*
		///////
		// Primary OpenCds Concept from vaccine group - check to make sure that the specified vaccine group CdsConcept has been specified with this vaccine group's cdsListItem definition
		///////
		CdsConcept lPrimaryOpenCdsConcept = new CdsConcept(pIceVaccineGroupSpecificationFile.getPrimaryOpenCdsConcept().getCode(), pIceVaccineGroupSpecificationFile.getPrimaryOpenCdsConcept().getDisplayName());
		if (! llccli.equals(this.supportedCdsLists.getSupportedCdsConcepts().
			getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.VACCINE_GROUP, lPrimaryOpenCdsConcept))) {
			String lErrStr = "Attempt to add vaccine group with a Primary CdsConcept that is not associated with the vaccine group; vaccine group" + lVaccineGroupCdsListItemName + 
					"; Primary CdsConcept: " + lPrimaryOpenCdsConcept; 
			logger.warn(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		*/

		///////
		// CdsListItem is a CdsConcept of type vaccine group? - check to make sure that the specified vaccine group CdsConcept has been specified with this vaccine group's cdsListItem definition
		///////
		CdsConcept lPrimaryOpenCdsConcept = new CdsConcept(lVaccineGroupCdsListItemName, llccli.getCdsListItemValue());
		if (! llccli.equals(this.supportedCdsLists.getSupportedCdsConcepts().
			getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.VACCINE_GROUP, lPrimaryOpenCdsConcept))) {
			String lErrStr = "Attempt to add vaccine group with a Primary CdsConcept that is not associated with the vaccine group; vaccine group" + lVaccineGroupCdsListItemName + 
					"; Primary CdsConcept: " + lPrimaryOpenCdsConcept; 
			logger.warn(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		
		///////
		// Related Diseases
		///////
		List<org.opencds.vmr.v1_0.schema.CD> lRelatedDiseases = pIceVaccineGroupSpecificationFile.getDiseaseImmunities();
		List<String> lRelatedDiseasesCdsListItems = new ArrayList<String>();
		if (lRelatedDiseases != null && ! lRelatedDiseases.isEmpty()) {
			for (org.opencds.vmr.v1_0.schema.CD lRelatedDisease : lRelatedDiseases) {
				LocallyCodedCdsListItem lRelatedDiseaseCdsListItem = this.supportedCdsLists.getCdsListItem(ConceptUtils.toInternalCD(lRelatedDisease));
				if (lRelatedDiseaseCdsListItem == null) {
					String lErrStr = "Attempt to add a related disease to a vaccine group that is not in the list of SupportedCdsLists" + 
						ConceptUtils.toInternalCD(lRelatedDisease) == null ? "" : ConceptUtils.toInternalCD(lRelatedDisease).toString();
					logger.warn(_METHODNAME + lErrStr);
					throw new InconsistentConfigurationException(lErrStr);
				}
				if (ICEConceptType.DISEASE != ICEConceptType.getSupportedIceConceptType(lRelatedDiseaseCdsListItem.getCdsListCode())) {
					String lErrStr = "Attempt to add an item as a related disease to a vaccine group which is not a DISEASE ICEConceptType; item: " + lRelatedDiseaseCdsListItem.toString();
					logger.warn(_METHODNAME + lErrStr);
					throw new InconsistentConfigurationException(lErrStr);
				}
				lRelatedDiseasesCdsListItems.add(lRelatedDiseaseCdsListItem.getCdsListItemName());
			}
		}
		
		///////
		// Vaccine group priority
		///////
		int lVaccineGroupPriority = 0;
		BigInteger lVaccineGroupPriorityInt = pIceVaccineGroupSpecificationFile.getPriority();
		if (lVaccineGroupPriorityInt != null) {
			lVaccineGroupPriority = lVaccineGroupPriorityInt.intValue();
		}
		
		///////
		// Create and add the LocallyCodedVaccineGroupItem
		///////
		LocallyCodedVaccineGroupItem lcvgi = null;
		try {
			lcvgi = new LocallyCodedVaccineGroupItem(lVaccineGroupCdsListItemName, lPrimaryOpenCdsConcept, lCdsVersions, lRelatedDiseasesCdsListItems, lVaccineGroupPriority);
		}
		catch (ImproperUsageException iue) {
			String lErrStr = "Caught an unexpected ImproperUsageException during instantiation of LocallyCodedVaccineGroupItem for vaccine group" + lVaccineGroupCdsListItemName;
			logger.error(_METHODNAME + lErrStr);
			throw new ICECoreError(lErrStr);
		}

		// Add the mapping from the String to reference the vaccine group to LocallyCodedVaccineGroupItem
		this.cdsListItemNameToVaccineGroupItem.put(lVaccineGroupCdsListItemName, lcvgi);
	}
	

	public Collection<LocallyCodedVaccineGroupItem> getAllVaccineGroupItems() {
		
		return this.cdsListItemNameToVaccineGroupItem.values();
	}


	public boolean vaccineGroupItemExists(String pVaccineGroupItemName) {
	
		if (pVaccineGroupItemName == null) {
			return false;
		}
		if (this.cdsListItemNameToVaccineGroupItem.get(pVaccineGroupItemName) != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public LocallyCodedVaccineGroupItem getVaccineGroupItem(String pCdsListItemName) {
		
		if (pCdsListItemName == null) {
			return null;
		}
		LocallyCodedVaccineGroupItem lLCVGI = this.cdsListItemNameToVaccineGroupItem.get(pCdsListItemName);
		if (lLCVGI == null) {
			return null;
		}
		else {
			return lLCVGI;
		}
	}

	
	/**
	 * Returns the associated LocallyCodedCdsListItem 
	 * @param pCdsListItemName
	 * @return
	 */
	public LocallyCodedCdsListItem getCdsListItem(String pCdsListItemName) {
		
		if (pCdsListItemName == null) {
			return null;
		}
		

		if(! vaccineGroupItemExists(pCdsListItemName)) {
			return null;
		}
		
		return this.supportedCdsLists.getCdsListItem(pCdsListItemName);
	}
	
	
	/**
	 * Returns true if there is a vaccineGroupItem associated with the local CD, false if not. (Invoked getGroupVaccineGroupItem(CD) to determine.)
	 */
	public boolean vaccineGroupItemExists(CD pVaccineGroupCD) {
		
		LocallyCodedVaccineGroupItem lVGI = getVaccineGroupItem(pVaccineGroupCD);
		if (lVGI == null) {
			return false;
		}
		else {
			return true;
		}
	}
	
	
	/**
	 * Returns the vaccineGroupItem associated with the local CD. If none exists, returns null.
	 */
	public LocallyCodedVaccineGroupItem getVaccineGroupItem(CD pVaccineGroupCD) {
		
		if (pVaccineGroupCD == null) {
			return null;
		}
		LocallyCodedCdsListItem llccli = this.supportedCdsLists.getCdsListItem(pVaccineGroupCD);
		if (llccli == null) {
			return null;
		}
		String lVaccineGroupCdsListItemName = llccli.getCdsListItemName();
		if (this.cdsListItemNameToVaccineGroupItem.containsKey(lVaccineGroupCdsListItemName)) {
			return this.cdsListItemNameToVaccineGroupItem.get(lVaccineGroupCdsListItemName);
		}
		else {
			return null;
		}	
	}
	

	@Override
	public String toString() {
		
		Collection<LocallyCodedVaccineGroupItem> slcvgis = this.cdsListItemNameToVaccineGroupItem.values();
		int i = 1;
		String ltoStringStr = "[ ";
		for (LocallyCodedVaccineGroupItem slcvgi : slcvgis) {
			ltoStringStr += "\n{" + i + "} " + slcvgi.toString();
			i++;
		}
		
		return ltoStringStr;
	}
	
}
