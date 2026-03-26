package org.opencds.common.utilities;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import lombok.Getter;

@Getter
public class AbsoluteTimeDifference implements Serializable
{
    @Serial
    private static final long serialVersionUID = 178221508897490900L;

    protected long yearDifference;
    protected long monthDifference;
    protected long dayDifference;
    protected long hourDifference;
    protected long minuteDifference;
    protected long secondDifference;
    protected long millisecondDifference;

    public AbsoluteTimeDifference(final Date time1, final Date time2, int highestReturnedCalendarTimeUnit,
            final boolean ignoreSmallTimeUnits, int highestCalendarTimeUnitToIgnore)
    {
        initialize();

        if (highestCalendarTimeUnitToIgnore == Calendar.HOUR)
            highestCalendarTimeUnitToIgnore = Calendar.HOUR_OF_DAY;
        if (highestReturnedCalendarTimeUnit == Calendar.HOUR)
            highestReturnedCalendarTimeUnit = Calendar.HOUR_OF_DAY;

        if (!time1.equals(time2))
        {
            final Calendar laterTime = new GregorianCalendar();
            final Calendar earlierTime = new GregorianCalendar();
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

            if (ignoreSmallTimeUnits)
            {
                clearThisTimeUnitAndBelow(laterTime, highestCalendarTimeUnitToIgnore);
                clearThisTimeUnitAndBelow(earlierTime, highestCalendarTimeUnitToIgnore);
            }

            setTimeDifferenceForUnitAndBelow(laterTime, earlierTime, highestReturnedCalendarTimeUnit);
        }
    }

    protected void initialize()
    {
        yearDifference = 0;
        monthDifference = 0;
        dayDifference = 0;
        hourDifference = 0;
        minuteDifference = 0;
        secondDifference = 0;
        millisecondDifference = 0;
    }

    protected void clearThisTimeUnitAndBelow(final Calendar time, final int highestCalendarTimeUnitToIgnore)
    {
        if (highestCalendarTimeUnitToIgnore == Calendar.YEAR)
        {
            time.set(Calendar.YEAR, 1);
            clearThisTimeUnitAndBelow(time, Calendar.MONTH);
        }
        else
            if (highestCalendarTimeUnitToIgnore == Calendar.MONTH)
            {
                time.set(Calendar.MONTH, 1);
                clearThisTimeUnitAndBelow(time, Calendar.DATE);
            }
            else
                if (highestCalendarTimeUnitToIgnore == Calendar.DATE || highestCalendarTimeUnitToIgnore == Calendar.DAY_OF_WEEK
                        || highestCalendarTimeUnitToIgnore == Calendar.DAY_OF_WEEK_IN_MONTH
                        || highestCalendarTimeUnitToIgnore == Calendar.DAY_OF_YEAR)

                {
                    time.set(Calendar.DATE, 1);
                    clearThisTimeUnitAndBelow(time, Calendar.HOUR_OF_DAY);
                }
                else
                    if ((highestCalendarTimeUnitToIgnore == Calendar.HOUR) || (highestCalendarTimeUnitToIgnore
                            == Calendar.HOUR_OF_DAY))
                    {
                        time.set(Calendar.HOUR_OF_DAY, 0);
                        clearThisTimeUnitAndBelow(time, Calendar.MINUTE);
                    }
                    else
                        if (highestCalendarTimeUnitToIgnore == Calendar.MINUTE)
                        {
                            time.set(Calendar.MINUTE, 0);
                            clearThisTimeUnitAndBelow(time, Calendar.SECOND);
                        }
                        else
                            if (highestCalendarTimeUnitToIgnore == Calendar.SECOND)
                            {
                                time.set(Calendar.SECOND, 0);
                                clearThisTimeUnitAndBelow(time, Calendar.MILLISECOND);
                            }
                            else
                                if (highestCalendarTimeUnitToIgnore == Calendar.MILLISECOND)
                                    time.set(Calendar.MILLISECOND, 0);
                                else
                                {
                                    System.err.println(
                                            "Error in AbsoluteTimeDifference.clearThisTimeUnitAndBelow; time unit to ignore of <"
                                                    + highestCalendarTimeUnitToIgnore + "> not expected.");
                                }
    }

    protected void setTimeDifferenceForUnitAndBelow(final Calendar laterTime, final Calendar earlierTime,
            final int calendarTimeUnit)
    {
        if (calendarTimeUnit == Calendar.YEAR)
        {
            yearDifference = getTimeDifferenceForUnit(laterTime, earlierTime, calendarTimeUnit);
            setTimeDifferenceForUnitAndBelow(laterTime, earlierTime, Calendar.MONTH);
        }
        else
            if (calendarTimeUnit == Calendar.MONTH)
            {
                monthDifference = getTimeDifferenceForUnit(laterTime, earlierTime, calendarTimeUnit);
                setTimeDifferenceForUnitAndBelow(laterTime, earlierTime, Calendar.DATE);
            }
            else
                if (calendarTimeUnit == Calendar.DATE || calendarTimeUnit == Calendar.DAY_OF_WEEK
                        || calendarTimeUnit == Calendar.DAY_OF_WEEK_IN_MONTH || calendarTimeUnit == Calendar.DAY_OF_YEAR)

                {
                    dayDifference = getTimeDifferenceForUnit(laterTime, earlierTime, Calendar.DATE);
                    setTimeDifferenceForUnitAndBelow(laterTime, earlierTime, Calendar.HOUR_OF_DAY);
                }
                else
                    if ((calendarTimeUnit == Calendar.HOUR) || (calendarTimeUnit == Calendar.HOUR_OF_DAY))
                    {
                        hourDifference = getTimeDifferenceForUnit(laterTime, earlierTime, Calendar.HOUR_OF_DAY);
                        setTimeDifferenceForUnitAndBelow(laterTime, earlierTime, Calendar.MINUTE);
                    }
                    else
                        if (calendarTimeUnit == Calendar.MINUTE)
                        {
                            minuteDifference = getTimeDifferenceForUnit(laterTime, earlierTime, calendarTimeUnit);
                            setTimeDifferenceForUnitAndBelow(laterTime, earlierTime, Calendar.SECOND);
                        }
                        else
                            if (calendarTimeUnit == Calendar.SECOND)
                            {
                                secondDifference = getTimeDifferenceForUnit(laterTime, earlierTime, calendarTimeUnit);
                                setTimeDifferenceForUnitAndBelow(laterTime, earlierTime, Calendar.MILLISECOND);
                            }
                            else
                                if (calendarTimeUnit == Calendar.MILLISECOND)
                                    millisecondDifference = getTimeDifferenceForUnit(laterTime, earlierTime, calendarTimeUnit);
                                else
                                {
                                    System.err.println(
                                            "Error in AbsoluteTimeDifference.setTimeDifferenceForUnitAndBelow; time unit of <"
                                                    + calendarTimeUnit + "> not expected.");
                                }
    }

    protected long getTimeDifferenceForUnit(final Calendar laterTime, final Calendar earlierTime, final int calendarTimeUnit)
    {
        long privateTimeUnitDifference = 0;

        boolean twoTimesAreClose = false;
        while (!twoTimesAreClose)
        {
            final int approxDifForUnit = (int) DateUtility.getInstance()
                    .getApproximateTimeDifference(laterTime.getTime(), earlierTime.getTime(), calendarTimeUnit, false);
            earlierTime.add(calendarTimeUnit, approxDifForUnit);
            privateTimeUnitDifference += approxDifForUnit;

            if ((approxDifForUnit == 0) || ((approxDifForUnit > 0) && (approxDifForUnit < 100)) || ((approxDifForUnit < 0) && (
                    approxDifForUnit > -100)))
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

            if (!earlierTime.equals(laterTime))
            {
                earlierTime.add(calendarTimeUnit, -1);
                privateTimeUnitDifference--;
            }
        }
        else
            if (earlierTime.after(laterTime))
            {
                while (earlierTime.after(laterTime))
                {
                    earlierTime.add(calendarTimeUnit, -1);
                    privateTimeUnitDifference--;
                }
            }

        return privateTimeUnitDifference;
    }
}
