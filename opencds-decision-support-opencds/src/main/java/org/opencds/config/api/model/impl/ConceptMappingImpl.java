package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.Concept;
import org.opencds.config.api.model.ConceptMapping;

public record ConceptMappingImpl(ConceptImpl toConcept,
                                 List<Concept> fromConcepts) implements ConceptMapping
{
    public static ConceptMappingImpl create(final Concept toConcept, final List<Concept> fromConcepts)
    {
        return new ConceptMappingImpl(ConceptImpl.create(toConcept), ConceptImpl.create(fromConcepts));
    }

    public static ConceptMappingImpl create(final ConceptMapping cm)
    {
        if (cm == null)
            return null;
        if (cm instanceof final ConceptMappingImpl conceptMappingImpl)
            return conceptMappingImpl;
        return create(cm.getToConcept(), cm.getFromConcepts());
    }

    public static List<ConceptMapping> create(final List<ConceptMapping> conceptMappings)
    {
        if (conceptMappings == null)
            return null;
        final var cmis = new ArrayList<ConceptMapping>();
        for (final var cm : conceptMappings)
            cmis.add(ConceptMappingImpl.create(cm));
        return cmis;
    }

    public ConceptMappingImpl
    {
        assert toConcept != null;
        assert fromConcepts != null && !fromConcepts.isEmpty();
    }

    @Override
    public Concept getToConcept()
    {
        return toConcept;
    }

    @Override
    public List<Concept> getFromConcepts()
    {
        return fromConcepts;
    }
}
