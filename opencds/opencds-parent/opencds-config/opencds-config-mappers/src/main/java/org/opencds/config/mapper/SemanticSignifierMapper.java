package org.opencds.config.mapper;

import java.util.ArrayList;
import java.util.List;

import org.opencds.common.utilities.XMLDateUtility;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.model.XSDComputableDefinition;
import org.opencds.config.api.model.impl.SSIdImpl;
import org.opencds.config.api.model.impl.SemanticSignifierImpl;
import org.opencds.config.api.model.impl.XSDComputableDefinitionImpl;
import org.opencds.config.schema.SemanticSignifierId;
import org.opencds.config.schema.SemanticSignifiers;

public abstract class SemanticSignifierMapper {

    public static SemanticSignifier internal(org.opencds.config.schema.SemanticSignifier external) {
        if (external == null) {
            return null;
        }
        List<XSDComputableDefinition> xsdComputableDefinitions = new ArrayList<>();

        XSDComputableDefinition xsdinternal = XSDComputableDefinitionImpl.create(external.getXsdComputableDefinition()
                .getXsdRootGlobalElementName(), external.getXsdComputableDefinition().getXsdURL(), external
                .getXsdComputableDefinition().getSchematronURL(), external.getXsdComputableDefinition()
                .getNarrativeModelRestrictionGuideURL());

        xsdComputableDefinitions.add(xsdinternal);

        SSId ssid = SSIdImpl.create(external.getIdentifier().getScopingEntityId(), external.getIdentifier()
                .getBusinessId(), external.getIdentifier().getVersion());

        return SemanticSignifierImpl.create(ssid, external.getName(), external.getDescription(),
                xsdComputableDefinitions, external.getEntryPoint(), external.getExitPoint(),
                external.getFactListsBuilder(), external.getResultSetBuilder(), external.getTimestamp()
                        .toGregorianCalendar().getTime(), external.getUserId());

    }

    public static List<SemanticSignifier> internal(SemanticSignifiers semanticSignifiers) {
        if (semanticSignifiers == null || semanticSignifiers.getSemanticSignifier() == null) {
            return null;
        }
        List<SemanticSignifier> internalSSes = new ArrayList<>();
        for (org.opencds.config.schema.SemanticSignifier ss : semanticSignifiers.getSemanticSignifier()) {
            internalSSes.add(internal(ss));
        }
        return internalSSes;
    }

    public static org.opencds.config.schema.SemanticSignifier external(SemanticSignifier internal) {
        if (internal == null) {
            return null;
        }
        org.opencds.config.schema.SemanticSignifier external = new org.opencds.config.schema.SemanticSignifier();

        external.setName(internal.getName());
        external.setDescription(internal.getDescription());
        external.setEntryPoint(internal.getEntryPoint());
        external.setExitPoint(internal.getExitPoint());
        external.setFactListsBuilder(internal.getFactListsBuilder());
        external.setResultSetBuilder(internal.getResultSetBuilder());
        external.setTimestamp(XMLDateUtility.date2XMLGregorian(internal.getTimestamp()));
        external.setUserId(internal.getUserId());

        SemanticSignifierId externalSSId = new SemanticSignifierId();
        externalSSId.setBusinessId(internal.getSSId().getBusinessId());
        externalSSId.setScopingEntityId(internal.getSSId().getScopingEntityId());
        externalSSId.setVersion(internal.getSSId().getVersion());
        external.setIdentifier(externalSSId);

        org.opencds.config.schema.XSDComputableDefinition xsdExternal = new org.opencds.config.schema.XSDComputableDefinition();
        if (internal.getXSDComputableDefinitions().size() > 0) {
            XSDComputableDefinition xcdInternal = internal.getXSDComputableDefinitions().get(0);
            xsdExternal.setNarrativeModelRestrictionGuideURL(xcdInternal.getNarrativeModelRestrictionGuideUrl());
            xsdExternal.setXsdRootGlobalElementName(xcdInternal.getRootGlobalElementName());
            xsdExternal.setXsdURL(xcdInternal.getUrl());
            external.setXsdComputableDefinition(xsdExternal);
        }

        return external;
    }

    public static SemanticSignifiers external(List<SemanticSignifier> sses) {
        SemanticSignifiers externalSSes = new SemanticSignifiers();
        for (SemanticSignifier ss : sses) {
            externalSSes.getSemanticSignifier().add(external(ss));
        }
        return externalSSes;
    }

}
