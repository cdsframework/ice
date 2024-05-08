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

package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencds.config.api.model.XSDComputableDefinition;

public class XSDComputableDefinitionImpl implements XSDComputableDefinition {

    private String rootGlobalElementName;
    private String url;
    private List<String> schematronUrls;
    private String narrativeModelRestrictionGuideUrl;

    private XSDComputableDefinitionImpl() {
    }

    public static XSDComputableDefinitionImpl create(String rootGlobalElementName, String url,
            List<String> schematronUrls, String narrativeModelRestrictionGuideUrl) {
        XSDComputableDefinitionImpl cdi = new XSDComputableDefinitionImpl();
        cdi.rootGlobalElementName = rootGlobalElementName;
        cdi.url = url;
        cdi.schematronUrls = schematronUrls;
        cdi.narrativeModelRestrictionGuideUrl = narrativeModelRestrictionGuideUrl;
        return cdi;
    }

    public static XSDComputableDefinitionImpl create(XSDComputableDefinition cd) {
        if (cd == null) {
            return null;
        }
        if (cd instanceof XSDComputableDefinitionImpl) {
            return XSDComputableDefinitionImpl.class.cast(cd);
        }
        return create(cd.getRootGlobalElementName(), cd.getUrl(), cd.getSchematronUrls(),
                cd.getNarrativeModelRestrictionGuideUrl());
    }

    public static List<XSDComputableDefinition> create(List<XSDComputableDefinition> xsdComputableDefinitions) {
        if (xsdComputableDefinitions == null) {
            return null;
        }
        List<XSDComputableDefinition> cds = new ArrayList<>();
        for (XSDComputableDefinition cd : xsdComputableDefinitions) {
            cds.add(create(cd));
        }
        return cds;
    }

    @Override
    public String getRootGlobalElementName() {
        return rootGlobalElementName;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public List<String> getSchematronUrls() {
        return Collections.unmodifiableList(schematronUrls);
    }

    @Override
    public String getNarrativeModelRestrictionGuideUrl() {
        return narrativeModelRestrictionGuideUrl;
    }

}
