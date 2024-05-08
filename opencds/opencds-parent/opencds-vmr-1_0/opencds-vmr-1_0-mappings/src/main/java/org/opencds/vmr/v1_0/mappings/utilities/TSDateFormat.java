package org.opencds.vmr.v1_0.mappings.utilities;

/**
 * Utility to identify the correct date format pattern based on a given lexical date input.
 */
public class TSDateFormat {
    /*
     * This array stores all supported timestamp format variations.
     *
     * For each variation we populate we add a format at index i, where i is the length of the input string that should
     * correspond to that format.
     *
     * For example, the format "yyyyMMddHHmm" should match an input whose length = 12, so that format is added at index 12.
     *
     * That allows us to find the appropriate format extremely efficiently without doing substring operations or pattern matching,
     * which is beneficial because we currently parse dates from the vMR potentially thousands of times for a large vMR.
     * We simply lookup the format based solely on the length of the input string. This only works because the lengths of all
     * currently supported formats are unique.
     *
     * For example, if the input comes in as "202109101230" (length=12) then we know the correct pattern is at index 12 (yyyyMMddHHmm).
     *
     * This is a bit unusual, but it is extremely efficient.
     */
    private static final String[] supportedFormats = new String[19];

    static {
        // Non-zoned formats
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

    // Zoned formats. These are treated separately so they don't overlap with the non-zoned ones
    // The important thing is that the array index still matches the *input* date string length.
    private static final String[] supportedZonedFormats = new String[24];

    static {
        // The length of the input will be the length of the date/time part + 5 (for the offset part)
        // e.g. if the input string is 20220826125959-0700, then that is 19 and corresponds to supportedZonedFormats[19]
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

    public static String forInput(String input) {
        if (hasZoneOffset(input))
            return findFormat(input, supportedZonedFormats);
        else
            return findFormat(input, supportedFormats);
    }

    private static String findFormat(String input, String[] potentialFormats) {
        int inputLength = input.length();
        if (inputLength > potentialFormats.length - 1)
            throw new IllegalArgumentException("Unsupported date pattern for input: '" + input + "'");

        String format = potentialFormats[inputLength];
        if (format == null)
            throw new IllegalArgumentException("Unsupported date pattern for input: '" + input + "'");
        return format;
    }

    private static boolean hasZoneOffset(String input) {
        if (input.length() <= 5)
            return false;
        char c = input.charAt(input.length() - 5);
        return c == '+' || c == '-';
    }
}
