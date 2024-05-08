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

import org.opencds.config.api.model.impl.XSDComputableDefinitionImpl;
import org.opencds.config.schema.XSDComputableDefinition;

public class XSDComputableDefinitionMapper {

    public static void internal(XSDComputableDefinition external) throws Exception {
        XSDComputableDefinitionImpl.create(external.getXsdRootGlobalElementName(), external.getXsdURL(),
                external.getSchematronURL(), external.getNarrativeModelRestrictionGuideURL());

    }

    public static XSDComputableDefinition pushOut(XSDComputableDefinitionImpl internal) throws Exception {

        XSDComputableDefinition external = new XSDComputableDefinition();

        external.setNarrativeModelRestrictionGuideURL(internal.getNarrativeModelRestrictionGuideUrl());
        external.setXsdRootGlobalElementName(internal.getRootGlobalElementName());
        external.setXsdURL(internal.getUrl());
        external.getSchematronURL().addAll(internal.getSchematronUrls());

        return external;
    }
}
