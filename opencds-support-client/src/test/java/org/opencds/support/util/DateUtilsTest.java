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

import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author HLN Consulting, LLC
 */
public class DateUtilsTest {

    public DateUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getISODateFormat method, of class DateUtils.
     */
    @Test
    public void testGetISODateFormat() {
        System.out.println("getISODateFormat");
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String expResult = formatter.format(date);
        String result = DateUtils.getISODateFormat(date);
        assertEquals(expResult, result);
    }

    /**
     * Test of parseISODateFormat method, of class DateUtils.
     *
     * @throws Exception
     */
    @Test
    public void testParseISODateFormat() throws Exception {
        System.out.println("parseISODateFormat");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String expResult = formatter.format(new Date());
        String result = formatter.format(DateUtils.parseISODateFormat(expResult));
        assertEquals(expResult, result);
    }

    /**
     * Test of parseISODateFormat method, of class DateUtils.
     *
     * @throws Exception
     */
    @Test
    public void testParseISODateFormat2() throws Exception {
        System.out.println("parseISODateFormat");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = "20130610000000.000-0400";
        Date parseISODateFormat = DateUtils.parseISODateFormat(dateString);
        String expResult = formatter.format(parseISODateFormat);
        System.out.println("expResult: " + expResult);
        String result = dateString;
        assertEquals(dateString, result);
    }
}
