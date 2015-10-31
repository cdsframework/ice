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
