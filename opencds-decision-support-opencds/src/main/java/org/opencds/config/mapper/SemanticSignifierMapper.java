package org.opencds.config.mapper;

import java.util.List;

import org.opencds.common.utilities.XMLDateUtility;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.schema.SemanticSignifierId;
import org.opencds.config.schema.SemanticSignifiers;
import org.opencds.service.evaluate.CDSInputEntryPoint;
import org.opencds.service.evaluate.CDSOutputExitPoint;
import org.opencds.service.evaluate.CdsInputFactListsBuilder;
import org.opencds.service.evaluate.CdsOutputResultSetBuilder;

public abstract class SemanticSignifierMapper
{
    public static SemanticSignifier internal(final org.opencds.config.schema.SemanticSignifier external)
    {
        if (external == null)
            return null;

        return new SemanticSignifier(
                new SSId(external.getIdentifier().getScopingEntityId(), external.getIdentifier().getBusinessId(),
                        external.getIdentifier().getVersion()), external.getName(), external.getDescription(),
                external.getTimestamp().toGregorianCalendar().toZonedDateTime().toLocalDate(), external.getUserId());

    }

    public static List<SemanticSignifier> internal(final SemanticSignifiers semanticSignifiers)
    {
        if (semanticSignifiers == null || semanticSignifiers.getSemanticSignifier() == null)
            return null;

        return semanticSignifiers.getSemanticSignifier().stream().map(SemanticSignifierMapper::internal).toList();
    }

    public static org.opencds.config.schema.SemanticSignifier external(final SemanticSignifier internal)
    {
        if (internal == null)
            return null;

        final org.opencds.config.schema.SemanticSignifier external = new org.opencds.config.schema.SemanticSignifier();

        external.setName(internal.name());
        external.setDescription(internal.description());
        external.setEntryPoint(CDSInputEntryPoint.class.getName());
        external.setExitPoint(CDSOutputExitPoint.class.getName());
        external.setFactListsBuilder(CdsInputFactListsBuilder.class.getName());
        external.setResultSetBuilder(CdsOutputResultSetBuilder.class.getName());
        external.setTimestamp(XMLDateUtility.date2XMLGregorian(internal.timestamp()));
        external.setUserId(internal.userId());

        final SemanticSignifierId externalSSId = new SemanticSignifierId();
        externalSSId.setBusinessId(internal.ssId().businessId());
        externalSSId.setScopingEntityId(internal.ssId().scopingEntityId());
        externalSSId.setVersion(internal.ssId().version());
        external.setIdentifier(externalSSId);

        return external;
    }

    public static SemanticSignifiers external(final List<SemanticSignifier> sses)
    {
        final SemanticSignifiers externalSSes = new SemanticSignifiers();

        sses.stream().map(SemanticSignifierMapper::external).forEach(externalSSes.getSemanticSignifier()::add);

        return externalSSes;
    }
}
