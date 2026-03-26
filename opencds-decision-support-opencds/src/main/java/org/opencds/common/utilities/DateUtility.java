package org.opencds.common.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtility
{
    private static final ThreadLocal<Map<String, SimpleDateFormat>> dateFormatters = ThreadLocal.withInitial(HashMap::new);

    @Getter
    private static final DateUtility instance = new DateUtility();

    private SimpleDateFormat getDateFormatter(final String pattern)
    {
        return dateFormatters.get().computeIfAbsent(pattern, SimpleDateFormat::new);
    }

    public Date getTime(final int year, final int month, final int day, final int hourOfDay, final int minute, final int second)
    {
        final Calendar calendar = new GregorianCalendar();
        calendar.set(year, month - 1, day, hourOfDay, minute, second);
        return calendar.getTime();
    }

    public Date getDateFromString(final String dateAsString, final String formatTemplate)

    {
        Date dateToReturn = null;

        try
        {
            dateToReturn = getDateFromStringOrThrow(dateAsString, formatTemplate);
        }
        catch (final Exception e)
        {
            log.warn("Invalid input '{}' for format '{}'", dateAsString, formatTemplate, e);
        }

        return dateToReturn;
    }

    public Date getDateFromStringOrThrow(final String dateAsString, final String formatTemplate) throws ParseException
    {
        final SimpleDateFormat formatter = getDateFormatter(formatTemplate);
        return formatter.parse(dateAsString);
    }

    public String getDateAsString(final Date date, final String formatTemplate)

    {
        String stringToReturn = null;

        try
        {
            final SimpleDateFormat formatter = getDateFormatter(formatTemplate);
            stringToReturn = formatter.format(date);
        }
        catch (final Exception e)
        {
            log.error(e.getMessage(), e);
        }

        return stringToReturn;
    }

    public double getApproximateTimeDifference(final Date date1, final Date date2, final int timeUnit,
            final boolean ignoreHoursMinutesSeconds)
    {
        final long date1AsLong;
        final long date2AsLong;

        final Calendar calendar = new GregorianCalendar();
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
            x = x * 60 * 60 * 24 * 365.25;
        else
            if (timeUnit == Calendar.MONTH)
                x = x * 60 * 60 * 24 * 30.4375;
            else
                if (timeUnit == Calendar.DATE || timeUnit == Calendar.DAY_OF_WEEK || timeUnit == Calendar.DAY_OF_WEEK_IN_MONTH
                        || timeUnit == Calendar.DAY_OF_YEAR)

                {
                    x = x * 60 * 60 * 24;
                }
                else
                    if ((timeUnit == Calendar.HOUR_OF_DAY) || (timeUnit == Calendar.HOUR))
                        x = x * 60 * 60;
                    else
                        if (timeUnit == Calendar.MINUTE)
                            x = x * 60;
                        else
                        {
                            if (timeUnit != Calendar.SECOND)
                            {
                                if (timeUnit == Calendar.MILLISECOND)
                                    x = x / 1000;
                                else
                                {
                                    System.err.println("Error in DateUtility.getTimeDifference: time unit of <" + timeUnit
                                            + "> not recognized.");
                                }
                            }
                        }

        return (date1AsLong - date2AsLong) / (x);
    }
}
