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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    public static String getISODateFormat(final Date date)
    {
        if (date == null)
            return "";

        return new SimpleDateFormat("yyyyMMdd").format(date);
    }

    public static String getISODatetimeFormat(final Date date)
    {
        if (date == null)
            return "";

        return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
    }

    public static Date parseISODateFormat(final String dateString) throws ParseException
    {
        if (!StringUtils.hasText(dateString))
            return null;

        return new SimpleDateFormat("yyyyMMdd").parse(dateString);
    }

    public static Date parseISODatetimeFormat(final String dateString) throws ParseException
    {
        if (!StringUtils.hasText(dateString))
            return null;

        return new SimpleDateFormat("yyyyMMddHHmmss").parse(dateString);
    }

    public static Date parseDate(final String s)
    {
        return DatatypeConverter.parseDate(s).getTime();
    }

    public static String printDate(final Date dt)
    {
        final Calendar cal = new GregorianCalendar();
        cal.setTime(dt);

        return DatatypeConverter.printDate(cal);
    }

    public static Date parseDateTime(final String s)
    {
        return DatatypeConverter.parseDateTime(s).getTime();
    }

    public static String printDateTime(final Date dt)
    {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(dt);
    }
}
