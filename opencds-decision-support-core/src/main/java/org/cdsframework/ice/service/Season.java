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
import java.time.MonthDay;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.kie.api.definition.type.ClassReactive;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Slf4j
@ClassReactive
public class Season
{
    private enum SeasonDateType
    {
        START,
        END,
        OFFSEASON_START,
        OFFSEASON_END
    }

    /**
     * Construct deep copy of Season object and return the newly created object to the caller
     */
    public static Season constructDeepCopyOfSeasonObject(final Season pS)
    {
        if (pS == null)
            return null;

        final Season lS = new Season();
        lS.seasonName = pS.seasonName;
        lS.associatedVaccineGroup = pS.associatedVaccineGroup;
        lS.definedBySeriesTableRules = pS.definedBySeriesTableRules;
        lS.seasonStartDate = pS.seasonStartDate;
        lS.seasonEndDate = pS.seasonEndDate;
        lS.offSeasonEndDate = pS.offSeasonEndDate;
        lS.defaultSeason = pS.defaultSeason;
        lS.defaultStartMonthAndDay = pS.defaultStartMonthAndDay;
        lS.defaultEndMonthAndDay = pS.defaultEndMonthAndDay;
        lS.offSeasonPermitted = pS.offSeasonPermitted;

        return lS;
    }

    /**
     * Returns a full (non-default) Season that applies to the provided date. If the season parameter provided in the call not a default season, it
     * is simply returned as is. If the date is not provided, null is returned.
     *
     * @param pSeason a default Season
     */
    public static Season constructFullySpecifiedSeasonFromDefaultSeasonAndDate(final Season pSeason, final LocalDate applicableDate)
    {
        if (pSeason == null || applicableDate == null)
            return null;

        if (!pSeason.isDefaultSeason())
            return pSeason;

        final int requestYear = applicableDate.getYear();
        final int requestMonth = applicableDate.getMonthValue();
        final int defaultStartMonth = pSeason.defaultStartMonthAndDay.getMonthValue();
        final int defaultStartDay = pSeason.defaultStartMonthAndDay.getDayOfMonth();
        final int defaultEndMonth = pSeason.defaultEndMonthAndDay.getMonthValue();
        final int defaultEndDay = pSeason.defaultEndMonthAndDay.getDayOfMonth();

        int startYear = requestYear;
        int endYear = requestYear;

        final MonthDay applicableDateMonthDay = MonthDay.from(applicableDate);
        if (monthAndDayFallsWithinRange(requestMonth, applicableDate.getDayOfMonth(), defaultStartMonth, defaultStartDay,
                defaultEndMonth, defaultEndDay))
        {
            if (applicableDateMonthDay.compareTo(pSeason.defaultEndMonthAndDay) <= 0)
                startYear--;
            else
                if (pSeason.defaultEndMonthAndDay.isBefore(pSeason.defaultStartMonthAndDay))
                    endYear++;
        }
        else
        {
            if (pSeason.defaultEndMonthAndDay.isBefore(pSeason.defaultStartMonthAndDay))
                startYear--;
        }

        String seasonName = pSeason.getSeasonName();
        if (seasonName == null)
            seasonName = "automated";

        return new Season(seasonName, pSeason.associatedVaccineGroup, pSeason.isDefinedBySeriesTableRules(), defaultStartMonth,
                defaultStartDay, startYear, defaultEndMonth, defaultEndDay, endYear);
    }

    /**
     * Checks if the supplied month and day falls within the range of the specified start month/day and end month/day, inclusive. If any of the month/day combinations are invalid,
     * an IllegalArgumentException is thrown.
     */
    public static boolean monthAndDayFallsWithinRange(final int month, final int day, final MonthDay rangeStart,
            final MonthDay rangeEnd)
    {
        final String _METHODNAME = "monthAndDayFallsWithinRange(): ";

        if (rangeStart == null || rangeEnd == null)
        {
            final String errStr = "Invalid rangeStart or rangeEnd parameter supplied";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        MonthDay.of(month, day);        // Check that month and day values sent are valid; MonthDay throws an exception if not

        // Only compare with respect to month and day. Determine set of relevant months of pS Season;
        final Set<Integer> relevantMonthsOfPS = new HashSet<>();
        final int rangeStartMonth = rangeStart.getMonthValue();
        final int rangeStartDay = rangeStart.getDayOfMonth();
        final int rangeEndMonth = rangeEnd.getMonthValue();
        final int rangeEndDay = rangeEnd.getDayOfMonth();
        if (rangeStartMonth > rangeEndMonth)
        {
            relevantMonthsOfPS.add(rangeEndMonth);
            int i = rangeStartMonth;
            while (i != rangeEndMonth)
            {
                relevantMonthsOfPS.add(i);
                if (i < 12)
                    i++;
                else
                    i = 1;
            }
        }
        else
        {
            int i = rangeStartMonth;
            while (i <= rangeEndMonth)
            {
                relevantMonthsOfPS.add(i);
                i++;
            }
        }

        // If both the start and end months fall outside of the list of relevant months, then return false (seasons do not overlap). If either or
        // both of the start/end months are on the same month, check that the start day is on/after pS or the end day is on/before pS's end day
        boolean fallsWithinStartRange = false;
        boolean fallsWithinEndRange = false;
        if (relevantMonthsOfPS.contains(month))
        {
            if (month == rangeStartMonth)
            {
                if (day >= rangeStartDay)
                    fallsWithinStartRange = true;
            }
            else
                fallsWithinStartRange = true;

            if (month == rangeEndMonth)
            {
                if (day <= rangeEndDay)
                    fallsWithinEndRange = true;
            }
            else
                fallsWithinEndRange = true;
        }

        return fallsWithinStartRange && fallsWithinEndRange;
    }

    /**
     * Checks if the supplied month and day falls within the range of the specified start month/day and end month/day, inclusive. If any of the month/day combinations are invalid,
     * an IllegalArgumentException is thrown.
     */
    public static boolean monthAndDayFallsWithinRange(final int month, final int day, final int rangeStartMonth,
            final int rangeStartDay, final int rangeEndMonth, final int rangeEndDay)
    {
        return monthAndDayFallsWithinRange(month, day, MonthDay.of(rangeStartMonth, rangeStartDay),
                MonthDay.of(rangeEndMonth, rangeEndDay));
    }

    private String seasonName;
    @EqualsAndHashCode.Include
    private LocalDate seasonStartDate;
    @EqualsAndHashCode.Include
    private LocalDate seasonEndDate;
    private LocalDate offSeasonEndDate;
    private boolean offSeasonPermitted;

    @EqualsAndHashCode.Include
    private boolean defaultSeason;
    private boolean definedBySeriesTableRules;
    @EqualsAndHashCode.Include
    private MonthDay defaultStartMonthAndDay;
    @EqualsAndHashCode.Include
    private MonthDay defaultEndMonthAndDay;
    @EqualsAndHashCode.Include
    private String associatedVaccineGroup;

    /**
     * Creates a fully-specified Season with a start and end dates (month/day/year). Off-season end dates permitted by default.
     *
     * @throws IllegalArgumentException if month/day values are invalid
     */
    public Season(final String seasonName, final String svgc, final boolean definedBySeriesTableRules, final int startMonth,
            final int startDay, final int startYear, final int endMonth, final int endDay, final int endYear)
    {
        final String _METHODNAME = "Season(): ";

        if (svgc == null)
            throw new IllegalArgumentException(_METHODNAME + "vaccine group not specified");

        if (seasonName == null)
            throw new IllegalArgumentException(_METHODNAME + "season name not specified");

        this.seasonName = seasonName;
        this.associatedVaccineGroup = svgc;
        this.definedBySeriesTableRules = definedBySeriesTableRules;
        this.seasonStartDate = LocalDate.of(startYear, startMonth, startDay);
        this.seasonEndDate = LocalDate.of(endYear, endMonth, endDay);
        this.defaultSeason = false;
        this.offSeasonPermitted = true;
    }

    /**
     * Creates a default Season with a start and end dates (month/day but no year). Off-season end dates permitted by default.
     *
     * @throws IllegalArgumentException if month/day values are invalid
     */
    public Season(final String seasonName, final String svgc, final boolean definedBySeriesTableRules, final int defaultStartMonth,
            final int defaultStartDay, final int defaultEndMonth, final int defaultEndDay)
    {
        final String _METHODNAME = "Season(): ";

        if (svgc == null)
            throw new IllegalArgumentException(_METHODNAME + "vaccine group not specified");

        if (seasonName == null)
            throw new IllegalArgumentException(_METHODNAME + "season name not specified");

        this.seasonName = seasonName;
        this.associatedVaccineGroup = svgc;
        this.definedBySeriesTableRules = definedBySeriesTableRules;
        this.defaultStartMonthAndDay = MonthDay.of(defaultStartMonth, defaultStartDay);
        this.defaultEndMonthAndDay = MonthDay.of(defaultEndMonth, defaultEndDay);
        this.defaultSeason = true;
        this.offSeasonPermitted = true;
    }

    public String getVaccineGroup()
    {
        return associatedVaccineGroup;
    }

    /**
     * For fully-specified Seasons, returns whether the season is before the specified season (or on the same date or before is startDateInclusive is set to true).
     * If either season is a default season, an IllegalArgumentException is thrown
     */
    public boolean startsBeforeStartDate(final Season pS, final boolean startDateInclusive)
    {
        final int compareTo = compareSeasonDates(pS, SeasonDateType.START);
        return startDateInclusive ? compareTo <= 0 : compareTo < 0;
    }

    /**
     * For fully-specified Seasons, returns whether the season is after the specified season (or on the same date or after if startDateInclusive is set to true).
     * If either season is a default season, an IllegalArgumentException is thrown
     */
    public boolean startsAfterStartDate(final Season pS, final boolean startDateInclusive)
    {
        final int compareTo = compareSeasonDates(pS, SeasonDateType.START);
        return startDateInclusive ? compareTo >= 0 : compareTo > 0;
    }

    /**
     * For fully-specified seasons, returns whether the season start date is equal to the specified season's start date.
     * If either season is a default season, an IllegalArgumentException is thrown
     */
    public boolean startsOnSameStartDate(final Season pS)
    {
        return compareSeasonDates(pS, SeasonDateType.START) == 0;
    }

    /**
     * For fully-specified seasons, returns whether the season ends before the specified season's end date (or on the same date or before is endDateInclusive is set to true).
     * If either season is a default season, an IllegalArgumentException is thrown
     */
    public boolean endsBeforeEndDate(final Season pS, final boolean endDateInclusive)
    {
        final int compareTo = compareSeasonDates(pS, SeasonDateType.END);
        return endDateInclusive ? compareTo <= 0 : compareTo < 0;
    }

    /**
     * For non-default Seasons, returns whether the season is after the specified season's end date (or on the same date or after if endDateInclusive is set to true).
     * If either season is a default season or does not have an end date, an IllegalArgumentException is thrown
     */
    public boolean endsAfterEndDate(final Season pS, final boolean endDateInclusive)
    {
        final int compareTo = compareSeasonDates(pS, SeasonDateType.END);
        return endDateInclusive ? compareTo >= 0 : compareTo > 0;
    }

    /**
     * For non-default Seasons, returns whether the season's end date is equal to the specified season's end date. If either season is a default season or does not have an end date,
     * an IllegalArgumentException is thrown
     */
    public boolean endsOnSameEndDate(final Season pS)
    {
        return compareSeasonDates(pS, SeasonDateType.END) == 0;
    }

    /**
     * If either season is a default season or does not have an end date, an IllegalArgumentException is thrown
     * TODO: Future support for default seasons
     */
    private int compareSeasonDates(final Season pS, final SeasonDateType seasonDateType)
    {
        final String _METHODNAME = "compareToSeasonDate(): ";
        if (seasonDateType == null || pS == null || pS.isDefaultSeason() || this.isDefaultSeason())
        {
            final String errStr = "one of the seasons is a default season";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        switch (seasonDateType)
        {
            case START ->
            {
                final LocalDate startDateThis = this.getFullySpecifiedSeasonStartDate();
                final LocalDate startDatePS = pS.getFullySpecifiedSeasonStartDate();
                if (startDateThis == null || startDatePS == null)
                {
                    final String errStr = "one or both season start dates not populated";
                    log.warn(_METHODNAME + errStr);
                    throw new IllegalArgumentException(errStr);
                }

                return startDateThis.compareTo(startDatePS);
            }
            case END ->
            {
                final LocalDate endDateThis = this.getFullySpecifiedSeasonEndDate();
                final LocalDate endDatePS = pS.getFullySpecifiedSeasonEndDate();
                if (this.getFullySpecifiedSeasonEndDate() == null || pS.getFullySpecifiedSeasonEndDate() == null)
                {
                    final String errStr = "one or both seasons end dates not populated";
                    log.warn(_METHODNAME + errStr);
                    throw new IllegalArgumentException(errStr);
                }
                return endDateThis.compareTo(endDatePS);
            }
            case OFFSEASON_START ->
            {
                final LocalDate offSeasonStartDateThis = this.getFullySpecifiedSeasonOffSeasonStartDate();
                final LocalDate offSeasonStartDatePS = pS.getFullySpecifiedSeasonOffSeasonStartDate();
                if (offSeasonStartDateThis == null || offSeasonStartDatePS == null)
                {
                    final String errStr = "one or both off-season start dates not populated";
                    log.warn(_METHODNAME + errStr);
                    throw new IllegalArgumentException(errStr);
                }
                return offSeasonStartDateThis.compareTo(offSeasonStartDatePS);
            }
            case OFFSEASON_END ->
            {
                final LocalDate offSeasonEndDateThis = this.getFullySpecifiedSeasonOffSeasonEndDate();
                final LocalDate offSeasonEndDatePS = pS.getFullySpecifiedSeasonOffSeasonEndDate();
                if (offSeasonEndDateThis == null || offSeasonEndDatePS == null)
                {
                    final String errStr = "one or both off-seasons end dates not populated";
                    log.warn(_METHODNAME + errStr);
                    throw new IllegalArgumentException(errStr);
                }
                return offSeasonEndDateThis.compareTo(offSeasonEndDatePS);
            }
            default ->
            {
                final String errStr = _METHODNAME + "invalid SeasonDateType parameter supplied";
                log.error(errStr);
                throw new IllegalArgumentException(errStr);
            }
        }
    }

    public void setOffSeasonPermission(final boolean truefalse)
    {
        this.offSeasonPermitted = truefalse;
    }

    /**
     * If NOT a default season, returns the season start date as a LocalDate. If a default season, null is returned.
     */
    public LocalDate getFullySpecifiedSeasonStartDate()
    {
        return seasonStartDate;
    }

    /**
     * IfNOT a default season, returns the season end date as a LocalDate. If a default season, null is returned.
     */
    public LocalDate getFullySpecifiedSeasonEndDate()
    {
        return seasonEndDate;
    }

    /**
     * If this is a fully-specified season and the off-season end date has been previously set, this method returns the off-season start date as one day
     * after the season's end date. Otherwise, null is returned.
     */
    public LocalDate getFullySpecifiedSeasonOffSeasonStartDate()
    {
        if (seasonEndDate == null || offSeasonEndDate == null)
            return null;

        if (offSeasonEndDate.isAfter(seasonEndDate))
            return seasonEndDate.plusDays(1);

        if (offSeasonEndDate.equals(seasonEndDate))
        {
            // Season off-season start and off-season end dates are on the same date as the end date. Effectively, no off-season but tracked/recorded
            // this way for internal calculations
            return seasonEndDate;
        }

        // There is no season start date - return null
        return null;
    }

    /**
     * Sets the off-season end date. The off season starts after the end date of the season and ends sometime after that. The start date of the off-season
     * is always after the end date of the season. Therefore, if there is no end, date, the end date specified here is not after the end date of the season,
     * or this is a default season, no action is taken. If the date provided is not a valid date, an IllegalArgumentException is thrown. If
     * isOffSeasonPermitted() is set to false, no action is taken. Thus, it is suggested that the caller verify that off-season dates can be set
     * by calling isOffSeasonPermitted() first and setOffSeasonPermitted() if necessary.
     */
    public void setOffSeasonEndDateForFullySpecifiedSeason(final int month, final int day, final int year)
    {
        setOffSeasonEndDateForFullySpecifiedSeason(LocalDate.of(year, month, day));
    }

    /**
     * Sets the off-season end date. The off season starts after the end date of the season and ends sometime after that. The start date of the off-season
     * is always after the end date of the season. Therefore, if there is no end, date, the end date specified here is not after the end date of the season,
     * or this is a default season, no action is taken. If the date provided is not a valid date, an IllegalArgumentException is thrown. If
     * isOffSeasonEndDatePermitted() is set to false, no action is taken. Thus, it is suggested that the caller verify that off-season dates can be set
     * by calling isOffSeasonEndDatePermitted() first and setOffSeasonEndDatePermitted() if necessary.
     */
    public void setOffSeasonEndDateForFullySpecifiedSeason(final LocalDate pLD)
    {
        final String _METHODNAME = "setOffSeasonEndDate(LocalDate): ";

        if (pLD == null)
            return;

        if (this.seasonEndDate == null || this.isDefaultSeason() || !isOffSeasonPermitted())
        {
            final String errStr =
                    "Cannot add an off-season end date to a default season, to a season that does not have an end date, or isOffSeasonEndDatePermitted() is false";
            log.warn(_METHODNAME + errStr);
            return;
        }

        final int compareTo = pLD.compareTo(this.seasonEndDate);
        if (compareTo < 0)
        {
            log.warn(_METHODNAME
                            + "Cannot specify an off-season end date that is before the season end date (end date: {}; specified off-season date: {}",
                    seasonEndDate, pLD);
            return;
        }

        offSeasonEndDate = pLD;
    }

    public LocalDate getFullySpecifiedSeasonOffSeasonEndDate()
    {
        return offSeasonEndDate;
    }

    /**
     * If a default season, returns the season start date as a MonthDay. Otherwise null is returned.
     */
    public MonthDay getDefaultSeasonStartMonthAndDay()
    {
        return defaultStartMonthAndDay;
    }

    /**
     * If a default season and an end month/day is defined, returns the season end date as a MonthDay.
     */
    public MonthDay getDefaultSeasonEndMonthAndDay()
    {
        return defaultEndMonthAndDay;
    }

    /**
     * Returns true if the seasons START dates are equivalent (same vaccine group, and end month/day/year or end month/day), or false if not.
     * This method is applicable to both fully-specified seasons as well as default seasons. Thus, if either or both seasons are a default season,
     * the year is not checked and equivalence is based on month and day only.
     */
    public boolean seasonsHaveEquivalentStartDates(final Season pS)
    {
        if (pS == null)
            return false;

        if (!Objects.equals(this.associatedVaccineGroup, pS.associatedVaccineGroup))
            return false;

        if (getSeasonStartMonth() != pS.getSeasonStartMonth() && getSeasonStartDay() != pS.getSeasonStartDay())
            return false;

        if (!pS.isDefaultSeason() && !isDefaultSeason())
            return getSeasonStartYear() == pS.getSeasonStartYear();

        return true;
    }

    /**
     * Returns true if the seasons END dates are equivalent (same vaccine group, and end month/day/year or end month/day), or false if not.
     * This method is applicable to both fully-specified seasons as well as default seasons. Thus, if either or both seasons are a default season,
     * the year is not checked and equivalence is based on month and day only.
     */
    public boolean seasonsHaveEquivalentEndDates(final Season pS)
    {
        if (pS == null)
            return false;

        if (!Objects.equals(this.associatedVaccineGroup, pS.associatedVaccineGroup))
            return false;

        if (getSeasonEndMonth() != pS.getSeasonEndMonth() && getSeasonEndDay() != pS.getSeasonEndDay())
            return false;

        if (!pS.isDefaultSeason() && !isDefaultSeason())
            return getSeasonEndYear() == pS.getSeasonEndYear();

        return true;
    }

    /**
     * Returns true if the seasons are equivalent (same vaccine group, and start & end month/day/year or start & end month/day), or false if not.
     * This method is applicable to both fully-specified seasons as well as default seasons. Thus, if either or both seasons are a default season,
     * the year is not checked and equivalence is based on month and day only.
     */
    public boolean seasonsHaveEquivalentStartAndEndDates(final Season pS)
    {
        return seasonsHaveEquivalentStartDates(pS) && seasonsHaveEquivalentEndDates(pS);
    }

    /**
     * Date falls within the off-season
     */
    public boolean dateIsApplicableToOffSeason(final LocalDate pDate)
    {
        if (pDate == null)
            return false;

        if (isDefaultSeason())
            return !monthAndDayFallsWithinRange(pDate.getMonthValue(), pDate.getDayOfMonth(),
                    this.getDefaultSeasonStartMonthAndDay(), this.getDefaultSeasonEndMonthAndDay());

        final LocalDate lOffSeasonStartDate = getFullySpecifiedSeasonOffSeasonStartDate();
        if (!this.offSeasonPermitted || this.offSeasonEndDate == null || lOffSeasonStartDate == null)
            return false;

        if (this.offSeasonEndDate.equals(lOffSeasonStartDate))
            return false;

        return !pDate.isAfter(this.offSeasonEndDate) && !pDate.isBefore(lOffSeasonStartDate);
    }

    /**
     * Overload to dateIsApplicableToSeason(Date, boolean), with the 2nd parameter set to true.
     */
    public boolean dateIsApplicableToSeason(final LocalDate pDate)
    {
        return dateIsApplicableToSeason(pDate, true);
    }

    /**
     * Check to see if the date falls within the start and the end dates of the season.
     * (1) If an off-season end date has been defined and the includeOffSeason parameter is set to true, then this method checks if the date falls between the start date
     * and off-season end date. Otherwise, it checks is the date falls between the start date and regular season end date.
     * (2) If the season is a default season, returns true always if off-season should be included. Otherwise checks to see if the date falls between the start date and end date.
     */
    public boolean dateIsApplicableToSeason(final LocalDate pDate, final boolean includeOffSeason)
    {
        final String _METHODNAME = "dateIsApplicableToSeason(LocalDate, boolean): ";
        if (pDate == null)
            return false;

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Parameters: {}; {}", pDate, includeOffSeason);

        if (!isDefaultSeason())
        {
            if (log.isDebugEnabled())
                log.debug("is NOT a default season{}", this);

            final LocalDate endDateToUse = (includeOffSeason && offSeasonEndDate != null) ? offSeasonEndDate : seasonEndDate;
            if (log.isDebugEnabled())
                log.debug("seasonStartDate: {}; endDateToUse: {}", seasonStartDate, endDateToUse);

            if (!pDate.isBefore(seasonStartDate) && !pDate.isAfter(endDateToUse))
            {
                if (log.isDebugEnabled())
                    log.debug(_METHODNAME + "returning true");
                return true;
            }

            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "returning false");

            return false;
        }

        if (log.isDebugEnabled())
            log.debug("is a default season{}", this);
        if (includeOffSeason)
            return true;

        return monthAndDayFallsWithinRange(pDate.getMonthValue(), pDate.getDayOfMonth(), this.getDefaultSeasonStartMonthAndDay(),
                this.getDefaultSeasonEndMonthAndDay());
    }

    /**
     * Return true of season dates (start and end) intersect in any way; false if they do not. If either of the seasons is a default season, only the month/day are taken into account.
     * Otherwise, the fully-specified month/date/year is taken into consideration. If one of the fully-specified seasons starts before the other and it does not have an end date, then
     * they will be considered to be overlapping. Off-season start/end dates, since they are typically calculated for seasons in reference to one another, are not taken into consideration.
     */
    public boolean seasonOverlapsWith(final Season pS)
    {
        if (pS == null)
            return false;

        if (this.isDefaultSeason() || pS.isDefaultSeason())
        {
            // Only compare with respect to month and day. Determine set of relevant months of pS Season;
            final int pSStartMonth = pS.getSeasonStartMonth();
            final int pSEndMonth = pS.getSeasonEndMonth();
            final Set<Integer> relevantMonthsOfPS = new HashSet<>();
            if (pSStartMonth > pSEndMonth)
            {
                relevantMonthsOfPS.add(pSEndMonth);
                int i = pSStartMonth;
                while (i != pSEndMonth)
                {
                    relevantMonthsOfPS.add(i);
                    if (i < 12)
                        i++;
                    else
                        i = 1;
                }
            }
            else
            {
                int i = pSStartMonth;
                while (i <= pSEndMonth)
                {
                    relevantMonthsOfPS.add(i);
                    i++;
                }
            }
            // If both the start and end months fall outside of the list of relevant months, then return false (seasons do not overlap). If either or
            // both of the start/end months are on the same month, check that the start day is on/after pS or the end day is on/before pS's end day
            if (relevantMonthsOfPS.contains(this.getSeasonStartMonth()))
            {
                if (this.getSeasonStartMonth() != pS.getSeasonStartMonth())
                    return true;

                if (this.getSeasonStartDay() >= pS.getSeasonStartDay())
                    return true;
            }
            if (relevantMonthsOfPS.contains(this.getSeasonEndMonth()))
            {
                if (this.getSeasonEndMonth() == pS.getSeasonEndMonth())
                    return this.getSeasonEndDay() <= pS.getSeasonEndDay();

                return true;
            }
        }
        else
        {
            // Both Seasons are fully specified season
            final LocalDate seasonStartThis = this.getFullySpecifiedSeasonStartDate();
            final LocalDate seasonStartPS = pS.getFullySpecifiedSeasonStartDate();

            final Season priorSeason;
            final Season laterSeason;
            if (!seasonStartThis.isAfter(seasonStartPS))
            {
                priorSeason = this;
                laterSeason = pS;
            }
            else
            {
                priorSeason = pS;
                laterSeason = this;
            }

            return !priorSeason.getFullySpecifiedSeasonStartDate().isBefore(laterSeason.getFullySpecifiedSeasonStartDate())
                    || priorSeason.getFullySpecifiedSeasonEndDate() == null || !priorSeason.getFullySpecifiedSeasonEndDate()
                    .isBefore(laterSeason.getFullySpecifiedSeasonStartDate());
        }

        // Seasons do not overlap
        return false;
    }

    /**
     * Returns the season start month, or 0 if there is no start month.
     */
    public int getSeasonStartMonth()
    {
        if (defaultSeason)
        {
            if (this.defaultStartMonthAndDay == null)
                return 0;

            return this.defaultStartMonthAndDay.getMonthValue();
        }

        if (seasonStartDate == null)
            return 0;

        return this.seasonStartDate.getMonthValue();
    }

    /**
     * For either default or fully-specified seasons, returns the season end month, or 0 if there is no end month
     */
    public int getSeasonEndMonth()
    {
        if (defaultSeason)
        {
            if (this.defaultEndMonthAndDay == null)
                return 0;

            return this.defaultEndMonthAndDay.getMonthValue();
        }

        if (seasonEndDate == null)
            return 0;

        return this.seasonEndDate.getMonthValue();
    }

    /**
     * For either default or fully-specified seasons, returns the season end month, or 0 if there is no end month
     */
    public int getOffSeasonEndMonth()
    {
        if (defaultSeason)
            return 0;

        if (offSeasonEndDate == null)
            return 0;

        return this.offSeasonEndDate.getMonthValue();
    }

    /**
     * For either default or fully-specified seasons, returns the season start date, or 0 if there is no start day.
     */
    public int getSeasonStartDay()
    {
        if (defaultSeason)
        {
            if (this.defaultStartMonthAndDay == null)
                return 0;

            return this.defaultStartMonthAndDay.getDayOfMonth();
        }

        if (seasonStartDate == null)
            return 0;

        return seasonStartDate.getDayOfMonth();
    }

    /**
     * For either default or fully-specified seasons, returns the season end day, or 0 if there is no end day
     */
    public int getSeasonEndDay()
    {
        if (defaultSeason)
        {
            if (this.defaultEndMonthAndDay == null)
                return 0;

            return this.defaultEndMonthAndDay.getDayOfMonth();
        }

        if (seasonEndDate == null)
            return 0;

        return seasonEndDate.getDayOfMonth();
    }

    /**
     * For either default or fully-specified seasons, returns the season end day, or 0 if there is no end day
     */
    public int getOffSeasonEndDay()
    {
        if (defaultSeason)
            return 0;

        if (offSeasonEndDate == null)
            return 0;

        return offSeasonEndDate.getDayOfMonth();
    }

    /**
     * Get the season start year, or 0 if there is no end year. (Always returns 0 in the case of default seasons)
     */
    public int getSeasonStartYear()
    {
        if (defaultSeason)
            return 0;

        if (seasonStartDate == null)
            return 0;

        return seasonStartDate.getYear();
    }

    /**
     * Get the season end year, or 0 if there is no end year. (Always returns 0 in the case of default seasons)
     */
    public int getSeasonEndYear()
    {
        if (defaultSeason)
            return 0;

        if (seasonEndDate == null)
            return 0;

        return seasonEndDate.getYear();
    }

    /**
     * Get the season end year, or 0 if there is no end year. (Always returns 0 in the case of default seasons)
     */
    public int getOffSeasonEndYear()
    {
        if (defaultSeason)
            return 0;

        if (offSeasonEndDate == null)
            return 0;

        return offSeasonEndDate.getYear();
    }

    @Override
    public String toString()
    {
        return "Season [getSeasonName()=%s, getAssociatedVaccineGroup()=%s, isDefaultSeason()=%s, getSeasonStartMonth()=%d, getSeasonStartDay()=%d, getSeasonStartYear()=%d, getSeasonEndMonth()=%d, getSeasonEndDay()=%d, getSeasonEndYear()=%d, getOffSeasonEndMonth()=%d, getOffSeasonEndDay()=%d, getOffSeasonEndYear()=%d, isDefinedBySeriesTableRules()=%s".formatted(
                getSeasonName(), getVaccineGroup(), isDefaultSeason(), getSeasonStartMonth(), getSeasonStartDay(),
                getSeasonStartYear(), getSeasonEndMonth(), getSeasonEndDay(), getSeasonEndYear(), getOffSeasonEndMonth(),
                getOffSeasonEndDay(), getOffSeasonEndYear(), isDefinedBySeriesTableRules());
    }
}
