/**
 * Copyright 2011 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.common.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * <p>DateUtility is used to perform date functions.  It is thread-safe,
 * in that a new Calendar objects are created for performing an operation or
 * evaluation if one is needed.
 * <p/>
 *
 * @author Kensaku Kawamoto
 * @version 1.00
 */

public class DateUtility extends java.lang.Object
{
	// TODO: consider changing methods in this and other utilities that can be turned into static methods into static methods.
	
	    public final static int DATE_PRECISION_LEVEL_FORMAT_APPENDED_TEXT = 1;
	    public final static int DATE_PRECISION_LEVEL_FORMAT_QUESTION_MARK = 2;
	    public final static int DATE_PRECISION_LEVEL_FORMAT_IGNORE_PRECISION_LEVEL = 3;
	    public final static double AVG_DAYS_PER_MONTH = 365.5 / 12.0;

	    private static DateUtility instance = new DateUtility();  //singleton instance

	    private DateUtility()
	    {
	    }

	    public static DateUtility getInstance()
	    {
	        return instance;
	    }

	    /**
	     * Month 1 = January (note difference from calendar).  Thus, to set as January 1, 2001,
	     * use (2001, 1, 1), NOT (2001, Calendar.JANUARY, 1).
	     * <p/>
	     * Hour, minutes, seconds, milliseconds will be set to 0
	     *
	     * @param year
	     * @param month
	     * @param day
	     * @return
	     */
	    public Date getDate(int year, int month, int day)
	    {
	        Calendar calendar = new GregorianCalendar();
	        calendar.set(year, month - 1, day);
	        calendar.set(Calendar.HOUR_OF_DAY, 0);
	        calendar.set(Calendar.MINUTE, 0);
	        calendar.set(Calendar.SECOND, 0);
	        calendar.set(Calendar.MILLISECOND, 0);
	        return calendar.getTime();
	    }

	    /**
	     * month 1 = January (note difference from calendar).   Thus, to set as January 1, 2001,
	     * use (2001, 1, 1), NOT (2001, Calendar.JANUARY, 1).
	     *
	     * @param year
	     * @param month
	     * @param day
	     * @param hourOfDay
	     * @param minute
	     * @param second
	     * @return
	     */
	    public Date getTime(int year, int month, int day, int hourOfDay, int minute, int second)
	    {
	        Calendar calendar = new GregorianCalendar();
	        calendar.set(year, month - 1, day, hourOfDay, minute, second);
	        return calendar.getTime();
	    }

	    /**
	     * Returns the timeElment of the date, where timeElement is the Calendar enum
	     * (Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY,
	     * Calendar.MINUTE, etc.).
	     * <p/>
	     * NOTE: if user asks for Calendar.HOUR (which includes AM/PM concepts), Calendar.HOUR_OF_DAY will be returned.
	     * Also, note that if user asks for Calendar.MONTH, note that January == 0; thus, for example, to check to see if
	     * it is February, check if returnedMonth == Calendar.FEBRUARY, rather than returnedMonth == 2.
	     *
	     * @param date
	     * @param timeElement
	     * @return
	     */
	    public int getTimeComponent(Date date, int timeElement)
	    {
	        if (timeElement == Calendar.HOUR)
	        {
	            timeElement = Calendar.HOUR_OF_DAY;
	        }
	        Calendar calendar = new GregorianCalendar();
	        calendar.setTime(date);
	        return calendar.get(timeElement);
	    }

	    /**
	     * Returns date after adding timeAmountToAdd (e.g. 15) in timeUnit (e.g. Calendar.DATE).
	     * Can subtract if use a negative timeAmountToAdd.  Original date is unchanged.
	     *
	     * @param originalDateUnchangedByFunction
	     *
	     * @param timeUnit
	     * @param timeAmountToAdd
	     * @return
	     */
	    public Date getDateAfterAddingTime(Date originalDateUnchangedByFunction, int timeUnit, int timeAmountToAdd)
	    {
	        Calendar calendar = new GregorianCalendar();
	        calendar.setTime(originalDateUnchangedByFunction);
	        calendar.add(timeUnit, timeAmountToAdd);
	        return calendar.getTime();
	    }

	    /**
	     * Gets Date using date in SEBASTIAN string format (yyyy-MM-dd HH:mm:ss).
	     * Returns null if string is null or "".
	     *
	     * @param dateInSqlLongStringFormat
	     * @return
	     */
	    public Date getDateFromSqlLongString(String dateInSqlLongStringFormat)
	    {
	        if ((dateInSqlLongStringFormat == null) || (dateInSqlLongStringFormat.equals("")))
	        {
	            return null;
	        }
	        return getDateFromString(dateInSqlLongStringFormat, "yyyy-MM-dd HH:mm:ss");
	    }

	    /**
	     * Gets Date using date in SEBASTIAN string format (yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd).
	     * Returns null if string is null or "".
	     *
	     * @param dateInSebastianStringFormat
	     * @return
	     */
	    public Date getDateFromSebastianString(String dateInSebastianStringFormat)
	    {
	        if ((dateInSebastianStringFormat == null) || (dateInSebastianStringFormat.equals("")))
	        {
	            return null;
	        }

	        if (dateInSebastianStringFormat.length() == 10)
	        {
	            return getDateFromString(dateInSebastianStringFormat, "yyyy-MM-dd");
	        }
	        else
	        {
	            return getDateFromString(dateInSebastianStringFormat, "yyyy-MM-dd'T'HH:mm:ss");
	        }
	    }


	    /**
	     * Returns date in yyyy-MM-dd'T'HH:mm:ss format
	     *
	     * @param date
	     * @return
	     */
	    public String getDateAsSebastianLongString(Date date)
	    {
	        return getDateAsString(date, "yyyy-MM-dd'T'HH:mm:ss");
	    }

	    /**
	     * Returns date in yyyy-MM-dd format
	     *
	     * @param date
	     * @return
	     */
	    public String getDateAsSebastianShortString(Date date)
	    {
	        return getDateAsString(date, "yyyy-MM-dd");
	    }

	    /**
	     * Returns date in yyyy-MM-dd HH:mm:ss format
	     *
	     * @param date
	     * @return
	     */
	    public String getDateAsSqlLongString(Date date)
	    {
	        return getDateAsString(date, "yyyy-MM-dd HH:mm:ss");
	    }

	    public Date getDateFromString(String dateAsString, String formatTemplate)
	    // formatTemplate example: "yyyy-MM-dd HH:mm:ss" (SQL server format)
	    // returns Date corresponding to the dateAsString
	    {
	        Date dateToReturn = null;

	        try
	        {
	            SimpleDateFormat formatter = new SimpleDateFormat(formatTemplate);
	            dateToReturn = formatter.parse(dateAsString);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }

	        return dateToReturn;
	    }

	    /**
	     * Returns whether getDateFromString will be able to succeed without error.
	     * Added because getDateFromString was not implemented with error throwing.
	     *
	     * @param dateAsString
	     * @param formatTemplate
	     * @return
	     */
	    public boolean canGetDateFromString(String dateAsString, String formatTemplate)
	    {
	        if ((dateAsString == null) || (dateAsString.equals("")))
	        {
	            return false;
	        }

	        try
	        {
	            SimpleDateFormat formatter = new SimpleDateFormat(formatTemplate);
	            formatter.parse(dateAsString);
	        }
	        catch (ParseException e)
	        {
	            return false;
	        }
	        return true;
	    }

	    public Date getDateFromStringAs_yyyy_MM_dd(String dateAsString)
	    {
	        return getDateFromString(dateAsString, "yyyy_MM_dd");
	    }

	    public String getDateAsString(Date date, String formatTemplate)
	    // formatTemplate example: "yyyy-MM-dd HH:mm:ss" (SQL server format) (note: hh returns 12-hr time rather than 24-hr time)
	    {
	        String stringToReturn = null;

	        try
	        {
	            SimpleDateFormat formatter = new SimpleDateFormat(formatTemplate);
	            stringToReturn = formatter.format(date);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }

	        return stringToReturn;
	    }


	    /**
	     * Returns date in the format "enero 5, 2005"
	     * Note: apparently, Spanish months aren't capitalized.
	     * Also, apparently, the style is el 29 de febrero de 1996
	     * <p/>
	     * Source: http://www.rlrouse.com/spanish/spanish-months.html, http://www.studyspanish.com/lessons/months.htm
	     *
	     * @param date
	     * @return
	     */
	    public String getDateAsSpanishLongString(Date date)
	    {
	        int month = getTimeComponent(date, Calendar.MONTH);
	        int day = getTimeComponent(date, Calendar.DATE);
	        int year = getTimeComponent(date, Calendar.YEAR);

	        StringBuffer buffer = new StringBuffer();

	        buffer.append("el ");
	        buffer.append(day);
	        buffer.append(" de ");
	        if (month == Calendar.JANUARY)
	        {
	            buffer.append("enero");
	        }
	        else if (month == Calendar.FEBRUARY)
	        {
	            buffer.append("febrero");
	        }
	        else if (month == Calendar.MARCH)
	        {
	            buffer.append("marzo");
	        }
	        else if (month == Calendar.APRIL)
	        {
	            buffer.append("abril");
	        }
	        else if (month == Calendar.MAY)
	        {
	            buffer.append("mayo");
	        }
	        else if (month == Calendar.JUNE)
	        {
	            buffer.append("junio");
	        }
	        else if (month == Calendar.JULY)
	        {
	            buffer.append("julio");
	        }
	        else if (month == Calendar.AUGUST)
	        {
	            buffer.append("agosto");
	        }
	        else if (month == Calendar.SEPTEMBER)
	        {
	            buffer.append("septiembre");
	        }
	        else if (month == Calendar.OCTOBER)
	        {
	            buffer.append("octubre");
	        }
	        else if (month == Calendar.NOVEMBER)
	        {
	            buffer.append("noviembre");
	        }
	        else if (month == Calendar.DECEMBER)
	        {
	            buffer.append("diciembre");
	        }

	        buffer.append(" de ");
	        buffer.append(year);

	        return buffer.toString();
	    }

	    /**
	     * Returns originalDateString in the targetDateStringFormat.
	     *
	     * @param originalDateString
	     * @param originalDateStringFormatTemplate
	     *
	     * @param targetDateStringFormatTemplate
	     * @return
	     */
	    public String getDateStringInDifferentFormat(String originalDateString, String originalDateStringFormatTemplate, String targetDateStringFormatTemplate)
	    {
	        Date date = getDateFromString(originalDateString, originalDateStringFormatTemplate);
	        return getDateAsString(date, targetDateStringFormatTemplate);
	    }

	    public List<Date> getFirstOfMonthBetweenDates(Date begin, Date end, boolean goBackInTime)
	    // returns array of Dates between begin and end (inclusive), which are the first of
	    // the month.  If goBackInTime is true, returns array in reverse chronological order.
	    {
	        ArrayList<Date> arrayForward = new ArrayList<Date>();
	        ArrayList<Date> arrayBackward = new ArrayList<Date>();

	        Calendar beginCalendar = new GregorianCalendar();
	        Calendar endCalendar = new GregorianCalendar();

	        beginCalendar.setTime(begin);
	        endCalendar.setTime(end);

	        while (!beginCalendar.after(endCalendar))
	        {
	            int currentDay = beginCalendar.get(Calendar.DATE);

	            if (currentDay == 1)
	            {
	                arrayForward.add(beginCalendar.getTime());
	                beginCalendar.add(Calendar.MONTH, 1);
	                // this keeps the date same as current date
	                // (e.g. 2nd goes to 2nd of next month) -- verified with testing
	            }
	            else
	            {
	                beginCalendar.add(Calendar.DATE, 1);
	            }
	        }

	        if (goBackInTime)
	        {
	            for (int k = 0; k < arrayForward.size(); k++)
	            {
	                Date d = (Date) arrayForward.get(k);
	                arrayBackward.add(d);
	            }
	            return arrayBackward;
	        }

	        return arrayForward;
	    }

	    public Date getFirstDayInMonth(Date referenceDate)
	    // returns first day of month which referenceDate is in
	    // e.g. referenceDate = 11/5/02 --> returns 11/1/02
	    // e.g. referenceDate = 11/1/02 --> returns 11/1/02
	    {
	        Calendar calendar = new GregorianCalendar();
	        calendar.setTime(referenceDate);

	        int currentDay = calendar.get(Calendar.DATE);

	        while (currentDay != 1)
	        {
	            calendar.add(Calendar.DATE, -1);
	            currentDay = calendar.get(Calendar.DATE);
	        }

	        return calendar.getTime();
	    }

	    /**
	     * Returns AbsoluteTimeDifference.  time1 and time2 are the times from which the difference is to be computed.
	     * Note that, as the name indicates, the difference is non-directional.  See the class description of
	     * edu.duke.mc.dci.sebastiancommon.objects.AbsoluteTimeDifference for a detailed description of how the other
	     * parameters should be specified.
	     * <p/>
	     * highestCalendarTimeUnitToIgnore not used if ignoreSmallTimeUnits == false (e.g. can specify to be -1)
	     *
	     * @param time1
	     * @param time2
	     * @param highestReturnedCalendarTimeUnit
	     *
	     * @param ignoreSmallTimeUnits
	     * @param highestCalendarTimeUnitToIgnore
	     *
	     * @return
	     */
	    public AbsoluteTimeDifference getAbsoluteTimeDifference(Date time1, Date time2, int highestReturnedCalendarTimeUnit,
	                                                            boolean ignoreSmallTimeUnits, int highestCalendarTimeUnitToIgnore)
	    {
	        return new AbsoluteTimeDifference(time1, time2, highestReturnedCalendarTimeUnit,
	                ignoreSmallTimeUnits, highestCalendarTimeUnitToIgnore);
	    }
	    
	    /**
	     * Returns an absolute time difference that ignores hours and smaller units if highest returned calendar unit is days or greater.
	     * @param time1
	     * @param time2
	     * @param highestReturnedCalendarTimeUnit
	     * @return
	     */
	    public AbsoluteTimeDifference getAbsoluteTimeDifference(Date time1, Date time2, int highestReturnedCalendarTimeUnit)
	    {
	    	//System.out.println("time1="+time1 +", time2="+time2);
	    	if ((highestReturnedCalendarTimeUnit == Calendar.HOUR) ||
	    			(highestReturnedCalendarTimeUnit == Calendar.HOUR_OF_DAY) ||
	    			(highestReturnedCalendarTimeUnit == Calendar.MINUTE) ||
	    			(highestReturnedCalendarTimeUnit == Calendar.SECOND) ||
	    			(highestReturnedCalendarTimeUnit == Calendar.MILLISECOND))
	    	{
		    	//System.out.println("atd="+getAbsoluteTimeDifference(time1, time2, highestReturnedCalendarTimeUnit, false, -1));
	    		return getAbsoluteTimeDifference(time1, time2, highestReturnedCalendarTimeUnit, false, -1);
	    	}
	    	//System.out.println("time1="+time1 +", time2="+time2);
	    	return getAbsoluteTimeDifference(time1, time2, highestReturnedCalendarTimeUnit, true, Calendar.HOUR);
	    }
	    
	    /**
	     * Returns true if the absolute time difference between time1 and time2 is within the indicated parameters, using 
	     * conventional notions of time.  Hours and smaller units are ignored if the calendar unit is days or greater.
	     * 
	     * If the time difference is at the specified boundary (e.g., 2 years specified as cutoff and two dates are
	     * 12/31/2009 and 12/31/2011, returns true).
	     * 
	     * Note that a time unit of week is currently not supported.
	     * 
	     * E.g., this means the following in the case that the maximumTimeDifferenceInUnit is 2:
	     * - for time unit of year, time1 and time2 are at most 2 years apart (e.g., 1/1/2000 at any time and 1/1/2002 at any time)
	     * - for time unit of month, time1 and time2 are at most 2 months apart (e.g., 1/1/2000 at any time and 3/1/2000 at any time)
	     * - for time unit of day, time1 and time2 are at most 2 days apart (e.g., 1/1/2000 at any time and 1/3/2000 at any time)
	     * - for time unit of hour, time1 and time2 are at most 2 hours apart (e.g., 1/1/2000 3:15pm and 1/1/2000 5:15pm)
	     * - for time unit of minutes, time1 and time2 are at most 2 minutes apart (e.g., 1/1/2000 3:15:25 pm and 1/1/2000 3:17:25pm)
	     * - for time unit of seconds, time1 and time2 are at most 2 seconds apart (e.g., 1/1/2000 3:15:25 pm and 1/1/2000 3:15:27pm)
	     * - for time unit of miliseconds, time1 and time2 are at most 2ms apart
	     * @param time1
	     * @param time2
	     * @param calendarTimeUnit
	     * @param maximumTimeDifferenceInUnit
	     * @throw IllegalArgumentException	Thrown if time1 null, time2 null, or calendarTimeUnit not recognized.
	     * @return
	     */
	    public boolean timeDifferenceLessThanOrEqualTo(Date time1, Date time2, int calendarTimeUnit, long maximumTimeDifferenceInUnit) throws IllegalArgumentException
	    {
	    	// TODO: consider adding support for time unit of week
	    	if ((time1 == null) || (time2 == null))
	    	{
	    		throw new IllegalArgumentException("time1 and time2 may not be null.");
	    	}
	    	
	    	AbsoluteTimeDifference atd = getAbsoluteTimeDifference(time1, time2, calendarTimeUnit);
	    	if (calendarTimeUnit == Calendar.YEAR)
	    	{
	    		if ((atd.getYearDifference() < maximumTimeDifferenceInUnit) || 
	    		   ((atd.getYearDifference() == maximumTimeDifferenceInUnit) && (atd.getMonthDifference() == 0) && (atd.getDayDifference() == 0)))
	    		{
	    			return true;
	    		}
	    	}
	    	else if (calendarTimeUnit == Calendar.MONTH)
	    	{
	    		if ((atd.getMonthDifference() < maximumTimeDifferenceInUnit) || 
	    		   ((atd.getMonthDifference() == maximumTimeDifferenceInUnit) && (atd.getDayDifference() == 0)))
	    		{
	    			return true;
	    		}
	    	}
	    	else if ((calendarTimeUnit == Calendar.DATE) ||
	    			(calendarTimeUnit == Calendar.DAY_OF_MONTH) ||
	    			(calendarTimeUnit == Calendar.DAY_OF_WEEK) ||
	    			(calendarTimeUnit == Calendar.DAY_OF_WEEK_IN_MONTH) ||
	    			(calendarTimeUnit == Calendar.DAY_OF_YEAR))
	    	{
	    		if (atd.getDayDifference() <= maximumTimeDifferenceInUnit)
	    		{
	    			return true;
	    		}
	    	}
	    	else if ((calendarTimeUnit == Calendar.HOUR_OF_DAY) ||
		                (calendarTimeUnit == Calendar.HOUR))
	        {
	    		if ((atd.getHourDifference() < maximumTimeDifferenceInUnit) || 
	    		((atd.getHourDifference() == maximumTimeDifferenceInUnit) && (atd.getMinuteDifference() == 0) && (atd.getSecondDifference() == 0) && (atd.getMillisecondDifference() == 0)))
	    		{
	    			return true;
	    		}
	        }
	    	else if (calendarTimeUnit == Calendar.MINUTE)
	    	{
	    		if ((atd.getMinuteDifference() < maximumTimeDifferenceInUnit) ||
	    		((atd.getMinuteDifference() == maximumTimeDifferenceInUnit) && (atd.getSecondDifference() == 0) && (atd.getMillisecondDifference() == 0)))
	    		{
	    			return true;
	    		}
	    	}
	    	else if (calendarTimeUnit == Calendar.SECOND)
	    	{
	    		if ((atd.getSecondDifference() < maximumTimeDifferenceInUnit) ||
	    		   ((atd.getSecondDifference() == maximumTimeDifferenceInUnit) && (atd.getMillisecondDifference() == 0)))
	    		{
	    			return true;
	    		}
	    	}
	    	else if (calendarTimeUnit == Calendar.MILLISECOND)
	    	{
	    		if (atd.getMillisecondDifference() <= maximumTimeDifferenceInUnit)
	    		{
	    			return true;
	    		}
	    	}
	    	else
	    	{
	    		throw new IllegalArgumentException("calendarTimeUnit of " + calendarTimeUnit + " is not supported.");
	    	}
			
	    	return false;	    	
	    }
		        
	    /**
	     * Returns true if the absolute time difference between time1 and time2 is within the indicated parameters, using 
	     * conventional notions of time.  Hours and smaller units are ignored if the calendar unit is days or greater.
	     * 
	     * Note that a time unit of week is currently not supported.
	     * 
	     * If the time difference is at the specified boundary (e.g., 2 years specified as cutoff and two dates are
	     * 12/31/2009 and 12/31/2011, returns false).
	     * 
	     * E.g., this means the following in the case that the maximumTimeDifferenceInUnit is 2:
	     * - for time unit of year, time1 and time2 are less than 2 years apart (e.g., 1/2/2000 at any time and 1/1/2002 at any time)
	     * - for time unit of month, time1 and time2 are less than 2 months apart (e.g., 1/2/2000 at any time and 3/1/2000 at any time)
	     * - for time unit of day, time1 and time2 are less than 2 days apart (e.g., 1/2/2000 at any time and 1/3/2000 at any time)
	     * - for time unit of hour, time1 and time2 are less than 2 hours apart (e.g., 1/1/2000 3:15pm and 1/1/2000 5:15pm)
	     * - for time unit of minutes, time1 and time2 are less than 2 minutes apart (e.g., 1/1/2000 3:16:25 pm and 1/1/2000 3:17:25pm)
	     * - for time unit of seconds, time1 and time2 are less than 2 seconds apart (e.g., 1/1/2000 3:15:26 pm and 1/1/2000 3:15:27pm)
	     * - for time unit of miliseconds, time1 and time2 are less than 2ms apart
	     * @param time1
	     * @param time2
	     * @param calendarTimeUnit
	     * @param maximumTimeDifferenceInUnit
	     * @throw IllegalArgumentException	Thrown if time1 null, time2 null, or calendarTimeUnit not recognized.
	     * @return
	     */
	    public boolean timeDifferenceLessThan(Date time1, Date time2, int calendarTimeUnit, long maximumTimeDifferenceInUnit) throws IllegalArgumentException
	    {
	    	// TODO: consider adding support for time unit of week
	    	if ((time1 == null) || (time2 == null))
	    	{
	    		throw new IllegalArgumentException("time1 and time2 may not be null.");
	    	}
	    	
	    	AbsoluteTimeDifference atd = getAbsoluteTimeDifference(time1, time2, calendarTimeUnit);
	    	if (calendarTimeUnit == Calendar.YEAR)
	    	{
	    		if (atd.getYearDifference() < maximumTimeDifferenceInUnit) 
	    		{
	    			return true;
	    		}
	    	}
	    	else if (calendarTimeUnit == Calendar.MONTH)
	    	{
	    		if (atd.getMonthDifference() < maximumTimeDifferenceInUnit) 
	    		{
	    			return true;
	    		}
	    	}
	    	else if ((calendarTimeUnit == Calendar.DATE) ||
	    			(calendarTimeUnit == Calendar.DAY_OF_MONTH) ||
	    			(calendarTimeUnit == Calendar.DAY_OF_WEEK) ||
	    			(calendarTimeUnit == Calendar.DAY_OF_WEEK_IN_MONTH) ||
	    			(calendarTimeUnit == Calendar.DAY_OF_YEAR))
	    	{
	    		if (atd.getDayDifference() < maximumTimeDifferenceInUnit)
	    		{
	    			return true;
	    		}
	    	}
	    	else if ((calendarTimeUnit == Calendar.HOUR_OF_DAY) ||
		                (calendarTimeUnit == Calendar.HOUR))
	        {
	    		if (atd.getHourDifference() < maximumTimeDifferenceInUnit)
	    		{
	    			return true;
	    		}
	        }
	    	else if (calendarTimeUnit == Calendar.MINUTE)
	    	{
	    		if (atd.getMinuteDifference() < maximumTimeDifferenceInUnit)
	    		{
	    			return true;
	    		}
	    	}
	    	else if (calendarTimeUnit == Calendar.SECOND)
	    	{
	    		if (atd.getSecondDifference() < maximumTimeDifferenceInUnit) 
	    		{
	    			return true;
	    		}
	    	}
	    	else if (calendarTimeUnit == Calendar.MILLISECOND)
	    	{
	    		if (atd.getMillisecondDifference() <= maximumTimeDifferenceInUnit)
	    		{
	    			return true;
	    		}
	    	}
	    	else
	    	{
	    		throw new IllegalArgumentException("calendarTimeUnit of " + calendarTimeUnit + " is not supported.");
	    	}
			
	    	return false;	    	
	    }

	    /**
	     * Get date1 - date2 (eg 2 days for 12/14/03 00:00:00 - 12/12/03 00:00:00), in the time unit specified (Calendar.DATE,
	     * etc.).  Ignores hours, minutes, and seconds if so specified.  If this parameter is set to true, date1 and date2
	     * are first normalized to the same hour, minutes, seconds, and milliseconds, so that time of day is not a factor.
	     * For example, 12/14/03 1am and 12/13/03 2pm are less than 12 hours apart, the difference will be computed to 1day
	     * when hours/miutes/seconds are ignored, because the calculation will be 12/14/03 00:00:00 - 12/13/03 00:00:00.
	     * Note that this function returns the approximate time difference using 30.4375 days in month, 365.25 days in year.
	     * Use getAbsoluteTimeDifference if need more precise method of determining time difference (e.g. for determining age).
	     * <p/>
	     * NOTE: despite the name of the function, this method gives the EXACT difference for dates if ignoreHoursMinutesSeconds
	     * is set to true.  Thus, for determining the number of days between 2 dates, this method can be used with equal
	     * accuracy as getAbsoluteTimeDifference, and will provide faster performance.
	     * <p/>
	     * Note that if the difference is greater than the max/min values allowed for doubles, a wrong answer could be returned.
	     * Also, note that if the difference is large, the returned value may be an approximation (e.g. 120 days may be estimated
	     * as 119.95 days).
	     *
	     * @param date1
	     * @param date2
	     * @param timeUnit
	     * @param ignoreHoursMinutesSeconds
	     * @return
	     */
	    public double getApproximateTimeDifference(Date date1, Date date2, int timeUnit, boolean ignoreHoursMinutesSeconds)
	    {
	        long date1AsLong;
	        long date2AsLong;

	        Calendar calendar = new GregorianCalendar();
	        calendar.clear();
	        calendar.setTime(date1);

	        if (ignoreHoursMinutesSeconds)
	        {
	            calendar.set(Calendar.HOUR_OF_DAY, 0);
	            calendar.set(Calendar.MINUTE, 0);
	            calendar.set(Calendar.SECOND, 0);
	            calendar.set(Calendar.MILLISECOND, 0);
	        }
	        date1AsLong = calendar.getTimeInMillis();

	        calendar.clear();
	        calendar.setTime(date2);

	        if (ignoreHoursMinutesSeconds)
	        {
	            calendar.set(Calendar.HOUR_OF_DAY, 0);
	            calendar.set(Calendar.MINUTE, 0);
	            calendar.set(Calendar.SECOND, 0);
	            calendar.set(Calendar.MILLISECOND, 0);
	        }
	        date2AsLong = calendar.getTimeInMillis();

	        double x = 1000;

	        if (timeUnit == Calendar.YEAR)
	        {
	            x = x * 60 * 60 * 24 * 365.25;
	        }
	        else if (timeUnit == Calendar.MONTH)
	        {
	            x = x * 60 * 60 * 24 * 30.4375;
	        }
	        else if ((timeUnit == Calendar.DATE) ||
	                (timeUnit == Calendar.DAY_OF_MONTH) ||
	                (timeUnit == Calendar.DAY_OF_WEEK) ||
	                (timeUnit == Calendar.DAY_OF_WEEK_IN_MONTH) ||
	                (timeUnit == Calendar.DAY_OF_YEAR))
	        // options provided in case of client confusion of which constant to use
	        {
	            x = x * 60 * 60 * 24;
	        }
	        else if ((timeUnit == Calendar.HOUR_OF_DAY) ||
	                (timeUnit == Calendar.HOUR))
	        {
	            x = x * 60 * 60;
	        }
	        else if (timeUnit == Calendar.MINUTE)
	        {
	            x = x * 60;
	        }
	        else if (timeUnit == Calendar.SECOND)
	        {
	            // do nothing       x = x;
	        }
	        else if (timeUnit == Calendar.MILLISECOND)
	        {
	            x = x / 1000;
	        }
	        else
	        {
	            System.err.println("Error in DateUtility.getTimeDifference: time unit of <" + timeUnit + "> not recognized.");
	        }

	        return (double) ((date1AsLong - date2AsLong) / (x));
	    }

	    public boolean monthsOffByAtMostOne(Date date1, Date date2)
	    // returns true if and only if date2 exists in the month of date1, in the
	    // month after date1, or in the month before date1
	    // e.g. 1/15/02 and 1/1/02 --> true
	    // e.g. 1/15/02 and 12/1/01 --> true
	    // e.g. 1/15/02 and 12/1/03 --> false
	    {
	        boolean boolToReturn = false;

	        Calendar calendar = new GregorianCalendar();
	        calendar.setTime(date1);

	        int date1_month = calendar.get(Calendar.MONTH);
	        int date1_year = calendar.get(Calendar.YEAR);

	        calendar.setTime(date2);

	        int date2_month = calendar.get(Calendar.MONTH);
	        int date2_year = calendar.get(Calendar.YEAR);

	        // for examples, assume date1 in 2003
	        if (date1_year == date2_year)
	        // 2003, 2003
	        {
	            if ((date1_month == date2_month) || (date1_month - date2_month == 1) || (date1_month - date2_month == -1))
	            // if 5/03, 5/03                     5/03, 4/03                           5/03, 6/03
	            {
	                boolToReturn = true;
	            }
	        }
	        else if (date1_year == date2_year + 1)
	        // if  2003, 2002
	        {
	            if ((date1_month == Calendar.JANUARY) && (date2_month == Calendar.DECEMBER))
	            //  only case to check is 1/03 and 12/02
	            {
	                boolToReturn = true;
	            }
	        }
	        else if (date1_year == date2_year - 1)
	        // if 2003, 2004
	        {
	            if ((date1_month == Calendar.DECEMBER) && (date2_month == Calendar.JANUARY))
	            // only case to check is 12/03 and 1/04
	            {
	                boolToReturn = true;
	            }
	        }

	        return boolToReturn;
	    }

	    /**
	     * Returns a person's age at the specified time.  If either birthDate or specifiedTime is null, returns -1.
	     *
	     * @param birthDate
	     * @param specifiedTime
	     * @return
	     */
	    public int getAgeAtSpecifiedTime(Date birthDate, Date specifiedTime)
	    {
	        if ((birthDate == null) || (specifiedTime == null))
	        {
	            return -1;
	        }

	        AbsoluteTimeDifference td = DateUtility.getInstance().getAbsoluteTimeDifference(specifiedTime, birthDate, Calendar.YEAR, true, Calendar.HOUR);
	        return (int) td.getYearDifference();
	    }

	    /**
	     * Returns a person's age in months at the specified time.  If either birthDate or specifiedTime is null, returns -1.
	     *
	     * @param birthDate
	     * @param specifiedTime
	     * @return
	     */
	    public int getAgeInMonthsAtSpecifiedTime(Date birthDate, Date specifiedTime)
	    {
	        if ((birthDate == null) || (specifiedTime == null))
	        {
	            return -1;
	        }

	        AbsoluteTimeDifference td = DateUtility.getInstance().getAbsoluteTimeDifference(specifiedTime, birthDate, Calendar.MONTH, true, Calendar.HOUR);
	        return (int) td.getMonthDifference();
	    }

	    /**
	     * Calculate the count of elapsed months from <code>date1</code> to <code>date2</code>.
	     *
	     * @param date1 the starting (earlier) date
	     * @param date2 the ending (later) date
	     * @return count of elapsed months
	     */
	    public static double getElapsedMonths(Date date1, Date date2)
	    {
	        AbsoluteTimeDifference atd = new AbsoluteTimeDifference(date1, date2, Calendar.MONTH, true, Calendar.HOUR);
	        return atd.getMonthDifference() + (atd.getDayDifference() / AVG_DAYS_PER_MONTH);
	    }

	    /**
	     * e.g. if dayOfWeek == Calendar.MONDAY,
	     * longFormat --> "Monday"
	     * not longFormat --> "Mon"
	     * <p/>
	     * Returns null if dayOfWeek invalid.
	     *
	     * @param dayOfWeek
	     * @param getLongFormat
	     * @return
	     */
	    public String getDayOfWeekAsString(int dayOfWeek, boolean getLongFormat)
	    {
	        if (dayOfWeek == Calendar.MONDAY)
	        {
	            if (getLongFormat)
	            {
	                return "Monday";
	            }
	            else
	            {
	                return "Mon";
	            }
	        }
	        else if (dayOfWeek == Calendar.TUESDAY)
	        {
	            if (getLongFormat)
	            {
	                return "Tuesday";
	            }
	            else
	            {
	                return "Tue";
	            }
	        }
	        else if (dayOfWeek == Calendar.WEDNESDAY)
	        {
	            if (getLongFormat)
	            {
	                return "Wednesday";
	            }
	            else
	            {
	                return "Wed";
	            }
	        }
	        else if (dayOfWeek == Calendar.THURSDAY)
	        {
	            if (getLongFormat)
	            {
	                return "Thursday";
	            }
	            else
	            {
	                return "Thu";
	            }
	        }
	        else if (dayOfWeek == Calendar.FRIDAY)
	        {
	            if (getLongFormat)
	            {
	                return "Friday";
	            }
	            else
	            {
	                return "Fri";
	            }
	        }
	        else if (dayOfWeek == Calendar.SATURDAY)
	        {
	            if (getLongFormat)
	            {
	                return "Saturday";
	            }
	            else
	            {
	                return "Sat";
	            }
	        }
	        else if (dayOfWeek == Calendar.SUNDAY)
	        {
	            if (getLongFormat)
	            {
	                return "Sunday";
	            }
	            else
	            {
	                return "Sun";
	            }
	        }
	        else
	        {
	            return null;
	        }
	    }

	    /**
	     * Returns true if date1 is in fact the same day as date2.  This is needed because date1.equals(date2) does not
	     * return the expected result.
	     * date1      date2       returns
	     * e.g. 4/15/05     4/15/05     true
	     * 4/15/05     4/15/04     false
	     * 4/15/05     4/15/05     true
	     * 4/15/05 1am 4/15/05 2am true
	     * either null             false
	     *
	     * @param date1
	     * @param date2
	     * @return
	     */
	    public boolean isSameDay(Date date1, Date date2)
	    {
	        if ((date1 == null) || (date2 == null))
	        {
	            return false;
	        }

	        if (getApproximateTimeDifference(date1, date2, Calendar.DATE, true) == 0)
	        {
	            return true;
	        }
	        else
	        {
	            return false;
	        }
	    }

	    /**
	     * Returns true if date1 is in fact the same day as date2.  This is needed because date1.equals(date2) does not
	     * return the expected result.
	     * date1      date2       returns
	     * e.g. 4/15/05     4/15/05     true
	     * 4/15/05     4/15/04     false
	     * 4/15/05     4/15/05     true
	     * 4/15/05 1am 4/15/05 2am true
	     * either null             false
	     *
	     * @param date1
	     * @param date2
	     * @return
	     */
	    public boolean isSameDay(Long date1, Long date2)
	    {
	        if ((date1 == null) || (date2 == null))
	        {
	            return false;
	        }

	        if (getApproximateTimeDifference(new Date(date1), new Date(date2), Calendar.DATE, true) == 0)
	        {
	            return true;
	        }
	        else
	        {
	            return false;
	        }
	    }

	    /**
	     * Checks if two date objects are close to the same day.
	     *
	     * @param date1              the first date, not altered, not null
	     * @param date2              the second date, not altered, not null
	     * @param maxHoursDifference the maximum tolerance between <code>date1</code> and <code>date2</code>
	     * @return true if they're within <code>maxHoursDifference</code> hours of each other
	     */
	    public boolean isOnOrAboutSameDay(Date date1, Date date2, int maxHoursDifference)
	    {
	        final long MILLIS_PER_HOUR = 1000 * 60 * 60;
	        return Math.abs(date1.getTime() - date2.getTime()) <= MILLIS_PER_HOUR * maxHoursDifference;
	    }
	    
	    /**
	     * Returns true if there is any overlap in the date 1 time interval and date 2 time interval.
	     * 
	     * Assumes date1Start before or equal to date1End and that date2Start before or equal to date2End.
	     * 
	     * See http://c2.com/cgi/wiki?TestIfDateRangesOverlap.
	     * 
	     * Returns false if any of the values are null.
	     * @param interval1Start
	     * @param interval1End
	     * @param interval2Start
	     * @param interval2End
	     * @return
	     */
	    public static boolean timeIntervalsOverlap(Date interval1Start, Date interval1End, Date interval2Start, Date interval2End)
	    {
	    	if ((interval1Start == null) || (interval1End == null) || (interval2Start == null) || (interval2End == null))
	    	{
	    		return false;
	    	}
	    	
	    	// ( start1 <= end2 and start2 <= end1 )
	    	if ((! interval1Start.after(interval2End)) && (! interval2Start.after(interval1End)))
	    	{
	    		return true;
	    	}
	    	return false;
	    }

	    public static void main(String args[])
	    // tester function; can delete
	    {
//	    	String hl7_TS_value = "20110127130123.123-0600";
	        DateUtility utility = DateUtility.getInstance();
//	        java.util.Date dt = utility.getDateFromString( hl7_TS_value, "yyyyMMddHHmmss.SSSZZZZZ" );
//	        
//	        System.out.println( dt );
//	        
//	        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS aaa");
//	        String pretty_time = formatter.format( dt );
//
//	        System.out.println( pretty_time );
	        
/**
	        AbsoluteTimeDifference atd = utility.getAbsoluteTimeDifference(
	                (new GregorianCalendar(2007, Calendar.FEBRUARY, 1)).getTime(), (new GregorianCalendar(2007, Calendar.MARCH, 31)).getTime(),
	                Calendar.MONTH, true, Calendar.HOUR);
	        atd.print();
	        System.out.println(atd.getMonthDifference());
**/
	        //System.out.println(utility.getDateAsString(new Date(), "MMMMM dd, yyyy"));
	        /**
	         Date date1 = utility.getDate(2003, 12, 14);
	         Date date2 = utility.getDate(2003, 12, 12);

	         double dateDifference = utility.getApproximateTimeDifference(date1, date2, Calendar.DATE, true);
	         System.out.println("Should be: 2.  returned: " + dateDifference);

	         **/

	        /**
	         Date date1 = utility.getDate(2002, 12, 14);
	         Date date2 = utility.getDate(2002, 12, 1);

	         System.out.println("Both should be 12/1/02:");
	         System.out.println("Date 1: " + utility.getFirstDayInMonth(date1));
	         System.out.println("Date 2: " + utility.getFirstDayInMonth(date2));
	         **/

	        /******
	         ArrayList array1 = new ArrayList();
	         ArrayList array2 = new ArrayList();

	         array1.add(utility.getDate(2002, 12, 14));
	         array2.add(utility.getDate(2002, 12,  1));

	         array1.add(utility.getDate(2002, 12, 14));
	         array2.add(utility.getDate(2003, 1,  28));

	         array1.add(utility.getDate(2002, 12, 14));
	         array2.add(utility.getDate(2003, 1,  1));

	         array1.add(utility.getDate(2002, 12, 14));
	         array2.add(utility.getDate(2003, 2,  1));

	         array1.add(utility.getDate(2002, 12, 14));
	         array2.add(utility.getDate(2004, 1,  1));

	         array1.add(utility.getDate(2002, 12, 14));
	         array2.add(utility.getDate(2004, 12,  1));

	         array1.add(utility.getDate(2002, 12, 14));
	         array2.add(utility.getDate(2002, 11,  1));

	         array1.add(utility.getDate(2002, 12, 14));
	         array2.add(utility.getDate(2002, 10, 21));

	         array1.add(utility.getDate(2002, 1, 14));
	         array2.add(utility.getDate(2002, 12, 21));

	         array1.add(utility.getDate(2002, 1, 14));
	         array2.add(utility.getDate(2001, 12, 21));

	         for (int k=0; k < array1.size(); k++)
	         {
	         Date date1 = (Date) array1.get(k);
	         Date date2 = (Date) array2.get(k);

	         System.out.println("Comparing " + utility.getDateAsString(date1, "MM/dd/yyyy") + " to " + utility.getDateAsString(date2, "MM/dd/yyyy"));

	         if(utility.monthsOffByAtMostOne(date1, date2))
	         {
	         System.out.println("  TRUE - dates off by at most one month");
	         }
	         else
	         {
	         System.out.println("  FALSE - dates NOT off by at most one month");
	         }
	         System.out.println();
	         }
	         *****/

	        /**
	         Date start = utility.getDate(2000, 1, 1);
	         Date end = utility.getDate(2003, 12, 1);

	         ArrayList arrayList = utility.getFirstOfMonthBetweenDates (start, end, true);

	         for (int k=0; k < arrayList.size(); k++)
	         {
	         System.out.println(arrayList.get(k));
	         }
	         **/
	        
	        Date date1 = utility.getDate(2012, 1, 1);
	        System.out.println( date1 );
	        
	        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS aaa");
	        String pretty_time = formatter.format( date1 );

	        System.out.println( pretty_time );
	        
	        Date date2 = utility.getDate(2010, 1, 1);
	        System.out.println( date2 );
	        String pretty_time2 = formatter.format( date2 );

	        System.out.println( pretty_time2 );
	        
	        System.out.println(utility.timeDifferenceLessThanOrEqualTo(date1, date2, Calendar.YEAR, 2));


	    }
}
