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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.UUID;

import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ICELogicHelper
{
    public static void logDRLDebugMessage(final String pDRLRule, final String pMessageToLog)
    {
        if (log.isDebugEnabled())
            log.debug("{}(): {}", pDRLRule, pMessageToLog);
    }

    public static String generateUniqueString()
    {
        return UUID.randomUUID().toString();
    }

    /**
     * This method simply takes a RuntimeException as parameter, logs it, and throws it. It is provided to so that DRL & DSLR rules can throw
     * exceptions, limited to RuntimeExceptions.
     *
     * @param pException RuntimeException or descendant
     */
    public static <T extends RuntimeException> void throwRuntimeException(final T pException)
    {
        final String _METHODNAME = "throwRuntimeException(): ";

        if (pException == null)
        {
            final String errStr = "Exception argument not supplied; throwing IllegalArgumentException instead";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        log.warn(_METHODNAME + "exception noted and thrown", pException);
        throw pException;
    }

    /**
     * Given a vMR IVLDate datatype where the high and low values are expected to be the same => convert to a Date datatype. Therefore, if the
     * high and low values of the IVLDate are different, an exception is thrown
     */
    public static Date extractSingularDateValueFromIVLDate(final IVLDate dateInterval) throws IllegalArgumentException
    {
        if (dateInterval == null)
            throw new IllegalArgumentException("No immunization date provided");

        final Date lowIntervalDate = dateInterval.getLow();
        final Date highIntervalDate = dateInterval.getHigh();

        if (lowIntervalDate == null && highIntervalDate == null)
            throw new IllegalArgumentException("No immunization date provided");

        if (lowIntervalDate == null)
            return highIntervalDate;

        if (highIntervalDate == null)
            return lowIntervalDate;

        final int dateComparison = compareDates(lowIntervalDate, highIntervalDate);
        if (dateComparison != 0)
            throw new IllegalArgumentException("Invalid immunization date data; interval date contained unequal dates");

        return lowIntervalDate;
    }

    /**
     * Compares two dates - month, date and year only
     *
     * @return the value 0 if the dates are equal, a value less than 0 if date1 is before date2, a value greater than 0 if date2 > date1.
     * If both dates are null, they're considered equal; if one date is null and the other is not, then the non-null date is returned as the greater
     * of the two.
     */
    private static int compareDates(final Date date1, final Date date2)
    {
        if (date1 == null && date2 == null)
            return 0;

        if (date1 != null && date2 == null)
            return -1;

        if (date1 == null)
            return 1;

        final Calendar calendar1 = new GregorianCalendar();
        calendar1.setLenient(false);
        calendar1.setTime(date1);
        final Calendar calendar2 = new GregorianCalendar();
        calendar2.setLenient(false);
        calendar2.setTime(date2);

        final int year1 = calendar1.get(Calendar.YEAR);
        final int month1 = calendar1.get(Calendar.MONTH);
        final int dateday1 = calendar1.get(Calendar.DATE);
        final int year2 = calendar2.get(Calendar.YEAR);
        final int month2 = calendar2.get(Calendar.MONTH);
        final int dateday2 = calendar2.get(Calendar.DATE);

        // Compare years
        if (year1 < year2)
            return 1;

        if (year1 > year2)
            return -1;

        // Compare months
        if (month1 < month2)
            return 1;

        if (month1 > month2)
            return -1;

        // Compare dates
        return Integer.compare(dateday2, dateday1);
    }

    @Deprecated(forRemoval = true)
    public static LocalDate toLocalDate(final Date date)
    {
        return Optional.ofNullable(date)
                .map(Date::toInstant)
                .map(i -> i.atZone(ZoneId.systemDefault()))
                .map(ZonedDateTime::toLocalDate)
                .orElse(null);
    }

    @Deprecated(forRemoval = true)
    public static Date toDate(final LocalDate localDate)
    {
        return Optional.ofNullable(localDate)
                .map(LocalDate::atStartOfDay)
                .map(ldt -> ldt.atZone(ZoneId.systemDefault()))
                .map(ZonedDateTime::toInstant)
                .map(Date::from)
                .orElse(null);
    }
}
