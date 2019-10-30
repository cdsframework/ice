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

package org.cdsframework.ice.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.opencds.common.exceptions.ImproperUsageException;


public class TimePeriod {
	
	private static final String TimePeriodStringFormat = "([-|+]?[ ]*[0-9]+[Yy])?([ ]*[-|+]?[ ]*[0-9]+[Mm])?([ ]*[-|+]?[ ]*[0-9]+[Ww])?([ ]*[-|+]?[ ]*[0-9]+[Dd])?";
	private static Log logger = LogFactory.getLog(TimePeriod.class);

	public enum DurationType {
		DAYS, MONTHS, WEEKS, YEARS
	}
	
	private DurationType durationType;
	private int duration;
	// private boolean isInclusive;
	private boolean timePeriodSet = false; 
	private String timePeriodRepresentation;
	
	
	private TimePeriod() {
		// Privately available
	}

	/**
	 * Create a new TimePeriod by specifying duration value and type. By using this, it is not possible to specify a combination TimePeriod with more than one duration type. i.e. -
	 * to specify a TimePeriod of 1 year, 5 months and 4 days, use TimePeriod(String). TimePeriods are immutable; once set, it is not possible to change it. 
	 * @param pDuration
	 * @param pDurationType
	 */
	public TimePeriod(int pDuration, DurationType pDurationType) {
		durationType = pDurationType;
		duration = pDuration;
		// isInclusive = false;
		timePeriodSet = true;
		setTimePeriod(determineTimePeriodStringRepresentationFromDurationValues());
	}
	

	/**
	 * Create a TimePeriod. TimePeriod is expressed as years, months and days. Each unit is optional.  Examples: "1y", "1m", "1d", "1y1m" "1y+5m+4d" is the same as "1y5m4d".
	 * Months and days can be subtracted, as follows: 1y-4m-4d. TimePeriods can be negative: "-4d", "-1y+4d", etc. No spaces between units.
	 * TimePeriods are immutable; once set, it is not possible to change it. 
	 * @param pTimePeriodStr
	 * @throws IllegalArgumentException if the TimePeriod argument is not in the correct format.
	 */
	public TimePeriod(String pTimePeriodStr) {
				
		setTimePeriod(pTimePeriodStr);
	}
	
	/**
	 * Construct deep copy of TimePeriod object and return it to the caller
	 * @return duplicated TimePeriod object
	 */
	public static TimePeriod constructDeepCopyOfTimePeriodObject(TimePeriod pTP) {
		
		if (pTP == null) {
			return pTP;
		}
		
		TimePeriod lTP = new TimePeriod();
		lTP.durationType = pTP.durationType;
		lTP.duration = pTP.duration;
		lTP.timePeriodSet = pTP.timePeriodSet;
		lTP.timePeriodRepresentation = pTP.timePeriodRepresentation;
		return lTP;
	}
	
	private int getDuration() {
		return duration;
	}
	
	
	private DurationType getDurationType() {
		return durationType;
	}

	/**
	 * Returns true if this TimePeriod has a time period value, and the DurationType is not UNSPECIFIED
	 */
	public boolean isTimePeriodSet() {
		return timePeriodSet;
	}

	/**
	 * Returns true if the String argument is in the correct TimePeriod string format, false if it is not.
	 */
	public static boolean isTimePeriodStringInCorrectFormat(String pTimePeriodStr) {
		
		if (pTimePeriodStr == null) {
			return false;
		}
		if (!pTimePeriodStr.matches(TimePeriodStringFormat)) {
			return false;
		}
		else {
			return true;
		}
	}

	/**
	 * Returns true if the time period is a negative duration (i.e.- prefixed with a minus sign); false if not. Throws an IllegalArgumentException if the TimePeriod 
	 * argument passed in is not in the correct TimePeriod string format. 
	 * @param pTimePeriod
	 * @return
	 */
	public static boolean isTimePeriodANegativeDuration(String pTimePeriodStr) {
		
		String _METHODNAME = "isTimePeriodANegativeDuration(): ";
		if (isTimePeriodStringInCorrectFormat(pTimePeriodStr) == false) {
			String lWarnStr = "TimePeriod argument is in the incorrect format: " + pTimePeriodStr;
			if (logger.isDebugEnabled()) {
				logger.debug(_METHODNAME + lWarnStr);
			}
			throw new IllegalArgumentException(lWarnStr);
		}
		
		if (pTimePeriodStr.startsWith("-")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public String getTimePeriodStringRepresentation() {
		
		return this.timePeriodRepresentation;
	}
	
	
	private void setTimePeriod(String pTimePeriodStr) {
		
		String _METHODNAME = "setTimePeriod()";
		if (isTimePeriodStringInCorrectFormat(pTimePeriodStr) == false) {
			String str = "TimePeriod string \"" + pTimePeriodStr + "\" does not match correct pattern: e.g. - 1y10m12d";
			logger.error(_METHODNAME + str);
			throw new IllegalArgumentException(str);
		}
		
		this.timePeriodRepresentation = pTimePeriodStr;
		this.timePeriodSet = true;
	}

	
	/**
	 * Return TimePeriod in string format year, month, or day. e.g. - "4y", "5m", "6d". 
	 * @return String If DurationType is not of type DAYS, MONTHS, WEEKS or YEARS, null is returned.
	 */
	private String determineTimePeriodStringRepresentationFromDurationValues() {
		
		String durationTypeStr = null;
		switch(durationType) {
		case DAYS:
			durationTypeStr = "d";
			break;
		case WEEKS:
			durationTypeStr = "w";
			break;
		case MONTHS:
			durationTypeStr = "m";
			break;
		case YEARS:
			durationTypeStr = "y";
			break;
		default:
			return null;
		}
		
		return duration + durationTypeStr;
	}
	
	public static boolean isValidMonth(int month) {
		if (month > 0 && month < 13) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static int numberOfDaysInMonth(int year, int month) {
		DateTime dateTime = new DateTime(year, month, 14, 12, 0, 0, 000);
		return dateTime.dayOfMonth().getMaximumValue();
	}
	
	public static int differenceInDays(Date startDate, Date endDate) {

		if (startDate == null || endDate == null) {
			return 0;
		}
		DateTime dtStart = new DateTime(startDate);
		DateTime dtEnd = new DateTime(endDate);
		Days d = Days.daysBetween(dtStart, dtEnd);
		return d.getDays();	
	}
	
	public static int differenceInMonths(Date startDate, Date endDate) {

		if (startDate == null || endDate == null) {
			return 0;
		}
		DateTime dtStart = new DateTime(startDate);
		DateTime dtEnd = new DateTime(endDate);
		Months m = Months.monthsBetween(dtStart, dtEnd);
		return m.getMonths();
	}

	public static int differenceInWeeks(Date startDate, Date endDate) {

		if (startDate == null || endDate == null) {
			return 0;
		}
		DateTime dtStart = new DateTime(startDate);
		DateTime dtEnd = new DateTime(endDate);
		Weeks w = Weeks.weeksBetween(dtStart, dtEnd);
		return w.getWeeks();
	}

	public static int differenceInYears(Date startDate, Date endDate) {

		if (startDate == null || endDate == null) {
			return 0;
		}
		DateTime dtStart = new DateTime(startDate);
		DateTime dtEnd = new DateTime(endDate);
		Years y = Years.yearsBetween(dtStart, dtEnd);
		return y.getYears();
	}

	/**
	 * Adds the specified amount of time to the supplied date and returns a new date the TimePeriod duration to the supplied date
	 * @param startDate
	 * @param pTP a TimePeriod where DurationType is either DAYS, WEEKS, MONTHS or YEARS. If the durationType is not set, this method returns the startDate. 
	 * If TimePeriod has a negative duration, it is substracted from the startDate
	 * @return a new data representing the supplied date plus the TimePeriod; startDate if the supplied TimePeriod is null
	 * @throws IllegalArgumentException if DurationType is not one of the above
	 */
	private static Date addTimePeriodUsingDurationValues(Date startDate, TimePeriod pTP) {

		String _METHODNAME = "addTimePeriod(): ";

		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Start date: " + startDate + "; TimePeriod: " + pTP);
		}
		
		if (startDate == null) {
			return null;
		}
		if (pTP == null) {
			return startDate;
		}

		int duration = pTP.getDuration();		
		LocalDate startLD = new LocalDate(startDate);
		DurationType tpType = pTP.getDurationType();

		if (tpType == DurationType.DAYS) {
			startLD = startLD.plusDays(duration);
		}
		else if (tpType == DurationType.MONTHS) {
			int dayOfMonthBeforeCalculation = startLD.getDayOfMonth();
			startLD = startLD.plusMonths(duration);
			if (startLD.getDayOfMonth() < dayOfMonthBeforeCalculation && startLD.isEqual(startLD.dayOfMonth().withMaximumValue())) {
				startLD = startLD.plusDays(1);
			}
		}
		else if (tpType == DurationType.WEEKS) {
			startLD = startLD.plusWeeks(duration);
		}
		else if (tpType == DurationType.YEARS) {
			int dayOfMonthBeforeCalculation = startLD.getDayOfMonth();
			startLD = startLD.plusYears(duration);
			if (startLD.getDayOfMonth() < dayOfMonthBeforeCalculation && startLD.isEqual(startLD.dayOfMonth().withMaximumValue())) {
				startLD = startLD.plusDays(1);
			}
		}
		else {
			String str = "Invalid TimePeriod.DurationType supplied";
			logger.warn(_METHODNAME + str);
			throw new IllegalArgumentException(str);
		}
		
		return startLD.toDate();
	}
	
	
	/**
	 * Add the TimePeriod to the supplied Date. If TimePeriod is negative for any unit, that unit is subtracted
	 * @param startDate
	 * @param pTP
	 * @return a new date representing the supplied date plus the TimePeriod; startDate if the supplied TimePeriod is null
	 */
	public static Date addTimePeriod(Date startDate, TimePeriod pTP) {
		
		return addTimePeriod(startDate, pTP.getTimePeriodStringRepresentation());
		
	}
	
	
	/**
	 * Add the TimePeriod duration to the supplied date
	 * @param startDate
	 * @param pTimePeriodStr in the format ymd (e.g. "5y", "1m", "4d", "5y4m1d" is the same as "5y+4m+1d", "5y-4d" is allowed, etc.). If TimePeriod is negative, 
	 * it is subtracted from the startDate
	 * @return a new date representing the supplied date plus the TimePeriod; startDate if the supplied TimePeriod is null
	 * @throws IllegalArgumentException if TimePeriod representation has format errors
	 */
	public static Date addTimePeriod(Date startDate, String pTimePeriodStr) {
		
		String _METHODNAME = "addTimePeriod(Date, String): ";
		
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Start date: " + startDate + "; TimePeriod Str: " + pTimePeriodStr);
		}
		
		if (startDate == null) {
			return null;
		}
		if (pTimePeriodStr == null) {
			return startDate;
		}
		if (isTimePeriodStringInCorrectFormat(pTimePeriodStr) == false) {
			String str = "TimePeriod string \"" + pTimePeriodStr + "\" does not match correct pattern: e.g. - 1y10m12d";
			logger.error(_METHODNAME + str);
			throw new IllegalArgumentException(str);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("TimePeriod String Supplied: " + pTimePeriodStr);
		}
		
		TimePeriod tp = null;
		Date interimDate = startDate;
		StringBuffer token = new StringBuffer();
		for (int i = 0; i < pTimePeriodStr.length(); i++){
			char c = pTimePeriodStr.charAt(i);
			if (c == ' ') {
				continue;
			}
			else if (c == 'd' || c == 'D') {
				if (token.length() == 0) {
					continue;
				}
				if (token.charAt(0) == '+') {
					token.deleteCharAt(0);
				}
				tp = new TimePeriod(new Integer(new String(token)).intValue(), DurationType.DAYS);
				if (logger.isDebugEnabled()) {
					logger.debug("TimePeriod: " + tp.toString() + " to be added to interimDate: " + interimDate);
				}
				interimDate = addTimePeriodUsingDurationValues(interimDate, tp);
				if (logger.isDebugEnabled()) {
					logger.debug(_METHODNAME + "New interimDate: " + interimDate);
				}
				token = new StringBuffer();
			}
			else if (c == 'w' || c == 'W') {
				if (token.length() == 0) {
					continue;
				}
				if (token.charAt(0) == '+') {
					token.deleteCharAt(0);
				}
				tp = new TimePeriod(new Integer(new String(token)).intValue(), DurationType.WEEKS);
				if (logger.isDebugEnabled()) {
					logger.debug(_METHODNAME + "TimePeriod: " + tp.toString() + " to be added to interimDate: " + interimDate);
				}
				interimDate = addTimePeriodUsingDurationValues(interimDate, tp);
				if (logger.isDebugEnabled()) {
					logger.debug(_METHODNAME + "New interimDate: " + interimDate);
				}
				token = new StringBuffer();
			}
			else if (c == 'm' || c == 'M') {
				if (token.length() == 0) {
					continue;
				}
				if (token.charAt(0) == '+') {
					token.deleteCharAt(0);
				}
				tp = new TimePeriod(new Integer(new String(token)).intValue(), DurationType.MONTHS);
				if (logger.isDebugEnabled()) {
					logger.debug(_METHODNAME + "TimePeriod: " + tp.toString() + " to be added to interimDate: " + interimDate);
				}
				interimDate = addTimePeriodUsingDurationValues(interimDate, tp);
				if (logger.isDebugEnabled()) {
					logger.debug(_METHODNAME + "New interimDate: " + interimDate);
				}
				token = new StringBuffer();
			}
			else if (c == 'y' || c == 'Y') {
				if (token.length() == 0) {
					continue;
				}
				if (token.charAt(0) == '+') {
					token.deleteCharAt(0);
				}
				tp = new TimePeriod(new Integer(new String(token)).intValue(), DurationType.YEARS);
				if (logger.isDebugEnabled()) {
					logger.debug(_METHODNAME + "TimePeriod: " + tp.toString() + " to be added to interimDate: " + interimDate);
				}
				interimDate = addTimePeriodUsingDurationValues(interimDate, tp);
				if (logger.isDebugEnabled()) {
					logger.debug(_METHODNAME + "New interimDate: " + interimDate);
				}
				token = new StringBuffer();
			}
			else {
				token.append(c);
			}
		}
		
		return interimDate;
	}

	/**
	 * Calculate time period in whole units. e.g. 45 days = 1 month; 364 days = 0 years; 9 days = 1 week
	 * @param pD1
	 * @param pD2
	 * @param pDurationType
	 * @return
	 * @throws ImproperUsageException
	 */
	public static TimePeriod calculateElapsedTimePeriod(Date pD1, Date pD2, DurationType pDurationType) 
		throws TimePeriodException {
		
		return calculateElapsedTimePeriod(pD1, pD2, pDurationType, false);
	}
	

	/**
	 * 
	 * @param pD1
	 * @param pD2
	 * @param pDurationType
	 * @param absoluteValue If true, will ensure that the elapsed time returned is always positive; that is, a TimePeriod with a negative elapsed time value will not be returned
	 * if pD1 is a later date than pD2
	 * @return TimePeriod representing elapsed time period in the specified 
	 * @throws IllegalArgumentException if one or more of the supplied date parameters are invalid (e.g. - null)
	 * @throws TimePeriodException if elapsed time period could not be calculated due to an internal error
	 */
	public static TimePeriod calculateElapsedTimePeriod(Date pD1, Date pD2, DurationType pDurationType, boolean absoluteValue) 
		throws TimePeriodException {
	
		String _METHODNAME = "calculateTimePeriod(): ";
		if (pD1 == null || pD2 == null) {
			String str = "One or more date parameters is null";
			logger.error(_METHODNAME + str);
			throw new IllegalArgumentException(str);
		}
		
		if (logger.isDebugEnabled()) {
			String str = _METHODNAME + "Date 1 is " + pD1 + ", Date 2 is " + pD2;
			logger.debug(str);
		}
		
		Date d1 = pD1;
		Date d2 = pD2;
		if (absoluteValue) {
			if (pD2.before(pD1)) {
				d1 = pD2;
				d2 = pD1;
			}
		}
		TimePeriod tp = null;
		if (pDurationType == DurationType.DAYS) {
			tp = new TimePeriod(differenceInDays(d1, d2) + "d");
		}
		else if (pDurationType == DurationType.WEEKS) {
			tp = new TimePeriod(differenceInWeeks(d1, d2) + "w");
		}
		else if (pDurationType == DurationType.MONTHS) {
			tp = new TimePeriod(differenceInMonths(d1, d2) + "m");
		}
		else if (pDurationType == DurationType.YEARS) {
			tp = new TimePeriod(differenceInYears(d1, d2) + "y");
		}
		else {
			String errStr = _METHODNAME + "Unexpected error: DurationType specified not supported by this method";
			logger.error(errStr);
			throw new TimePeriodException(errStr);
		}
		return tp;
	}
	
	
	public static int compareElapsedTimePeriodToDateRange(Date pD1, Date pD2, TimePeriod tp) {
		
		return compareElapsedTimePeriodToDateRange(pD1, pD2, tp, false);
	}
	
	
	public static int compareElapsedTimePeriodToDateRange(Date pD1, Date pD2, TimePeriod tp, boolean absoluteValue) {
		
		String _METHODNAME = "compareElapsedTimePeriodToDateRange(Date, Date, TimePeriod): ";
		if (tp == null) {
			String str = "TimePeriod supplied is null";
			logger.error(_METHODNAME + str);
			throw new IllegalArgumentException(str);
		}

		return compareElapsedTimePeriodToDateRange(pD1, pD2, tp.timePeriodRepresentation, absoluteValue);
	}

	
	public static int compareElapsedTimePeriodToDateRange(Date pD1, Date pD2, String pTimePeriodStr) {
	
		return compareElapsedTimePeriodToDateRange(pD1, pD2, pTimePeriodStr, false);
	}
	
	
	/**
	 * If the elapsed time between the supplied dates is greater than that specified by the TimePeriod string starting from the specified start date pD1,
	 * return 1; -1 if elapsed time is less than that specified by the TimePeriod string, and 0 if they are the same
	 * @param pD1
	 * @param pD2
	 * @param pTimePeriodStr
	 * @param absoluteValue
	 * @throws IllegalArgumentException if either date or TimePeriod is not supplied
	 * @return
	 */
	public static int compareElapsedTimePeriodToDateRange(Date pD1, Date pD2, String pTimePeriodStr, boolean absoluteValue) {
		
		String _METHODNAME = "compareElapsedTimePeriodToDateRange(Date, Date, String, boolean): ";
		if (pTimePeriodStr == null || pTimePeriodStr.length() == 0) {
			String str = "TimePeriod string supplied not supplied";
			logger.warn(_METHODNAME + str);
			throw new IllegalArgumentException(str);
		}
		if (pD1 == null || pD2 == null) {
			String str = "One or more date parameters is null";
			logger.error(_METHODNAME + str);
			throw new IllegalArgumentException(str);
		}
		
		Date d1 = null;
		Date d2 = null;
		if (absoluteValue == true && pD2.before(pD1)) {
			d1 = pD2;
			d2 = pD1;
		}
		else {
			d1 = pD1;
			d2 = pD2;
		}
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Date d1 is " + d1.toString() + "; Date d2 is " + d2.toString());
		}

		if (!pTimePeriodStr.matches(TimePeriodStringFormat)) {
			String str = "TimePeriod string \"" + pTimePeriodStr + "\" does not match correct pattern: e.g. - 1y 10m 12d";
			logger.error(_METHODNAME + str);
			throw new IllegalArgumentException(str);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("TimePeriod String Supplied: " + pTimePeriodStr);
		}
		
		Date interimDate = d1;
		interimDate = addTimePeriod(d1, pTimePeriodStr);
		if (interimDate.before(d2)) {
			return 1;
		}
		else if (interimDate.after(d2)) {
			return -1;
		}
		else {
			return 0;
		}
	}
	
	

	/**
	 * Compare two time periods of the same type
	 * @param pTD
	 * @param orEqualTo
	 * @throws IllegalArgumentException if TimePeriod is of a different DurationType than this one
	 * @return true if the TimePeriod is less than [or equal to] the supplied parameter, false if it is not. If supplied TimePeriod is set to null, then it is treated as a 
	 * TimePeriod with of zero days
	 */
	public boolean isLessThan(TimePeriod pTD, boolean orEqualTo) {
		
		// String _METHODNAME = "isLessThan_old(): ";
		
		TimePeriod lTimePeriod = pTD;
		if (pTD == null) {
			lTimePeriod = new TimePeriod("0d");
		}
		
		Date lReferenceDate = new Date();
		Date lTPDate1 = addTimePeriod(lReferenceDate, this);
		Date lTPDate2 = addTimePeriod(lReferenceDate, lTimePeriod);
		
		int compareTo = lTPDate1.compareTo(lTPDate2);
		if (orEqualTo) {
			if (compareTo <= 0)
				return true;
			else
				return false;
		}
		else {
			if (compareTo < 0) 
				return true;
			else
				return false;
		}
	}
	
	public boolean isLessThan(TimePeriod pTD) {
		
		return isLessThan(pTD, false);
	}
	
	public boolean isLessThanEqualTo(TimePeriod pTD) {
		
		return isLessThan(pTD, true);
	}
	
	/**
	 * Compare two time periods of the same type. Return true if this TimePeriod is greater than (greater than/equal to) the supplied TimePeriod.
	 * @param pTD
	 * @param orEqualTo
	 * @return true if the TimePeriod is less than [or equal to] the supplied parameter, false if it is not. If supplied TimePeriod is set to null, then it is treated as a 
	 * TimePeriod with of zero days
	 */
	public boolean isGreaterThan(TimePeriod pTD, boolean orEqualTo) {
		
		// String _METHODNAME = "isGreaterThan(TimePeriod, boolean): ";
		
		TimePeriod lTimePeriod = pTD;
		if (pTD == null) {
			lTimePeriod = new TimePeriod("0d");
		}
		
		Date lReferenceDate = new Date();
		Date lTPDate1 = addTimePeriod(lReferenceDate, this);
		Date lTPDate2 = addTimePeriod(lReferenceDate, lTimePeriod);
		
		int compareTo = lTPDate1.compareTo(lTPDate2);
		if (orEqualTo) {
			if (compareTo >= 0)
				return true;
			else
				return false;
		}
		else {
			if (compareTo > 0) 
				return true;
			else
				return false;
		}
	}
	
	public boolean isGreaterThan(TimePeriod pTD) {
		
		return isGreaterThan(pTD, false);
		
	}

	public boolean isGreaterThanEqualTo(TimePeriod pTD) {
		
		return isGreaterThan(pTD, true);
	}
	
	/**
	 * Return a Date from a string. String provided must be in format used by Drools: dd-MMM-yyyy. e.g. - "04-Jul-1999")
	 * If no string is provided, null is returned;
	 */
	public static Date generateDateFromStringInDroolsDateFormat(String pDateAsString) {
		
        if (pDateAsString == null || pDateAsString.isEmpty()) {
        	return null;
        }

        Date lDateToReturn = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            lDateToReturn = formatter.parse(pDateAsString);
        }
        catch (Exception e) {
        	String lErrStr = "An error occurred generating a Date from the string representation provided: " + pDateAsString;
        	logger.error("generateDateFromStringIn_dd-MMM-yyy_Format: " + lErrStr);
        	throw new IllegalArgumentException(lErrStr);
        }

        return lDateToReturn;
	}
	
	/**
	 * Compare two time periods of the same type. Return true if this TimePeriod is equal to the supplied TimePeriod.
	 * @param pTD
	 * @return true if the TimePeriod is less than [or equal to] the supplied parameter, false if it is not. If supplied TimePeriod is set to null, then it is treated as a 
	 * TimePeriod with of zero days
	 */
	public boolean isEqualTo(TimePeriod pTD) {

		TimePeriod lTimePeriod = pTD;
		if (pTD == null) {
			lTimePeriod = new TimePeriod("0d");
		}
		
		Date lReferenceDate = new Date();
		Date lTPDate1 = addTimePeriod(lReferenceDate, this);
		Date lTPDate2 = addTimePeriod(lReferenceDate, lTimePeriod);
		
		int compareTo = lTPDate1.compareTo(lTPDate2);
		if (compareTo == 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "TimePeriod [timePeriodStringRepresentation=" + this.timePeriodRepresentation + "]";
	}

}
