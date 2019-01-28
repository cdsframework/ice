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

package org.opencds.support.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author HLN Consulting, LLC
 */
public class DateUtils {

    public static String getISODateFormat(Date date) {
        String result;
        if (date == null) {
            result = "";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            result = formatter.format(date);
        }
        return result;
    }

    public static String getISODatetimeFormat(Date date) {
        String result;
        if (date == null) {
            result = "";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            result = formatter.format(date);
        }
        return result;
    }

    public static Date parseISODateFormat(String dateString) throws ParseException {
        Date result;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        if (dateString == null || dateString.trim().isEmpty()) {
            result = null;
        } else {
            result = formatter.parse(dateString);
        }
        return result;
    }

    public static Date parseISODatetimeFormat(String dateString) throws ParseException {
        Date result;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        if (dateString == null || dateString.trim().isEmpty()) {
            result = null;
        } else {
            result = formatter.parse(dateString);
        }
        return result;
    }

    public static Date parseDate(String s) {
        return DatatypeConverter.parseDate(s).getTime();
    }

    public static String printDate(Date dt) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dt);
        return DatatypeConverter.printDate(cal);
    }

    public static Date parseDateTime(String s) {
        return DatatypeConverter.parseDateTime(s).getTime();
    }

    public static String printDateTime(Date dt) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return df.format(dt);
    }
}
