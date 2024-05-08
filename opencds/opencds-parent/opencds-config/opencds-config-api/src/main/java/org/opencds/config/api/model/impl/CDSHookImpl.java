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

package org.opencds.config.api.model.impl;

import org.opencds.config.api.model.CDSHook;
import org.opencds.config.api.model.FhirVersion;
import org.opencds.config.api.model.Prefetch;

public class CDSHookImpl implements CDSHook {
    private String hook;
    private String id;
    private String title;
    private String description;
    private Prefetch prefetch;
    private FhirVersion fhirVersion;

    private CDSHookImpl() {
    }

    public static CDSHook create(String hook, String id, String title, String description, Prefetch prefetch,
            FhirVersion fhirVersion) {
        CDSHookImpl chi = new CDSHookImpl();
        chi.hook = hook;
        chi.id = id;
        chi.title = title;
        chi.description = description;
        chi.prefetch = PrefetchImpl.create(prefetch);
        chi.fhirVersion = fhirVersion;
        return chi;
    }

    public static CDSHook create(CDSHook cdsHook) {
        if (cdsHook == null) {
            return null;
        }
        return create(cdsHook.getHook(), cdsHook.getId(), cdsHook.getTitle(), cdsHook.getDescription(),
                cdsHook.getPrefetch(), cdsHook.getFhirVersion());
    }

    @Override
    public String getHook() {
        return hook;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Prefetch getPrefetch() {
        return prefetch;
    }

    public FhirVersion getFhirVersion() {
        return fhirVersion;
    }

}
