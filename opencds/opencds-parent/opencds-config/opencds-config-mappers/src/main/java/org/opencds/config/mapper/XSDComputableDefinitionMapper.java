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