package org.opencds.common.utilities;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import lombok.experimental.UtilityClass;

@UtilityClass
public class XMLDateUtility
{
    private static XMLGregorianCalendar long2XMLGregorian(final long dateAsLong)
    {
        final DatatypeFactory dataTypeFactory;
        try
        {
            dataTypeFactory = DatatypeFactory.newInstance();
        }
        catch (final DatatypeConfigurationException e)
        {
            throw new RuntimeException(e);
        }
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(dateAsLong);
        return dataTypeFactory.newXMLGregorianCalendar(gc);
    }

    public static XMLGregorianCalendar date2XMLGregorian(final Date date)
    {
        return long2XMLGregorian(date.getTime());
    }

    public static GregorianCalendar xmlGregorian2Gregorian(final XMLGregorianCalendar xmlGC)
    {
        return xmlGC.toGregorianCalendar();
    }

    public static Date xmlGregorian2Date(final XMLGregorianCalendar xmlGC)
    {
        return xmlGC.toGregorianCalendar().getTime();
    }
}
