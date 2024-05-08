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

import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.impl.CDMIdImpl;
import org.opencds.config.schema.ConceptDeterminationMethodBase;

public abstract class CDMIdMapper {

    public static CDMId internal(org.opencds.config.schema.ConceptDeterminationMethodBase external) {
        if (external == null) {
            return null;
        }
        return CDMIdImpl.create(external.getCodeSystem(), external.getCode(), external.getVersion());
    }

    public static ConceptDeterminationMethodBase external(CDMId internal) {
        if (internal == null) {
            return null;
        }
        ConceptDeterminationMethodBase external = new ConceptDeterminationMethodBase();
        external.setCode(internal.getCode());
        external.setCodeSystem(internal.getCodeSystem());
        external.setVersion(internal.getVersion());
        return external;
    }
}
