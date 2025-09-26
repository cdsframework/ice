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

package org.cdsframework.ice.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Objects;

import org.cdsframework.ice.service.ICELogicHelper;
import org.springframework.util.ObjectUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString(onlyExplicitlyIncluded = true)
public class TimePeriod
{
    public enum DurationType
    {
        DAYS,
        MONTHS,
        WEEKS,
        YEARS
    }

    private static final String TimePeriodStringFormat =
            "[([-|+]?[ ]*[0-9]+[Yy])?([ ]*[-|+]?[ ]*[0-9]+[Mm])?([ ]*[-|+]?[ ]*[0-9]+[Ww])?([ ]*[-|+]?[ ]*[0-9]+[Dd])?]+";

    /**
     * Construct deep copy of TimePeriod object and return it to the caller
     *
     * @return duplicated TimePeriod object
     */
    public static TimePeriod constructDeepCopyOfTimePeriodObject(final TimePeriod pTP)
    {
        if (pTP == null)
            return null;

        final TimePeriod lTP = new TimePeriod();
        lTP.durationType = pTP.durationType;
        lTP.duration = pTP.duration;
        lTP.timePeriodSet = pTP.timePeriodSet;
        lTP.timePeriodRepresentation = pTP.timePeriodRepresentation;
        return lTP;
    }

    /**
     * Returns true if the String argument is in the correct TimePeriod string format, false if it is not.
     */
    public static boolean isTimePeriodStringInCorrectFormat(final String pTimePeriodStr)
    {
        if (pTimePeriodStr == null)
            return false;

        return pTimePeriodStr.matches(TimePeriodStringFormat);
    }

    /**
     * Returns true if the time period is a negative duration (i.e.- prefixed with a minus sign); false if not. Throws an IllegalArgumentException if the TimePeriod
     * argument passed in is not in the correct TimePeriod string format.
     */
    public static boolean isTimePeriodANegativeDuration(final String pTimePeriodStr)
    {
        final String _METHODNAME = "isTimePeriodANegativeDuration(): ";
        if (!isTimePeriodStringInCorrectFormat(pTimePeriodStr))
        {
            final String lWarnStr = "TimePeriod argument is in the incorrect format: " + pTimePeriodStr;
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "{}", lWarnStr);
            throw new IllegalArgumentException(lWarnStr);
        }

        return pTimePeriodStr.startsWith("-");
    }

    public static boolean isValidMonth(final int month)
    {
        return month > 0 && month < 13;
    }

    public static int numberOfDaysInMonth(final int year, final int month)
    {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    public static int differenceInDays(final Date startDate, final Date endDate)
    {
        if (startDate == null || endDate == null)
            return 0;

        return Math.toIntExact(ChronoUnit.DAYS.between(ICELogicHelper.toLocalDate(startDate), ICELogicHelper.toLocalDate(endDate)));
    }

    public static int differenceInMonths(final Date startDate, final Date endDate)
    {
        if (startDate == null || endDate == null)
            return 0;

        return Math.toIntExact(
                ChronoUnit.MONTHS.between(ICELogicHelper.toLocalDate(startDate), ICELogicHelper.toLocalDate(endDate)));
    }

    public static int differenceInWeeks(final Date startDate, final Date endDate)
    {
        if (startDate == null || endDate == null)
            return 0;

        return Math.toIntExact(
                ChronoUnit.WEEKS.between(ICELogicHelper.toLocalDate(startDate), ICELogicHelper.toLocalDate(endDate)));
    }

    public static int differenceInYears(final Date startDate, final Date endDate)
    {
        if (startDate == null || endDate == null)
            return 0;

        return Math.toIntExact(
                ChronoUnit.YEARS.between(ICELogicHelper.toLocalDate(startDate), ICELogicHelper.toLocalDate(endDate)));
    }

    /**
     * Adds the specified amount of time to the supplied date and returns a new date the TimePeriod duration to the supplied date
     *
     * @return a new data representing the supplied date plus the TimePeriod; startDate if the supplied TimePeriod is null
     * @throws IllegalArgumentException if DurationType is not one of the above
     */
    private static Date addTimePeriodUsingDurationValues(final Date startDate, final TimePeriod pTP)
    {
        final String _METHODNAME = "addTimePeriod(): ";

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Start date: {}; TimePeriod: {}", startDate, pTP);

        if (startDate == null)
            return null;

        if (pTP == null)
            return startDate;

        final int duration = pTP.getDuration();
        final DurationType tpType = pTP.getDurationType();

        return Date.from((switch (tpType)
        {
            case DAYS -> startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(duration);
            case MONTHS ->
            {
                LocalDate startLD = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                final int dayOfMonthBeforeCalculation = startLD.getDayOfMonth();
                startLD = startLD.plusMonths(duration);
                if (startLD.getDayOfMonth() < dayOfMonthBeforeCalculation && startLD.isEqual(
                        startLD.with(TemporalAdjusters.lastDayOfMonth())))
                {
                    startLD = startLD.plusDays(1);
                }

                yield startLD;
            }
            case WEEKS -> startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusWeeks(duration);
            case YEARS ->
            {
                LocalDate startLD = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                final int dayOfMonthBeforeCalculation = startLD.getDayOfMonth();
                startLD = startLD.plusYears(duration);
                if (startLD.getDayOfMonth() < dayOfMonthBeforeCalculation && startLD.isEqual(
                        startLD.with(TemporalAdjusters.lastDayOfMonth())))
                {
                    startLD = startLD.plusDays(1);
                }

                yield startLD;
            }
        }).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Add the TimePeriod to the supplied Date. If TimePeriod is negative for any unit, that unit is subtracted
     *
     * @return a new date representing the supplied date plus the TimePeriod; startDate if the supplied TimePeriod is null
     */
    public static Date addTimePeriod(final Date startDate, final TimePeriod pTP)
    {
        if (pTP == null || startDate == null)
            return null;

        return addTimePeriod(startDate, pTP.getTimePeriodStringRepresentation());

    }

    /**
     * Add the TimePeriod duration to the supplied date
     *
     * @return a new date representing the supplied date plus the TimePeriod; startDate if the supplied TimePeriod is null
     * @throws IllegalArgumentException if TimePeriod representation has format errors
     */
    public static Date addTimePeriod(final Date startDate, final String pTimePeriodStr)
    {
        final String _METHODNAME = "addTimePeriod(Date, String): ";

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Start date: {}; TimePeriod Str: {}", startDate, pTimePeriodStr);

        if (startDate == null)
            return null;

        if (pTimePeriodStr == null)
            return startDate;

        if (!isTimePeriodStringInCorrectFormat(pTimePeriodStr))
        {
            final String str = "TimePeriod string \"" + pTimePeriodStr + "\" does not match correct pattern: e.g. - 1y10m12d";
            log.error(_METHODNAME + "{}", str);
            throw new IllegalArgumentException(str);
        }

        if (log.isDebugEnabled())
            log.debug("TimePeriod String Supplied: {}", pTimePeriodStr);

        TimePeriod tp;
        Date interimDate = startDate;
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < pTimePeriodStr.length(); i++)
        {
            final char c = pTimePeriodStr.charAt(i);
            switch (c)
            {
                case 'd', 'D' ->
                {
                    if (token.isEmpty())
                        continue;

                    if (token.charAt(0) == '+')
                        token.deleteCharAt(0);

                    tp = new TimePeriod(Integer.parseInt(new String(token)), DurationType.DAYS);

                    if (log.isDebugEnabled())
                        log.debug("TimePeriod: {} to be added to interimDate: {}", tp, interimDate);

                    interimDate = addTimePeriodUsingDurationValues(interimDate, tp);

                    if (log.isDebugEnabled())
                        log.debug(_METHODNAME + "New interimDate: {}", interimDate);

                    token = new StringBuilder();
                }
                case 'w', 'W' ->
                {
                    if (token.isEmpty())
                        continue;

                    if (token.charAt(0) == '+')
                        token.deleteCharAt(0);

                    tp = new TimePeriod(Integer.parseInt(new String(token)), DurationType.WEEKS);

                    if (log.isDebugEnabled())
                        log.debug(_METHODNAME + "TimePeriod: {} to be added to interimDate: {}", tp, interimDate);

                    interimDate = addTimePeriodUsingDurationValues(interimDate, tp);

                    if (log.isDebugEnabled())
                        log.debug(_METHODNAME + "New interimDate: {}", interimDate);

                    token = new StringBuilder();
                }
                case 'm', 'M' ->
                {
                    if (token.isEmpty())
                        continue;

                    if (token.charAt(0) == '+')
                        token.deleteCharAt(0);

                    tp = new TimePeriod(Integer.parseInt(new String(token)), DurationType.MONTHS);

                    if (log.isDebugEnabled())
                        log.debug(_METHODNAME + "TimePeriod: {} to be added to interimDate: {}", tp, interimDate);

                    interimDate = addTimePeriodUsingDurationValues(interimDate, tp);

                    if (log.isDebugEnabled())
                        log.debug(_METHODNAME + "New interimDate: {}", interimDate);

                    token = new StringBuilder();
                }
                case 'y', 'Y' ->
                {
                    if (token.isEmpty())
                        continue;

                    if (token.charAt(0) == '+')
                        token.deleteCharAt(0);

                    tp = new TimePeriod(Integer.parseInt(new String(token)), DurationType.YEARS);

                    if (log.isDebugEnabled())
                        log.debug(_METHODNAME + "TimePeriod: {} to be added to interimDate: {}", tp, interimDate);

                    interimDate = addTimePeriodUsingDurationValues(interimDate, tp);

                    if (log.isDebugEnabled())
                        log.debug(_METHODNAME + "New interimDate: {}", interimDate);

                    token = new StringBuilder();
                }
                case ' ' ->
                {
                }
                default -> token.append(c);
            }
        }

        return interimDate;
    }

    /**
     * Calculate time period in whole units. e.g. 45 days = 1 month; 364 days = 0 years; 9 days = 1 week
     */
    public static TimePeriod calculateElapsedTimePeriod(final Date pD1, final Date pD2, final DurationType pDurationType)
            throws TimePeriodException
    {
        return calculateElapsedTimePeriod(pD1, pD2, pDurationType, false);
    }

    /**
     * @return TimePeriod representing elapsed time period in the specified
     * @throws IllegalArgumentException if one or more of the supplied date parameters are invalid (e.g. - null)
     * @throws TimePeriodException      if elapsed time period could not be calculated due to an internal error
     */
    public static TimePeriod calculateElapsedTimePeriod(final Date pD1, final Date pD2, final DurationType pDurationType,
            final boolean absoluteValue) throws TimePeriodException
    {
        final String _METHODNAME = "calculateTimePeriod(): ";
        if (pD1 == null || pD2 == null)
        {
            final String str = "One or more date parameters is null";
            log.error(_METHODNAME + str);
            throw new IllegalArgumentException(str);
        }

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Date 1 is {}, Date 2 is {}", pD1, pD2);

        Date d1 = pD1;
        Date d2 = pD2;
        if (absoluteValue)
        {
            if (pD2.before(pD1))
            {
                d1 = pD2;
                d2 = pD1;
            }
        }

        if (pDurationType == null)
        {
            final String errStr = _METHODNAME + "Unexpected error: DurationType specified not supported by this method";
            log.error(errStr);
            throw new TimePeriodException(errStr);
        }

        return switch (pDurationType)
        {
            case DAYS -> new TimePeriod(differenceInDays(d1, d2) + "d");
            case WEEKS -> new TimePeriod(differenceInWeeks(d1, d2) + "w");
            case MONTHS -> new TimePeriod(differenceInMonths(d1, d2) + "m");
            case YEARS -> new TimePeriod(differenceInYears(d1, d2) + "y");
        };
    }

    public static int compareElapsedTimePeriodToDateRange(final Date pD1, final Date pD2, final TimePeriod tp)
    {
        return compareElapsedTimePeriodToDateRange(pD1, pD2, tp, false);
    }

    public static int compareElapsedTimePeriodToDateRange(final Date pD1, final Date pD2, final TimePeriod tp,
            final boolean absoluteValue)
    {
        final String _METHODNAME = "compareElapsedTimePeriodToDateRange(Date, Date, TimePeriod): ";
        if (tp == null)
        {
            final String str = "TimePeriod supplied is null";
            log.error(_METHODNAME + str);
            throw new IllegalArgumentException(str);
        }

        return compareElapsedTimePeriodToDateRange(pD1, pD2, tp.timePeriodRepresentation, absoluteValue);
    }

    public static int compareElapsedTimePeriodToDateRange(final Date pD1, final Date pD2, final String pTimePeriodStr)
    {
        return compareElapsedTimePeriodToDateRange(pD1, pD2, pTimePeriodStr, false);
    }

    /**
     * If the elapsed time between the supplied dates is greater than that specified by the TimePeriod string starting from the specified start date pD1,
     * return 1; -1 if elapsed time is less than that specified by the TimePeriod string, and 0 if they are the same
     *
     * @throws IllegalArgumentException if either date or TimePeriod is not supplied
     */
    public static int compareElapsedTimePeriodToDateRange(final Date pD1, final Date pD2, final String pTimePeriodStr,
            final boolean absoluteValue)
    {
        final String _METHODNAME = "compareElapsedTimePeriodToDateRange(Date, Date, String, boolean): ";
        if (ObjectUtils.isEmpty(pTimePeriodStr))
        {
            final String str = "TimePeriod string supplied not supplied";
            log.warn(_METHODNAME + str);
            throw new IllegalArgumentException(str);
        }

        if (pD1 == null || pD2 == null)
        {
            final String str = "One or more date parameters is null";
            log.error(_METHODNAME + str);
            throw new IllegalArgumentException(str);
        }

        final Date d1;
        final Date d2;
        if (absoluteValue && pD2.before(pD1))
        {
            d1 = pD2;
            d2 = pD1;
        }
        else
        {
            d1 = pD1;
            d2 = pD2;
        }
        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Date d1 is {}; Date d2 is {}", d1, d2);

        if (!pTimePeriodStr.matches(TimePeriodStringFormat))
        {
            final String str = "TimePeriod string \"" + pTimePeriodStr + "\" does not match correct pattern: e.g. - 1y 10m 12d";
            log.error(_METHODNAME + "{}", str);
            throw new IllegalArgumentException(str);
        }

        if (log.isDebugEnabled())
            log.debug("TimePeriod String Supplied: {}", pTimePeriodStr);

        final Date interimDate;
        interimDate = addTimePeriod(d1, pTimePeriodStr);
        if (Objects.requireNonNull(interimDate).before(d2))
            return 1;

        if (interimDate.after(d2))
            return -1;

        return 0;
    }

    /**
     * Return a Date from a string. String provided must be in format used by Drools: dd-MMM-yyyy. e.g. - "04-Jul-1999")
     * If no string is provided, null is returned;
     */
    public static Date generateDateFromStringInDroolsDateFormat(final String pDateAsString)
    {
        if (ObjectUtils.isEmpty(pDateAsString))
            return null;

        final Date lDateToReturn;
        try
        {
            lDateToReturn = new SimpleDateFormat("dd-MMM-yyyy").parse(pDateAsString);
        }
        catch (final Exception e)
        {
            final String lErrStr = "An error occurred generating a Date from the string representation provided: " + pDateAsString;
            log.error("generateDateFromStringIn_dd-MMM-yyy_Format: {}", lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        return lDateToReturn;
    }

    private DurationType durationType;
    private int duration;
    // private boolean isInclusive;
    private boolean timePeriodSet;
    @ToString.Include
    private String timePeriodRepresentation;

    /**
     * Create a new TimePeriod by specifying duration value and type. By using this, it is not possible to specify a combination TimePeriod with more than one duration type. i.e. -
     * to specify a TimePeriod of 1 year, 5 months and 4 days, use TimePeriod(String). TimePeriods are immutable; once set, it is not possible to change it.
     */
    public TimePeriod(final int pDuration, final DurationType pDurationType)
    {
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
     *
     * @throws IllegalArgumentException if the TimePeriod argument is not in the correct format.
     */
    public TimePeriod(final String pTimePeriodStr)
    {
        setTimePeriod(pTimePeriodStr);
    }

    public String getTimePeriodStringRepresentation()
    {
        return this.timePeriodRepresentation;
    }

    private void setTimePeriod(final String pTimePeriodStr)
    {
        final String _METHODNAME = "setTimePeriod()";
        if (!isTimePeriodStringInCorrectFormat(pTimePeriodStr))
        {
            final String str = "TimePeriod string \"" + pTimePeriodStr + "\" does not match correct pattern: e.g. - 1y10m12d";
            log.error(_METHODNAME + "{}", str);
            throw new IllegalArgumentException(str);
        }

        this.timePeriodRepresentation = pTimePeriodStr;
        this.timePeriodSet = true;
    }

    /**
     * Return TimePeriod in string format year, month, or day. e.g. - "4y", "5m", "6d".
     *
     * @return String If DurationType is not of type DAYS, MONTHS, WEEKS or YEARS, null is returned.
     */
    private String determineTimePeriodStringRepresentationFromDurationValues()
    {
        if (durationType == null)
            return null;

        return switch (durationType)
        {
            case DAYS -> duration + "d";
            case WEEKS -> duration + "w";
            case MONTHS -> duration + "m";
            case YEARS -> duration + "y";
        };
    }

    /**
     * Compare two time periods of the same type
     *
     * @return true if the TimePeriod is less than [or equal to] the supplied parameter, false if it is not. If supplied TimePeriod is set to null, then it is treated as a
     * TimePeriod with of zero days
     * @throws IllegalArgumentException if TimePeriod is of a different DurationType than this one
     */
    public boolean isLessThan(final TimePeriod pTD, final boolean orEqualTo)
    {
        // String _METHODNAME = "isLessThan_old(): ";

        TimePeriod lTimePeriod = pTD;
        if (pTD == null)
            lTimePeriod = new TimePeriod("0d");

        final Date lReferenceDate = new Date();
        final Date lTPDate1 = addTimePeriod(lReferenceDate, this);
        final Date lTPDate2 = addTimePeriod(lReferenceDate, lTimePeriod);

        final int compareTo = lTPDate1.compareTo(lTPDate2);
        return orEqualTo ? compareTo <= 0 : compareTo < 0;
    }

    public boolean isLessThan(final TimePeriod pTD)
    {
        return isLessThan(pTD, false);
    }

    public boolean isLessThanEqualTo(final TimePeriod pTD)
    {
        return isLessThan(pTD, true);
    }

    /**
     * Compare two time periods of the same type. Return true if this TimePeriod is greater than (greater than/equal to) the supplied TimePeriod.
     *
     * @return true if the TimePeriod is less than [or equal to] the supplied parameter, false if it is not. If supplied TimePeriod is set to null, then it is treated as a
     * TimePeriod with of zero days
     */
    public boolean isGreaterThan(final TimePeriod pTD, final boolean orEqualTo)
    {
        // String _METHODNAME = "isGreaterThan(TimePeriod, boolean): ";

        TimePeriod lTimePeriod = pTD;
        if (pTD == null)
            lTimePeriod = new TimePeriod("0d");

        final Date lReferenceDate = new Date();
        final Date lTPDate1 = addTimePeriod(lReferenceDate, this);
        final Date lTPDate2 = addTimePeriod(lReferenceDate, lTimePeriod);

        final int compareTo = lTPDate1.compareTo(lTPDate2);
        return orEqualTo ? compareTo >= 0 : compareTo > 0;
    }

    public boolean isGreaterThan(final TimePeriod pTD)
    {
        return isGreaterThan(pTD, false);

    }

    public boolean isGreaterThanEqualTo(final TimePeriod pTD)
    {
        return isGreaterThan(pTD, true);
    }

    /**
     * Compare two time periods of the same type. Return true if this TimePeriod is equal to the supplied TimePeriod.
     *
     * @return true if the TimePeriod is less than [or equal to] the supplied parameter, false if it is not. If supplied TimePeriod is set to null, then it is treated as a
     * TimePeriod with of zero days
     */
    public boolean isEqualTo(final TimePeriod pTD)
    {
        TimePeriod lTimePeriod = pTD;
        if (pTD == null)
            lTimePeriod = new TimePeriod("0d");

        final Date lReferenceDate = new Date();
        final Date lTPDate1 = addTimePeriod(lReferenceDate, this);
        final Date lTPDate2 = addTimePeriod(lReferenceDate, lTimePeriod);

        return lTPDate1.compareTo(lTPDate2) == 0;
    }
}
