package org.opencds.config.api.model.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.ConceptMapping;

public record ConceptDeterminationMethodImpl(CDMId cdmId,
                                             String displayName,
                                             String description,
                                             Date timestamp,
                                             String userId,
                                             List<ConceptMapping> conceptMappings) implements ConceptDeterminationMethod
{
    public static ConceptDeterminationMethodImpl create(final CDMId cdmId, final String displayName, final String description,
            final Date timestamp, final String userId, final List<ConceptMapping> conceptMappings)
    {
        return new ConceptDeterminationMethodImpl(CDMIdImpl.create(cdmId), displayName, description, timestamp, userId,
                ConceptMappingImpl.create(conceptMappings));
    }

    public static ConceptDeterminationMethodImpl create(final ConceptDeterminationMethod cdm)
    {
        if (cdm == null)
            return null;
        if (cdm instanceof final ConceptDeterminationMethodImpl conceptDeterminationMethodImpl)
            return conceptDeterminationMethodImpl;
        return create(cdm.getCDMId(), cdm.getDisplayName(), cdm.getDescription(), cdm.getTimestamp(), cdm.getUserId(),
                cdm.getConceptMappings());
    }

    public ConceptDeterminationMethodImpl
    {
        assert cdmId != null;
        assert timestamp != null;
        conceptMappings = conceptMappings == null ? Collections.emptyList() : Collections.unmodifiableList(conceptMappings);
    }

    @Override
    public CDMId getCDMId()
    {
        return cdmId;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public Date getTimestamp()
    {
        return timestamp;
    }

    @Override
    public String getUserId()
    {
        return userId;
    }

    @Override
    public List<ConceptMapping> getConceptMappings()
    {
        return conceptMappings;
    }
}
