package org.opencds.config.api.model;

public enum LoadContext
{
    CLASSPATH,
    IMPORTED;

    public static LoadContext resolve(final String loadContext)
    {
        for (final LoadContext lc : values())
        {
            if (lc.toString().equalsIgnoreCase(loadContext))
                return lc;
        }
        return null;
    }
}
