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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.ice.service.ICEConcept;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.util.CollectionUtils;
import org.cdsframework.ice.util.ConceptUtils;
import org.cdsframework.util.support.data.ice.vaccinegroup.IceVaccineGroupSpecificationFile;
import org.opencds.common.exceptions.ImproperUsageException;

public class SupportedCdsVaccineGroups {
	
	// Supported Cds Versions
	private List<String> cdsVersions;
	// Supporting Data CdsLists from which this vaccine group supporting data is built
	private SupportedCdsLists supportedCdsLists;
	
	private Map<String, LocallyCodedVaccineGroupItem> vaccineGroupConcepts;		// LOCAL CODE-RELATED: cdsListCode().cdsListItemKey -> LocallyCodedVaccineGroupItem
	
	private static Log logger = LogFactory.getLog(SupportedCdsVaccineGroups.class);	

	
	protected SupportedCdsVaccineGroups(List<String> pCdsVersions, SupportedCdsLists pSupportedCdsLists) {
	
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
		
		this.vaccineGroupConcepts = new HashMap<String, LocallyCodedVaccineGroupItem>();
	}
	
	protected boolean isEmpty() {
		
		if (this.vaccineGroupConcepts.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Add the vaccine group information specified in the ice vaccine group specification file to the list of supported vaccine groups. If the IceVaccineGroupSpecificationFile is 
	 * not specified, then this method simply returns. If the IceVaccineGroupSpecificationFile does not contain all of the provided information, or refers to a code system and 
	 * value that is not known a known CdsListItem, an ImproperUsageException is thrown.
	 * @param the IceVaccineGroupSpecification file
	 * @param the 
	 * @throws InconsistentConfigurationException if the information provided in the IceVaccineGroupSpecificationFile is not consistent
	 * @throws ImproperUsageException if this operation is used incorrectly
	 */
	protected void addSupportedVaccineGroupItemFromIceVaccineGroupSpecificationFile(IceVaccineGroupSpecificationFile pIceVaccineGroupSpecificationFile) 
		throws ImproperUsageException {
		
		String _METHODNAME = "addSupportedVaccineGroupItem(): ";
		
		if (pIceVaccineGroupSpecificationFile == null) {
			return;
		}

		// If adding a code that is not one of the supported cdsVersions, then return
		Collection<String> lCdsVersions = CollectionUtils.intersectionOfStringCollections(pIceVaccineGroupSpecificationFile.getCdsVersions(), this.cdsVersions);
		if (lCdsVersions == null) {
			return;
		}

		// Verify that there is a primary opencds concept code
		if (pIceVaccineGroupSpecificationFile.getPrimaryOpenCdsConcept() == null || pIceVaccineGroupSpecificationFile.getPrimaryOpenCdsConcept().getCode() == null) {
			String lErrStr = "Attempt to add the following vaccine group which has no specified corresponding primary OpenCDS concept: " +
					(pIceVaccineGroupSpecificationFile.getVaccineGroup() == null ? "null" : ConceptUtils.toInternalCD(pIceVaccineGroupSpecificationFile.getVaccineGroup()));
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}

		LocallyCodedCdsListItem llccli = this.supportedCdsLists.getCdsListItem(ConceptUtils.toInternalCD(pIceVaccineGroupSpecificationFile.getVaccineGroup()));
		// Now verify that there is a CdsListItem for this vaccine group (i.e. - we are tracking the codes and code systems in SupportedCdsLists - it must be there too).
		if (llccli == null) {
			String lErrStr = "Attempt to add the following vaccine group which is not in the list of SupportedCdsLists: " + 
					(pIceVaccineGroupSpecificationFile.getVaccineGroup() == null ? "null" : ConceptUtils.toInternalCD(pIceVaccineGroupSpecificationFile.getVaccineGroup()));
			logger.warn(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);			
		}
		String lVaccineGroupCdsListItemName = llccli.getSupportedCdsListItemName();
		
		// Primary OpenCds Concept
		ICEConcept lPrimaryOpenCdsConcept = new ICEConcept(pIceVaccineGroupSpecificationFile.getPrimaryOpenCdsConcept().getCode(), true, pIceVaccineGroupSpecificationFile.getPrimaryOpenCdsConcept().getDisplayName());
		
		// Related Diseases
		List<org.opencds.vmr.v1_0.schema.CD> lRelatedDiseases = pIceVaccineGroupSpecificationFile.getDiseaseImmunities();
		List<String> lRelatedDiseasesCdsListItems = new ArrayList<String>();
		if (lRelatedDiseases != null && ! lRelatedDiseases.isEmpty()) {
			for (org.opencds.vmr.v1_0.schema.CD lRelatedDisease : lRelatedDiseases) {
				LocallyCodedCdsListItem lRelatedDiseaseCdsListItem = this.supportedCdsLists.getCdsListItem(ConceptUtils.toInternalCD(lRelatedDisease));
				if (lRelatedDiseaseCdsListItem == null) {
					String lErrStr = "Attempt to add a related vaccine to a vaccine group that is not in the list of SupportedCdsLists";
					logger.warn(_METHODNAME + lErrStr);
					throw new InconsistentConfigurationException(lErrStr);
				}
				lRelatedDiseasesCdsListItems.add(lRelatedDiseaseCdsListItem.getSupportedCdsListItemName());
			}
		}
		
		// Vaccine group priority
		int lVaccineGroupPriority = 0;
		BigInteger lVaccineGroupPriorityInt = pIceVaccineGroupSpecificationFile.getPriority();
		if (lVaccineGroupPriorityInt != null) {
			lVaccineGroupPriority = lVaccineGroupPriorityInt.intValue();
		}
		LocallyCodedVaccineGroupItem lcvgi = new LocallyCodedVaccineGroupItem(lVaccineGroupCdsListItemName, lCdsVersions, lRelatedDiseasesCdsListItems, lPrimaryOpenCdsConcept, lVaccineGroupPriority);

		// Add the mapping from the String to reference the vaccine group to LocallyCodedVaccineGroupItem
		this.vaccineGroupConcepts.put(lVaccineGroupCdsListItemName, lcvgi);
	}
	
	
	public void removeSupportedVaccineGroupItem(String pVaccineGroupItemName) {
		
		if (pVaccineGroupItemName != null) { 
			this.vaccineGroupConcepts.remove(pVaccineGroupItemName);
		}		
	}
	
	
	public Collection<LocallyCodedVaccineGroupItem> getAllLocallyCodedVaccineGroupItems() {
		
		return this.vaccineGroupConcepts.values();
	}

	
	public LocallyCodedVaccineGroupItem getVaccineGroupItem(String pVaccineGroupItemName) {
		
		if (pVaccineGroupItemName == null) {
			return null;
		}
		LocallyCodedVaccineGroupItem lLCVGI = this.vaccineGroupConcepts.get(pVaccineGroupItemName);
		if (lLCVGI == null) {
			return null;
		}
		else {
			return lLCVGI;
		}
	}
	
	
	@Override
	public String toString() {
		
		Collection<LocallyCodedVaccineGroupItem> slcvgis = this.vaccineGroupConcepts.values();
		int i = 1;
		String ltoStringStr = "[ ";
		for (LocallyCodedVaccineGroupItem slcvgi : slcvgis) {
			ltoStringStr += "\n{" + i + "} " + slcvgi.toString();
			i++;
		}
		
		return ltoStringStr;
	}
	
}
