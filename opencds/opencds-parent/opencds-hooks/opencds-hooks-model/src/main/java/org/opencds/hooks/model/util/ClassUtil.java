/*
 * Copyright 2020 OpenCDS.org
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

package org.opencds.hooks.model.util;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Stream;

import com.google.common.reflect.ClassPath;

public class ClassUtil {

    public static <R> Stream<Class<R>> getResourceClassesAssignableFrom(String packageName, Class<R> targetClass) {
        try {
            return ClassPath.from(ClassUtil.class.getClassLoader()).getTopLevelClasses(packageName).stream()
                    .map(ci -> ci.load()).filter(cls -> targetClass.isAssignableFrom(cls))
                    .map(cls -> targetClass.getClass().cast(cls));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Stream.empty();
    }


    public static <R> Class<R> resolveType(String pkg, String type) {
        try {
            return (Class<R>) Class.forName(pkg + "." + type);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
