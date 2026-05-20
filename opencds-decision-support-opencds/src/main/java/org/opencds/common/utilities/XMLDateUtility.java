package org.opencds.common.utilities;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import lombok.experimental.UtilityClass;

@UtilityClass
public class XMLDateUtility
{
    private static XMLGregorianCalendar localDateTime2XMLGregorian(final LocalDate date)
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

        return dataTypeFactory.newXMLGregorianCalendar(GregorianCalendar.from(date.atStartOfDay(ZoneId.systemDefault())));
    }

    public static XMLGregorianCalendar date2XMLGregorian(final LocalDate date)
    {
        return localDateTime2XMLGregorian(date);
    }

    public static LocalDate xmlGregorian2Date(final XMLGregorianCalendar xmlGC)
    {
        return xmlGC.toGregorianCalendar().toZonedDateTime().toLocalDate();
    }
}
