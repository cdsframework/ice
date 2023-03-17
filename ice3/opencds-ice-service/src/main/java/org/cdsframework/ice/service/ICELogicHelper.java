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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

public class ICELogicHelper {

	private static final Logger logger = LogManager.getLogger();


	public static void logDRLDebugMessage(String pDRLRule, String pMessageToLog) {

		if (logger.isDebugEnabled()) {
			logger.debug(pDRLRule + "(): " + pMessageToLog);
		}
	}


	public static String generateUniqueString() {
		return UUID.randomUUID().toString();
	}
	

	public List<TargetDose> getAllTargetDosesAcrossTargetSeries(List<TargetSeries> tss) {

		List<TargetDose> tds = new ArrayList<TargetDose>();
		if (tss == null) {
			return tds;
		}

		for (TargetSeries ts : tss) {
			Collection<TargetDose> tsd = ts.getTargetDoses();
			if (tsd != null && ! tsd.isEmpty())
				tds.addAll(tsd);
		}

		return tds;
	}

	
	/**
	 * This method simply takes a RuntimeException as parameter, logs it, and throws it. It is provided to so that DRL & DSLR rules can throw 
	 * exceptions, limited to RuntimeExceptions.
	 * @param pException RuntimeException or descendant
	 */
	public static <T extends RuntimeException> void throwRuntimeException(T pException) {

		String _METHODNAME = "throwRuntimeException(): ";

		if (pException == null) {
			String errStr = "Exception argument not supplied; throwing IllegalArgumentException instead";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		else {
			logger.warn(_METHODNAME + "exception noted and thrown", pException);
			throw pException;
		}
	}


	/**
	 * Given a vMR IVLDate datatype where the high and low values are expected to be the same => convert to a Date datatype. Therefore, if the
	 * high and low values of the IVLDate are different, an exception is thrown
	 * @param dateInterval
	 * @return
	 * @throws InvalidDataException
	 */
	public static Date extractSingularDateValueFromIVLDate(IVLDate dateInterval) 
			throws InvalidDataException {

		if (dateInterval == null) {
			throw new InvalidDataException("No immunization date provided");
		}
		Date lowIntervalDate = dateInterval.getLow();
		Date highIntervalDate = dateInterval.getHigh();
		if (lowIntervalDate == null && highIntervalDate == null) {
			throw new InvalidDataException("No immunization date provided");
		}
		if (lowIntervalDate == null && highIntervalDate != null) {
			return highIntervalDate;
		}
		else if (lowIntervalDate != null && highIntervalDate == null) {
			return lowIntervalDate;
		}

		int dateComparison = compareDates(lowIntervalDate, highIntervalDate);
		if (dateComparison != 0) {
			throw new InvalidDataException("Invalid immunization date data; interval date contained unequal dates");
		}
		return lowIntervalDate;
	}

	/**
	 * Compares two dates - month, date and year only
	 * @param date1
	 * @param date2
	 * @return the value 0 if the dates are equal, a value less than 0 if date1 is before date2, a value greater than 0 if date2 > date1. 
	 * If both dates are null, they're considered equal; if one date is null and the other is not, then the non-null date is returned as the greater
	 * of the two.
	 */
	private static int compareDates(Date date1, Date date2) {

		if (date1 == null && date2 == null) {
			return 0;
		}
		if (date1 != null && date2 == null) {
			return -1;
		}
		if (date1 == null && date2 != null) {
			return 1;
		}

		Calendar calendar1 = new GregorianCalendar();
		calendar1.setLenient(false);
		calendar1.setTime(date1);
		Calendar calendar2 = new GregorianCalendar();
		calendar2.setLenient(false);
		calendar2.setTime(date2);

		int year1 = calendar1.get(Calendar.YEAR);
		int month1 = calendar1.get(Calendar.MONTH);
		int dateday1 = calendar1.get(Calendar.DATE);
		int year2 = calendar2.get(Calendar.YEAR);
		int month2 = calendar2.get(Calendar.MONTH);
		int dateday2 = calendar2.get(Calendar.DATE);

		// Compare years
		if (year1 < year2)
			return 1;
		else if (year1 > year2)
			return -1;
		// Compare months
		if (month1 < month2)
			return 1;
		else if (month1 > month2)
			return -1;
		// Compare dates
		if (dateday1 < dateday2)
			return 1;
		else if (dateday1 > dateday2)
			return -1;
		else 
			return 0;
	}

}
