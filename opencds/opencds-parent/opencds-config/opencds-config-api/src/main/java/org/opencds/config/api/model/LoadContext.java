package org.opencds.config.api.model;

public enum LoadContext {
    CLASSPATH,
    IMPORTED;

    public static LoadContext resolve(String loadContext) {
        for (LoadContext lc : values()) {
            if (lc.toString().equalsIgnoreCase(loadContext)) {
                return lc;
            }
        }
        return null;
    }
}
