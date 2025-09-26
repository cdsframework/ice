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

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.kie.api.definition.type.ClassReactive;

import com.google.common.base.Predicates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@ClassReactive
public class TargetSeasons
{
    /**
     * TargetSeasonComparator. As this comparator is primarily concerned with ordering, it does not check if the seasons overlap with one another or not. If this functionality is required,
     * perform a separate Season.seasonOverlapsWith() check first. This method simply compares on the starting date to determine ordering. Seasons are sorted by reverse order... most
     * recent seasons are stored first, prior seasons after, and default season is last.
     */
    private static class TargetSeasonComparator implements Comparator<Season>
    {
        public int compare(final Season a, final Season b)
        {
            // String _METHODNAME = "TargetSeasonComparator.compare(): ";

            if (a == null && b == null)
                return 0;

            if (a == null)
                return 1;

            if (b == null)
                return -1;

            if (!a.isDefaultSeason() && b.isDefaultSeason())
                return -1;

            if (a.isDefaultSeason() && b.isDefaultSeason())
                return 0;

            if (a.isDefaultSeason())
                return 1;

            // Both are non-default seasons - reverse order
            if (a.startsOnSameStartDate(b))
                return 0;

            if (a.startsBeforeStartDate(b, false))
                return 1;

            return -1;
        }
    }

    private final Date evalTime;
    //private Map<SupportedVaccineGroupConcept, Season> currentSeasonManuallySet;
    // The current season for this invocation
    private final Map<String, Season> currentSeason = new HashMap<>();
    // Vaccine Group -> Season
    // The TargetSeasons for this invocation., sorted in reverse order according to TargetSeasonComparator, default season is last
    private final Map<String, NavigableSet<Season>> allSeasons = new HashMap<>();
    // Vaccine Group -> Season
    // The default season for this invocation
    private final Map<String, Season> defaultSeason = new HashMap<>();
    // Vaccine Group -> Season

    /**
     * This method checks to see if the supplied date falls within one or more start and off-season end dates (or just end date if the off-season
     * hasn't been defined) of those Seasons tracked by this class (i.e. - TargetSeasons). Default seasons do not meet this criteria.
     *
     * @param pDate the date for which to check applicability
     * @param pSVGC the vaccine group that scopes the set of TargetSeasons to scan for this criteria
     * @return true if a Season tracked by this class has been found for which this criteria is true, false if no such season is found.
     */
    public boolean dateIsApplicableToOneOrMoreFullySpecifiedTargetSeasonsInVaccineGroup(final Date pDate, final String pSVGC)
    {
        return getFullySpecifiedTargetSeasonInVaccineGroupApplicableToDate(pDate, pSVGC) != null;
    }

    /**
     * This method returns the Season the supplied date falls within one or more start and off-season end dates (or just end date if the off-season
     * hasn't been defined) of those Seasons tracked by this class (i.e. - TargetSeasons). Default seasons do not meet this criteria.
     *
     * @return vaccine group Season that the date is applicable to, or null if there is no season for the specified criteria
     */
    public Season getFullySpecifiedTargetSeasonInVaccineGroupApplicableToDate(final Date pDate, final String pSVGC)
    {
        if (pDate == null || pSVGC == null)
            return null;

        final Set<Season> lAllSeasons = allSeasons.get(pSVGC);
        if (lAllSeasons == null)
            return null;

        return lAllSeasons.stream()
                .filter(Objects::nonNull)
                .filter(Predicates.not(Season::isDefaultSeason))
                .filter(lS -> lS.dateIsApplicableToSeason(pDate, true))
                .findFirst()
                .orElse(null);
    }

    /**
     * Populate the off-season end dates for each season being tracked by this class. For each season, the off-season end date is set to the day before
     * the next season's start date. Default seasons are not populated with off-season dates.
     */
    @SuppressWarnings("unused")
    private void populateOffSeasonEndDatesForAllTargetSeasons()
    {
        allSeasons.keySet().forEach(this::populateOffSeasonEndDatesForTargetSeasonsInVaccineGroup);
    }

    /**
     * Populate the off-season end date, as long as there are no ordering inconsistencies (i.e. - start date of next season starts on or before end date of previous season)
     */
    private void populateOffSeasonEndDatesForTargetSeasonsInVaccineGroup(final String pSVGC)
    {
        if (pSVGC == null)
            return;

        final String _METHODNAME = "populateOffSeasonEndDatesForTargetSeasonsInVaccineGroup(): ";
        final NavigableSet<Season> lVGSeasonSet = allSeasons.get(pSVGC);
        if (lVGSeasonSet == null)
            return;

        final Iterator<Season> lSeasonRIter = lVGSeasonSet.descendingIterator();
        Season lMostRecentSeasonExamined = null;
        Season lIdentifiedDefaultSeason = null;
        boolean orderingInconsistencyFound = false;
        while (lSeasonRIter.hasNext())
        {
            lMostRecentSeasonExamined = lSeasonRIter.next();
            if (lMostRecentSeasonExamined == null)
            {
                // This should not happen
                lSeasonRIter.remove();
            }
            else
                if (lMostRecentSeasonExamined.isDefaultSeason())
                    lIdentifiedDefaultSeason = lMostRecentSeasonExamined;
                else
                {
                    final Season lNextSeason = lVGSeasonSet.lower(lMostRecentSeasonExamined);
                    if (lNextSeason != null && !lNextSeason.isDefaultSeason() && !orderingInconsistencyFound)
                    {
                        final LocalDate lNextSeasonStartDate = lNextSeason.getFullySpecifiedSeasonStartDate();
                        final LocalDate lMostRecentExaminedOffSeasonEndDate = lNextSeasonStartDate.minusDays(1);
                        if (!lMostRecentExaminedOffSeasonEndDate.isBefore(
                                lMostRecentSeasonExamined.getFullySpecifiedSeasonEndDate()))
                        {
                            if (lMostRecentSeasonExamined.isOffSeasonPermitted())
                            {
                                final LocalDate lOffSeasonEndDateBasedOnDefault =
                                        determineOffSeasonEndDateForFullySpecifiedSeasonBasedOnDefaultSeason(
                                                lMostRecentSeasonExamined, lIdentifiedDefaultSeason);
                                /////// if (lMostRecentExaminedOffSeasonEndDate.isAfter(lOffSeasonEndDateBasedOnDefault) &&
                                ////// DEBUG: first line
                                if (lOffSeasonEndDateBasedOnDefault == null || (
                                        lMostRecentExaminedOffSeasonEndDate.isAfter(lOffSeasonEndDateBasedOnDefault)
                                                && lMostRecentExaminedOffSeasonEndDate.isBefore(
                                                lOffSeasonEndDateBasedOnDefault.plusYears(1))))
                                {
                                    lMostRecentSeasonExamined.setOffSeasonEndDateForFullySpecifiedSeason(
                                            lMostRecentExaminedOffSeasonEndDate);
                                }
                                else
                                {
                                    lMostRecentSeasonExamined.setOffSeasonEndDateForFullySpecifiedSeason(
                                            lOffSeasonEndDateBasedOnDefault);
                                }
                            }
                        }
                        else
                        {
                            orderingInconsistencyFound = true;
                            log.warn(_METHODNAME
                                    + "Ordering inconsistency found in TargetSeasons - aborting population of off-seasons");
                        }
                    }
                }
        }

        // If an off-season end date could not be determined for the most recent season; just determine the off-season to be the day before the start date as specified by
        // the default season (if defined)
        if (lMostRecentSeasonExamined != null && !lMostRecentSeasonExamined.isDefaultSeason() && lIdentifiedDefaultSeason != null)
            lMostRecentSeasonExamined.setOffSeasonEndDateForFullySpecifiedSeason(
                    determineOffSeasonEndDateForFullySpecifiedSeasonBasedOnDefaultSeason(lMostRecentSeasonExamined,
                            lIdentifiedDefaultSeason));
    }

    /**
     * Determine and return what the off-season end date would be for the fully-specified season parameter based on the default season supplied. If the default season supplied is null,
     * the returned date is null.  (i.e. - presumably there is no off-season end date based on the default date)
     *
     * @return LocalDate Off-Season End Date
     * @throws IllegalArgumentException if the first parameter is not a fully-specified season and the 2nd parameter is not a default season
     */
    private LocalDate determineOffSeasonEndDateForFullySpecifiedSeasonBasedOnDefaultSeason(final Season pFullySpecifiedSeason,
            final Season pDefaultSeason)
    {
        final String _METHODNAME = "determineNextSeasonStartDateForFullySpecifiedSeasonBasedOnDefaultSeason(): ";

        if (pFullySpecifiedSeason == null || pFullySpecifiedSeason.isDefaultSeason())
        {
            final String errStr =
                    "Improper fully specified season argument - specified season is either not populated or is not a fully-specified season";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        if (pDefaultSeason == null)
            return null;

        if (!pDefaultSeason.isDefaultSeason())
        {
            final String errStr =
                    "Improper default season argument - specified season is either not populated or is not a default season";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        final int lSeasonEndDateYear = pFullySpecifiedSeason.getSeasonEndYear();
        LocalDate nextSeasonStartDate =
                LocalDate.of(lSeasonEndDateYear, pDefaultSeason.getSeasonStartMonth(), pDefaultSeason.getSeasonStartDay());
        if (!nextSeasonStartDate.isAfter(pFullySpecifiedSeason.getFullySpecifiedSeasonEndDate()))
            nextSeasonStartDate = nextSeasonStartDate.plusYears(1);

        return nextSeasonStartDate.minusDays(1);
    }

    /**
     * Adds a Season to the list of Seasons being tracked by this class (if it is not already being tracked-- otherwise simply return). If any of the parameters provided to this method
     * is null, this method simply returns. If the provided season is a default season, then the existing default season (if there is one) is replaced by the one provided to this
     * method. If the current season has not been manually set, this method also automatically sets the current season to be the latest season in the list of all Seasons being tracked.
     * However, if the current season has been explicitly set via setCurrentSeason(), this method will not set the current season as per above. Finally, all Seasons in the same vaccine
     * group tracked by this class will have their off-season end dates updated (as long as there are no ordering inconsistencies such as a start date of next season specified as being
     * as on before the prior season's end date), including the Season parameter passed into this method.
     *
     * @param pS Season to add to the list of seasons being tracked by this class
     *           ////@throws InconsistentConfigurationDataException if the end date of prior season ends on or after start date of specified season, or the specified season start dates starts on or
     *           ////		before the next season's end date
     */
    public void addTargetSeason(final Season pS)
    {
        if (pS == null)
            return;

        // String _METHODNAME = "addTargetSeason(): ";
        final String svgc = pS.getVaccineGroup();
        NavigableSet<Season> vgSeasons = allSeasons.get(svgc);
        if (vgSeasons == null)
            vgSeasons = new TreeSet<>(new TargetSeasonComparator());
        if (vgSeasons.contains(pS) && !pS.isDefaultSeason())
            pS.setOffSeasonEndDateForFullySpecifiedSeason(getMatchingTargetSeason(pS).getFullySpecifiedSeasonOffSeasonEndDate());

        if (pS.isDefaultSeason())
            defaultSeason.put(svgc, pS);
        vgSeasons.add(pS);
        allSeasons.put(svgc, vgSeasons);
        populateOffSeasonEndDatesForTargetSeasonsInVaccineGroup(svgc);

        final Season lCurrentSeason = getFullySpecifiedTargetSeasonInVaccineGroupApplicableToDate(evalTime, svgc);

        if (lCurrentSeason == null)
            currentSeason.remove(svgc);
        else
            currentSeason.put(svgc, lCurrentSeason);
    }

    /**
     * Get the next season for the specified SupportedVaccineGroupConcept with respect to the current season, or return null if it is not defined
     */
    public Season getNextSeason(final String svgc)
    {
        final String _METHODNAME = "getNextSeason(): ";
        if (svgc == null)
            return null;

        final Season lCurrentSeason = getCurrentSeason(svgc);
        if (lCurrentSeason == null)
            return null;

        final NavigableSet<Season> vgSeasons = getAllSeasons(svgc);
        if (vgSeasons == null)
        {
            final String errStr = "Season list for vaccine group " + svgc + " but current season is set??? This should not happen.";
            log.error(_METHODNAME + "{}", errStr);
            throw new ICECoreError(errStr);
        }

        return vgSeasons.stream().filter(s -> s.equals(lCurrentSeason)).findFirst().map(vgSeasons::lower).orElse(null);
    }

    /**
     * Get the current season for the specified supported vaccine group, or null if there is none.
     */
    public Season getCurrentSeason(final String svgc)
    {
        if (svgc == null)
            return null;

        return currentSeason.get(svgc);
    }

    /**
     * Get the previous season with respect to the current season for the specified vaccine group, or null if there is none or if it is a default season.
     */
    public Season getPreviousSeason(final String svgc)
    {
        // String _METHODNAME = "getPreviousSeason(SupportedVaccineGroupConcept): ";

        if (svgc == null)
            return null;

        return getPreviousSeason(getCurrentSeason(svgc));
    }

	
	/*
	 * Sets the fully-specified season as the current season for the season's vaccine group. (If the supplied Season is a default season, this method
	 * simply returns; this method also returns if the supplied season or vaccine group is null.) If the season being set was not previously added to the 
	 * list of seasons to be tracked, it is also added. Any previous settings for current season are overridden.
	 * @param pS Season to set the current season to
	 *
	///////
	public void setCurrentSeason(Season pS) {
		
		if (pS == null || pS.isDefaultSeason())
return;
		
		addTargetSeason(pS);
		currentSeasonManuallySet.put(pS.getAssociatedVaccineGroup(), pS);
	}
	///////
	*/

    /**
     * Get the previous season with respect to the specified Season, or null if there is none or if it is a default season
     */
    public Season getPreviousSeason(final Season pS)
    {
        final String _METHODNAME = "getPreviousSeason(Season): ";

        final SortedSet<Season> vgSeasons = allSeasons.get(pS.getVaccineGroup());
        if (vgSeasons == null)
        {
            final String errStr = "Season list for vaccine group %s but current season is set??? This should not happen.".formatted(
                    pS.getVaccineGroup());
            log.error(_METHODNAME + "{}", errStr);
            throw new ICECoreError(errStr);
        }

        boolean foundSpecified = false;
        Season lPrevious = null;
        for (final Season s : vgSeasons)
        {
            if (foundSpecified)
            {
                if (!s.isDefaultSeason())
                    lPrevious = s;
                break;
            }

            if (s.equals(pS))
                foundSpecified = true;
        }

        return lPrevious;
    }

    /**
     * Get all seasons being tracked for the specified vaccine group
     */
    public NavigableSet<Season> getAllSeasons(final String svgc)
    {
        if (svgc == null)
            return null;

        return allSeasons.get(svgc);
    }

    /**
     * Get the matching TargetSeason for the specified Season. NULL is returned if there is no matching TargetSeason
     */
    private Season getMatchingTargetSeason(final Season s)
    {
        if (s == null)
            return null;

        return allSeasons.get(s.getVaccineGroup()).stream().filter(s::equals).findFirst().orElse(null);
    }

    /**
     * Update fully-specified Season specified with off-season dates determined by this TargetSeason
     */
    public void updateFullySpecifiedSeasonWithOffSeasonDates(final Season pS)
    {
        if (pS.isDefaultSeason())
            return;

        final Season lMatchingSeason = getMatchingTargetSeason(pS);
        if (lMatchingSeason != null)
            pS.setOffSeasonEndDateForFullySpecifiedSeason(lMatchingSeason.getFullySpecifiedSeasonOffSeasonEndDate());
    }

    /**
     * Returns true if the specified Season is being tracked by this object, false if not
     */
    public boolean containsTargetSeason(final Season s)
    {
        if (s == null)
            return false;

        final Set<Season> lAllTrackedSeasonsInVG = getAllSeasons(s.getVaccineGroup());
        if (lAllTrackedSeasonsInVG == null)
            return false;

        return lAllTrackedSeasonsInVG.contains(s);
    }

    @Override
    public String toString()
    {
        final StringBuilder toStr = new StringBuilder("TargetSeasons [ \n");
        {
            toStr.append("    allSeasons {{{ ");
            int i = 1;
            for (final Entry<String, NavigableSet<Season>> lASEntry : allSeasons.entrySet())
            {
                final String lSVGC = lASEntry.getKey();
                for (final Season lSeason : lASEntry.getValue())
                {
                    toStr.append(i++).append(") ").append(lSVGC).append(": ");
                    toStr.append(lSeason.toString());
                    toStr.append("; ");
                }
            }
            toStr.append(" }}}\n");
        }

        {
            int i = 1;
            toStr.append("    currentSeason {{{ ");
            for (final Entry<String, Season> lCSEntry : currentSeason.entrySet())
            {
                toStr.append(i++).append(") ").append(lCSEntry.getKey()).append(": ");
                toStr.append(lCSEntry.getValue().toString());
            }
            toStr.append(" }}}\n");
        }

        {
            int i = 1;
            toStr.append("    defaultSeason {{{ ");
            final Set<Entry<String, Season>> lDefaultEntrySet = defaultSeason.entrySet();
            for (final Entry<String, Season> lDefaultEntry : lDefaultEntrySet)
                toStr.append(i++)
                        .append(") ")
                        .append(lDefaultEntry.getKey())
                        .append(": ")
                        .append(lDefaultEntry.getValue().toString());
            toStr.append(" }}}\n");
            toStr.append("]");
        }

        return toStr.toString();
    }
}
