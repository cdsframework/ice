/**
 * Copyright (C) 2019 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportedCdsConcepts;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.cdsframework.ice.supportingdata.ICESupportingDataConfiguration;
import org.cdsframework.ice.supportingdata.LocallyCodedVaccineGroupItem;
import org.cdsframework.ice.supportingdata.LocallyCodedVaccineItem;
import org.cdsframework.ice.supportingdata.SupportedVaccineGroups;
import org.cdsframework.ice.supportingdata.SupportedVaccines;
import org.opencds.common.exceptions.ImproperUsageException;


public class Schedule {

	private String scheduleId;
	private ICESupportingDataConfiguration iceSupportingDataConfiguration;
	private boolean scheduleHasBeenInitialized;
	
	private static Log logger = LogFactory.getLog(Schedule.class);


	/**
	 * Initialize the Immunization Schedule. Throws an ImproperUsageException if any data (including supporting data) is improperly specified. Throws an 
	 * InconsistentConfigurationException if the supporting data is "inconsistent" in some manner
	 * @param pScheduleId The ID of the schedule
	 * @param pKnowledgeModules The CDS versions supported by this schedule
	 * @param pKnowledgeRepositoryLocation the knowledge base directory location; where all of the knowledge modules are
	 * @throws ImproperUsageException
	 * @throws InconsistentConfigurationException If supporting data is inconsistent
	 */
	public Schedule(String pScheduleId, String pCommonLogicModule, File pCommonLogicModuleLocation, List<String> pKnowledgeModules, File pKnowledgeRepositoryLocation) 
		throws ImproperUsageException, InconsistentConfigurationException {
		
		String _METHODNAME = "ScheduleImpl(): ";

		this.scheduleHasBeenInitialized = false;
		if (pScheduleId == null || pCommonLogicModule == null|| pCommonLogicModuleLocation == null || pKnowledgeRepositoryLocation == null) {
			String lErrStr = "Schedule not properly initialized: one or more parameters null";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		this.scheduleId = pScheduleId;

		// Initialize the supporting data for the common logic and knowledge modules specified and available
		this.iceSupportingDataConfiguration = new ICESupportingDataConfiguration(pCommonLogicModule, pCommonLogicModuleLocation, pKnowledgeModules, pKnowledgeRepositoryLocation);

		// Log initialization of Schedule
		StringBuilder lSbScheduleInfo = new StringBuilder(80);
		lSbScheduleInfo.append(_METHODNAME); lSbScheduleInfo.append("Completed Initialization of Schedule: "); lSbScheduleInfo.append(this.scheduleId);
		logger.info(lSbScheduleInfo.toString());
		this.scheduleHasBeenInitialized = true;
	}

	
	/**
	 * Get SupportedCdsConcepts associated with this schedule
	 */
	public SupportedCdsConcepts getSupportedCdsConcepts() {

		return this.iceSupportingDataConfiguration.getSupportedCdsConcepts();
	}

	
	/**
	 * Get SupportedCdsLists associated with this schedule
	 */
	public SupportedCdsLists getSupportedCdsLists() {

		return this.iceSupportingDataConfiguration.getSupportedCdsLists();
	}

	/**
	 * Get SupportedVaccineGroups associated with this schedule
	 */
	public SupportedVaccineGroups getSupportedVaccineGroups() {
		
		return this.iceSupportingDataConfiguration.getSupportedVaccineGroups();
	}

	/**
	 * Get SupportedVaccines associated with this schedule
	 */
	public SupportedVaccines getSupportedVaccines() {
		
		return this.iceSupportingDataConfiguration.getSupportedVaccines();
	}
	
	public boolean isScheduleInitialized() {
		
		return this.scheduleHasBeenInitialized;
	}
	
	
	public String getScheduleId() {
		return scheduleId;
	}

	
	public void setScheduleId(String scheduleName) {
		this.scheduleId = scheduleName;
	}

	
	public ICESupportingDataConfiguration getICESupportingDataConfiguration() {
		
		return this.iceSupportingDataConfiguration;
	}
	

	/**
	 * Get SeriesRules based on vaccine group and series name. Returns the SeriesRules representing the specified series by name, or null if not found
	 */
	public SeriesRules getScheduleSeriesByName(String svg, String seriesName) {
		
		if (svg == null || seriesName == null) {
			return null;
		}

		LocallyCodedVaccineGroupItem lcvgi = this.iceSupportingDataConfiguration.getSupportedVaccineGroups().getVaccineGroupItem(svg);    // getSupportedCdsVaccineGroups().getVaccineGroupItem(svg);
		if (lcvgi == null) {
			return null;
		}
		
		List<SeriesRules> lSRs = this.iceSupportingDataConfiguration.getSupportedSeries().getCopyOfSeriesRulesForVaccineGroup(lcvgi);
		if (lSRs == null || lSRs.isEmpty()) {
			return null;
		}
		for (SeriesRules sr : lSRs) {
			if (seriesName.equals(sr.getSeriesName())) {
				return sr;
			}
		}
		return null;
	}
	

	/**
	 * Utilizing supporting data. Return the Vaccine associated with its OpenCDS concept code value
	 * @param openCdsConceptValue
	 * @return Vaccine, or null if there is no associated Vaccine for the OpenCDS concept code provided
	 */
	public Vaccine getVaccineByCdsConceptValue(String openCdsConceptValue) {

		if (openCdsConceptValue == null) {
			return null;
		}

		LocallyCodedCdsListItem lVaccineCdsItem = this.iceSupportingDataConfiguration.getSupportedCdsConcepts().
			getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.OPENCDS, new CdsConcept(openCdsConceptValue)); 
		if (lVaccineCdsItem == null) {
			return null;
		}
		
		// Supporting data restrictions ensure all of the values are non-null
		LocallyCodedVaccineItem lcvi = this.iceSupportingDataConfiguration.getSupportedVaccines().getVaccineItem(lVaccineCdsItem.getCdsListItemName());
		if (lcvi == null) {
			return null;
		}
		
		return lcvi.getVaccine();
	}

	
	/**
	 * Utilizing supporting data. Obtain the list of diseases targeted by the specified vaccine group. Both the String supplied as the parameter and String returned  
	 * are compliant to LocallyCodedCdsListItem.getSupportedListConceptItemName().
	 * @param String specifying the vaccine group by _concept_ name
	 * @return Collection of Strings representing the diseases targeted by the vaccine group; empty collection if none. If the specified vaccine group is  
	 * 		either null or not a vaccine group tracked in the supporting data, null is returned.
	 */
	public Collection<String> getDiseasesTargetedByVaccineGroup(String pVaccineGroupConceptName) {
		
		if (pVaccineGroupConceptName == null) {
			return null;
		}
		
		return getDiseasesTargetedByVaccineGroup(new CdsConcept(pVaccineGroupConceptName));
	}
	
	
	/**
	 * Utilizing supporting data. Obtain the list of diseases targeted by the specified vaccine group. Both the String supplied as the parameter and String returned  
	 * are compliant to LocallyCodedCdsListItem.getSupportedListConceptItemName().
	 * @param String specifying the vaccine group by _concept_ name
	 * @return Collection of Strings representing the diseases targeted by the vaccine group; empty collection if none. If the specified vaccine group is  
	 * 		either null or not a vaccine group tracked in the supporting data, null is returned.
	 */
	public Collection<String> getDiseasesTargetedByVaccineGroup(CdsConcept pVaccineGroupConcept) {
		
		if (pVaccineGroupConcept == null) {
			return null;
		}
		
		LocallyCodedCdsListItem lCodedCdsListItem = this.iceSupportingDataConfiguration.getSupportedCdsConcepts().
			getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.VACCINE_GROUP, pVaccineGroupConcept);
		LocallyCodedVaccineGroupItem lCodedVaccineGroupItem = this.iceSupportingDataConfiguration.getSupportedVaccineGroups().getVaccineGroupItem(lCodedCdsListItem.getCdsListItemName());
		if (lCodedVaccineGroupItem == null) {
			return null;
		}
		
		// It's okay to simply return the list of cdsListItemNames directly; all these have been added as ICEConcepts too during supporting data initialization, and 
		// verified that they are indeed DISEASE ice concepts at that point in the process
		return lCodedVaccineGroupItem.getCopyOfRelatedDiseasesCdsListItemNames();
	}
	

	/**
	 * Return true if the vaccine targets one or more of the specified diseases, false if it does not.
	 * @param vaccine the vaccine in question to inspect
	 * @param diseases the list of diseases in question; see supporting data file that has the ICEConceptType.DISEASE codes specified for a list of diseases
	 */
	public boolean vaccineTargetsOneOrMoreOfSpecifiedDiseases(Vaccine vaccine, Collection<String> diseases) {
		
		if (diseases == null || vaccine == null || diseases.isEmpty() == true) {
			return false;
		}
		
		Collection<String> sds = vaccine.getAllDiseasesTargetedForImmunity();
		for (String sdc : sds) {
			if (diseases.contains(sdc)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Utilizing supporting data. Get the number of vaccine groups across which the specified diseases are handled.
	 * @param pSDCs The list of diseases in question; see supporting data file that has the ICEConceptType.DISEASE codes specified for a list of diseases
	 * @return int specifying number of vaccine groups encompassing the union of the specified diseases
	 */
	private int getCountOfVaccineGroupsEncompassingDiseases(List<String> pSDCs) {
		
		if (pSDCs == null) {
			return 0;
		}
		
		int i=0;
		Collection<LocallyCodedVaccineGroupItem> lAllVaccineGroups = this.iceSupportingDataConfiguration.getSupportedVaccineGroups().getAllVaccineGroupItems();
		for (LocallyCodedVaccineGroupItem lVaccineGroup : lAllVaccineGroups) {
			Collection<String> lAllRelatedDiseases = lVaccineGroup.getCopyOfRelatedDiseasesCdsListItemNames();
			if (lAllRelatedDiseases != null) {			// should never be null
				for (String lRelatedDiseaseName : lAllRelatedDiseases) {
					if (pSDCs.contains(lRelatedDiseaseName)) {
						i++;
						break;
					}
				}
			}
		}
		
		return i;
	}


	// Get a list of all SeriesRules supported by this Schedule.
	public List<SeriesRules> getAllSeries() {

		return this.iceSupportingDataConfiguration.getSupportedSeries().getCopyOfAllSeriesRules();
	}

	
	/**
	 * This is deprecated; simply returns all series 
	 */
	@Deprecated
	public List<SeriesRules> getCandidateSeries() {

		return getAllSeries();
	}


}
