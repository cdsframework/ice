package org.opencds.config.client.rest.util;

public class PathUtil {
    private static final String SEPARATOR = "/";

    public static String buildPath(String path, String object) {
        return path + SEPARATOR + object;
    }

    public static String buildPath(String path, String object, String path2) {
        return buildPath(buildPath(path, object), path2);
    }

    public static String buildPath(String path, String object, String path2, String object2) {
        return buildPath(buildPath(path, object, path2), object2);
    }

    public static String buildPath(String path, String object, String path2, String object2, String path3) {
        return buildPath(buildPath(path, object, path2, object2), path3);
    }
    
}
