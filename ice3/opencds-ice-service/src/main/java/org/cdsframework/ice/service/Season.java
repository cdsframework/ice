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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.MonthDay;

public class Season {

	private String seasonName;
	private LocalDate seasonStartDate;
	private LocalDate seasonEndDate;
	private LocalDate offSeasonEndDate;
	private boolean offSeasonPermitted;
	private boolean defaultSeason;
	private boolean definedBySeriesTableRules;
	private MonthDay defaultStartMonthAndDay;
	private MonthDay defaultEndMonthAndDay;
	private String associatedVaccineGroup;

	private enum SeasonDateType { START, END, OFFSEASON_START, OFFSEASON_END }; 
	private static final Logger logger = LogManager.getLogger();

	
	private Season() {
		// Internally available only
	}
	
	/**
	 * Creates a fully-specified Season with a start and end dates (month/day/year). Off-season end dates permitted by default.
	 * @throws IllegalArgumentException if month/day values are invalid
	 */
	public Season(String seasonName, String svgc, boolean definedBySeriesTableRules, int startMonth, int startDay, int startYear, int endMonth, int endDay, int endYear) {
		
		String _METHODNAME = "Season(): ";
		
		if (svgc == null) {
			String errStr = _METHODNAME + "vaccine group not specified";
			throw new IllegalArgumentException(errStr);
		}
		if (seasonName == null) {
			String errStr = _METHODNAME + "season name not specified";
			throw new IllegalArgumentException(errStr);
		}
		this.seasonName = seasonName;
		this.associatedVaccineGroup = svgc;
		this.definedBySeriesTableRules = definedBySeriesTableRules;
		this.seasonStartDate = new LocalDate(startYear, startMonth, startDay);
		this.seasonEndDate = new LocalDate(endYear, endMonth, endDay);
		this.defaultSeason = false;
		this.offSeasonPermitted = true;
	}
	
	/**
	 * Creates a default Season with a start and end dates (month/day but no year). Off-season end dates permitted by default.
	 * @throws IllegalArgumentException if month/day values are invalid
	 */
	public Season(String seasonName, String svgc, boolean definedBySeriesTableRules, int defaultStartMonth, int defaultStartDay, int defaultEndMonth, int defaultEndDay) {
		
		String _METHODNAME = "Season(): ";
		
		if (svgc == null) {
			String errStr = _METHODNAME + "vaccine group not specified";
			throw new IllegalArgumentException(errStr);
		}
		if (seasonName == null) {
			String errStr = _METHODNAME + "season name not specified";
			throw new IllegalArgumentException(errStr);
		}
		this.seasonName = seasonName;
		this.associatedVaccineGroup = svgc;
		this.definedBySeriesTableRules = definedBySeriesTableRules;
		this.defaultStartMonthAndDay = new MonthDay(defaultStartMonth, defaultStartDay);
		this.defaultEndMonthAndDay = new MonthDay(defaultEndMonth, defaultEndDay);
		this.defaultSeason = true;
		this.offSeasonPermitted = true;
	}
	
	
	/**
	 * Construct deep copy of Season object and return the newly created object to the caller
	 * @return
	 */
	public static Season constructDeepCopyOfSeasonObject(Season pS) {
		
		if (pS == null) {
			return null;
		}
		
		Season lS = new Season();
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
	
	
	public String getSeasonName() {
		return seasonName;
	}
	
	
	public String getVaccineGroup() {
		return associatedVaccineGroup;
	}


	public boolean isDefaultSeason() {
		
		return defaultSeason;
	}
	
	
	public boolean isDefinedBySeriesTableRules() {
		return this.definedBySeriesTableRules;
	}
	
	/**
	 * For fully-specified Seasons, returns whether the season is before the specified season (or on the same date or before is startDateInclusive is set to true). 
	 * If either season is a default season, an IllegalArgumentException is thrown
	 * @param pS
	 * @return
	 */
	public boolean startsBeforeStartDate(Season pS, boolean startDateInclusive) {
		
		int compareTo = compareSeasonDates(pS, SeasonDateType.START);
		if (startDateInclusive) {
			if (compareTo <= 0) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if (compareTo < 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	
	/**
	 * For fully-specified Seasons, returns whether the season is after the specified season (or on the same date or after if startDateInclusive is set to true). 
	 * If either season is a default season, an IllegalArgumentException is thrown
	 * @param pS
	 * @return
	 */
	public boolean startsAfterStartDate(Season pS, boolean startDateInclusive) {
		
		int compareTo = compareSeasonDates(pS, SeasonDateType.START);
		if (startDateInclusive) {
			if (compareTo >= 0) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if (compareTo > 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	
	/**
	 * For fully-specified seasons, returns whether the season start date is equal to the specified season's start date. 
	 * If either season is a default season, an IllegalArgumentException is thrown
	 */
	public boolean startsOnSameStartDate(Season pS) {
		
		int compareTo = compareSeasonDates(pS, SeasonDateType.START);
		if (compareTo == 0) {
			return true;
		}
		else {
			return false;
		}
	}

	
	/**
	 * For fully-specified seasons, returns whether the season ends before the specified season's end date (or on the same date or before is endDateInclusive is set to true). 
	 * If either season is a default season, an IllegalArgumentException is thrown
	 */
	public boolean endsBeforeEndDate(Season pS, boolean endDateInclusive) {
		
		int compareTo = compareSeasonDates(pS, SeasonDateType.END);
		if (endDateInclusive) {
			if (compareTo <= 0) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if (compareTo < 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	/**
	 * For non-default Seasons, returns whether the season is after the specified season's end date (or on the same date or after if endDateInclusive is set to true). 
	 * If either season is a default season or does not have an end date, an IllegalArgumentException is thrown
	 * @param pS
	 * @return
	 */
	public boolean endsAfterEndDate(Season pS, boolean endDateInclusive) {
		
		int compareTo = compareSeasonDates(pS, SeasonDateType.END);
		if (endDateInclusive) {
			if (compareTo >= 0) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if (compareTo > 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	/**
	 * For non-default Seasons, returns whether the season's end date is equal to the specified season's end date. If either season is a default season or does not have an end date, 
	 * an IllegalArgumentException is thrown
	 */
	public boolean endsOnSameEndDate(Season pS) {

		int compareTo = compareSeasonDates(pS, SeasonDateType.END);
		if (compareTo == 0) {
			return true;
		}
		else {
			return false;
		}
	}

	
	/**
	 * If either season is a default season or does not have an end date, an IllegalArgumentException is thrown
	 * TODO: Future support for default seasons
	 */
	private int compareSeasonDates(Season pS, SeasonDateType seasonDateType) {
		
 		String _METHODNAME = "compareToSeasonDate(): ";
		if (seasonDateType == null || pS == null || pS.isDefaultSeason() || this.isDefaultSeason()) {
			String errStr = "one of the seasons is a default season";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);			
		}
	
		if (seasonDateType == SeasonDateType.START) {
			LocalDate startDateThis = this.getFullySpecifiedSeasonStartDate();
			LocalDate startDatePS = pS.getFullySpecifiedSeasonStartDate();
			if (startDateThis == null || startDatePS == null) {
				String errStr = "one or both season start dates not populated";
				logger.warn(_METHODNAME + errStr);
				throw new IllegalArgumentException(errStr);
			}
			return startDateThis.compareTo(startDatePS);
		}		
		else if (seasonDateType == SeasonDateType.END) {
			LocalDate endDateThis = this.getFullySpecifiedSeasonEndDate();
			LocalDate endDatePS = pS.getFullySpecifiedSeasonEndDate();
			if (this.getFullySpecifiedSeasonEndDate() == null || pS.getFullySpecifiedSeasonEndDate() == null) {
				String errStr = "one or both seasons end dates not populated";
				logger.warn(_METHODNAME + errStr);
				throw new IllegalArgumentException(errStr);
			}
			return endDateThis.compareTo(endDatePS);
		}
		else if (seasonDateType == SeasonDateType.OFFSEASON_START) {
			LocalDate offSeasonStartDateThis = this.getFullySpecifiedSeasonOffSeasonStartDate();
			LocalDate offSeasonStartDatePS = pS.getFullySpecifiedSeasonOffSeasonStartDate();
			if (offSeasonStartDateThis == null || offSeasonStartDatePS == null) {
				String errStr = "one or both off-season start dates not populated";
				logger.warn(_METHODNAME + errStr);
				throw new IllegalArgumentException(errStr);
			}
			return offSeasonStartDateThis.compareTo(offSeasonStartDatePS);
		}
		else if (seasonDateType == SeasonDateType.OFFSEASON_END) {
			LocalDate offSeasonEndDateThis = this.getFullySpecifiedSeasonOffSeasonEndDate();
			LocalDate offSeasonEndDatePS = pS.getFullySpecifiedSeasonOffSeasonEndDate();
			if (offSeasonEndDateThis == null || offSeasonEndDatePS == null) {
				String errStr = "one or both off-seasons end dates not populated";
				logger.warn(_METHODNAME + errStr);
				throw new IllegalArgumentException(errStr);
			}
			return offSeasonEndDateThis.compareTo(offSeasonEndDatePS);
		}
		else {
			String errStr = _METHODNAME + "invalid SeasonDateType parameter supplied";
			logger.error(errStr);
			throw new IllegalArgumentException(errStr);
		}
	}
	
	
	public boolean isOffSeasonPermitted() {
		
		return this.offSeasonPermitted;
	}

	
	public void setOffSeasonPermission(boolean truefalse) {
		
		this.offSeasonPermitted = truefalse;
	}

	
	/**
	 * If NOT a default season, returns the season start date as a LocalDate. If a default season, null is returned.
	 */
	public LocalDate getFullySpecifiedSeasonStartDate() {
		
		return seasonStartDate;
	}
	
	
	/**
	 * IfNOT a default season, returns the season end date as a LocalDate. If a default season, null is returned.
	 */
	public LocalDate getFullySpecifiedSeasonEndDate() {
		
		return seasonEndDate;
	}
	
	
	/** 
	 * If this is a fully-specified season and the off-season end date has been previously set, this method returns the off-season start date as one day 
	 * after the season's end date. Otherwise, null is returned. 
	 */
	public LocalDate getFullySpecifiedSeasonOffSeasonStartDate() {
		
		if (seasonEndDate == null || offSeasonEndDate == null) {
			return null;
		}
		
		if (offSeasonEndDate != null) {
			if (offSeasonEndDate.isAfter(seasonEndDate)) {
				return seasonEndDate.plusDays(1);
			}
			else if (offSeasonEndDate.equals(seasonEndDate)) {
				// Season off-season start and off-season end dates are on the same date as the end date. Effectively, no off-season but tracked/recorded
				// this way for internal calculations
				return seasonEndDate;
			}
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
	 * @param month
	 * @param day
	 * @param year
	 * @throws IllegalArgumentException
	 */
	public void setOffSeasonEndDateForFullySpecifiedSeason(int month, int day, int year) {
	
		LocalDate lOffSeasonDate = new LocalDate(year, month, day);
		setOffSeasonEndDateForFullySpecifiedSeason(lOffSeasonDate);
	}

	
	/**
	 * Sets the off-season end date. The off season starts after the end date of the season and ends sometime after that. The start date of the off-season
	 * is always after the end date of the season. Therefore, if there is no end, date, the end date specified here is not after the end date of the season,  
	 * or this is a default season, no action is taken. If the date provided is not a valid date, an IllegalArgumentException is thrown. If 
	 * isOffSeasonEndDatePermitted() is set to false, no action is taken. Thus, it is suggested that the caller verify that off-season dates can be set
	 * by calling isOffSeasonEndDatePermitted() first and setOffSeasonEndDatePermitted() if necessary.
	 */
	public void setOffSeasonEndDateForFullySpecifiedSeason(LocalDate pLD) {
		
		String _METHODNAME = "setOffSeasonEndDate(LocalDate): ";
		
		if (pLD == null) {
			return;
		}
		if (this.seasonEndDate == null || this.isDefaultSeason() == true || isOffSeasonPermitted() == false) {
			String errStr = "Cannot add an off-season end date to a default season, to a season that does not have an end date, or isOffSeasonEndDatePermitted() is false";
			logger.warn(_METHODNAME + errStr);
			return;
		}
		int compareTo = pLD.compareTo(this.seasonEndDate);
		if (compareTo < 0) {
			String errStr = "Cannot specify an off-season end date that is before the season end date (end date: " + seasonEndDate + "; specified off-season date: " + pLD;
			logger.warn(_METHODNAME + errStr);
			return;
		}
		
		offSeasonEndDate = pLD;
	}
	
	public LocalDate getFullySpecifiedSeasonOffSeasonEndDate() {
		
		return offSeasonEndDate;
	}
	
	
	/**
	 * If a default season, returns the season start date as a MonthDay. Otherwise null is returned. 
	 */
	public MonthDay getDefaultSeasonStartMonthAndDay() {
		
		return defaultStartMonthAndDay;
	}
	
	/**
	 * If a default season and an end month/day is defined, returns the season end date as a MonthDay. 
	 */
	public MonthDay getDefaultSeasonEndMonthAndDay() {
		
		return defaultEndMonthAndDay;
	}

	/**
	 * Returns a full (non-default) Season that applies to the provided date. If the season parameter provided in the call not a default season, it
	 * is simply returned as is. If the date is not provided, null is returned. 
	 * @param pSeason a default Season
	 * @param pApplicableDate 
	 * @return
	 */
	public static Season constructFullySpecifiedSeasonFromDefaultSeasonAndDate(Season pSeason, Date applicableDate) {
		
		// String _METHODNAME = "constructFullySpecifiedSeasonFromDefaultSeasonAndDate(): ";
		
		if (pSeason == null || applicableDate == null) {
			return null;
		}
		
		if (pSeason.isDefaultSeason() == false) {
			return pSeason;
		}

		LocalDate requestDate = new LocalDate(applicableDate);
		int requestYear = requestDate.getYear();
		int requestMonth = requestDate.getMonthOfYear();
		int defaultStartMonth = pSeason.defaultStartMonthAndDay.getMonthOfYear();
		int defaultStartDay = pSeason.defaultStartMonthAndDay.getDayOfMonth();
		int defaultEndMonth = pSeason.defaultEndMonthAndDay.getMonthOfYear();
		int defaultEndDay = pSeason.defaultEndMonthAndDay.getDayOfMonth();
		
		int startYear = requestYear;
		int endYear = requestYear;
		
		MonthDay applicableDateMonthDay = new MonthDay(applicableDate);
		if (monthAndDayFallsWithinRange(requestMonth, requestDate.getDayOfMonth(), defaultStartMonth, defaultStartDay, defaultEndMonth, defaultEndDay)) {
			if (applicableDateMonthDay.compareTo(pSeason.defaultEndMonthAndDay) <= 0) {
				startYear--;
			}
			else if (pSeason.defaultEndMonthAndDay.isBefore(pSeason.defaultStartMonthAndDay)) {
				endYear++;
			}
		}
		else {
			if (pSeason.defaultEndMonthAndDay.isBefore(pSeason.defaultStartMonthAndDay))
				startYear--;
		}

		String seasonName = pSeason.getSeasonName();
		if (seasonName == null) {
			seasonName = "automated";
		}
		Season full = new Season(seasonName, pSeason.associatedVaccineGroup, pSeason.isDefinedBySeriesTableRules(), defaultStartMonth, defaultStartDay, startYear, defaultEndMonth, defaultEndDay, endYear);
				
		return full;
	}
	

	/**
	 * Returns true if the seasons START dates are equivalent (same vaccine group, and end month/day/year or end month/day), or false if not.
	 * This method is applicable to both fully-specified seasons as well as default seasons. Thus, if either or both seasons are a default season, 
	 * the year is not checked and equivalence is based on month and day only.
	 */
	public boolean seasonsHaveEquivalentStartDates(Season pS) {
		
		if (pS == null) {
			return false;
		}
		
		if (this.associatedVaccineGroup != pS.associatedVaccineGroup) {
			return false;
		}
		if (getSeasonStartMonth() != pS.getSeasonStartMonth() && getSeasonStartDay() != pS.getSeasonStartDay()) {
			return false;
		}
		if (! pS.isDefaultSeason() && ! isDefaultSeason()) {
			if (getSeasonStartYear() != pS.getSeasonStartYear()) {
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * Returns true if the seasons END dates are equivalent (same vaccine group, and end month/day/year or end month/day), or false if not.
	 * This method is applicable to both fully-specified seasons as well as default seasons. Thus, if either or both seasons are a default season, 
	 * the year is not checked and equivalence is based on month and day only.
	 */
	public boolean seasonsHaveEquivalentEndDates(Season pS) {
		
		if (pS == null) {
			return false;
		}
		
		if (this.associatedVaccineGroup != pS.associatedVaccineGroup) {
			return false;
		}
		if (getSeasonEndMonth() != pS.getSeasonEndMonth() && getSeasonEndDay() != pS.getSeasonEndDay()) {
			return false;
		}
		if (! pS.isDefaultSeason() && ! isDefaultSeason()) {
			if (getSeasonEndYear() != pS.getSeasonEndYear()) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns true if the seasons are equivalent (same vaccine group, and start & end month/day/year or start & end month/day), or false if not.
	 * This method is applicable to both fully-specified seasons as well as default seasons. Thus, if either or both seasons are a default season, 
	 * the year is not checked and equivalence is based on month and day only.
	 */
	public boolean seasonsHaveEquivalentStartAndEndDates(Season pS) {
		
		if (seasonsHaveEquivalentStartDates(pS) && seasonsHaveEquivalentEndDates(pS)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Date falls within the off-season
	 */
	public boolean dateIsApplicableToOffSeason(Date pDate) {
		
		if (pDate == null) {
			return false;
		}
		
		LocalDate lDate = LocalDate.fromDateFields(pDate);
		if (isDefaultSeason()) {
			return ! monthAndDayFallsWithinRange(lDate.getMonthOfYear(), lDate.getDayOfMonth(), this.getDefaultSeasonStartMonthAndDay(), this.getDefaultSeasonEndMonthAndDay());
		}
		else {
			LocalDate lOffSeasonStartDate = getFullySpecifiedSeasonOffSeasonStartDate();
			if (this.offSeasonPermitted && this.offSeasonEndDate != null && lOffSeasonStartDate != null) {
				if (this.offSeasonEndDate.equals(lOffSeasonStartDate)) {
					return false;
				}
				else {
					if (lDate.compareTo(this.offSeasonEndDate) <= 0 && lDate.compareTo(lOffSeasonStartDate) >= 0) {
						return true;
					}
					else {
						return false;
					}
				}
			}
			else {
				return false;
			}
		}
	}

	/**
	 * Overload to dateIsApplicableToSeason(Date, boolean), with the 2nd parameter set to true.
	 */
	public boolean dateIsApplicableToSeason(Date pDate) {
		
		return dateIsApplicableToSeason(pDate, true);
	}
	
	
	/**
	 * If the date falls between the season start date and off-season end date (inclusive), return true, else false. If the date is on or after season start date, the includeOffSeason
	 * is set to true, and there is no off-season end date, true is returned. 
	 * @param pDate
	 * @param includeOffSeason
	 * @return
	 */
	public boolean dateIsApplicableToSeason(Date pDate, boolean includeOffSeason) {
		
		if (pDate == null) {
			return false;
		}

		return dateIsApplicableToSeason(LocalDate.fromDateFields(pDate), includeOffSeason);
	}
	
	
	/**
	 * Check to see if the date falls within the start and the end dates of the season. 
	 * (1) If an off-season end date has been defined and the includeOffSeason parameter is set to true, then this method checks if the date falls between the start date 
	 * and off-season end date. Otherwise, it checks is the date falls between the start date and regular season end date. 
	 * (2) If the season is a default season, returns true always if off-season should be included. Otherwise checks to see if the date falls between the start date and end date.
	 */
	private boolean dateIsApplicableToSeason(LocalDate pDate, boolean includeOffSeason) {

		
		String _METHODNAME = "dateIsApplicableToSeason(LocalDate, boolean): ";
		if (pDate == null) {
			return false;
		}

		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Parameters: " + pDate.toString() + "; " + includeOffSeason);
		}

		if (! isDefaultSeason()) {
			if (logger.isDebugEnabled()) {
				logger.debug("is NOT a default season" + this.toString());
			}
			LocalDate endDateToUse = (includeOffSeason == true && offSeasonEndDate != null) ? offSeasonEndDate : seasonEndDate;
			if (logger.isDebugEnabled()) {
				logger.debug("seasonStartDate: " + seasonStartDate + "; endDateToUse: " + endDateToUse);
			}
			if (pDate.compareTo(seasonStartDate) >= 0 && pDate.compareTo(endDateToUse) <= 0) {
				if (logger.isDebugEnabled()) {
					logger.debug(_METHODNAME + "returning true");
				}
				return true;
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug(_METHODNAME + "returning false");
				}
				return false;
			}
		}
		else if (isDefaultSeason()) {
			if (logger.isDebugEnabled()) {
				logger.debug("is a default season" + this.toString());
			}
			if (includeOffSeason) {
				return true;
			}
			else {
				return monthAndDayFallsWithinRange(pDate.getMonthOfYear(), pDate.getDayOfMonth(), this.getDefaultSeasonStartMonthAndDay(), this.getDefaultSeasonEndMonthAndDay());
			}
		}
		else {
			return false;
		}
		
	}
	
	
	/**
	 * Checks if the supplied month and day falls within the range of the specified start month/day and end month/day, inclusive. If any of the month/day combinations are invalid, 
	 * an IllegalArgumentException is thrown.
	 */
	public static boolean monthAndDayFallsWithinRange(int month, int day, MonthDay rangeStart, MonthDay rangeEnd) {
	
		String _METHODNAME = "monthAndDayFallsWithinRange(): ";
		
		if (rangeStart == null || rangeEnd == null) {
			String errStr = "Invalid rangeStart or rangeEnd parameter supplied";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		
		new MonthDay(month, day);		// Check that month and day values sent are valid; MonthDay throws an exception if not
		
		// Only compare with respect to month and day. Determine set of relevant months of pS Season;
		Set<Integer> relevantMonthsOfPS = new HashSet<Integer>();
		int rangeStartMonth = rangeStart.getMonthOfYear();
		int rangeStartDay = rangeStart.getDayOfMonth();
		int rangeEndMonth = rangeEnd.getMonthOfYear();
		int rangeEndDay = rangeEnd.getDayOfMonth();
		if (rangeStartMonth > rangeEndMonth) {
			relevantMonthsOfPS.add(new Integer(rangeEndMonth));
			int i = rangeStartMonth;
			while (i != rangeEndMonth) {
				relevantMonthsOfPS.add(new Integer(i));
				if (i < 12) {
					i++;
				}
				else {
					i = 1;
				}
			}
		}
		else if (rangeStartMonth <= rangeEndMonth) {
			int i = rangeStartMonth;
			while (i <= rangeEndMonth) {
				relevantMonthsOfPS.add(new Integer(i));
				i++;
			}
		}
		
		// If both the start and end months fall outside of the list of relevant months, then return false (seasons do not overlap). If either or 
		// both of the start/end months are on the same month, check that the start day is on/after pS or the end day is on/before pS's end day
		boolean fallsWithinStartRange = false;
		boolean fallsWithinEndRange = false;
		if (relevantMonthsOfPS.contains(new Integer(month))) {
			if (month == rangeStartMonth) {
				if (day >= rangeStartDay) {
					fallsWithinStartRange = true;
				}
			}
			else {
				fallsWithinStartRange = true;
			}

			if (month == rangeEndMonth) {
				if (day <= rangeEndDay) {
					fallsWithinEndRange = true;
				}
			}
			else {
				fallsWithinEndRange = true;
			}				
		}
		
		if (fallsWithinStartRange && fallsWithinEndRange) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * Checks if the supplied month and day falls within the range of the specified start month/day and end month/day, inclusive. If any of the month/day combinations are invalid, 
	 * an IllegalArgumentException is thrown.
	 *  
	 * @param month
	 * @param day
	 * @param rangeStartMonth
	 * @param startDayToFallOnOrAfter
	 * @param endMonthToFallOnOrBefore
	 * @param endDayToFallOnOrBefore
	 * @return
	 */
	public static boolean monthAndDayFallsWithinRange(int month, int day, int rangeStartMonth, int rangeStartDay, int rangeEndMonth, int rangeEndDay) {
	
		MonthDay rangeStart = new MonthDay(rangeStartMonth, rangeStartDay);
		MonthDay rangeEnd = new MonthDay(rangeEndMonth, rangeEndDay);
		return monthAndDayFallsWithinRange(month, day, rangeStart, rangeEnd);
	}
	
	
	/**
	 * Return true of season dates (start and end) intersect in any way; false if they do not. If either of the seasons is a default season, only the month/day are taken into account.
	 * Otherwise, the fully-specified month/date/year is taken into consideration. If one of the fully-specified seasons starts before the other and it does not have an end date, then
	 * they will be considered to be overlapping. Off-season start/end dates, since they are typically calculated for seasons in reference to one another, are not taken into consideration. 
	 */
	public boolean seasonOverlapsWith(Season pS) {
	
		if (pS == null) {
			return false;
		}
		
		if (this.isDefaultSeason() || pS.isDefaultSeason()) {
			// Only compare with respect to month and day. Determine set of relevant months of pS Season;
			int pSStartMonth = pS.getSeasonStartMonth();
			int pSEndMonth = pS.getSeasonEndMonth();
			Set<Integer> relevantMonthsOfPS = new HashSet<Integer>();
			if (pSStartMonth > pSEndMonth) {
				relevantMonthsOfPS.add(new Integer(pSEndMonth));
				int i = pSStartMonth;
				while (i != pSEndMonth) {
					relevantMonthsOfPS.add(new Integer(i));
					if (i < 12) {
						i++;
					}
					else {
						i = 1;
					}
				}
			}
			else if (pSStartMonth <= pSEndMonth) {
				int i = pSStartMonth;
				while (i <= pSEndMonth) {
					relevantMonthsOfPS.add(new Integer(i));
					i++;
				}
			}
			// If both the start and end months fall outside of the list of relevant months, then return false (seasons do not overlap). If either or 
			// both of the start/end months are on the same month, check that the start day is on/after pS or the end day is on/before pS's end day
			if (relevantMonthsOfPS.contains(new Integer(this.getSeasonStartMonth()))) {
				if (this.getSeasonStartMonth() == pS.getSeasonStartMonth()) {
					if (this.getSeasonStartDay() >= pS.getSeasonStartDay()) {
						return true;
					}
				}
				else {
					return true;
				}
			}
			if (relevantMonthsOfPS.contains(new Integer(this.getSeasonEndMonth()))) {
				if (this.getSeasonEndMonth() == pS.getSeasonEndMonth()) {
					if (this.getSeasonEndDay() <= pS.getSeasonEndDay()) {
						return true;
					}
				}
				else {
					return true;
				}				
			}
		}
		else {
			// Both Seasons are fully specified season
			LocalDate seasonStartThis = this.getFullySpecifiedSeasonStartDate();
			LocalDate seasonStartPS = pS.getFullySpecifiedSeasonStartDate();
			
			Season priorSeason = null;
			Season laterSeason = null;
			if (seasonStartThis.compareTo(seasonStartPS) <= 0) {
				priorSeason = this;
				laterSeason = pS;
			}
			else {
				priorSeason = pS;
				laterSeason = this;
			}
			
			if (priorSeason.getFullySpecifiedSeasonStartDate().compareTo(laterSeason.getFullySpecifiedSeasonStartDate()) >= 0 ||
					priorSeason.getFullySpecifiedSeasonEndDate() == null || priorSeason.getFullySpecifiedSeasonEndDate().compareTo(laterSeason.getFullySpecifiedSeasonStartDate()) >= 0) {
				return true;
			}
		}
		
		// Seasons do not overlap
		return false;
	}

	
	/**
	 * Returns the season start month, or 0 if there is no start month.
	 */
	public int getSeasonStartMonth() {
		
		if (defaultSeason) {
			if (this.defaultStartMonthAndDay == null) {
				return 0;
			}
			return this.defaultStartMonthAndDay.getMonthOfYear();
		}
		else {
			if (seasonStartDate == null) {
				return 0;
			}
			return this.seasonStartDate.getMonthOfYear();
		}
	}

	
	/**
	 * For either default or fully-specified seasons, returns the season end month, or 0 if there is no end month
	 */
	public int getSeasonEndMonth() {
		
		if (defaultSeason) {
			if (this.defaultEndMonthAndDay == null) {
				return 0;
			}
			return this.defaultEndMonthAndDay.getMonthOfYear();
		}
		else {
			if (seasonEndDate == null) {
				return 0;
			}
			return this.seasonEndDate.getMonthOfYear();
		}
	}

	
	/**
	 * For either default or fully-specified seasons, returns the season end month, or 0 if there is no end month
	 */
	public int getOffSeasonEndMonth() {
		
		if (defaultSeason) {
			return 0;
		}
		else {
			if (offSeasonEndDate == null) {
				return 0;
			}
			return this.offSeasonEndDate.getMonthOfYear();
		}
	}
	
	
	/**
	 * For either default or fully-specified seasons, returns the season start date, or 0 if there is no start day.
	 */
	public int getSeasonStartDay() {
		
		if (defaultSeason) {
			if (this.defaultStartMonthAndDay == null) {
				return 0;
			}
			return this.defaultStartMonthAndDay.getDayOfMonth();
		}
		else {
			if (seasonStartDate == null) {
				return 0;
			}
			return seasonStartDate.getDayOfMonth();
		}
	}
	
	
	/**
	 * For either default or fully-specified seasons, returns the season end day, or 0 if there is no end day
	 */
	public int getSeasonEndDay() {
		
		if (defaultSeason) {
			if (this.defaultEndMonthAndDay == null) {
				return 0;
			}
			return this.defaultEndMonthAndDay.getDayOfMonth();
		}
		else {
			if (seasonEndDate == null) {
				return 0;
			}
			return seasonEndDate.getDayOfMonth();
		}
	}
	
	
	/**
	 * For either default or fully-specified seasons, returns the season end day, or 0 if there is no end day
	 */
	public int getOffSeasonEndDay() {
		
		if (defaultSeason) {
			return 0;
		}
		else {
			if (offSeasonEndDate == null) {
				return 0;
			}
			return offSeasonEndDate.getDayOfMonth();
		}
	}
	
	
	/**
	 * Get the season start year, or 0 if there is no end year. (Always returns 0 in the case of default seasons)
	 */
	public int getSeasonStartYear() {
				
		if (defaultSeason) {
			return 0;
		}
		else {
			if (seasonStartDate == null) {
				return 0;
			}
			return seasonStartDate.getYear();
		}
	}
	
	
	/**
	 * Get the season end year, or 0 if there is no end year. (Always returns 0 in the case of default seasons)
	 */
	public int getSeasonEndYear() {
		
		if (defaultSeason) {
			return 0;
		}
		else {
			if (seasonEndDate == null) {
				return 0;
			}
			return seasonEndDate.getYear();
		}
	}
	

	/**
	 * Get the season end year, or 0 if there is no end year. (Always returns 0 in the case of default seasons)
	 */
	public int getOffSeasonEndYear() {
		
		if (defaultSeason) {
			return 0;
		}
		else {
			if (offSeasonEndDate == null) {
				return 0;
			}
			return offSeasonEndDate.getYear();
		}
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((associatedVaccineGroup == null) ? 0
						: associatedVaccineGroup.hashCode());
		result = prime
				* result
				+ ((defaultEndMonthAndDay == null) ? 0 : defaultEndMonthAndDay
						.hashCode());
		result = prime * result + (defaultSeason ? 1231 : 1237);
		result = prime
				* result
				+ ((defaultStartMonthAndDay == null) ? 0
						: defaultStartMonthAndDay.hashCode());
		result = prime * result
				+ ((seasonEndDate == null) ? 0 : seasonEndDate.hashCode());
		result = prime * result
				+ ((seasonStartDate == null) ? 0 : seasonStartDate.hashCode());
		return result;
	}

	/**
	 * Tests for "equivalent" equality between 2 Season objects. Because off-season begin/end dates are calculated, off-season begin/end dates are not included in this equality test.
	 * @return true if the objects are equal, false if they are not
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Season other = (Season) obj;
		if (associatedVaccineGroup != other.associatedVaccineGroup)
			return false;
		if (defaultEndMonthAndDay == null) {
			if (other.defaultEndMonthAndDay != null)
				return false;
		} else if (!defaultEndMonthAndDay.equals(other.defaultEndMonthAndDay))
			return false;
		if (defaultSeason != other.defaultSeason)
			return false;
		if (defaultStartMonthAndDay == null) {
			if (other.defaultStartMonthAndDay != null)
				return false;
		} else if (!defaultStartMonthAndDay
				.equals(other.defaultStartMonthAndDay))
			return false;
		if (seasonEndDate == null) {
			if (other.seasonEndDate != null)
				return false;
		} else if (!seasonEndDate.equals(other.seasonEndDate))
			return false;
		if (seasonStartDate == null) {
			if (other.seasonStartDate != null)
				return false;
		} else if (!seasonStartDate.equals(other.seasonStartDate))
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "Season [getSeasonName()=" + getSeasonName() + 
				", getAssociatedVaccineGroup()=" + getVaccineGroup() + 
				", isDefaultSeason()=" + isDefaultSeason() + 
				", getSeasonStartMonth()=" + getSeasonStartMonth() + 
				", getSeasonStartDay()=" + + getSeasonStartDay() +
				", getSeasonStartYear()=" + getSeasonStartYear() + 
				", getSeasonEndMonth()=" + getSeasonEndMonth() +
				", getSeasonEndDay()=" + getSeasonEndDay() +
				", getSeasonEndYear()=" + getSeasonEndYear() + 
				", getOffSeasonEndMonth()=" + getOffSeasonEndMonth() + 
				", getOffSeasonEndDay()=" + getOffSeasonEndDay() + 
				", getOffSeasonEndYear()=" + getOffSeasonEndYear();
	}

}
