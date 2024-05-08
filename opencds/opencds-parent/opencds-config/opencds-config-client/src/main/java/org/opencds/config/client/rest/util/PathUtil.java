/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
