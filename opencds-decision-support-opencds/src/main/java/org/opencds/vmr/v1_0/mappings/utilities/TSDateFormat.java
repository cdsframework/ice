package org.opencds.vmr.v1_0.mappings.utilities;

public class TSDateFormat
{
    private static final String[] supportedFormats = new String[19];

    private static final String[] supportedZonedFormats = new String[24];

    static
    {
        supportedFormats[4] = "yyyy";
        supportedFormats[5] = "yyyyM";
        supportedFormats[6] = "yyyyMM";
        supportedFormats[7] = "yyyyMMd";
        supportedFormats[8] = "yyyyMMdd";
        supportedFormats[9] = "yyyyMMddH";
        supportedFormats[10] = "yyyyMMddHH";
        supportedFormats[11] = "yyyyMMddHHm";
        supportedFormats[12] = "yyyyMMddHHmm";
        supportedFormats[13] = "yyyyMMddHHmms";
        supportedFormats[14] = "yyyyMMddHHmmss";
        supportedFormats[16] = "yyyyMMddHHmmss.S";
        supportedFormats[17] = "yyyyMMddHHmmss.SS";
        supportedFormats[18] = "yyyyMMddHHmmss.SSS";
    }

    static
    {
        supportedZonedFormats[9] = "yyyyZ";
        supportedZonedFormats[10] = "yyyyMZ";
        supportedZonedFormats[11] = "yyyyMMZ";
        supportedZonedFormats[12] = "yyyyMMdZ";
        supportedZonedFormats[13] = "yyyyMMddZ";
        supportedZonedFormats[14] = "yyyyMMddHZ";
        supportedZonedFormats[15] = "yyyyMMddHHZ";
        supportedZonedFormats[16] = "yyyyMMddHHmZ";
        supportedZonedFormats[17] = "yyyyMMddHHmmZ";
        supportedZonedFormats[18] = "yyyyMMddHHmmsZ";
        supportedZonedFormats[19] = "yyyyMMddHHmmssZ";
        supportedZonedFormats[21] = "yyyyMMddHHmmss.SZ";
        supportedZonedFormats[22] = "yyyyMMddHHmmss.SSZ";
        supportedZonedFormats[23] = "yyyyMMddHHmmss.SSSZ";
    }

    public static String forInput(final String input)
    {
        if (hasZoneOffset(input))
            return findFormat(input, supportedZonedFormats);
        else
            return findFormat(input, supportedFormats);
    }

    private static String findFormat(final String input, final String[] potentialFormats)
    {
        final int inputLength = input.length();
        if (inputLength > potentialFormats.length - 1)
            throw new IllegalArgumentException("Unsupported date pattern for input: '" + input + "'");

        final String format = potentialFormats[inputLength];
        if (format == null)
            throw new IllegalArgumentException("Unsupported date pattern for input: '" + input + "'");
        return format;
    }

    private static boolean hasZoneOffset(final String input)
    {
        if (input.length() <= 5)
            return false;
        final char c = input.charAt(input.length() - 5);
        return c == '+' || c == '-';
    }
}
