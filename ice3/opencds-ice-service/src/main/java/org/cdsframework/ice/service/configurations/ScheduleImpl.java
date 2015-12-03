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

package org.cdsframework.ice.service.configurations;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.ice.service.DoseRule;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Schedule;
import org.cdsframework.ice.service.Season;
import org.cdsframework.ice.service.SeriesRules;
import org.cdsframework.ice.service.TimePeriod;
import org.cdsframework.ice.service.TimePeriod.DurationType;
import org.cdsframework.ice.service.Vaccine;
import org.cdsframework.ice.service.VaccineComponent;
import org.cdsframework.ice.supportingdata.ICESupportingDataConfiguration;
import org.cdsframework.ice.supportingdata.LocallyCodedVaccineGroupItem;
import org.cdsframework.ice.supportingdata.tmp.SupportedDiseaseConcept;
import org.cdsframework.ice.supportingdata.tmp.SupportedVaccineConcept;
import org.cdsframework.ice.supportingdata.tmp.SupportedVaccineGroupConcept;
import org.opencds.common.exceptions.ImproperUsageException;


public class ScheduleImpl implements Schedule {

	private String scheduleId;
	private List<String> cdsVersions;
	private ICESupportingDataConfiguration iceSupportingDataConfiguration;
	
	// Structures to be deprecated
	private Map<SupportedVaccineGroupConcept, List<SupportedDiseaseConcept>> vaccineGroupDiseases;
	private Map<SupportedVaccineGroupConcept, List<SeriesRules>> vaccineGroupSeries;	// Vaccine Group -> List of SeriesRules
	private Map<SupportedVaccineConcept, Vaccine> supportedVaccinesMap;

	private static Log logger = LogFactory.getLog(ScheduleImpl.class);


	/**
	 * Initialize the Immunization Schedule. Throws an ImproperUsageException if any data (including supporting data) is improperly specified. Throws an 
	 * InconsistentConfigurationException if the supporting data is "inconsistent" in some manner
	 * @param pScheduleId The ID of the schedule
	 * @param pCdsVersions The CDS versions supported by this schedule
	 * @param pBaseKnowledgeRepositoryLocation the knowledge base directory location; where all of the knowledge modules are
	 * @throws ImproperUsageException
	 * @throws InconsistentConfigurationException If supporting data is inconsistent
	 */
	public ScheduleImpl(String pScheduleId, List<String> pCdsVersions, File pBaseKnowledgeRepositoryLocation) 
		throws ImproperUsageException, InconsistentConfigurationException {
		
		String _METHODNAME = "ScheduleImpl(): ";
		if (pScheduleId == null || pBaseKnowledgeRepositoryLocation == null) {
			String lErrStr = "Schedule not properly initialized: one or more parameters null";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		this.scheduleId = pScheduleId;
		this.cdsVersions = pCdsVersions;

		// Initialize the supporting data for all of the CDS versions specified and available from this base knowledge repository location
		this.iceSupportingDataConfiguration = new ICESupportingDataConfiguration(cdsVersions, pBaseKnowledgeRepositoryLocation);

		// Log initialization of Schedule
		StringBuilder lSbScheduleInfo = new StringBuilder(80);
		lSbScheduleInfo.append(_METHODNAME); lSbScheduleInfo.append("Completed Initialization of Schedule: "); lSbScheduleInfo.append(this.scheduleId);
		logger.info(lSbScheduleInfo.toString());
	}

	
	public String getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(String scheduleName) {
		this.scheduleId = scheduleName;
	}

	public Map<SupportedVaccineGroupConcept, List<SeriesRules>> getScheduleSeries() {
		return vaccineGroupSeries;
	}
	
	// public void setSupportingDataLocation(String supportingDataLocation) {
	//	this.supportingDataLocation = supportingDataLocation;
	// }

	/**
	 * Get SeriesRules based on vaccine group and series name
	 * @param svg
	 * @param seriesName
	 * @return SeriesRule, or null if not found
	 */
	@Deprecated
	public SeriesRules getScheduleSeriesByName(SupportedVaccineGroupConcept svg, String seriesName) {
		
		if (svg == null || seriesName == null) {
			return null;
		}
		
		List<SeriesRules> srs = vaccineGroupSeries.get(svg);
		for (SeriesRules sr : srs) {
			if (seriesName.equals(sr.getSeriesName())) {
				return sr;
			}
		}
		return null;
	}

	@Deprecated
	public Vaccine getVaccineBySupportedVaccineConceptTypeValue(SupportedVaccineConcept vaccineValue) {

		if (vaccineValue == null) {
			return null;
		}

		return supportedVaccinesMap.get(vaccineValue);
	}
	
	/**
	 * Return the Vaccine associated with its OpenCDS concept code value
	 * @param openCdsConceptValue
	 * @return Vaccine, or null if there is no associated Vaccine for the OpenCDS concept code provided
	 */
	public Vaccine getVaccineByOpenCDSConceptTypeValue(String openCdsConceptValue) {

		if (openCdsConceptValue == null) {
			return null;
		}

		SupportedVaccineConcept sv = SupportedVaccineConcept.getSupportedVaccineConceptByConceptCode(openCdsConceptValue);
		if (sv == null) {
			return null;
		}
		Vaccine v = supportedVaccinesMap.get(sv);
		return v;	
	}
	
	/**
	 * Return the VaccineComponent associated with its OpenCDS concept code value and 
	 * @param openCdsConceptValue
	 * @param sdc
	 * @return
	 */
	public VaccineComponent getVaccineByOpenCDSConceptTypeValueAndDisease(String openCdsConceptValue, SupportedDiseaseConcept sdc) {
		
		return null;
	}
	
	
	
	/**
	 * @deprecated Once CAT-ICE integration complete, will be replaced by {@link getDiseasesTargetedByVaccineGroup(String) }
	 * @param pSVGC
	 * @return null if SupportedVaccineGroupConcept is null; Collection of diseases if not
	 */
	@Deprecated 
	public Collection<SupportedDiseaseConcept> getDiseasesTargetedByVaccineGroup(SupportedVaccineGroupConcept pSVGC) {
		
		if (pSVGC == null) {
			return null;
		}
		
		List<SupportedDiseaseConcept> targetedDiseases = vaccineGroupDiseases.get(pSVGC);
		return targetedDiseases;
	}
	
	
	/**
	 * Obtain the list of diseases targeted by the specified vaccine group. Both the String supplied as the parameter and String returned are compliant 
	 * to LocallyCodedCdsListItem.getSupportedListConceptItemName().
	 * @param String specifying the vaccine group by name
	 * @return Collection of Strings representing the diseases targeted by the vaccine group; empty collection if none. If the specified vaccine group is  
	 *	either null or not a vaccine group tracked in the supporting data, null is returned.
	 */
	public Collection<String> getDiseasesTargetedByVaccineGroup(String pVaccineGroupItemName) {
		
		if (pVaccineGroupItemName == null) {
			return null;
		}
		
		LocallyCodedVaccineGroupItem lCodedVaccineGroupItem = this.iceSupportingDataConfiguration.getSupportedCdsVaccineGroups().getVaccineGroupItem(pVaccineGroupItemName);
		if (lCodedVaccineGroupItem == null) {
			return null;
		}
		
		return lCodedVaccineGroupItem.getRelatedDiseasesCdsListItemNames();
	}
	
	
	
	/**
	 * @deprecated Once CAT-ICE integration complete, will be replaced
	 */
	@Deprecated 
	public boolean vaccineTargetsOneOrMoreOfSpecifiedDiseases(Vaccine vaccine, Collection<SupportedDiseaseConcept> diseases) {
		
		if (diseases == null || vaccine == null) {
			return false;
		}
		
		Collection<SupportedDiseaseConcept> sds = vaccine.getAllDiseasesTargetedForImmunity();
		for (SupportedDiseaseConcept sdc : sds) {
			if (diseases.contains(sdc)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Get the number of vaccine groups across which the specified diseases are handled.
	 * @param pSDCs The list of diseases in question
	 * @return int specifying number of vaccine groups encompassing the union of the specified diseases
	 */
	private int getCountOfVaccineGroupsEncompassingDiseases(List<String> pSDCs) {
		
		if (pSDCs == null) {
			return 0;
		}
		List<SupportedDiseaseConcept> sdcs = new ArrayList<SupportedDiseaseConcept>();		
		int i=0;
		Collection<LocallyCodedVaccineGroupItem> lAllVaccineGroups = this.iceSupportingDataConfiguration.getSupportedCdsVaccineGroups().getAllLocallyCodedVaccineGroupItems();
		for (LocallyCodedVaccineGroupItem lVaccineGroup : lAllVaccineGroups) {
			Collection<String> lAllRelatedDiseases = lVaccineGroup.getRelatedDiseasesCdsListItemNames();
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
	
	
	/**
	 * @deprecated Once CAT-ICE integration complete, will be replaced by {@link #getCountOfVaccineGroupsEncompassingDiseases()}
	 */
	@Deprecated 
	private int getNumberOfVaccineGroupsEncompassingDiseases(List<SupportedDiseaseConcept> pSDCs) {
	
		if (pSDCs == null) {
			return 0;
		}
		
		int i = 0;
		Set<SupportedVaccineGroupConcept> svcs = vaccineGroupDiseases.keySet();
		for (SupportedVaccineGroupConcept svc : svcs) {
			List<SupportedDiseaseConcept> sdcs = vaccineGroupDiseases.get(svc);
			if (sdcs != null) {
				for (SupportedDiseaseConcept lSDC : sdcs) {
					if (pSDCs.contains(lSDC)) {
						i++;
						break;
					}
				}
			}
		}
		
		return i;
	}
	
	
	/**
	 * Incoming immunizations from the patient's history imply which diseases are being treated, and from this information, we return
	 * all VaccineGroup series associated with each of these diseases as potential series to try to evaluate and forecast against. 
	 * @return List<TargetSeries> candidate series to try to evaluate against; empty if no candidate series
	 */
	 // TODO: 
	public List<SeriesRules> getCandidateSeries() {

		List<SeriesRules> allSeries = new ArrayList<SeriesRules>();
		if (vaccineGroupSeries == null) {
			return allSeries;
		}

		List<SeriesRules> seriesRules = new ArrayList<SeriesRules>();
		List<SeriesRules> seriesForVG = vaccineGroupSeries.get(SupportedVaccineGroupConcept.HepB);
		if (seriesForVG != null)
			seriesRules.addAll(seriesForVG);
		seriesForVG = vaccineGroupSeries.get(SupportedVaccineGroupConcept.HepA);
		if (seriesForVG != null) 
			seriesRules.addAll(seriesForVG);
		seriesForVG = vaccineGroupSeries.get(SupportedVaccineGroupConcept.MMR);
		if (seriesForVG != null) {
			seriesRules.addAll(seriesForVG);
		}
		seriesForVG = vaccineGroupSeries.get(SupportedVaccineGroupConcept.Varicella);
		if (seriesForVG != null) {
			seriesRules.addAll(seriesForVG);
		}
		seriesForVG = vaccineGroupSeries.get(SupportedVaccineGroupConcept.Rotavirus);
		if (seriesForVG != null) {
			seriesRules.addAll(seriesForVG);
		}
		seriesForVG = vaccineGroupSeries.get(SupportedVaccineGroupConcept.Hib);
		if (seriesForVG != null) {
			seriesRules.addAll(seriesForVG);
		}
		seriesForVG = vaccineGroupSeries.get(SupportedVaccineGroupConcept.HPV);
		if (seriesForVG != null) {
			seriesRules.addAll(seriesForVG);
		}
		seriesForVG = vaccineGroupSeries.get(SupportedVaccineGroupConcept.PCV);
		if (seriesForVG != null) {
			seriesRules.addAll(seriesForVG);
		}
		seriesForVG = vaccineGroupSeries.get(SupportedVaccineGroupConcept.PPSV);
		if (seriesForVG != null) {
			seriesRules.addAll(seriesForVG);
		}
		seriesForVG = vaccineGroupSeries.get(SupportedVaccineGroupConcept.Influenza);
		if (seriesForVG != null) {
			seriesRules.addAll(seriesForVG);
		}
		seriesForVG = vaccineGroupSeries.get(SupportedVaccineGroupConcept.H1N1);
		if (seriesForVG != null) {
			seriesRules.addAll(seriesForVG);
		}
		seriesForVG = vaccineGroupSeries.get(SupportedVaccineGroupConcept.Meningococcal);
		if (seriesForVG != null) {
			seriesRules.addAll(seriesForVG);
		}
		seriesForVG = vaccineGroupSeries.get(SupportedVaccineGroupConcept.Polio);
		if (seriesForVG != null) {
			seriesRules.addAll(seriesForVG);
		}

		return seriesRules;
	}
	

	private void initializeVaccineGroupSeries() 
		throws ImproperUsageException {

		String _METHODNAME = "initializeVaccineGroupSeries(): ";

		vaccineGroupSeries = new EnumMap<SupportedVaccineGroupConcept, List<SeriesRules>> (SupportedVaccineGroupConcept.class);

		///////////////////////////////////// TODO: Import this supporting data from Rules Authoring Tool //////////////////////////////////////////////////////////////////////

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// HepB Vaccine Group
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Create target doses for series "HepB Child/Adult Series" ///////////////////////////////////////////

		List<Vaccine> hepBPreferableVaccines = new ArrayList<Vaccine>();
		hepBPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._HEPB_LESSTHAN_20));
		hepBPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._HEPB_HIGHRISK_INFANT));
		hepBPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._HEPB_NOS));
		hepBPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._HEPB_GREATEREQUAL_20));
		hepBPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._HEPB_DIALYSIS));
		hepBPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._TWINRIX));
		hepBPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP_HEPB_IPV));
		hepBPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._HEPB_HIB));
		
		List<Vaccine> hepBAllowableVaccines = new ArrayList<Vaccine>();
		hepBAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTP_HIB_HEPB));

		DoseRule seriesDoseRule0_1 = new DoseRule();
		seriesDoseRule0_1.setDoseNumber(1);
		seriesDoseRule0_1.setAbsoluteMinimumAge(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule0_1.setMinimumAge(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule0_1.setEarliestRecommendedAge(new TimePeriod(2, TimePeriod.DurationType.MONTHS));
		seriesDoseRule0_1.setAbsoluteMinimumInterval(new TimePeriod(24, TimePeriod.DurationType.DAYS));
		seriesDoseRule0_1.setMinimumInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule0_1.setEarliestRecommendedInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule0_1.setPreferableVaccines(hepBPreferableVaccines);
		seriesDoseRule0_1.setAllowableVaccines(hepBAllowableVaccines);
		// Vaccine Group Level Rule, Target Dose 2 
		DoseRule seriesDoseRule0_2 = new DoseRule();
		seriesDoseRule0_2.setDoseNumber(2);
		seriesDoseRule0_2.setAbsoluteMinimumAge(new TimePeriod(52, TimePeriod.DurationType.DAYS));
		seriesDoseRule0_2.setMinimumAge(new TimePeriod(56, TimePeriod.DurationType.DAYS));
		seriesDoseRule0_2.setEarliestRecommendedAge(new TimePeriod(4, TimePeriod.DurationType.MONTHS));
		seriesDoseRule0_2.setAbsoluteMinimumInterval(new TimePeriod(52, TimePeriod.DurationType.DAYS));
		seriesDoseRule0_2.setMinimumInterval(new TimePeriod(56, TimePeriod.DurationType.DAYS));
		seriesDoseRule0_2.setEarliestRecommendedInterval(new TimePeriod(56, TimePeriod.DurationType.DAYS));
		seriesDoseRule0_2.setPreferableVaccines(hepBPreferableVaccines);
		seriesDoseRule0_2.setAllowableVaccines(hepBAllowableVaccines);
		// Vaccine Group Level Rule, Target Dose 3
		DoseRule seriesDoseRule0_3 = new DoseRule();
		seriesDoseRule0_3.setDoseNumber(3);
		seriesDoseRule0_3.setAbsoluteMinimumAge(new TimePeriod(164, TimePeriod.DurationType.DAYS));
		seriesDoseRule0_3.setMinimumAge(new TimePeriod(168, TimePeriod.DurationType.DAYS));
		seriesDoseRule0_3.setEarliestRecommendedAge(new TimePeriod(6, TimePeriod.DurationType.MONTHS));
		seriesDoseRule0_3.setAbsoluteMinimumInterval(null);
		seriesDoseRule0_3.setMinimumInterval(null);
		seriesDoseRule0_3.setEarliestRecommendedInterval(null);				
		seriesDoseRule0_3.setPreferableVaccines(hepBPreferableVaccines);
		seriesDoseRule0_3.setAllowableVaccines(hepBAllowableVaccines);
		
		// Create Target Series "HepB Child/Adult Series" /////////////////////////////////////////////////////
		// As per above, routine ages and intervals are the same across the vaccines in this series
		SeriesRules seriesRules1 = new SeriesRules("HepB Child/Adult Series", SupportedVaccineGroupConcept.HepB);
		List<DoseRule> tds = new ArrayList<DoseRule>();
		tds.add(seriesDoseRule0_1);
		tds.add(seriesDoseRule0_2);
		tds.add(seriesDoseRule0_3);
		seriesRules1.setSeriesDoseRules(tds);

		// Insert Series1 into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(seriesRules1);
			

		// Create Series 2 "Hepb Newborn Series" //////////////////////////////////////////////////////////////////////////////////
		// Vaccine Group Level, Target Dose 1
		DoseRule seriesDoseRule1_1 = new DoseRule();
		seriesDoseRule1_1.setDoseNumber(1);
		seriesDoseRule1_1.setAbsoluteMinimumAge(new TimePeriod(0, TimePeriod.DurationType.DAYS));
		seriesDoseRule1_1.setMinimumAge(new TimePeriod(0, TimePeriod.DurationType.DAYS));
		seriesDoseRule1_1.setEarliestRecommendedAge(new TimePeriod(0, TimePeriod.DurationType.MONTHS));
		seriesDoseRule1_1.setAbsoluteMinimumInterval(new TimePeriod(24, TimePeriod.DurationType.DAYS));
		seriesDoseRule1_1.setMinimumInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule1_1.setEarliestRecommendedInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule1_1.setPreferableVaccines(hepBPreferableVaccines);
		seriesDoseRule1_1.setAllowableVaccines(hepBAllowableVaccines);
		// Vaccine Group Level, Target Dose 2
		DoseRule seriesDoseRule1_2 = new DoseRule();
		seriesDoseRule1_2.setDoseNumber(2);
		seriesDoseRule1_2.setAbsoluteMinimumAge(new TimePeriod(24, TimePeriod.DurationType.DAYS));
		seriesDoseRule1_2.setMinimumAge(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule1_2.setEarliestRecommendedAge(new TimePeriod(2, TimePeriod.DurationType.MONTHS));
		seriesDoseRule1_2.setAbsoluteMinimumInterval(new TimePeriod(52, TimePeriod.DurationType.DAYS));
		seriesDoseRule1_2.setMinimumInterval(new TimePeriod(56, TimePeriod.DurationType.DAYS));
		seriesDoseRule1_2.setEarliestRecommendedInterval(new TimePeriod(56, TimePeriod.DurationType.DAYS));
		seriesDoseRule1_2.setPreferableVaccines(hepBPreferableVaccines);
		seriesDoseRule1_2.setAllowableVaccines(hepBAllowableVaccines);
		// Vaccine Group Level , Target Dose 3
		DoseRule seriesDoseRule1_3 = new DoseRule();
		seriesDoseRule1_3.setDoseNumber(3);
		seriesDoseRule1_3.setAbsoluteMinimumAge(new TimePeriod(164, TimePeriod.DurationType.DAYS));
		seriesDoseRule1_3.setMinimumAge(new TimePeriod(168, TimePeriod.DurationType.DAYS));
		seriesDoseRule1_3.setEarliestRecommendedAge(new TimePeriod(6, TimePeriod.DurationType.MONTHS));
		seriesDoseRule1_3.setAbsoluteMinimumInterval(null);
		seriesDoseRule1_3.setMinimumInterval(null);
		seriesDoseRule1_3.setEarliestRecommendedInterval(null);	
		seriesDoseRule1_3.setPreferableVaccines(hepBPreferableVaccines);
		seriesDoseRule1_3.setAllowableVaccines(hepBAllowableVaccines);
		
		// Create Target Series "HepB Newborn Series" /////////////////////////////////////////////////////
		// As per above, routine ages and intervals are the same across the vaccines in this series
		SeriesRules seriesRules2 = new SeriesRules("HepB Newborn Series", SupportedVaccineGroupConcept.HepB);
		tds = new ArrayList<DoseRule>();
		tds.add(seriesDoseRule1_1);
		tds.add(seriesDoseRule1_2);
		tds.add(seriesDoseRule1_3);
		seriesRules2.setSeriesDoseRules(tds);

		// Insert Series2 into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(seriesRules2);


		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// HepA Vaccine Group
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//
		// Create Series "HepA Child/Adult Series" //////////////////////////////////////////////////////////////////////////////////
		//
		List<Vaccine> hepAPreferableVaccines = new ArrayList<Vaccine>();
		hepAPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._HEPA_PEDADOL_2_DOSE));
		hepAPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._HEPA_PEDADOL_3_DOSE));
		hepAPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._HEPA_ADULT));
		hepAPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._HEPA_PED_NOS));
		hepAPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._HEPA_NOS));
		hepAPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._TWINRIX));

		DoseRule seriesDoseRule2_1 = new DoseRule();
		seriesDoseRule2_1.setDoseNumber(1);
		seriesDoseRule2_1.setAbsoluteMinimumAge(new TimePeriod(361, TimePeriod.DurationType.DAYS));
		seriesDoseRule2_1.setMinimumAge(new TimePeriod(1, TimePeriod.DurationType.YEARS));
		seriesDoseRule2_1.setEarliestRecommendedAge(new TimePeriod(1, TimePeriod.DurationType.YEARS));
		// seriesDoseRule2_1.setAbsoluteMinimumInterval(new TimePeriod(164, TimePeriod.DurationType.DAYS));
		seriesDoseRule2_1.setAbsoluteMinimumInterval(new TimePeriod("6m-4d"));
		seriesDoseRule2_1.setMinimumInterval(new TimePeriod(6, TimePeriod.DurationType.MONTHS));
		seriesDoseRule2_1.setEarliestRecommendedInterval(new TimePeriod(6, TimePeriod.DurationType.MONTHS));
		seriesDoseRule2_1.setPreferableVaccines(hepAPreferableVaccines);
		// Vaccine Group Level Rule, Target Dose 2 
		DoseRule seriesDoseRule2_2 = new DoseRule();
		seriesDoseRule2_2.setDoseNumber(2);
		// seriesDoseRule2_2.setAbsoluteMinimumAge(new TimePeriod(525, TimePeriod.DurationType.DAYS));
		seriesDoseRule2_2.setAbsoluteMinimumAge(new TimePeriod("18m-4d"));
		// seriesDoseRule2_2.setMinimumAge(new TimePeriod(529, TimePeriod.DurationType.DAYS));
		seriesDoseRule2_2.setMinimumAge(new TimePeriod(18, TimePeriod.DurationType.MONTHS));
		seriesDoseRule2_2.setEarliestRecommendedAge(new TimePeriod(18, TimePeriod.DurationType.MONTHS));
		seriesDoseRule2_2.setAbsoluteMinimumInterval(null);
		seriesDoseRule2_2.setMinimumInterval(null);
		seriesDoseRule2_2.setEarliestRecommendedInterval(null);
		seriesDoseRule2_2.setPreferableVaccines(hepAPreferableVaccines);

		// Create encompassing Series Rule for "HepA Child/Adult Series" /////////////////////////////////////////////////////
		SeriesRules seriesRules3 = new SeriesRules("HepA 2-Dose Child/Adult Series", SupportedVaccineGroupConcept.HepA);
		tds = new ArrayList<DoseRule>();
		tds.add(seriesDoseRule2_1);
		tds.add(seriesDoseRule2_2);
		seriesRules3.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(seriesRules3);
			
		
		//
		// Create Series "HepA Adult 3-Dose Series" //////////////////////////////////////////////////////////////////////////////////
		//
		DoseRule seriesDoseRule3_1 = new DoseRule();
		seriesDoseRule3_1.setDoseNumber(1);
		seriesDoseRule3_1.setAbsoluteMinimumAge(new TimePeriod(19, TimePeriod.DurationType.YEARS));
		seriesDoseRule3_1.setMinimumAge(new TimePeriod(19, TimePeriod.DurationType.YEARS));
		seriesDoseRule3_1.setEarliestRecommendedAge(null);
		seriesDoseRule3_1.setAbsoluteMinimumInterval(new TimePeriod(24, TimePeriod.DurationType.DAYS));
		seriesDoseRule3_1.setMinimumInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule3_1.setEarliestRecommendedInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule3_1.setPreferableVaccines(hepAPreferableVaccines);
		// Vaccine Group Level Rule, Dose 2 
		DoseRule seriesDoseRule3_2 = new DoseRule();
		seriesDoseRule3_2.setDoseNumber(2);
		seriesDoseRule3_2.setAbsoluteMinimumAge(null);
		seriesDoseRule3_2.setEarliestRecommendedAge(null);
		// seriesDoseRule3_2.setAbsoluteMinimumInterval(new TimePeriod(136, TimePeriod.DurationType.DAYS));
		seriesDoseRule3_2.setAbsoluteMinimumInterval(new TimePeriod("5m-4d"));
		seriesDoseRule3_2.setMinimumInterval(new TimePeriod(5, TimePeriod.DurationType.MONTHS));
		seriesDoseRule3_2.setEarliestRecommendedInterval(new TimePeriod(5, TimePeriod.DurationType.MONTHS));
		seriesDoseRule3_2.setPreferableVaccines(hepAPreferableVaccines);
		// Vaccine Group Level Rule, Dose 3 
		DoseRule seriesDoseRule3_3 = new DoseRule();
		seriesDoseRule3_3.setDoseNumber(3);
		seriesDoseRule3_3.setAbsoluteMinimumAge(null);
		seriesDoseRule3_3.setMinimumAge(null);
		seriesDoseRule3_3.setEarliestRecommendedAge(null);
		seriesDoseRule3_3.setAbsoluteMinimumInterval(null);
		seriesDoseRule3_3.setMinimumInterval(null);
		seriesDoseRule3_3.setEarliestRecommendedInterval(null);
		seriesDoseRule3_3.setPreferableVaccines(hepAPreferableVaccines);

		// Create encompassing Series Rule for "HepA Child/Adult Series" /////////////////////////////////////////////////////
		SeriesRules seriesRules4 = new SeriesRules("HepA Adult 3-Dose Series", SupportedVaccineGroupConcept.HepA);
		tds = new ArrayList<DoseRule>();
		tds.add(seriesDoseRule3_1);
		tds.add(seriesDoseRule3_2);
		tds.add(seriesDoseRule3_3);
		seriesRules4.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(seriesRules4);

		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// MMR Vaccine Group
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		//
		// Create Series "MMR 2-Dose Series" //////////////////////////////////////////////////////////////////////////////////
		//
		List<Vaccine> mmrPreferableVaccines = new ArrayList<Vaccine>();
		mmrPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._MMR));
		mmrPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._MMR_VARICELLA));
		List<Vaccine> mmrAllowableVaccines = new ArrayList<Vaccine>();
		mmrAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._MEASLES));
		mmrAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._MUMPS));
		mmrAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._RUBELLA));
		mmrAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._MEASLES_RUBELLA));
		mmrAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._MUMPS_RUBELLA));

		DoseRule seriesDoseRule5_1 = new DoseRule();
		seriesDoseRule5_1.setDoseNumber(1);
		seriesDoseRule5_1.setAbsoluteMinimumAge(new TimePeriod(361, TimePeriod.DurationType.DAYS));
		seriesDoseRule5_1.setMinimumAge(new TimePeriod(1, TimePeriod.DurationType.YEARS));
		seriesDoseRule5_1.setEarliestRecommendedAge(new TimePeriod(1, TimePeriod.DurationType.YEARS));
		seriesDoseRule5_1.setAbsoluteMinimumInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule5_1.setMinimumInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule5_1.setEarliestRecommendedInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule5_1.setPreferableVaccines(mmrPreferableVaccines);
		seriesDoseRule5_1.setAllowableVaccines(mmrAllowableVaccines);
		// Vaccine Group Level Rule, Dose 2 
		DoseRule seriesDoseRule5_2 = new DoseRule();
		seriesDoseRule5_2.setDoseNumber(2);
		seriesDoseRule5_2.setAbsoluteMinimumAge(new TimePeriod(389, TimePeriod.DurationType.DAYS));
		seriesDoseRule5_2.setMinimumAge(new TimePeriod(389, TimePeriod.DurationType.DAYS));
		seriesDoseRule5_2.setEarliestRecommendedAge(new TimePeriod(4, DurationType.YEARS));
		seriesDoseRule5_2.setAbsoluteMinimumInterval(null);
		seriesDoseRule5_2.setMinimumInterval(null);
		seriesDoseRule5_2.setEarliestRecommendedInterval(null);
		seriesDoseRule5_2.setPreferableVaccines(mmrPreferableVaccines);
		seriesDoseRule5_2.setAllowableVaccines(mmrAllowableVaccines);

		// Create encompassing Series Rule for "MMR 2-Dose Series" /////////////////////////////////////////////////////
		SeriesRules seriesRules6 = new SeriesRules("MMR 2-Dose Series", SupportedVaccineGroupConcept.MMR);
		tds = new ArrayList<DoseRule>();
		tds.add(seriesDoseRule5_1);
		tds.add(seriesDoseRule5_2);
		seriesRules6.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(seriesRules6);
			
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Varicella Vaccine Group
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		//
		// Create Series "Varicella 2-Dose Series" //////////////////////////////////////////////////////////////////////////////////
		//
		List<Vaccine> varicellaPreferableVaccines = new ArrayList<Vaccine>();
		varicellaPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._VARICELLA));
		varicellaPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._MMR_VARICELLA));

		DoseRule seriesDoseRule6_1 = new DoseRule();
		seriesDoseRule6_1.setDoseNumber(1);
		seriesDoseRule6_1.setAbsoluteMinimumAge(new TimePeriod(361, TimePeriod.DurationType.DAYS));
		seriesDoseRule6_1.setMinimumAge(new TimePeriod(365, TimePeriod.DurationType.DAYS));
		seriesDoseRule6_1.setEarliestRecommendedAge(new TimePeriod(1, TimePeriod.DurationType.YEARS));
		seriesDoseRule6_1.setAbsoluteMinimumInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule6_1.setMinimumInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule6_1.setEarliestRecommendedInterval(new TimePeriod(84, TimePeriod.DurationType.DAYS));
		seriesDoseRule6_1.setPreferableVaccines(varicellaPreferableVaccines);
		// Vaccine Group Level Rule, Dose 2 
		DoseRule seriesDoseRule6_2 = new DoseRule();
		seriesDoseRule6_2.setDoseNumber(2);
		seriesDoseRule6_2.setAbsoluteMinimumAge(new TimePeriod(389, TimePeriod.DurationType.DAYS));
		seriesDoseRule6_2.setMinimumAge(new TimePeriod(389, TimePeriod.DurationType.DAYS));
		seriesDoseRule6_2.setEarliestRecommendedAge(new TimePeriod(4, DurationType.YEARS));
		seriesDoseRule6_2.setAbsoluteMinimumInterval(null);
		seriesDoseRule6_2.setMinimumInterval(null);
		seriesDoseRule6_2.setEarliestRecommendedInterval(null);
		seriesDoseRule6_2.setPreferableVaccines(varicellaPreferableVaccines);

		// Create encompassing Series Rule for "MMR 2-Dose Series" /////////////////////////////////////////////////////
		SeriesRules seriesRules7 = new SeriesRules("Varicella 2-Dose Series", SupportedVaccineGroupConcept.Varicella);
		tds = new ArrayList<DoseRule>();
		tds.add(seriesDoseRule6_1);
		tds.add(seriesDoseRule6_2);
		seriesRules7.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(seriesRules7);
		
	
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Rotavirus Vaccine Group
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		//
		// Create Series "Rotavirus 2-Dose Series" //////////////////////////////////////////////////////////////////////////////////
		// Only Rotavirus RV1 is applicable to this series
		//
		List<Vaccine> rotavirusPreferableVaccines = new ArrayList<Vaccine>();
		rotavirusPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._ROTAVIRUS_RV1));
		List<Vaccine> rotavirusAllowableVaccines = new ArrayList<Vaccine>();
		
		DoseRule seriesDoseRule7_1 = new DoseRule();
		seriesDoseRule7_1.setDoseNumber(1);
		seriesDoseRule7_1.setAbsoluteMinimumAge(new TimePeriod(38, TimePeriod.DurationType.DAYS));
		seriesDoseRule7_1.setMinimumAge(new TimePeriod(42, TimePeriod.DurationType.DAYS));
		seriesDoseRule7_1.setEarliestRecommendedAge(new TimePeriod(2, TimePeriod.DurationType.MONTHS));
		seriesDoseRule7_1.setAbsoluteMinimumInterval(new TimePeriod(24, TimePeriod.DurationType.DAYS));
		seriesDoseRule7_1.setMinimumInterval(new TimePeriod(28, DurationType.DAYS));
		seriesDoseRule7_1.setEarliestRecommendedInterval(new TimePeriod(28, DurationType.DAYS));
		seriesDoseRule7_1.setPreferableVaccines(rotavirusPreferableVaccines);
		seriesDoseRule7_1.setAllowableVaccines(rotavirusAllowableVaccines);
		// Vaccine Group Level Rule, Dose 2 
		DoseRule seriesDoseRule7_2 = new DoseRule();
		seriesDoseRule7_2.setDoseNumber(2);
		seriesDoseRule7_2.setAbsoluteMinimumAge(new TimePeriod(66, TimePeriod.DurationType.DAYS));
		seriesDoseRule7_2.setMinimumAge(new TimePeriod(70, TimePeriod.DurationType.DAYS));
		seriesDoseRule7_2.setEarliestRecommendedAge(new TimePeriod(4, DurationType.MONTHS));
		seriesDoseRule7_2.setAbsoluteMinimumInterval(null);
		seriesDoseRule7_2.setMinimumInterval(null);
		seriesDoseRule7_2.setEarliestRecommendedInterval(null);
		seriesDoseRule7_2.setPreferableVaccines(rotavirusPreferableVaccines);
		seriesDoseRule7_2.setAllowableVaccines(rotavirusAllowableVaccines);
		
		// Create encompassing Series Rule for "Rotavirus 2-Dose Series" /////////////////////////////////////////////////////
		SeriesRules seriesRules8 = new SeriesRules("Rotavirus 2-Dose Series", SupportedVaccineGroupConcept.Rotavirus);
		tds = new ArrayList<DoseRule>();
		tds.add(seriesDoseRule7_1);
		tds.add(seriesDoseRule7_2);
		seriesRules8.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(seriesRules8);

		
		//
		// Create Series "Rotavirus 3-Dose Series" //////////////////////////////////////////////////////////////////////////////////
		// All 4 rotavirus vaccines can be administered for this series
		//
		rotavirusPreferableVaccines = new ArrayList<Vaccine>();
		rotavirusPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._ROTAVIRUS_RV5));
		rotavirusPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._ROTAVIRUS_RV1));
		rotavirusPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._ROTAVIRUS_NOS));
		rotavirusPreferableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._ROTAVIRUS));

		DoseRule seriesDoseRule8_1 = new DoseRule();
		seriesDoseRule8_1.setDoseNumber(1);
		seriesDoseRule8_1.setAbsoluteMinimumAge(new TimePeriod(38, TimePeriod.DurationType.DAYS));
		seriesDoseRule8_1.setMinimumAge(new TimePeriod(42, TimePeriod.DurationType.DAYS));
		seriesDoseRule8_1.setEarliestRecommendedAge(new TimePeriod(2, TimePeriod.DurationType.MONTHS));
		seriesDoseRule8_1.setAbsoluteMinimumInterval(new TimePeriod(24, TimePeriod.DurationType.DAYS));
		seriesDoseRule8_1.setMinimumInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule8_1.setEarliestRecommendedInterval(new TimePeriod(28, DurationType.DAYS));
		seriesDoseRule8_1.setPreferableVaccines(rotavirusPreferableVaccines);
		// Vaccine Group Level Rule, Dose 2 
		DoseRule seriesDoseRule8_2 = new DoseRule();
		seriesDoseRule8_2.setDoseNumber(2);
		seriesDoseRule8_2.setAbsoluteMinimumAge(new TimePeriod(66, TimePeriod.DurationType.DAYS));
		seriesDoseRule8_2.setMinimumAge(new TimePeriod(70, TimePeriod.DurationType.DAYS));
		seriesDoseRule8_2.setEarliestRecommendedAge(new TimePeriod(4, DurationType.MONTHS));
		seriesDoseRule8_2.setAbsoluteMinimumInterval(new TimePeriod(24, TimePeriod.DurationType.DAYS));
		seriesDoseRule8_2.setMinimumInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule8_2.setEarliestRecommendedInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule8_2.setPreferableVaccines(rotavirusPreferableVaccines);
		// Vaccine Group Level Rule, Dose 3
		DoseRule seriesDoseRule8_3 = new DoseRule();
		seriesDoseRule8_3.setDoseNumber(3);
		seriesDoseRule8_3.setAbsoluteMinimumAge(new TimePeriod(94, TimePeriod.DurationType.DAYS));
		seriesDoseRule8_3.setMinimumAge(new TimePeriod(98, TimePeriod.DurationType.DAYS));
		seriesDoseRule8_3.setEarliestRecommendedAge(new TimePeriod(6, DurationType.MONTHS));
		seriesDoseRule8_3.setAbsoluteMinimumInterval(null);
		seriesDoseRule8_3.setMinimumInterval(null);
		seriesDoseRule8_3.setEarliestRecommendedInterval(null);
		seriesDoseRule8_3.setPreferableVaccines(rotavirusPreferableVaccines);

		// Create encompassing Series Rule for "Rotavirus 3-Dose Series" /////////////////////////////////////////////////////
		SeriesRules seriesRules9 = new SeriesRules("Rotavirus 3-Dose Series", SupportedVaccineGroupConcept.Rotavirus);
		tds = new ArrayList<DoseRule>();
		tds.add(seriesDoseRule8_1);
		tds.add(seriesDoseRule8_2);
		tds.add(seriesDoseRule8_3);
		seriesRules9.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(seriesRules9);

		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Hib Vaccine Group
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		//
		// Create Series "Hib 4-Dose Series" //////////////////////////////////////////////////////////////////////////////////
		// Allowable CVX codes: 17, 46, 47, 48, 49, 51, 120, 22, 148 for first 3 doses. All of those and 50 for last (4th) dose
		//
		List<Vaccine> HibPreferableVaccinesDoses1to3 = new ArrayList<Vaccine>();
		HibPreferableVaccinesDoses1to3.add(supportedVaccinesMap.get(SupportedVaccineConcept._HIB_NOS));
		HibPreferableVaccinesDoses1to3.add(supportedVaccinesMap.get(SupportedVaccineConcept._HIB_PRP_D));
		HibPreferableVaccinesDoses1to3.add(supportedVaccinesMap.get(SupportedVaccineConcept._HIB_HBOC));
		HibPreferableVaccinesDoses1to3.add(supportedVaccinesMap.get(SupportedVaccineConcept._HIB_PRP_T));
		HibPreferableVaccinesDoses1to3.add(supportedVaccinesMap.get(SupportedVaccineConcept._HIB_PRP_OMP));
		HibPreferableVaccinesDoses1to3.add(supportedVaccinesMap.get(SupportedVaccineConcept._HEPB_HIB));
		HibPreferableVaccinesDoses1to3.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP_HIB_IPV));
		HibPreferableVaccinesDoses1to3.add(supportedVaccinesMap.get(SupportedVaccineConcept._MENING_HIB));
		HibPreferableVaccinesDoses1to3.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTP_HIB));
	
		List<Vaccine> HibAllowableVaccinesDoses1to3 = new ArrayList<Vaccine>();
		HibAllowableVaccinesDoses1to3.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTP_HIB_HEPB));
		
		DoseRule seriesDoseRule1 = new DoseRule();
		seriesDoseRule1.setDoseNumber(1);
		seriesDoseRule1.setAbsoluteMinimumAge(new TimePeriod(38, TimePeriod.DurationType.DAYS));
		seriesDoseRule1.setMinimumAge(new TimePeriod(42, TimePeriod.DurationType.DAYS));
		seriesDoseRule1.setEarliestRecommendedAge(new TimePeriod(2, TimePeriod.DurationType.MONTHS));
		seriesDoseRule1.setAbsoluteMinimumInterval(new TimePeriod(24, TimePeriod.DurationType.DAYS));
		seriesDoseRule1.setMinimumInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule1.setEarliestRecommendedInterval(new TimePeriod(28, DurationType.DAYS));
		seriesDoseRule1.setPreferableVaccines(HibPreferableVaccinesDoses1to3);
		seriesDoseRule1.setAllowableVaccines(HibAllowableVaccinesDoses1to3);
		// Vaccine Group Level Rule, Dose 2 
		DoseRule seriesDoseRule2 = new DoseRule();
		seriesDoseRule2.setDoseNumber(2);
		seriesDoseRule2.setAbsoluteMinimumAge(new TimePeriod(66, TimePeriod.DurationType.DAYS));
		seriesDoseRule2.setMinimumAge(new TimePeriod(70, TimePeriod.DurationType.DAYS));
		seriesDoseRule2.setEarliestRecommendedAge(new TimePeriod(4, DurationType.MONTHS));
		seriesDoseRule2.setAbsoluteMinimumInterval(new TimePeriod(24, TimePeriod.DurationType.DAYS));
		seriesDoseRule2.setMinimumInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule2.setEarliestRecommendedInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule2.setPreferableVaccines(HibPreferableVaccinesDoses1to3);
		seriesDoseRule2.setAllowableVaccines(HibAllowableVaccinesDoses1to3);
		// Vaccine Group Level Rule, Dose 3
		DoseRule seriesDoseRule3 = new DoseRule();
		seriesDoseRule3.setDoseNumber(3);
		seriesDoseRule3.setAbsoluteMinimumAge(new TimePeriod(94, TimePeriod.DurationType.DAYS));
		seriesDoseRule3.setMinimumAge(new TimePeriod(98, TimePeriod.DurationType.DAYS));
		seriesDoseRule3.setEarliestRecommendedAge(new TimePeriod(6, DurationType.MONTHS));
		seriesDoseRule3.setAbsoluteMinimumInterval(new TimePeriod(52, TimePeriod.DurationType.DAYS));
		seriesDoseRule3.setMinimumInterval(new TimePeriod(56, TimePeriod.DurationType.DAYS));
		seriesDoseRule3.setEarliestRecommendedInterval(new TimePeriod(56, TimePeriod.DurationType.DAYS));
		seriesDoseRule3.setPreferableVaccines(HibPreferableVaccinesDoses1to3);
		seriesDoseRule3.setAllowableVaccines(HibAllowableVaccinesDoses1to3);
		// Vaccine Group Level Rule, Dose 4
		DoseRule seriesDoseRule4 = new DoseRule();
		seriesDoseRule4.setDoseNumber(4);
		seriesDoseRule4.setAbsoluteMinimumAge(new TimePeriod(361, TimePeriod.DurationType.DAYS));
		seriesDoseRule4.setMinimumAge(new TimePeriod(365, TimePeriod.DurationType.DAYS));
		seriesDoseRule4.setEarliestRecommendedAge(new TimePeriod(12, DurationType.MONTHS));
		seriesDoseRule4.setAbsoluteMinimumInterval(null);
		seriesDoseRule4.setMinimumInterval(null);
		seriesDoseRule4.setEarliestRecommendedInterval(null);
		seriesDoseRule4.setPreferableVaccines(HibPreferableVaccinesDoses1to3);
		seriesDoseRule4.setAllowableVaccines(HibAllowableVaccinesDoses1to3);
		
		// Create encompassing Series Rule for "Hib 4-Dose Series" /////////////////////////////////////////////////////
		SeriesRules seriesRules = new SeriesRules("Hib 4-Dose Series", SupportedVaccineGroupConcept.Hib);
		tds = new ArrayList<DoseRule>();
		tds.add(seriesDoseRule1);
		tds.add(seriesDoseRule2);
		tds.add(seriesDoseRule3);
		tds.add(seriesDoseRule4);
		seriesRules.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(seriesRules);

		
		//
		// Create Series "Hib OMP Series" //////////////////////////////////////////////////////////////////////////////////
		// Allowable CVX codes: 17, 46, 47, 48, 49, 51, 120, 22, 148 for first 3 doses. All of those and 50 for last (4th) dose
		//
		List<Vaccine> hibPreferableVaccinesOMPDoses1to2 = new ArrayList<Vaccine>();
		hibPreferableVaccinesOMPDoses1to2.add(supportedVaccinesMap.get(SupportedVaccineConcept._HIB_PRP_OMP));
		hibPreferableVaccinesOMPDoses1to2.add(supportedVaccinesMap.get(SupportedVaccineConcept._HEPB_HIB));
		
		DoseRule seriesDoseRule1O = new DoseRule();
		seriesDoseRule1O.setDoseNumber(1);
		seriesDoseRule1O.setAbsoluteMinimumAge(new TimePeriod(38, TimePeriod.DurationType.DAYS));
		seriesDoseRule1O.setMinimumAge(new TimePeriod(42, TimePeriod.DurationType.DAYS));
		seriesDoseRule1O.setEarliestRecommendedAge(new TimePeriod(2, TimePeriod.DurationType.MONTHS));
		seriesDoseRule1O.setAbsoluteMinimumInterval(new TimePeriod(24, TimePeriod.DurationType.DAYS));
		seriesDoseRule1O.setMinimumInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		seriesDoseRule1O.setEarliestRecommendedInterval(new TimePeriod(28, DurationType.DAYS));
		seriesDoseRule1O.setPreferableVaccines(hibPreferableVaccinesOMPDoses1to2);
		// Vaccine Group Level Rule, Dose 2 
		DoseRule seriesDoseRule2O = new DoseRule();
		seriesDoseRule2O.setDoseNumber(2);
		seriesDoseRule2O.setAbsoluteMinimumAge(new TimePeriod(66, TimePeriod.DurationType.DAYS));
		seriesDoseRule2O.setMinimumAge(new TimePeriod(70, TimePeriod.DurationType.DAYS));
		seriesDoseRule2O.setEarliestRecommendedAge(new TimePeriod(4, DurationType.MONTHS));
		seriesDoseRule2O.setAbsoluteMinimumInterval(new TimePeriod(52, TimePeriod.DurationType.DAYS));
		seriesDoseRule2O.setMinimumInterval(new TimePeriod(56, TimePeriod.DurationType.DAYS));
		seriesDoseRule2O.setEarliestRecommendedInterval(new TimePeriod(56, TimePeriod.DurationType.DAYS));
		seriesDoseRule2O.setPreferableVaccines(hibPreferableVaccinesOMPDoses1to2);
		// Vaccine Group Level Rule, Dose 3
		DoseRule seriesDoseRule3O = new DoseRule();
		seriesDoseRule3O.setDoseNumber(3);
		seriesDoseRule3O.setAbsoluteMinimumAge(new TimePeriod(361, TimePeriod.DurationType.DAYS));
		seriesDoseRule3O.setMinimumAge(new TimePeriod(365, TimePeriod.DurationType.DAYS));
		seriesDoseRule3O.setEarliestRecommendedAge(new TimePeriod(12, DurationType.MONTHS));
		seriesDoseRule3O.setAbsoluteMinimumInterval(null);
		seriesDoseRule3O.setMinimumInterval(null);
		seriesDoseRule3O.setEarliestRecommendedInterval(null);
		// Add remaining allowable doses permitted for Dose 3 of this series
		List<Vaccine> hibPreferableVaccinesOMPDoses3 = new ArrayList<Vaccine>();
		hibPreferableVaccinesOMPDoses3.add(supportedVaccinesMap.get(SupportedVaccineConcept._HIB_PRP_OMP));
		hibPreferableVaccinesOMPDoses3.add(supportedVaccinesMap.get(SupportedVaccineConcept._HEPB_HIB));
		hibPreferableVaccinesOMPDoses3.add(supportedVaccinesMap.get(SupportedVaccineConcept._HIB_NOS));
		hibPreferableVaccinesOMPDoses3.add(supportedVaccinesMap.get(SupportedVaccineConcept._HIB_PRP_D));
		hibPreferableVaccinesOMPDoses3.add(supportedVaccinesMap.get(SupportedVaccineConcept._HIB_HBOC));
		hibPreferableVaccinesOMPDoses3.add(supportedVaccinesMap.get(SupportedVaccineConcept._HIB_PRP_T));
		hibPreferableVaccinesOMPDoses3.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP_HIB_IPV));
		hibPreferableVaccinesOMPDoses3.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTP_HIB));
		hibPreferableVaccinesOMPDoses3.add(supportedVaccinesMap.get(SupportedVaccineConcept._MENING_HIB));
		// HibPreferableVaccinesDoses1to2.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP_HIB));		/// Only allowed per custom rule - not included in supporting data
		seriesDoseRule3O.setPreferableVaccines(hibPreferableVaccinesOMPDoses3);
		
		List<Vaccine> HibAllowableVaccinesOMPDose3 = new ArrayList<Vaccine>();
		HibAllowableVaccinesDoses1to3.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTP_HIB_HEPB));
		seriesDoseRule3O.setAllowableVaccines(HibAllowableVaccinesOMPDose3);
		
		// Create encompassing Series Rule for "Hib 4-Dose Series" /////////////////////////////////////////////////////
		seriesRules = new SeriesRules("Hib OMP Series", SupportedVaccineGroupConcept.Hib);
		tds = new ArrayList<DoseRule>();
		tds.add(seriesDoseRule1O);
		tds.add(seriesDoseRule2O);
		tds.add(seriesDoseRule3O);
		seriesRules.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(seriesRules);
		
		
		//
		// Create Series "HPV Series" //////////////////////////////////////////////////////////////////////////////////
		// Allowable CVX codes: 62,118,137
		//
		List<Vaccine> hpvVaccines = new ArrayList<Vaccine>();
		hpvVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._HPV_QUADRIVALENT));
		hpvVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._HPV_BIVALENT));
		hpvVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._HPV_NOS));
		
		DoseRule seriesDoseRuleHPV1 = new DoseRule();
		seriesDoseRuleHPV1.setDoseNumber(1);
		seriesDoseRuleHPV1.setAbsoluteMinimumAge(new TimePeriod("9y-4d"));
		seriesDoseRuleHPV1.setMinimumAge(new TimePeriod("9y"));
		seriesDoseRuleHPV1.setEarliestRecommendedAge(new TimePeriod("11y"));
		seriesDoseRuleHPV1.setAbsoluteMinimumInterval(new TimePeriod("24d"));
		seriesDoseRuleHPV1.setMinimumInterval(new TimePeriod("28d"));
		seriesDoseRuleHPV1.setEarliestRecommendedInterval(new TimePeriod("2m"));
		seriesDoseRuleHPV1.setPreferableVaccines(hpvVaccines);
		// Vaccine Group Level Rule, Dose 2 
		DoseRule seriesDoseRuleHPV2 = new DoseRule();
		seriesDoseRuleHPV2.setDoseNumber(2);
		seriesDoseRuleHPV2.setAbsoluteMinimumAge(new TimePeriod("9y24d"));
		seriesDoseRuleHPV2.setMinimumAge(new TimePeriod("9y28d"));
		seriesDoseRuleHPV2.setEarliestRecommendedAge(null);
		seriesDoseRuleHPV2.setAbsoluteMinimumInterval(new TimePeriod("80d"));
		seriesDoseRuleHPV2.setMinimumInterval(new TimePeriod("84d"));
		seriesDoseRuleHPV2.setEarliestRecommendedInterval(new TimePeriod("4m"));
		seriesDoseRuleHPV2.setPreferableVaccines(hpvVaccines);
		// Vaccine Group Level Rule, Dose 3
		DoseRule seriesDoseRuleHPV3 = new DoseRule();
		seriesDoseRuleHPV3.setDoseNumber(3);
		seriesDoseRuleHPV3.setAbsoluteMinimumAge(new TimePeriod("9y112d"));
		seriesDoseRuleHPV3.setMinimumAge(new TimePeriod("9y112d"));
		seriesDoseRuleHPV3.setEarliestRecommendedAge(null);
		seriesDoseRuleHPV3.setAbsoluteMinimumInterval(null);
		seriesDoseRuleHPV3.setMinimumInterval(null);
		seriesDoseRuleHPV3.setEarliestRecommendedInterval(null);
		seriesDoseRuleHPV3.setPreferableVaccines(hpvVaccines);
		
		// Create encompassing Series Rule for "HPV Series" /////////////////////////////////////////////////////
		seriesRules = new SeriesRules("HPV 3-Dose Series", SupportedVaccineGroupConcept.HPV);
		tds = new ArrayList<DoseRule>();
		tds.add(seriesDoseRuleHPV1);
		tds.add(seriesDoseRuleHPV2);
		tds.add(seriesDoseRuleHPV3);
		seriesRules.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(seriesRules);

		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// PCV Vaccine Group
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		//
		// Create Series "PCV 4-Dose Series" //////////////////////////////////////////////////////////////////////////////////
		// Allowable CVX codes: 
		//
		List<Vaccine> pcvPreferableVaccinesDoses = new ArrayList<Vaccine>();
		pcvPreferableVaccinesDoses.add(supportedVaccinesMap.get(SupportedVaccineConcept._PCV_7));
		pcvPreferableVaccinesDoses.add(supportedVaccinesMap.get(SupportedVaccineConcept._PCV_13));
		pcvPreferableVaccinesDoses.add(supportedVaccinesMap.get(SupportedVaccineConcept._PCV_NOS));
		pcvPreferableVaccinesDoses.add(supportedVaccinesMap.get(SupportedVaccineConcept._PCV_NOS109));
	
		DoseRule pcv4doseSeriesDoseRule1 = new DoseRule();
		pcv4doseSeriesDoseRule1.setDoseNumber(1);
		pcv4doseSeriesDoseRule1.setAbsoluteMinimumAge(new TimePeriod(38, TimePeriod.DurationType.DAYS));
		pcv4doseSeriesDoseRule1.setMinimumAge(new TimePeriod(42, TimePeriod.DurationType.DAYS));
		pcv4doseSeriesDoseRule1.setEarliestRecommendedAge(new TimePeriod(2, TimePeriod.DurationType.MONTHS));
		pcv4doseSeriesDoseRule1.setAbsoluteMinimumInterval(new TimePeriod(24, TimePeriod.DurationType.DAYS));
		pcv4doseSeriesDoseRule1.setMinimumInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		pcv4doseSeriesDoseRule1.setEarliestRecommendedInterval(new TimePeriod(28, DurationType.DAYS));
		pcv4doseSeriesDoseRule1.setPreferableVaccines(pcvPreferableVaccinesDoses);
		// Vaccine Group Level Rule, Dose 2 
		DoseRule pcv4DoseSeriesDoseRule2 = new DoseRule();
		pcv4DoseSeriesDoseRule2.setDoseNumber(2);
		pcv4DoseSeriesDoseRule2.setAbsoluteMinimumAge(new TimePeriod(66, TimePeriod.DurationType.DAYS));
		pcv4DoseSeriesDoseRule2.setMinimumAge(new TimePeriod(70, TimePeriod.DurationType.DAYS));
		pcv4DoseSeriesDoseRule2.setEarliestRecommendedAge(new TimePeriod(4, DurationType.MONTHS));
		pcv4DoseSeriesDoseRule2.setAbsoluteMinimumInterval(new TimePeriod(24, TimePeriod.DurationType.DAYS));
		pcv4DoseSeriesDoseRule2.setMinimumInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		pcv4DoseSeriesDoseRule2.setEarliestRecommendedInterval(new TimePeriod(28, TimePeriod.DurationType.DAYS));
		pcv4DoseSeriesDoseRule2.setPreferableVaccines(pcvPreferableVaccinesDoses);
		// Vaccine Group Level Rule, Dose 3
		DoseRule pcv4DoseSeriesDoseRule3 = new DoseRule();
		pcv4DoseSeriesDoseRule3.setDoseNumber(3);
		pcv4DoseSeriesDoseRule3.setAbsoluteMinimumAge(new TimePeriod(94, TimePeriod.DurationType.DAYS));
		pcv4DoseSeriesDoseRule3.setMinimumAge(new TimePeriod(98, TimePeriod.DurationType.DAYS));
		pcv4DoseSeriesDoseRule3.setEarliestRecommendedAge(new TimePeriod(6, DurationType.MONTHS));
		pcv4DoseSeriesDoseRule3.setAbsoluteMinimumInterval(new TimePeriod(52, TimePeriod.DurationType.DAYS));
		pcv4DoseSeriesDoseRule3.setMinimumInterval(new TimePeriod(56, TimePeriod.DurationType.DAYS));
		pcv4DoseSeriesDoseRule3.setEarliestRecommendedInterval(new TimePeriod(56, TimePeriod.DurationType.DAYS));
		pcv4DoseSeriesDoseRule3.setPreferableVaccines(pcvPreferableVaccinesDoses);
		// Vaccine Group Level Rule, Dose 4
		DoseRule pcv4DoseSeriesDoseRule4 = new DoseRule();
		pcv4DoseSeriesDoseRule4.setDoseNumber(4);
		pcv4DoseSeriesDoseRule4.setAbsoluteMinimumAge(new TimePeriod(361, TimePeriod.DurationType.DAYS));
		pcv4DoseSeriesDoseRule4.setMinimumAge(new TimePeriod(365, TimePeriod.DurationType.DAYS));
		pcv4DoseSeriesDoseRule4.setEarliestRecommendedAge(new TimePeriod(12, DurationType.MONTHS));
		pcv4DoseSeriesDoseRule4.setAbsoluteMinimumInterval(null);
		pcv4DoseSeriesDoseRule4.setMinimumInterval(null);
		pcv4DoseSeriesDoseRule4.setEarliestRecommendedInterval(null);
		pcv4DoseSeriesDoseRule4.setPreferableVaccines(pcvPreferableVaccinesDoses);
		
		// Create encompassing Series Rule for "Hib 4-Dose Series" /////////////////////////////////////////////////////
		SeriesRules pcv4DoseSeriesRules = new SeriesRules("PCV 4-Dose Series", SupportedVaccineGroupConcept.PCV);
		tds = new ArrayList<DoseRule>();
		tds.add(pcv4doseSeriesDoseRule1);
		tds.add(pcv4DoseSeriesDoseRule2);
		tds.add(pcv4DoseSeriesDoseRule3);
		tds.add(pcv4DoseSeriesDoseRule4);
		pcv4DoseSeriesRules.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(pcv4DoseSeriesRules);

		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// PPSV Vaccine Group START
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		//
		// Create Series "PPSV 1-Dose Series" //////////////////////////////////////////////////////////////////////////////////
		// Allowable CVX codes: 
		//
		List<Vaccine> ppsvPreferableVaccinesDoses = new ArrayList<Vaccine>();
		ppsvPreferableVaccinesDoses.add(supportedVaccinesMap.get(SupportedVaccineConcept._PPSV_23VALENT));
		ppsvPreferableVaccinesDoses.add(supportedVaccinesMap.get(SupportedVaccineConcept._PCV_NOS109));
	
		DoseRule ppsv1doseSeriesDoseRule1 = new DoseRule();
		ppsv1doseSeriesDoseRule1.setDoseNumber(1);
		ppsv1doseSeriesDoseRule1.setAbsoluteMinimumAge(new TimePeriod("65y-4d"));
		ppsv1doseSeriesDoseRule1.setMinimumAge(new TimePeriod("65y"));
		ppsv1doseSeriesDoseRule1.setEarliestRecommendedAge(new TimePeriod("65y"));
		ppsv1doseSeriesDoseRule1.setAbsoluteMinimumInterval(new TimePeriod("5y-4d"));
		ppsv1doseSeriesDoseRule1.setMinimumInterval(new TimePeriod("5y"));
		ppsv1doseSeriesDoseRule1.setEarliestRecommendedInterval(new TimePeriod("5y"));
		ppsv1doseSeriesDoseRule1.setPreferableVaccines(ppsvPreferableVaccinesDoses);
		
		// Create encompassing Series Rule for "PPSV 1-Dose Series" /////////////////////////////////////////////////////
		SeriesRules ppsv1DoseSeriesRules = new SeriesRules("PPSV 1-Dose Series", SupportedVaccineGroupConcept.PPSV);
		tds = new ArrayList<DoseRule>();
		tds.add(ppsv1doseSeriesDoseRule1);
		ppsv1DoseSeriesRules.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(ppsv1DoseSeriesRules);

		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// PPSV Vaccine Group END
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Influenza Vaccine Group START
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		List<Season> influenzaSeasons = new ArrayList<Season>();
		influenzaSeasons.add(new Season("2014-2015 Influenza Season", SupportedVaccineGroupConcept.Influenza, true, 8, 1, 2014, 6, 30, 2015));
		influenzaSeasons.add(new Season("2013-2014 Influenza Season", SupportedVaccineGroupConcept.Influenza, true, 8, 1, 2013, 6, 30, 2014));
		influenzaSeasons.add(new Season("2012-2013 Influenza Season", SupportedVaccineGroupConcept.Influenza, true, 8, 1, 2012, 6, 30, 2013));

		//
		// Create Series "Influenza 2-dose Series" //////////////////////////////////////////////////////////////////////////////////
		// Allowable CVX codes: 15, 16, 111, 140, 141, 144, 135, 88, 149, 150, 151, 153, 155, 158
		//
		List<Vaccine> influenzaAllowableVaccines = new ArrayList<Vaccine>();
		influenzaAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._INFLUENZA_HIGHDOSE_SEASONAL));
		influenzaAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._INFLUENZA_INJECTABLE_MDCK_PRESERVATIVEFREE));
		influenzaAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._INFLUENZA_INJECTABLE_QUADRIVALENT_PRESERVATIVEFREE));
		influenzaAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._INFLUENZA_LIVE_INTRANASAL));
		influenzaAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._INFLUENZA_LIVE_INTRANASAL_QUADRIVALENT));
		influenzaAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._INFLUENZA_NASAL_UNSPECIFIED));
		influenzaAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._INFLUENZA_RECOMBINANT_INJECTABLE_PRESERVATIVEFREE));
		influenzaAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._INFLUENZA_SEASONAL_INJECTABLE));
		influenzaAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._INFLUENZA_SEASONAL_INJECTABLE_PRESERVATIVEFREE));
		influenzaAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._INFLUENZA_SEASONAL_INTRADERMAL_PRESERVATIVEFREE));
		influenzaAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._INFLUENZA_SPLIT));
		influenzaAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._INFLUENZA_WHOLE));
		influenzaAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._INFLUENZA_UNSPECIFIED));
		influenzaAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._INFLUENZA_INJECTABLE_QUADRIVALENT));
		influenzaAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._INFLUENZA_INJECTABLE_QUADRIVALENT_PRESERVATIVEFREE_PEDIATRIC));
		
		DoseRule influenza2DoseSeriesDoseRule1 = new DoseRule();
		influenza2DoseSeriesDoseRule1.setDoseNumber(1);
		influenza2DoseSeriesDoseRule1.setAbsoluteMinimumAge(new TimePeriod("6m-4d"));
		influenza2DoseSeriesDoseRule1.setMinimumAge(new TimePeriod("6m"));
		influenza2DoseSeriesDoseRule1.setEarliestRecommendedAge(new TimePeriod("6m"));
		influenza2DoseSeriesDoseRule1.setAbsoluteMinimumInterval(new TimePeriod("24d"));
		influenza2DoseSeriesDoseRule1.setMinimumInterval(new TimePeriod("28d"));
		influenza2DoseSeriesDoseRule1.setEarliestRecommendedInterval(new TimePeriod("28d"));
		influenza2DoseSeriesDoseRule1.setPreferableVaccines(influenzaAllowableVaccines);
		
		DoseRule influenza2DoseSeriesDoseRule2 = new DoseRule();
		influenza2DoseSeriesDoseRule2.setDoseNumber(2);
		influenza2DoseSeriesDoseRule2.setAbsoluteMinimumAge(null);
		influenza2DoseSeriesDoseRule2.setMinimumAge(null);
		influenza2DoseSeriesDoseRule2.setEarliestRecommendedAge(null);
		influenza2DoseSeriesDoseRule2.setAbsoluteMinimumInterval(null);
		influenza2DoseSeriesDoseRule2.setMinimumInterval(null);
		influenza2DoseSeriesDoseRule2.setEarliestRecommendedInterval(null);
		influenza2DoseSeriesDoseRule2.setPreferableVaccines(influenzaAllowableVaccines);
		
		// Create encompassing Series Rule for "Influena 2-Dose Series" /////////////////////////////////////////////////////
		SeriesRules influenza2DoseSeriesRules = new SeriesRules("Influenza 2-Dose Series", SupportedVaccineGroupConcept.Influenza, influenzaSeasons);
		tds = new ArrayList<DoseRule>();
		tds.add(influenza2DoseSeriesDoseRule1);
		tds.add(influenza2DoseSeriesDoseRule2);
		influenza2DoseSeriesRules.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(influenza2DoseSeriesRules);

		
		//
		// Create Series "Influenza 1-dose Series" //////////////////////////////////////////////////////////////////////////////////
		// Allowable CVX codes: 15, 16, 111, 140, 141, 144, 135, 88, 149, 150, 151, 153, 155
		//
		DoseRule influenza1DoseSeriesDoseRule1 = new DoseRule();
		influenza1DoseSeriesDoseRule1.setDoseNumber(1);
		influenza1DoseSeriesDoseRule1.setAbsoluteMinimumAge(new TimePeriod("6m-4d"));
		influenza1DoseSeriesDoseRule1.setMinimumAge(new TimePeriod("6m"));
		influenza1DoseSeriesDoseRule1.setEarliestRecommendedAge(new TimePeriod("6m"));
		influenza1DoseSeriesDoseRule1.setAbsoluteMinimumInterval(null);
		influenza1DoseSeriesDoseRule1.setMinimumInterval(null);
		influenza1DoseSeriesDoseRule1.setEarliestRecommendedInterval(new TimePeriod("28d"));
		influenza1DoseSeriesDoseRule1.setPreferableVaccines(influenzaAllowableVaccines);
		
		// Create encompassing Series Rule for "Influena 2-Dose Series" /////////////////////////////////////////////////////
		SeriesRules influenza1DoseSeriesRules = new SeriesRules("Influenza 1-Dose Series", SupportedVaccineGroupConcept.Influenza, influenzaSeasons);
		tds = new ArrayList<DoseRule>();
		tds.add(influenza1DoseSeriesDoseRule1);
		influenza1DoseSeriesRules.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(influenza1DoseSeriesRules);
		
		//
		// Create Series "Influenza 2-dose Default Series" //////////////////////////////////////////////////////////////////////////////////
		// Allowable CVX codes: 15, 16, 111, 140, 141, 144, 135, 88, 149, 150, 151, 153, 155
		//
		DoseRule influenza2DoseDefaultSeriesDoseRule1 = new DoseRule();
		influenza2DoseDefaultSeriesDoseRule1.setDoseNumber(1);
		influenza2DoseDefaultSeriesDoseRule1.setAbsoluteMinimumAge(new TimePeriod("6m-4d"));
		influenza2DoseDefaultSeriesDoseRule1.setMinimumAge(new TimePeriod("6m"));
		influenza2DoseDefaultSeriesDoseRule1.setEarliestRecommendedAge(null);
		influenza2DoseDefaultSeriesDoseRule1.setAbsoluteMinimumInterval(new TimePeriod("24d"));
		influenza2DoseDefaultSeriesDoseRule1.setMinimumInterval(new TimePeriod("28d"));
		influenza2DoseDefaultSeriesDoseRule1.setEarliestRecommendedInterval(null);
		influenza2DoseDefaultSeriesDoseRule1.setPreferableVaccines(influenzaAllowableVaccines);
		
		DoseRule influenza2DoseDefaultSeriesDoseRule2 = new DoseRule();
		influenza2DoseDefaultSeriesDoseRule2.setDoseNumber(2);
		influenza2DoseDefaultSeriesDoseRule2.setAbsoluteMinimumAge(null);
		influenza2DoseDefaultSeriesDoseRule2.setMinimumAge(null);
		influenza2DoseDefaultSeriesDoseRule2.setEarliestRecommendedAge(null);
		influenza2DoseDefaultSeriesDoseRule2.setAbsoluteMinimumInterval(null);
		influenza2DoseDefaultSeriesDoseRule2.setMinimumInterval(null);
		influenza2DoseDefaultSeriesDoseRule2.setEarliestRecommendedInterval(null);
		influenza2DoseDefaultSeriesDoseRule2.setPreferableVaccines(influenzaAllowableVaccines);

		// Default Season
		Season influenzaSeasonDefault = new Season("Default Influenza Season", SupportedVaccineGroupConcept.Influenza, true, 8, 1, 6, 30);
		List<Season> influenzaDefaultSeasons = new ArrayList<Season>();
		influenzaDefaultSeasons.add(influenzaSeasonDefault);		

		// Create encompassing Series Rule for "Influena 2-Dose Series" /////////////////////////////////////////////////////
		SeriesRules influenza2DoseDefaultSeriesRules = new SeriesRules("Influenza 2-Dose Default Series", SupportedVaccineGroupConcept.Influenza, influenzaDefaultSeasons);
		tds = new ArrayList<DoseRule>();
		tds.add(influenza2DoseDefaultSeriesDoseRule1);
		tds.add(influenza2DoseDefaultSeriesDoseRule2);
		influenza2DoseDefaultSeriesRules.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(influenza2DoseDefaultSeriesRules);

		
		// Now check for consistency across the series to make sure there aren't any overlapping seasons; can't have more than one default series
		boolean consistencyCheck = checkConsistencyOfSeasonsSupportingDataAcrossSeriesInVaccineGroup(SupportedVaccineGroupConcept.Influenza);
		if (consistencyCheck == false) {
			String errStr = _METHODNAME + "Inconsistency of season configurations in series for vaccine group " + SupportedVaccineGroupConcept.Influenza.getConceptDisplayNameValue();
			throw new ImproperUsageException(errStr);
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Influenza Vaccine Group END
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// H1N1 Vaccine Group START
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		List<Season> h1n1Seasons = new ArrayList<Season>();
		Season h1n12009Season = new Season("2009 H1N1 Season", SupportedVaccineGroupConcept.H1N1, true, 10, 1, 2009, 6, 30, 2010);
		h1n1Seasons.add(h1n12009Season);

		//
		// Create Series "H1N1 2-dose Series" //////////////////////////////////////////////////////////////////////////////////
		// Allowable CVX codes: 125, 126, 127, 128
		//
		List<Vaccine> h1n1AllowableVaccines = new ArrayList<Vaccine>();
		h1n1AllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._H1N1_09_NASAL));
		h1n1AllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._H1N1_09_PRESERVATIVEFREE));
		h1n1AllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._H1N1_09));
		h1n1AllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._H1N1_09_ALLFORMULATIONS));
		
		DoseRule h1n12DoseSeriesRule1 = new DoseRule();
		h1n12DoseSeriesRule1.setDoseNumber(1);
		h1n12DoseSeriesRule1.setAbsoluteMinimumAge(new TimePeriod("6m-4d"));
		h1n12DoseSeriesRule1.setMinimumAge(new TimePeriod("6m"));
		h1n12DoseSeriesRule1.setEarliestRecommendedAge(new TimePeriod("6m"));
		h1n12DoseSeriesRule1.setAbsoluteMinimumInterval(new TimePeriod("21d"));
		h1n12DoseSeriesRule1.setMinimumInterval(new TimePeriod("28d"));
		h1n12DoseSeriesRule1.setEarliestRecommendedInterval(new TimePeriod("28d"));
		h1n12DoseSeriesRule1.setPreferableVaccines(h1n1AllowableVaccines);
		
		DoseRule h1n12DoseSeriesRule2 = new DoseRule();
		h1n12DoseSeriesRule2.setDoseNumber(2);
		h1n12DoseSeriesRule2.setAbsoluteMinimumAge(null);
		h1n12DoseSeriesRule2.setMinimumAge(null);
		h1n12DoseSeriesRule2.setEarliestRecommendedAge(null);
		h1n12DoseSeriesRule2.setAbsoluteMinimumInterval(null);
		h1n12DoseSeriesRule2.setMinimumInterval(null);
		h1n12DoseSeriesRule2.setEarliestRecommendedInterval(null);
		h1n12DoseSeriesRule2.setPreferableVaccines(h1n1AllowableVaccines);
		
		// Create encompassing Series Rule for "H1N1 2-Dose Series" /////////////////////////////////////////////////////
		SeriesRules h1n12DoseSeriesRules = new SeriesRules("H1N1 2-Dose Series", SupportedVaccineGroupConcept.H1N1, h1n1Seasons);
		tds = new ArrayList<DoseRule>();
		tds.add(h1n12DoseSeriesRule1);
		tds.add(h1n12DoseSeriesRule2);
		h1n12DoseSeriesRules.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(h1n12DoseSeriesRules);
		
		//
		// Create Series "H1N1 1-dose Series" //////////////////////////////////////////////////////////////////////////////////
		// Allowable CVX codes: 125, 126, 127, 128
		//
		DoseRule h1n11DoseSeriesDoseRule1 = new DoseRule();
		h1n11DoseSeriesDoseRule1.setDoseNumber(1);
		h1n11DoseSeriesDoseRule1.setAbsoluteMinimumAge(new TimePeriod("6m-4d"));
		h1n11DoseSeriesDoseRule1.setMinimumAge(new TimePeriod("6m"));
		h1n11DoseSeriesDoseRule1.setEarliestRecommendedAge(new TimePeriod("6m"));
		h1n11DoseSeriesDoseRule1.setAbsoluteMinimumInterval(null);
		h1n11DoseSeriesDoseRule1.setMinimumInterval(null);
		h1n11DoseSeriesDoseRule1.setEarliestRecommendedInterval(null);
		h1n11DoseSeriesDoseRule1.setPreferableVaccines(h1n1AllowableVaccines);
		
		// Create encompassing Series Rule for "H1N1 1-Dose Series" /////////////////////////////////////////////////////
		SeriesRules h1n11DoseSeriesRules = new SeriesRules("H1N1 1-Dose Series", SupportedVaccineGroupConcept.H1N1, h1n1Seasons);
		tds = new ArrayList<DoseRule>();
		tds.add(h1n11DoseSeriesDoseRule1);
		h1n11DoseSeriesRules.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(h1n11DoseSeriesRules);

		// Now check for consistency across the series to make sure there aren't any overlapping seasons; can't have more than one default series
		//boolean h1n1ConsistencyCheck = checkConsistencyOfSeasonsSupportingDataAcrossSeriesInVaccineGroup(SupportedVaccineGroupConcept.H1N1);
		//if (h1n1ConsistencyCheck == false) {
		//	String errStr = _METHODNAME + "Inconsistency of season configurations in series for vaccine group " + SupportedVaccineGroupConcept.H1N1.getConceptDisplayNameValue();
		//	throw new ImproperUsageException(errStr);
		//}
	
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// H1N1 Vaccine Group end
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Meningococcal Vaccine Group START
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		//
		// Create Series "MCV4 2-Dose Series" //////////////////////////////////////////////////////////////////////////////////
		// Allowable CVX codes: 114, 136, 32, 147, 108
		//
		List<Vaccine> meningAllowableVaccines = new ArrayList<Vaccine>();
		meningAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._MENING_MCV4P));
		meningAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._MENING_MCV4O));
		meningAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._MENING_MPSV4));
		meningAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._MENING_UNSPECIFIED));
		meningAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._MENING_MCV4_UNSPECIFIED));
		
		DoseRule mening2DoseSeriesRule1 = new DoseRule();
		mening2DoseSeriesRule1.setDoseNumber(1);
		mening2DoseSeriesRule1.setAbsoluteMinimumAge(new TimePeriod("10y-4d"));
		mening2DoseSeriesRule1.setMinimumAge(new TimePeriod("10y"));
		mening2DoseSeriesRule1.setEarliestRecommendedAge(new TimePeriod("11y"));
		mening2DoseSeriesRule1.setAbsoluteMinimumInterval(new TimePeriod("52d"));
		mening2DoseSeriesRule1.setMinimumInterval(new TimePeriod("56d"));
		mening2DoseSeriesRule1.setEarliestRecommendedInterval(new TimePeriod("3y"));
		mening2DoseSeriesRule1.setPreferableVaccines(meningAllowableVaccines);
		
		DoseRule mening2DoseSeriesRule2 = new DoseRule();
		mening2DoseSeriesRule2.setDoseNumber(2);
		mening2DoseSeriesRule2.setAbsoluteMinimumAge(null);
		mening2DoseSeriesRule2.setMinimumAge(null);
		mening2DoseSeriesRule2.setEarliestRecommendedAge(new TimePeriod("16y"));
		mening2DoseSeriesRule2.setAbsoluteMinimumInterval(null);
		mening2DoseSeriesRule2.setMinimumInterval(null);
		mening2DoseSeriesRule2.setEarliestRecommendedInterval(null);
		mening2DoseSeriesRule2.setPreferableVaccines(meningAllowableVaccines);
		
		// Create encompassing Series Rule for "MCV4 2-Dose Series" /////////////////////////////////////////////////////
		SeriesRules mening2DoseSeriesRules = new SeriesRules("MCV4 2-Dose Series", SupportedVaccineGroupConcept.Meningococcal);
		tds = new ArrayList<DoseRule>();
		tds.add(mening2DoseSeriesRule1);
		tds.add(mening2DoseSeriesRule2);
		mening2DoseSeriesRules.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(mening2DoseSeriesRules);
		

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Meningococcal Vaccine Group end
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Polio Vaccine Group START
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		//
		// Create Series "Polio 4-Dose Series" //////////////////////////////////////////////////////////////////////////////////
		// Allowable CVX codes: 02, 10, 89, 110, 120, 130
		//
		List<Vaccine> polioAllowableVaccines = new ArrayList<Vaccine>();
		polioAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._IPV));
		polioAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._OPV));
		polioAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._POLIO_UNSPECIFIED));
		polioAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP_HEPB_IPV));
		polioAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP_HIB_IPV));
		polioAllowableVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP_IPV));
		
		DoseRule polioDoseSeriesRule1 = new DoseRule();
		polioDoseSeriesRule1.setDoseNumber(1);
		polioDoseSeriesRule1.setAbsoluteMinimumAge(new TimePeriod("38d"));
		polioDoseSeriesRule1.setMinimumAge(new TimePeriod("42d"));
		polioDoseSeriesRule1.setEarliestRecommendedAge(new TimePeriod("2m"));
		polioDoseSeriesRule1.setAbsoluteMinimumInterval(new TimePeriod("24d"));
		polioDoseSeriesRule1.setMinimumInterval(new TimePeriod("28d"));
		polioDoseSeriesRule1.setEarliestRecommendedInterval(new TimePeriod("28d"));
		polioDoseSeriesRule1.setPreferableVaccines(polioAllowableVaccines);
		
		DoseRule polioDoseSeriesRule2 = new DoseRule();
		polioDoseSeriesRule2.setDoseNumber(2);
		polioDoseSeriesRule2.setAbsoluteMinimumAge(new TimePeriod("66d"));
		polioDoseSeriesRule2.setMinimumAge(new TimePeriod("70d"));
		polioDoseSeriesRule2.setEarliestRecommendedAge(new TimePeriod("4m"));
		polioDoseSeriesRule2.setAbsoluteMinimumInterval(new TimePeriod("24d"));
		polioDoseSeriesRule2.setMinimumInterval(new TimePeriod("28d"));
		polioDoseSeriesRule2.setEarliestRecommendedInterval(new TimePeriod("28d"));
		polioDoseSeriesRule2.setPreferableVaccines(polioAllowableVaccines);
		
		DoseRule polioDoseSeriesRule3 = new DoseRule();
		polioDoseSeriesRule3.setDoseNumber(3);
		polioDoseSeriesRule3.setAbsoluteMinimumAge(new TimePeriod("94d"));
		polioDoseSeriesRule3.setMinimumAge(new TimePeriod("98d"));
		polioDoseSeriesRule3.setEarliestRecommendedAge(new TimePeriod("6m"));
		polioDoseSeriesRule3.setAbsoluteMinimumInterval(new TimePeriod("6m-4d"));
		polioDoseSeriesRule3.setMinimumInterval(new TimePeriod("6m"));
		polioDoseSeriesRule3.setEarliestRecommendedInterval(new TimePeriod("6m"));
		polioDoseSeriesRule3.setPreferableVaccines(polioAllowableVaccines);
		
		DoseRule polioDoseSeriesRule4 = new DoseRule();
		polioDoseSeriesRule4.setDoseNumber(4);
		polioDoseSeriesRule4.setAbsoluteMinimumAge(new TimePeriod("4y-4d"));
		polioDoseSeriesRule4.setMinimumAge(new TimePeriod("4y"));
		polioDoseSeriesRule4.setEarliestRecommendedAge(new TimePeriod("4y"));
		polioDoseSeriesRule4.setAbsoluteMinimumInterval(null);
		polioDoseSeriesRule4.setMinimumInterval(null);
		polioDoseSeriesRule4.setEarliestRecommendedInterval(null);
		polioDoseSeriesRule4.setPreferableVaccines(polioAllowableVaccines);
		
		// Create encompassing Series Rule for "H1N1 2-Dose Series" /////////////////////////////////////////////////////
		SeriesRules polioDoseSeriesRules = new SeriesRules("Polio 4-Dose Series", SupportedVaccineGroupConcept.Polio);
		tds = new ArrayList<DoseRule>();
		tds.add(polioDoseSeriesRule1);
		tds.add(polioDoseSeriesRule2);
		tds.add(polioDoseSeriesRule3);
		tds.add(polioDoseSeriesRule4);
		polioDoseSeriesRules.setSeriesDoseRules(tds);

		// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
		addSeriesToSchedule(polioDoseSeriesRules);
		

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Polio Vaccine Group end
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		
	}

	
	/** 
	 * Check to make sure that all seasons do not overlap with each other, or if they do, they have the exact same season start and end dates. 
	 * All series in the vaccine group must be seasonal series, or none of them. If some are or others aren't, this method logs a warning and returns false.
	 * In addition, there cannot be more than one default series in a vaccine group.
	 * @param svgc vaccine group in which to check series consistency.
	 * @return true of these conditions are met, false if not.
	 */
	private boolean checkConsistencyOfSeasonsSupportingDataAcrossSeriesInVaccineGroup(SupportedVaccineGroupConcept svgc) {
		
		String _METHODNAME = "checkConsistencyOfSeasonsSupportingDataAcrossSeriesInVaccineGroup(): ";
		if (svgc == null) {
			return false;
		}
		
		List<SeriesRules> srs = vaccineGroupSeries.get(svgc);
		int countOfDefaultSeasonsAcrossSeries = 0;
		int countOfSeasons = 0;
		boolean aNonSeasonalSeriesExists = false;
		List<Season> seasonsTracker = new ArrayList<Season>();
		for (SeriesRules sr : srs) {
			if (countOfDefaultSeasonsAcrossSeries > 1) {
				logger.warn(_METHODNAME + "more than one default season across in vaccine group " + svgc.getConceptDisplayNameValue());
				return false;
			}
			List<Season> seriesSeasons = sr.getSeasons();
			if (seriesSeasons == null || seriesSeasons.isEmpty()) {
				aNonSeasonalSeriesExists = true;
				if (countOfSeasons > 0) {
					logger.warn(_METHODNAME + "a non-seasonal series was found in a vaccine group with seasons " + svgc.getConceptDisplayNameValue());
					return false;
				}
			}
			for (Season s : seriesSeasons) {
				if (aNonSeasonalSeriesExists) {
					logger.warn(_METHODNAME + "a non-seasonal series was found in a vaccine group with seasons " + svgc.getConceptDisplayNameValue());
					return false;
				}
				boolean lSeasonAlreadyEncountered = false;
				if (seasonsTracker.contains(s)) {
					lSeasonAlreadyEncountered = true;
				}
				else {
					countOfSeasons++;
				}
				if (s.isDefaultSeason() == true) {
					countOfDefaultSeasonsAcrossSeries++;
					if (countOfDefaultSeasonsAcrossSeries >= 2) {
						logger.warn(_METHODNAME + "more than one default season in Series in vaccine group " + svgc.getConceptDisplayNameValue());
						return false;
					}
				}
				else if (lSeasonAlreadyEncountered == false) {
					for (Season seasonIter : seasonsTracker) {
						// Check to see if the season start or end dates overlaps with another season. Overlaps are only allowed if the start and end dates 
						// of the season for the different series are exactly the same. Default seasons do not have a specified start or end date, so they are 
						// not checked here. (This is because if a fully-specified season can take place at a time when a default season is specified; it  
						// overrides the default season which will then not be used.)
						if (! s.seasonsHaveEquivalentStartAndEndDates(seasonIter) && s.seasonOverlapsWith(seasonIter)) {
							logger.warn(_METHODNAME + "overlapping seasons exist in vaccine group " + svgc.getConceptDisplayNameValue());
							return false;
						}
					}
					seasonsTracker.add(s);
				}
			}
		}
		
		int lNumberOfDistinctSeasons = seasonsTracker.size();
		// if (seasonsTracker.size() > 0) {
		if (lNumberOfDistinctSeasons > 0) {
			if (countOfDefaultSeasonsAcrossSeries != 1 && countOfDefaultSeasonsAcrossSeries != 0 ) {
				logger.warn(_METHODNAME + "a seasonal vaccine group must have exactly either 0 or 1 default seasons defined. The # of seasonal series " + 
					"found for " + "vaccine group " + svgc.getConceptDisplayNameValue() + ": " + countOfDefaultSeasonsAcrossSeries);
				return false;
			}
			else if (lNumberOfDistinctSeasons > 1 && countOfDefaultSeasonsAcrossSeries == 0) {
				logger.warn(_METHODNAME + "a seasonal vaccine group wiht more than one season defined must also have a default season defined. No default season has been defined");
				return false;
			}
			else {
				// This is a properly configured seasonal vaccine group with a default season for evaluation
				return true;
			}
		}
		else if (countOfDefaultSeasonsAcrossSeries == 0 && seasonsTracker.size() == 0) {
			// This is not a seasonal vaccine group
			return true;
		}
		else {
			return false;
		}
	}

	
	private void addSeriesToSchedule(SeriesRules pTS) 
		throws ImproperUsageException { 

		String _METHODNAME = "addVaccineGroupSeriesToSchedule(): ";
		if (pTS == null) {
			throw new ImproperUsageException(_METHODNAME + "null VaccineGroup or SeriesRules parameter supplied"); 
		}

		SupportedVaccineGroupConcept vg = pTS.getVaccineGroup();
		String requestedSeriesName = pTS.getSeriesName();
		if (requestedSeriesName == null || vg == null) {
			throw new ImproperUsageException(_METHODNAME + "supplied SeriesRules name or vaccine group is not populated"); 
		}
		List<SeriesRules> ts = vaccineGroupSeries.get(vg);
		if (ts != null) {
			// Check to make sure the SeriesRules name is unique
			for (SeriesRules sr : ts) {
				if (sr.getSeriesName().equalsIgnoreCase(requestedSeriesName)) {
					throw new ImproperUsageException(_METHODNAME + "SeriesRules with name " + requestedSeriesName + " already in schedule");
				}
			}
			ts.add(pTS);
			vaccineGroupSeries.put(vg, ts);
		}
		else {
			ts = new ArrayList<SeriesRules>();
			ts.add(pTS);
			vaccineGroupSeries.put(vg, ts);
		}
	}

	
	public List<SeriesRules> getAllSeries() {

		// String _METHODNAME = "getCandidateSeries(): ";

		List<SeriesRules> allSeries = new ArrayList<SeriesRules>();
		if (vaccineGroupSeries == null) {
			return allSeries;
		}
		Collection<List<SeriesRules>> clts = vaccineGroupSeries.values();
		Iterator<List<SeriesRules>> clIter = clts.iterator();
		while (clIter.hasNext()) {
			List<SeriesRules> lts = clIter.next();
			if (lts != null) {
				allSeries.addAll(lts);
			}
		}

		return allSeries;
	}


	/**
	 * Return supported vaccine group concepts
	 * @return List of SupportedVaccineGroupConcepts; empty set if no supported vaccine groups
	 */
	public Set<SupportedVaccineGroupConcept> getSupportedVaccineGroups() {
		
		Set<SupportedVaccineGroupConcept> supportedVGs = new HashSet<SupportedVaccineGroupConcept>();
		if (vaccineGroupSeries == null) {
			return supportedVGs;
		}
		
		return vaccineGroupSeries.keySet();
	}

}
