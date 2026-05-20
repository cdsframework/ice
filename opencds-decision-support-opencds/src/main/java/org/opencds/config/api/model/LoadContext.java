package org.opencds.config.api.model;

import java.util.Arrays;

public enum LoadContext
{
    CLASSPATH,
    IMPORTED;

    public static LoadContext resolve(final String loadContext)
    {
        return Arrays.stream(values()).filter(lc -> lc.toString().equalsIgnoreCase(loadContext)).findFirst().orElse(null);
    }
}
