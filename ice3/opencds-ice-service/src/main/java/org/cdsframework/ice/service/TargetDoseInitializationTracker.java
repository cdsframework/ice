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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.definition.type.ClassReactive;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.internal.concepts.ImmunizationConcept;


@ClassReactive
public class TargetDoseInitializationTracker {

	private Set<String> initializedTargetDoseList;
	private Map<String, String> initializedTargetDoseByVgMap;
	
	private static final Logger logger = LogManager.getLogger();
	
	
	public TargetDoseInitializationTracker() {
		
		//String _METHODNAME = "TargetDoseInitializationTracker(): ";
		initializedTargetDoseList = new HashSet<String>();
		initializedTargetDoseByVgMap = new HashMap<String, String>();
	}

	public List<TargetDose> addTargetDoseInitialization(Vaccine vaccineAdministered, SubstanceAdministrationEvent sae, TargetSeries ts, Schedule scheduleBackingSeries) {
		
		return addTargetDoseInitialization(vaccineAdministered, sae, ts, scheduleBackingSeries, false);
	}
	
	/**
	 * 
	 * @param ic
	 * @param sae
	 * @param ts
	 * @param scheduleBackingSeries
	 * @return List of TargetDoses that were added to the initialization tracker; empty if none are added
	 * @throws IllegalArgumentException If any of the parameters are null or SubstanceAdministrationDates are inconsistent
	 */
	public List<TargetDose> addTargetDoseInitialization(Vaccine vaccineAdministered, SubstanceAdministrationEvent sae, TargetSeries ts,	Schedule scheduleBackingSeries, 
		boolean overrideSeasonalDateRestriction) {
		
		String _METHODNAME = "addTargetDoseInitialization(): ";
		if (vaccineAdministered == null || sae == null || ts == null || scheduleBackingSeries == null) {
			String errStr = "Vaccine, SubstanceAdministrationEvent, TargetSeries or Schedule parameters not populated";
			logger.warn(errStr);
			throw new IllegalArgumentException(errStr);
		}
		
		Date adminDate = null;
		try {
			adminDate = ICELogicHelper.extractSingularDateValueFromIVLDate(sae.getAdministrationTimeInterval());
		}
		catch (InvalidDataException ide) {
			String errStr = "Caught an InvalidDataException attempting to extract a singular date from the SubstanceAdministrationEvent IVLDate";
			logger.warn(errStr);
			throw new IllegalArgumentException(errStr);
		}
		if (adminDate == null) {
			String errStr = "substance administration date is not populated";
			logger.warn(errStr);
			throw new IllegalArgumentException(errStr);
		}
		
		// Get the vaccine component(s) associated with the vaccine group in focus and create a TargetDose for each (should only be 1 or something weird is going on)
		String vaccineGroupStr = ts.getSeriesRules().getVaccineGroup();
		Collection<String> targetedDiseases = scheduleBackingSeries.getDiseasesTargetedByVaccineGroup(vaccineGroupStr);
		Collection<VaccineComponent> vcsContainingTargetedDiseases = vaccineAdministered.getVaccineComponentsTargetingSpecifiedDiseases(targetedDiseases);
		List<TargetDose> initializedTargetDoses = new ArrayList<TargetDose>();
		for (VaccineComponent vc : vcsContainingTargetedDiseases) {
			String ctid = sae.getId();
			/////// String ctidvcc = ctid + vc.getVaccineConcept().getOpenCdsConceptCode();
			String ctidvcc = ctid + vc.getCdsConceptName();
			if (! initializedTargetDoseByVgMap.containsKey(ctidvcc) || initializedTargetDoseByVgMap.get(ctidvcc).equals(vaccineGroupStr)) {
				/////// TODO: For seasons, only add the TargetDose to the TargetSeries if the dose was administered during timeframe permitted by the Series 
				/////// TargetDose td = new TargetDose(ctid, vaccineAdministered, vc, adminDate);
				TargetDose td = new TargetDose(vaccineAdministered, vc, adminDate, ts, sae);
				boolean lTargetDoseAdded = false;
				if (overrideSeasonalDateRestriction) {
					lTargetDoseAdded = ts.addTargetDoseToSeries(td, overrideSeasonalDateRestriction);
				}
				else {
					lTargetDoseAdded = ts.addTargetDoseToSeries(td);
				}
				if (! lTargetDoseAdded) {
					continue;
				}
				String setAddition = ctidvcc + ts.getTargetSeriesIdentifier();	// Set tracker - SE+vaccinecomponent+targetSeriesIdentifier must be unique
				initializedTargetDoseList.add(setAddition);
				// ICELogicHelper.logDRLDebugMessage(_METHODNAME, "Set key: " + setAddition);
				initializedTargetDoseByVgMap.put(ctidvcc, vaccineGroupStr);	// Map tracker - SE+vaccineComponentCC must be in the same vaccine group
				// ICELogicHelper.logDRLDebugMessage(_METHODNAME, "Map key: " + ctidvcc + "; value " + vaccineGroupStr);
				initializedTargetDoses.add(td);
				if (logger.isDebugEnabled()) {
					ICELogicHelper.logDRLDebugMessage(_METHODNAME, "Added " + td.toString() + " to TargetSeries " + ts.getSeriesName());
				}
			}
		}
		
		return initializedTargetDoses;
	}
	
	
	/**
	 * If the specified substance administration event and associated ImmunizationConcept has not previously been initialized for a DIFFERENT vaccine group, 
	 * and at least one vaccine component of the shot has not been initialized for the specified substance administration event and TargetSeries, and the
	 * (REMOVING SEASON DATE CHECK) shot falls within any specified season dates, then the shot administered can be loaded into the TargetSeries (returns true). Otherwise, it cannot 
	 * (returns false).
	 * 
	 * This method also updates the list of applicable seasons to the TargetSeries object (attribute applicableSeasons)
	 * 
	 * @param ic ImmunizationConcept
	 * @param svgc Vaccine Group
	 * @param sae SubstanceAdministrationEvent
	 * @param targetSeries TargetSeries to which the shot will be added
	 * @param vaccineAdministered Vaccine Administered
	 * @return true or false according to criteria above
	 */
	public boolean shotAdministeredIsEligibleForInclusionInTargetSeries(ImmunizationConcept ic, String svgc, SubstanceAdministrationEvent sae, 
		TargetSeries targetSeries, Vaccine vaccineAdministered) {
	
		if (specifiedSubstanceAdministrationEventAndAssociatedConceptHasNotPreviouslyBeenInitializedForAnotherVaccineGroup(ic, svgc) == true &&
			atLeastOneVaccineComponentHasNotBeenNotInitializedForSpecifiedSubstanceAdministrationEventAndSeries(sae, targetSeries, vaccineAdministered) == true) {
			
			// If there is a season associated with the SeriesRules, ensure that the shot administration date falls within the set of dates
			/*
			 * * * * * * * * 
			 * Season targetSeason = targetSeries.getTargetSeason();
			 *if (targetSeason == null) {
			 *	return true;
			 *}
			 *else {
			 *	Date dateOfShot = null;
			 *	try {
			 *		dateOfShot = ICELogicHelper.extractSingularDateValueFromIVLDate(sae.getAdministrationTimeInterval());
			 *	}
			 *	catch (InvalidDataException ide) {
			 *		return false;
			 *	}
			 *	if (targetSeason.dateIsApplicableToSeason(dateOfShot, true)) {
			 *		return true;
			 *	}
			 *	else {
			 *		return false;
			 *	}
			 * }
			 * * * * * * * * 
			 */
			return true;
		}
		else {
			return false;
		}
	}
	
	
	private boolean atLeastOneVaccineComponentHasNotBeenNotInitializedForSpecifiedSubstanceAdministrationEventAndSeries(SubstanceAdministrationEvent sae, TargetSeries targetSeries, 
		Vaccine vaccine) {
		
		if (vaccine == null || sae == null || targetSeries == null) {
			return false;
		}	
		
		String conceptTargetId = sae.getId();
		String targetSeriesIdentifier = targetSeries.getTargetSeriesIdentifier();
		if (conceptTargetId == null || targetSeriesIdentifier == null) {
			return false;
		}
		
		List<VaccineComponent> vaccineComponents = vaccine.getVaccineComponents();
		for (VaccineComponent vc : vaccineComponents) {
			/////// String vcConceptCode = vc.getVaccineConcept().getOpenCdsConceptCode();
			String vcConceptCode = vc.getCdsConceptName();
			if (! initializedTargetDoseList.contains(conceptTargetId + vcConceptCode + targetSeriesIdentifier)) {
				return true;
			}
		}
		
		return false; 
	}
	
	
	private boolean specifiedSubstanceAdministrationEventAndAssociatedConceptHasNotPreviouslyBeenInitializedForAnotherVaccineGroup(ImmunizationConcept ic, 
		String vaccineGroup) {
		
		if (ic != null && vaccineGroup != null) {
			
			String conceptTargetId = ic.getConceptTargetId();
			String immunizationConcept = ic.getOpenCdsConceptCode();
			if (conceptTargetId == null || immunizationConcept == null) {
				return false;
			}
			
			String mapKey = conceptTargetId + immunizationConcept;
			if (! initializedTargetDoseByVgMap.containsKey(mapKey) || initializedTargetDoseByVgMap.get(mapKey).equals(vaccineGroup)) {
				return true;
			}
			else {
				return false;
			}
		}

		 return false;
	}
}
