package org.opencds.config.api.model.impl;

import org.opencds.config.api.model.Concept;
import org.opencds.config.api.model.ConceptView;

public record ConceptViewImpl(Concept toConcept,
                              String cdmCode) implements ConceptView
{
    public static ConceptViewImpl create(final Concept toConcept, final String cdmCode)
    {
        return new ConceptViewImpl(toConcept, cdmCode);
    }

    @Override
    public Concept getToConcept()
    {
        return toConcept;
    }

    @Override
    public String getCdmCode()
    {
        return cdmCode;
    }
}
