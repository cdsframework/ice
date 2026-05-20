package org.cdsframework.ice.service.conversion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.opencds.vmr.v1_0.schema.IVLTS;
import org.springframework.util.StringUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
class ConversionDateSupport
{
    private static final DateTimeFormatter VMR_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    static LocalDate parseIsoLocalDate(final String value)
    {
        if (!StringUtils.hasText(value))
            return null;

        try
        {
            return LocalDate.parse(value);
        }
        catch (final DateTimeParseException ignored)
        {
        }

        try
        {
            return OffsetDateTime.parse(value).toLocalDate();
        }
        catch (final DateTimeParseException ignored)
        {
        }

        try
        {
            return LocalDateTime.parse(value).toLocalDate();
        }
        catch (final DateTimeParseException nested)
        {
            log.error("Failed to parse ISO local date/dateTime: {}", value, nested);
        }

        return null;
    }

    static TimeInterval toTimeInterval(final IVLTS ivlts)
    {
        if (ivlts == null)
            return null;

        return new TimeInterval(parseVmrDate(ivlts.getLow()), parseVmrDate(ivlts.getHigh()));
    }

    private static LocalDate parseVmrDate(final String value)
    {
        if (value == null || value.length() <= 7)
            return null;

        try
        {
            return LocalDate.parse(value.substring(0, 8), VMR_DATE_FORMAT);
        }
        catch (final DateTimeParseException e)
        {
            log.error("Failed to parse VMR date: {}", value, e);
        }

        return null;
    }
}
