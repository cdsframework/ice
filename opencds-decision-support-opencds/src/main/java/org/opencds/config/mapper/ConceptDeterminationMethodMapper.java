package org.opencds.config.mapper;

import java.util.List;

import org.opencds.common.utilities.XMLDateUtility;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.schema.ConceptDeterminationMethods;

public abstract class ConceptDeterminationMethodMapper
{
    public static ConceptDeterminationMethod internal(final org.opencds.config.schema.ConceptDeterminationMethod external)
    {
        if (external == null)
            return null;

        return new ConceptDeterminationMethod(new CDMId(external.getCodeSystem(), external.getCode(), external.getVersion()),
                external.getDisplayName(), external.getDescription(),
                external.getTimestamp().toGregorianCalendar().toZonedDateTime().toLocalDate(), external.getUserId(),
                external.getConceptMapping().stream().map(ConceptMappingMapper::internal).toList());
    }

    public static List<ConceptDeterminationMethod> internal(final ConceptDeterminationMethods cdms)
    {
        if (cdms == null)
            return null;

        return cdms.getConceptDeterminationMethod().stream().map(ConceptDeterminationMethodMapper::internal).toList();
    }

    public static org.opencds.config.schema.ConceptDeterminationMethod external(final ConceptDeterminationMethod internal)
    {
        if (internal == null)
            return null;

        final org.opencds.config.schema.ConceptDeterminationMethod external =
                new org.opencds.config.schema.ConceptDeterminationMethod();

        external.setCode(internal.cdmId().code());
        external.setCodeSystem(internal.cdmId().codeSystem());
        external.setVersion(internal.cdmId().version());

        external.setDisplayName(internal.displayName());
        external.setTimestamp(XMLDateUtility.date2XMLGregorian(internal.timestamp()));
        external.setUserId(internal.userId());

        internal.conceptMappings().stream().map(ConceptMappingMapper::external).forEach(external.getConceptMapping()::add);

        return external;
    }

    public static ConceptDeterminationMethods external(final List<ConceptDeterminationMethod> cdmList)
    {
        if (cdmList == null)
            return null;

        final ConceptDeterminationMethods cdms = new ConceptDeterminationMethods();

        cdmList.stream().map(ConceptDeterminationMethodMapper::external).forEach(cdms.getConceptDeterminationMethod()::add);

        return cdms;
    }
}
