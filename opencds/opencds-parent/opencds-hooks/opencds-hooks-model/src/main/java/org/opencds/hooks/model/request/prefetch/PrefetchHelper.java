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

import java.util.function.Function;

import ca.uhn.fhir.context.FhirContext;
import org.opencds.hooks.model.util.TypeUtil;

public interface PrefetchHelper<O, R> extends Function<Function<Class<?>, Object>, PrefetchResult<O, R>> {

    default PrefetchResult<O, R> apply(Function<Class<?>, Object> f) {
        Object o = f.apply(getResourceType());
        R resource = TypeUtil.cast(getResourceType(), o);
        if (resource == null) {
            return PrefetchResult.nullResult();
        }
        if (getOperationOutcomeClass().isAssignableFrom(resource.getClass())) {
            O oo = TypeUtil.cast(getOperationOutcomeClass(), o);
            return PrefetchResult.withOperationOutcome(oo);
        } else {
            return PrefetchResult.withResource(resource);
        }
    }

    Class<O> getOperationOutcomeClass();

    Class<R> getResourceType();

}
