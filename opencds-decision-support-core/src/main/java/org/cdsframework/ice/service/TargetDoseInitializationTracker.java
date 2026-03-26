/**
 * Copyright (C) 2025 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 * <p>
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 * <p>
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * <p>
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
import java.util.function.Predicate;

import org.kie.api.definition.type.ClassReactive;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.internal.concepts.ImmunizationConcept;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ClassReactive
public class TargetDoseInitializationTracker
{
    private final Set<String> initializedTargetDoseList = new HashSet<>();
    private final Map<String, String> initializedTargetDoseByVgMap = new HashMap<>();

    public List<TargetDose> addTargetDoseInitialization(final Vaccine vaccineAdministered, final SubstanceAdministrationEvent sae,
            final TargetSeries ts, final Schedule scheduleBackingSeries)
    {
        return addTargetDoseInitialization(vaccineAdministered, sae, ts, scheduleBackingSeries, false);
    }

    public List<TargetDose> addTargetDoseInitialization(final Vaccine vaccineAdministered, final SubstanceAdministrationEvent sae,
            final TargetSeries ts, final Schedule scheduleBackingSeries, final VaccineComponent addThisVaccineComponentOnly)
    {
        return addTargetDoseInitialization(vaccineAdministered, sae, ts, scheduleBackingSeries, addThisVaccineComponentOnly, false);
    }

    public List<TargetDose> addTargetDoseInitialization(final Vaccine vaccineAdministered, final SubstanceAdministrationEvent sae,
            final TargetSeries ts, final Schedule scheduleBackingSeries, final boolean overrideSeasonalDateRestriction)
    {
        return addTargetDoseInitialization(vaccineAdministered, sae, ts, scheduleBackingSeries, null,
                overrideSeasonalDateRestriction);
    }

    /**
     * @return List of TargetDoses that were added to the initialization tracker; empty if none are added
     * @throws IllegalArgumentException If any of the parameters are null or SubstanceAdministrationDates are inconsistent
     */
    public List<TargetDose> addTargetDoseInitialization(final Vaccine vaccineAdministered, final SubstanceAdministrationEvent sae,
            final TargetSeries ts, final Schedule scheduleBackingSeries, final VaccineComponent addThisVaccineComponentOnly,
            final boolean overrideSeasonalDateRestriction)
    {
        final String _METHODNAME = "addTargetDoseInitialization(): ";
        if (vaccineAdministered == null || sae == null || ts == null || scheduleBackingSeries == null)
        {
            final String errStr = "Vaccine, SubstanceAdministrationEvent, TargetSeries or Schedule parameters not populated";
            log.warn(errStr);
            throw new IllegalArgumentException(errStr);
        }

        final Date adminDate;
        try
        {
            adminDate = ICELogicHelper.extractSingularDateValueFromIVLDate(sae.getAdministrationTimeInterval());
        }
        catch (final IllegalArgumentException ide)
        {
            final String errStr =
                    "Caught an IllegalArgumentException attempting to extract a singular date from the SubstanceAdministrationEvent IVLDate";
            log.warn(errStr);
            throw new IllegalArgumentException(errStr);
        }

        if (adminDate == null)
        {
            final String errStr = "substance administration date is not populated";
            log.warn(errStr);
            throw new IllegalArgumentException(errStr);
        }

        // Get the vaccine component(s) associated with the vaccine group in focus and create a TargetDose for each (should only be 1 or something weird is going on)
        final String vaccineGroupStr = ts.getSeriesRules().getVaccineGroup();
        Collection<VaccineComponent> vcsContainingTargetedDiseases =
                vaccineAdministered.getVaccineComponentsTargetingSpecifiedDiseases(
                        scheduleBackingSeries.getDiseasesTargetedByVaccineGroup(vaccineGroupStr));

        // If a specific vaccine component was specified but that vaccine component is not a disease supported by the series, exit out
        if (addThisVaccineComponentOnly != null)
        {
            final Collection<VaccineComponent> vcSelected = new ArrayList<>();
            boolean vaccineComponentMatched = false;
            for (final VaccineComponent vc : vcsContainingTargetedDiseases)
            {
                if (vc.equals(addThisVaccineComponentOnly))
                {
                    vcSelected.add(vc);
                    vaccineComponentMatched = true;
                    break;
                }
            }

            if (!vaccineComponentMatched)
            {
                final String errStr = "Specified a VaccineComponent not supported by this series; cannot continue";
                log.warn(errStr);
                throw new IllegalArgumentException(errStr);
            }

            vcsContainingTargetedDiseases = vcSelected;
        }

        // Initialize the target dose(s).
        final List<TargetDose> initializedTargetDoses = new ArrayList<>();
        for (final VaccineComponent vc : vcsContainingTargetedDiseases)
        {
            final String ctid = sae.getId();
            final String ctidvcc = ctid + vc.getCdsConceptName();
            if (!initializedTargetDoseByVgMap.containsKey(ctidvcc) || initializedTargetDoseByVgMap.get(ctidvcc)
                    .equals(vaccineGroupStr))
            {
                final TargetDose td = new TargetDose(vaccineAdministered, vc, adminDate, ts, sae);
                final boolean lTargetDoseAdded;
                if (overrideSeasonalDateRestriction)
                    lTargetDoseAdded = ts.addTargetDoseToSeries(td, true);
                else
                    lTargetDoseAdded = ts.addTargetDoseToSeries(td);
                if (!lTargetDoseAdded)
                    continue;

                final String setAddition = ctidvcc
                        + ts.getTargetSeriesIdentifier();    // Set tracker - SE+vaccinecomponent+targetSeriesIdentifier must be unique
                initializedTargetDoseList.add(setAddition);
                // ICELogicHelper.logDRLDebugMessage(_METHODNAME, "Set key: " + setAddition);
                initializedTargetDoseByVgMap.put(ctidvcc,
                        vaccineGroupStr);    // Map tracker - SE+vaccineComponentCC must be in the same vaccine group
                // ICELogicHelper.logDRLDebugMessage(_METHODNAME, "Map key: " + ctidvcc + "; value " + vaccineGroupStr);
                initializedTargetDoses.add(td);
                if (log.isDebugEnabled())
                    ICELogicHelper.logDRLDebugMessage(_METHODNAME, "Added " + td + " to TargetSeries " + ts.getSeriesName());
            }
        }

        return initializedTargetDoses;
    }

    public boolean isTargetDoseInitializedInSeries(final SubstanceAdministrationEvent sae, final VaccineComponent vc,
            final TargetSeries targetSeries)
    {
        if (vc == null || sae == null || targetSeries == null)
            return false;

        final String conceptTargetId = sae.getId();
        final String targetSeriesIdentifier = targetSeries.getTargetSeriesIdentifier();
        final String vcConceptCode = vc.getCdsConceptName();
        if (conceptTargetId == null || targetSeriesIdentifier == null || vcConceptCode == null)
            return false;

        return initializedTargetDoseList.contains(conceptTargetId + vcConceptCode + targetSeriesIdentifier);
    }

    public boolean allTargetDosesHaveBeenInitializedInSeries(final SubstanceAdministrationEvent sae, final Vaccine vaccine,
            final TargetSeries targetSeries)
    {
        if (vaccine == null || sae == null || targetSeries == null)
            return false;

        return vaccine.getVaccineComponents().stream().noneMatch(vc -> isTargetDoseInitializedInSeries(sae, vc, targetSeries));
    }

    /**
     * If the specified substance administration event and associated ImmunizationConcept has not previously been initialized for a DIFFERENT vaccine group,
     * and at least one vaccine component of the shot has not been initialized for the specified substance administration event and TargetSeries, and the
     * (REMOVING SEASON DATE CHECK) shot falls within any specified season dates, then the shot administered can be loaded into the TargetSeries (returns true). Otherwise, it cannot
     * (returns false).
     * <p>
     * This method also updates the list of applicable seasons to the TargetSeries object (attribute applicableSeasons)
     *
     * @param ic                  ImmunizationConcept
     * @param svgc                Vaccine Group
     * @param sae                 SubstanceAdministrationEvent
     * @param targetSeries        TargetSeries to which the shot will be added
     * @param vaccineAdministered Vaccine Administered
     * @return true or false according to criteria above
     */
    public boolean shotAdministeredIsEligibleForInclusionInTargetSeries(final ImmunizationConcept ic, final String svgc,
            final SubstanceAdministrationEvent sae, final TargetSeries targetSeries, final Vaccine vaccineAdministered)
    {
        // If not all vaccine components of the Vaccine have been initialized for the ....
        // AI:

        return specifiedSubstanceAdministrationEventAndAssociatedConceptHasNotPreviouslyBeenInitializedForAnotherVaccineGroup(ic,
                svgc) && atLeastOneVaccineComponentHasNotBeenNotInitializedForSpecifiedSubstanceAdministrationEventAndSeries(sae,
                targetSeries, vaccineAdministered);

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
         *	catch (IllegalArgumentException ide) {
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
    }

    private boolean atLeastOneVaccineComponentHasNotBeenNotInitializedForSpecifiedSubstanceAdministrationEventAndSeries(
            final SubstanceAdministrationEvent sae, final TargetSeries targetSeries, final Vaccine vaccine)
    {
        if (vaccine == null || sae == null || targetSeries == null)
            return false;

        final String conceptTargetId = sae.getId();
        final String targetSeriesIdentifier = targetSeries.getTargetSeriesIdentifier();
        if (conceptTargetId == null || targetSeriesIdentifier == null)
            return false;

        return vaccine.getVaccineComponents()
                .stream()
                .map(vc -> conceptTargetId + vc.getCdsConceptName() + targetSeriesIdentifier)
                .anyMatch(Predicate.not(initializedTargetDoseList::contains));
    }

    private boolean specifiedSubstanceAdministrationEventAndAssociatedConceptHasNotPreviouslyBeenInitializedForAnotherVaccineGroup(
            final ImmunizationConcept ic, final String vaccineGroup)
    {
        if (ic == null || vaccineGroup == null)
            return false;

        final String conceptTargetId = ic.getConceptTargetId();
        final String immunizationConcept = ic.getOpenCdsConceptCode();
        if (conceptTargetId == null || immunizationConcept == null)
            return false;

        final String mapKey = conceptTargetId + immunizationConcept;
        return !initializedTargetDoseByVgMap.containsKey(mapKey) || initializedTargetDoseByVgMap.get(mapKey).equals(vaccineGroup);
    }
}
