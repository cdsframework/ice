package org.opencds.config.api.model;

import java.time.LocalDate;
import java.util.List;

public record ConceptDeterminationMethod(CDMId cdmId,
                                         String displayName,
                                         String description,
                                         LocalDate timestamp,
                                         String userId,
                                         List<ConceptMapping> conceptMappings)
{
    public ConceptDeterminationMethod
    {
        assert cdmId != null;
        assert timestamp != null;
        conceptMappings = conceptMappings == null ? List.of() : List.copyOf(conceptMappings);
    }
}
