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

package org.opencds.config.mapper;

import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.impl.KMIdImpl;

public abstract class KMIdMapper {

    public static KMId internal(org.opencds.config.schema.KMId external) {
        if (external == null) {
            return null;
        }
        return KMIdImpl.create(external.getScopingEntityId(), external.getBusinessId(), external.getVersion());
    }

    public static org.opencds.config.schema.KMId external(KMId internal) {
        if (internal == null) {
            return null;
        }
        org.opencds.config.schema.KMId external = new org.opencds.config.schema.KMId();
        external.setBusinessId(internal.getBusinessId());
        external.setScopingEntityId(internal.getScopingEntityId());
        external.setVersion(internal.getVersion());
        return external;
    }
}
