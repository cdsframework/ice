/**
 * Copyright (C) 2019 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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

package org.opencds.support.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;

import org.springframework.util.StringUtils;

import jakarta.xml.bind.DatatypeConverter;
import lombok.experimental.UtilityClass;

/**
 * @author HLN Consulting, LLC
 */
@UtilityClass
public class DateUtils
{
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static String getISODateFormat(final LocalDate date)
    {
        if (date == null)
            return "";

        return date.format(dateFormat);
    }

    public static LocalDate parseISODateFormat(final String dateString)
    {
        if (!StringUtils.hasText(dateString))
            return null;

        return LocalDate.parse(dateString, dateFormat);
    }

    public static LocalDate parseDate(final String s)
    {
        return LocalDate.ofInstant(DatatypeConverter.parseDate(s).toInstant(), ZoneId.systemDefault());
    }

    public static String printDate(final LocalDate dt)
    {
        return DatatypeConverter.printDate(GregorianCalendar.from(dt.atStartOfDay().atZone(ZoneId.systemDefault())));
    }

    public static LocalDateTime parseDateTime(final String s)
    {
        return LocalDateTime.from(DatatypeConverter.parseDateTime(s).toInstant());
    }

    public static String printDateTime(final LocalDateTime dt)
    {
        return dt.format(dateTimeFormat);
    }
}
