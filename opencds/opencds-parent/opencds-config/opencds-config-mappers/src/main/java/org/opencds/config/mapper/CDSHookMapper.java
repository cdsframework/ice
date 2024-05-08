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

package org.opencds.config.mapper;

import org.opencds.config.api.model.CDSHook;
import org.opencds.config.api.model.FhirVersion;
import org.opencds.config.api.model.impl.CDSHookImpl;

public class CDSHookMapper {

    public static CDSHook internal(org.opencds.config.schema.CDSHook external) {
        if (external == null) {
            return null;
        }
        return CDSHookImpl.create(external.getHook(), external.getId(), external.getTitle(), external.getDescription(),
                PrefetchMapper.internal(external.getPrefetch()), FhirVersion.valueOf(external.getFhirVersion()));
    }

    public static org.opencds.config.schema.CDSHook external(CDSHook internal) {
        if (internal == null) {
            return null;
        }
        org.opencds.config.schema.CDSHook cdsHook = new org.opencds.config.schema.CDSHook();
        cdsHook.setHook(internal.getHook());
        cdsHook.setTitle(internal.getTitle());
        cdsHook.setDescription(internal.getDescription());
        cdsHook.setId(internal.getId());
        cdsHook.setPrefetch(PrefetchMapper.external(internal.getPrefetch()));
        cdsHook.setFhirVersion(internal.getFhirVersion().toString());
        return cdsHook;
    }

}
