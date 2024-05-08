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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;

public final class PrefetchResult<O, R> {
    private static final Log log = LogFactory.getLog(PrefetchResult.class);
    
    private O outcome;
    private R resource;
    
    private PrefetchResult() {}
    
    public static <O, R> PrefetchResult<O, R> withOperationOutcome(O oo) {
        return new PrefetchResult<O, R>().setOperationOutcome(oo);
    }

    public static <O, R> PrefetchResult<O, R> withResource(R resource) {
        return new PrefetchResult<O, R>().setResource(resource);
    }
    
    public static <O, R> PrefetchResult<O, R> nullResult() {
        return new PrefetchResult<>();
    }

    public boolean hasResource() {
        return resource != null;
    }
    
    public <FR extends R> boolean hasResourceOfType(Class<FR> type) {
        return hasResource() && type.isAssignableFrom(resource.getClass());
    }
    
    public Class<? extends Object> getResourceType() {
        if (resource == null) {
            return null;
        }
        return resource.getClass();
    }
    
    public <FR extends R> FR getResource(Class<FR> type) {
        Class<? extends Object> resourceType = getResourceType();
        if (resourceType != null && type.isAssignableFrom(resourceType)) {
            return type.cast(resource);
        }
        log.error("Incompatible types.");
        throw new OpenCDSRuntimeException("Resource is not the requested type: " + type.getCanonicalName()
                + "; Resource type: " + resourceType);
    }
    
    public boolean hasOutcome() {
        return outcome != null;
    }
    
    public O getOutcome() {
        return outcome;
    }
    
    private PrefetchResult<O, R> setResource(R resource) {
        this.resource = resource;
        return this;
    }

    private PrefetchResult<O, R> setOperationOutcome(O oo) {
        this.outcome = oo;
        return this;
    }
    
    
    
}
