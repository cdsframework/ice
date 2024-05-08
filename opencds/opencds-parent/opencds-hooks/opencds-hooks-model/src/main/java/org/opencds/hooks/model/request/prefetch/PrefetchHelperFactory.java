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

package org.opencds.hooks.model.request.prefetch;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.hooks.model.request.prefetch.annotation.PrefetchFhirVersion;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PrefetchHelperFactory {
    private static final Log log = LogFactory.getLog(PrefetchHelperFactory.class);
    private static final Map<FhirVersionEnum, PrefetchHelper<?, ?>> prefetchHelperMap;

    static {
        prefetchHelperMap = new HashMap<>();
        Reflections reflections = new Reflections("org.opencds.hooks.model");
        Set<Class<?>> annotatedHelpers = reflections.getTypesAnnotatedWith(PrefetchFhirVersion.class);
        annotatedHelpers.forEach(clazz -> {
            if (!Modifier.isAbstract(clazz.getModifiers())) {
                PrefetchFhirVersion annotation = clazz.getAnnotation(PrefetchFhirVersion.class);
                try {
                    prefetchHelperMap.put(annotation.value(), (PrefetchHelper<?, ?>) clazz.newInstance());
                } catch (Exception e) {
                    log.warn("Could not instantiate class with default constructor: " + clazz.getCanonicalName());
                }
            }
        });
    }

    public static <O, R> PrefetchHelper<O, R> get(FhirVersionEnum fhirVersionEnum) {
        return (PrefetchHelper<O, R>) prefetchHelperMap.get(fhirVersionEnum);
    }

}
