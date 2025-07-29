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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author HLN Consulting, LLC
 */
@Slf4j
public class DateUtilsTest
{
    /**
     * Test of getISODateFormat method, of class DateUtils.
     */
    @Test
    public void testGetISODateFormat()
    {
        log.info("getISODateFormat");
        final Date date = new Date();
        Assert.assertEquals(new SimpleDateFormat("yyyyMMdd").format(date), DateUtils.getISODateFormat(date));
    }

    /**
     * Test of parseISODateFormat method, of class DateUtils.
     */
    @Test
    public void testParseISODateFormat() throws Exception
    {
        log.info("parseISODateFormat");
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        final String expResult = formatter.format(new Date());
        Assert.assertEquals(expResult, formatter.format(DateUtils.parseISODateFormat(expResult)));
    }

    /**
     * Test of parseISODateFormat method, of class DateUtils.
     */
    @Test
    public void testParseISODateFormat2() throws Exception
    {
        log.info("parseISODateFormat");
        final String dateString = "20130610000000.000-0400";
        final String expResult = new SimpleDateFormat("yyyyMMdd").format(DateUtils.parseISODateFormat(dateString));
        log.info("expResult: {}", expResult);
        Assert.assertEquals(dateString, dateString);
    }
}
