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
import java.util.Objects;
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
    public static LocalDate extractSingularDateValueFromIVLDate(final IVLDate dateInterval) throws IllegalArgumentException
    {
        if (dateInterval == null)
            throw new IllegalArgumentException("No immunization date provided");

        final LocalDate lowIntervalDate = dateInterval.getLow();
        final LocalDate highIntervalDate = dateInterval.getHigh();

        if (lowIntervalDate == null && highIntervalDate == null)
            throw new IllegalArgumentException("No immunization date provided");

        if (lowIntervalDate == null)
            return highIntervalDate;

        if (highIntervalDate == null)
            return lowIntervalDate;

        if (!Objects.equals(lowIntervalDate, highIntervalDate))
            throw new IllegalArgumentException("Invalid immunization date data; interval date contained unequal dates");

        return lowIntervalDate;
    }
}
