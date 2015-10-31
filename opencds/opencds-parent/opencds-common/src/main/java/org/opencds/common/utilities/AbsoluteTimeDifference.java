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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;







/**
 * <p>AbsoluteTimeDifference represents an absolute time difference between two
 * java.util.Date times.  The class has a single constructor that
 * sets all of its returnable parameters.
 * A TimeDifference consists of a yearDifference, monthDifference,
 * dayDifference, hourDifference, minuteDifference, secondDifference,
 * and millisecondDifference.  These all contain int values.</p>
 * <p/>
 * <p>Under normal circumstanes, a TimeDifference would be expected
 * to have the highest time unit populated before moving on to
 * the next time unit; e.g. 500 days would be represented as
 * 1 year several months and several days.  However, if a client
 * wishes to designate some Calendar.timeUnit (e.g. Calendar.DATE)
 * as the highest time unit populated, 500 days would be returned,
 * for example, as 0 years 0 months and 500 days.</p>
 * <p/>
 * <p>Also, if a client wishes to ignore timeUnit differences between two times, then
 * this can be set.  For example, if it is requested that
 * 11/12/04 11pm be compared against 11/13/04 1am, usually the
 * difference would be less than 1 day because 24 hours has not
 * passed.  However, if it is requested that hours and all lower
 * time units be ignored, then these units will be set to the
 * same value so that they can be ignored --> i.e. the times will
 * now be 11/12/04 00:00:00 and 11/13/04 00:00:00, so that the
 * difference now will be 1 day.</p>
 * <p/>
 * <p>Also note: this class does NOT use approximations of month and
 * year values (e.g. 30 days in month, 365 days in year).  Instead,
 * it uses Calendar functions to accurately calculate year and month
 * differences.  Thus, for example, if two dates both occur on the
 * same date of the same month, they will ALWAYS be different only
 * by X years, with no differences in month, day, etc.
 * <p/>
 * <p>Initial Create Date: 10-17-04</p>
 * <p>Last Update Date: 7-21-05</p>
 * <p>Version history: v1.00 (10-17-04).  Initial creation.  v1.01 (7-21-05).  Modifying to fix infinite loop
 * problem that was occurring in getTimeDifferenceForUnit while loop.</p>
 *
 * @author Kensaku Kawamoto
 * @version 1.01
 */
//TODO:  support week differences, which are often used in pediatrics

public class AbsoluteTimeDifference extends Object implements Serializable
{
	    /**
	 * 
	 */
	private static final long serialVersionUID = 178221508897490900L;
		protected long myYearDifference;
	    protected long myMonthDifference;
	    protected long myDayDifference;
	    protected long myHourDifference;
	    protected long myMinuteDifference;
	    protected long mySecondDifference;
	    protected long myMillisecondDifference;

	    /**
	     * Creates new AbsoluteTimeDifference.  time1 and time2 are the times from which the difference is to be computed.
	     * Note that, as the name indicates, the difference is non-directional.  See the class description of
	     * edu.duke.mc.dci.sebastiancommon.objects.AbsoluteTimeDifference for a detailed description of how the other
	     * parameters should be specified.
	     *
	     * If highestReturnedCalendarTimeUnit or highestCalendarTimeUnitToIgnore are Calendar.HOUR (12-hr time),
	     * they will be converted to Calendar.HOUR_OF_DAY (24-hr military time)
	     *
	     * @param time1
	     * @param time2
	     * @param highestReturnedCalendarTimeUnit
	     *
	     * @param ignoreSmallTimeUnits
	     * @param highestCalendarTimeUnitToIgnore
	     *
	     */
	    public AbsoluteTimeDifference(Date time1, Date time2, int highestReturnedCalendarTimeUnit,
	                                  boolean ignoreSmallTimeUnits, int highestCalendarTimeUnitToIgnore)
	    {
	        initialize();

	        if (highestCalendarTimeUnitToIgnore == Calendar.HOUR)
	        {
	            highestCalendarTimeUnitToIgnore = Calendar.HOUR_OF_DAY;
	        }
	        if (highestReturnedCalendarTimeUnit == Calendar.HOUR)
	        {
	            highestReturnedCalendarTimeUnit = Calendar.HOUR_OF_DAY;
	        }

	        // first, check to see if the times are equal to the millisecond.  Only proceed if different,
	        // as differences already set to 0
	        if (!time1.equals(time2))
	        {
	            // set calendars to the earlier/later times
	            Calendar laterTime = new GregorianCalendar();
	            Calendar earlierTime = new GregorianCalendar();
	            if (time1.after(time2))
	            {
	                laterTime.setTime(time1);
	                earlierTime.setTime(time2);
	            }
	            else
	            {
	                laterTime.setTime(time2);
	                earlierTime.setTime(time1);
	            }

	            // clear time units that should be normalized
	            if (ignoreSmallTimeUnits)
	            {
	                clearThisTimeUnitAndBelow(laterTime, highestCalendarTimeUnitToIgnore);
	                clearThisTimeUnitAndBelow(earlierTime, highestCalendarTimeUnitToIgnore);
	            }

	            // compute time difference
	            setTimeDifferenceForUnitAndBelow(laterTime, earlierTime, highestReturnedCalendarTimeUnit);
	        }
	    }

	    protected void initialize()
	    {
	        myYearDifference = 0;
	        myMonthDifference = 0;
	        myDayDifference = 0;
	        myHourDifference = 0;
	        myMinuteDifference = 0;
	        mySecondDifference = 0;
	        myMillisecondDifference = 0;
	    }

	    // helper functions

	    /**
	     * Resets calendar's time units at or below the designated time unit.  E.g. if timeUnit to ignore is
	     * Calendar.HOUR_OF_DAY, Calendar.HOUR_OF_DAY/MINUTES/SECONDS/MILLISECONDS will all be reset to be the same value for
	     * timeA and timeB (specifically, 0).  The reset value will be 1 for days, months, and years.  Note that 1
	     * for month corresponds to February, not January.
	     *
	     * @param time
	     * @param highestCalendarTimeUnitToIgnore
	     *
	     */
	    protected void clearThisTimeUnitAndBelow(Calendar time, int highestCalendarTimeUnitToIgnore)
	    {
	        if (highestCalendarTimeUnitToIgnore == Calendar.YEAR)
	        {
	            time.set(Calendar.YEAR, 1);
	            clearThisTimeUnitAndBelow(time, Calendar.MONTH);
	        }
	        else if (highestCalendarTimeUnitToIgnore == Calendar.MONTH)
	        {
	            time.set(Calendar.MONTH, 1); // note this is February, not January
	            clearThisTimeUnitAndBelow(time, Calendar.DATE);
	        }
	        else if ((highestCalendarTimeUnitToIgnore == Calendar.DATE) ||
	                (highestCalendarTimeUnitToIgnore == Calendar.DAY_OF_MONTH) ||
	                (highestCalendarTimeUnitToIgnore == Calendar.DAY_OF_WEEK) ||
	                (highestCalendarTimeUnitToIgnore == Calendar.DAY_OF_WEEK_IN_MONTH) ||
	                (highestCalendarTimeUnitToIgnore == Calendar.DAY_OF_YEAR))
	        // including the various choices above in case of user confusion
	        {
	            time.set(Calendar.DATE, 1);
	            clearThisTimeUnitAndBelow(time, Calendar.HOUR_OF_DAY);
	        }
	        else if ((highestCalendarTimeUnitToIgnore == Calendar.HOUR) ||
	                (highestCalendarTimeUnitToIgnore == Calendar.HOUR_OF_DAY))
	        {
	            time.set(Calendar.HOUR_OF_DAY, 0);
	            clearThisTimeUnitAndBelow(time, Calendar.MINUTE);
	        }
	        else if (highestCalendarTimeUnitToIgnore == Calendar.MINUTE)
	        {
	            time.set(Calendar.MINUTE, 0);
	            clearThisTimeUnitAndBelow(time, Calendar.SECOND);
	        }
	        else if (highestCalendarTimeUnitToIgnore == Calendar.SECOND)
	        {
	            time.set(Calendar.SECOND, 0);
	            clearThisTimeUnitAndBelow(time, Calendar.MILLISECOND);
	        }
	        else if (highestCalendarTimeUnitToIgnore == Calendar.MILLISECOND)
	        {
	            time.set(Calendar.MILLISECOND, 0);
	        }
	        else
	        {
	            System.err.println("Error in AbsoluteTimeDifference.clearThisTimeUnitAndBelow; time unit to ignore of <" +
	                    highestCalendarTimeUnitToIgnore + "> not expected.");
	        }
	    }

	    /**
	     * Sets the time difference for calendarTimeUnit and below.
	     *
	     * @param laterTime
	     * @param earlierTime
	     * @param calendarTimeUnit
	     */
	    protected void setTimeDifferenceForUnitAndBelow(Calendar laterTime, Calendar earlierTime, int calendarTimeUnit)
	    {
	        // Algorithm:
	        // - start with the highest time unit that the user wishes to have returned
	        // - get the approximate difference in the two times using approximation procedures
	        //   (get difference in ms then convert to appropriate unit, with conversions of
	        //    30.4375 days in month, 365.25 days in year)
	        // - start by adding the approximate difference in two times as the nearest int, while
	        //   incrementing the internal time difference by that amount
	        // - do this while approximate difference is large (needed because the difference may
	        //   be larger than the max values allowed by int's)
	        // - if earlierTime < laterTime, increment by 1 until equal or earlier
	        // - if earlierTime > laterTime, decrement by 1 until equal or earlier


	        if (calendarTimeUnit == Calendar.YEAR)
	        {
	            myYearDifference = getTimeDifferenceForUnit(laterTime, earlierTime, calendarTimeUnit);
	            setTimeDifferenceForUnitAndBelow(laterTime, earlierTime, Calendar.MONTH);
	        }
	        else if (calendarTimeUnit == Calendar.MONTH)
	        {
	            myMonthDifference = getTimeDifferenceForUnit(laterTime, earlierTime, calendarTimeUnit);
	            setTimeDifferenceForUnitAndBelow(laterTime, earlierTime, Calendar.DATE);
	        }
	        else if ((calendarTimeUnit == Calendar.DATE) ||
	                (calendarTimeUnit == Calendar.DAY_OF_MONTH) ||
	                (calendarTimeUnit == Calendar.DAY_OF_WEEK) ||
	                (calendarTimeUnit == Calendar.DAY_OF_WEEK_IN_MONTH) ||
	                (calendarTimeUnit == Calendar.DAY_OF_YEAR))
	        // including the various choices above in case of user confusion
	        {
	            myDayDifference = getTimeDifferenceForUnit(laterTime, earlierTime, Calendar.DATE);
	            setTimeDifferenceForUnitAndBelow(laterTime, earlierTime, Calendar.HOUR_OF_DAY);
	        }
	        else if ((calendarTimeUnit == Calendar.HOUR) ||
	                (calendarTimeUnit == Calendar.HOUR_OF_DAY))
	        {
	            myHourDifference = getTimeDifferenceForUnit(laterTime, earlierTime, Calendar.HOUR_OF_DAY); // use 24-hr clock
	            setTimeDifferenceForUnitAndBelow(laterTime, earlierTime, Calendar.MINUTE);
	        }
	        else if (calendarTimeUnit == Calendar.MINUTE)
	        {
	            myMinuteDifference = getTimeDifferenceForUnit(laterTime, earlierTime, calendarTimeUnit);
	            setTimeDifferenceForUnitAndBelow(laterTime, earlierTime, Calendar.SECOND);
	        }
	        else if (calendarTimeUnit == Calendar.SECOND)
	        {
	            mySecondDifference = getTimeDifferenceForUnit(laterTime, earlierTime, calendarTimeUnit);
	            setTimeDifferenceForUnitAndBelow(laterTime, earlierTime, Calendar.MILLISECOND);
	        }
	        else if (calendarTimeUnit == Calendar.MILLISECOND)
	        {
	            myMillisecondDifference = getTimeDifferenceForUnit(laterTime, earlierTime, calendarTimeUnit);
	        }
	        else
	        {
	            System.err.println("Error in AbsoluteTimeDifference.setTimeDifferenceForUnitAndBelow; time unit of <" +
	                    calendarTimeUnit + "> not expected.");
	        }
	    }
	    
	    /**
	     * Returns -1 if calendarTimeUnit not recognized.
	     * @param calendarTimeUnit
	     * @return
	     */
	    public long getTimeDifferenceForUnit(int calendarTimeUnit)
	    {
	    	if (calendarTimeUnit == Calendar.MILLISECOND)
	    	{
	    		return getMillisecondDifference();
	    	} 
	    	else if (calendarTimeUnit == Calendar.SECOND)
	    	{
	    		return getSecondDifference();
	    	}
	    	else if (calendarTimeUnit == Calendar.MINUTE)
	    	{
	    		return getMinuteDifference();
	    	}
	    	else if ((calendarTimeUnit == Calendar.HOUR) || (calendarTimeUnit == Calendar.HOUR_OF_DAY)) 
	    	{
	    		return getHourDifference();
	    	}
	    	else if (calendarTimeUnit == Calendar.DATE) // Note: purposely not supporting other date-related enumerations 
	    	{
	    		return getDayDifference();
	    	}
	    	else if (calendarTimeUnit == Calendar.MONTH)
	    	{
	    		return getMonthDifference();
	    	}
	    	else if (calendarTimeUnit == Calendar.YEAR)
	    	{
	    		return getYearDifference();
	    	}
	    	return -1;
	    }

	    /**
	     * Helper function for setTimeDifferenceForUnitAndBelow; increments earlierTime to the appropriate value, and
	     * returns the appropriate privateTimeUnitDifference
	     * @param laterTime
	     * @param earlierTime
	     */
	    protected long getTimeDifferenceForUnit(Calendar laterTime, Calendar earlierTime, int calendarTimeUnit)
	    {
	        // - start by adding the approximate difference in two times as the nearest int, while
	        //   incrementing the internal time difference by that amount
	        // - do this while approximate difference is large (needed because the difference may
	        //   be larger than the max values allowed by int's)
	        // - if earlierTime < laterTime, increment by 1 until equal or earlier
	        // - if earlierTime > laterTime, decrement by 1 until equal or earlier

	        long privateTimeUnitDifference = 0;

	        boolean twoTimesAreClose = false;
	        while (twoTimesAreClose == false)
	        {
	            int approxDifForUnit = (int) DateUtility.getInstance().getApproximateTimeDifference(laterTime.getTime(), earlierTime.getTime(), calendarTimeUnit, false);
	            earlierTime.add(calendarTimeUnit, approxDifForUnit);
	            privateTimeUnitDifference += approxDifForUnit;

	            if ((approxDifForUnit == 0) || ((approxDifForUnit > 0) && (approxDifForUnit < 100)) || ((approxDifForUnit < 0) && (approxDifForUnit > -100)))
	            {
	                twoTimesAreClose = true;
	            }
	        }

	        if (earlierTime.before(laterTime))
	        {
	            while (earlierTime.before(laterTime))
	            {
	                earlierTime.add(calendarTimeUnit, 1);
	                privateTimeUnitDifference++;
	            }

	            if (! earlierTime.equals(laterTime))
	            {
	                earlierTime.add(calendarTimeUnit, -1);
	                privateTimeUnitDifference--;
	            }
	        }
	        else if (earlierTime.after(laterTime))
	        {
	            while (earlierTime.after(laterTime))
	            {
	                earlierTime.add(calendarTimeUnit, -1);
	                privateTimeUnitDifference--;
	            }
	        }

	        return privateTimeUnitDifference;
	    }

	    // getter functions
	    public long getYearDifference()
	    {
	        return myYearDifference;
	    }

	    public long getMonthDifference()
	    {
	        return myMonthDifference;
	    }

	    public long getDayDifference()
	    {
	        return myDayDifference;
	    }

	    public long getHourDifference()
	    {
	        return myHourDifference;
	    }

	    public long getMinuteDifference()
	    {
	        return myMinuteDifference;
	    }

	    public long getSecondDifference()
	    {
	        return mySecondDifference;
	    }

	    public long getMillisecondDifference()
	    {
	        return myMillisecondDifference;
	    }

	    // debugging print function
	    public void print()
	    {
	        System.out.println("<< Absolute time difference: >>");
	        System.out.println("   - Year(s): " + myYearDifference);
	        System.out.println("   - Month(s): " + myMonthDifference);
	        System.out.println("   - Day(s): " + myDayDifference);
	        System.out.println("   - Hour(s): " + myHourDifference);
	        System.out.println("   - Minute(s): " + myMinuteDifference);
	        System.out.println("   - Second(s): " + mySecondDifference);
	        System.out.println("   - Millisecond(s): " + myMillisecondDifference);
	        System.out.println();
	    }
	

}
