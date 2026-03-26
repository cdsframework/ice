package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opencds.config.api.model.Concept;
import org.opencds.config.api.model.ValueSet;

public record ConceptImpl(String code,
                          String codeSystem,
                          String codeSystemName,
                          String displayName,
                          String comment,
                          ValueSetImpl valueSet) implements Concept
{
    public static ConceptImpl create(final String code, final String codeSystem, final String codeSystemName,
            final String displayName, final String comment, final ValueSet valueSet)
    {
        return new ConceptImpl(code, codeSystem, codeSystemName, displayName, comment, ValueSetImpl.create(valueSet));
    }

    public static ConceptImpl create(final Concept concept)
    {
        if (concept == null)
            return null;
        if (concept instanceof final ConceptImpl conceptImpl)
            return conceptImpl;
        return create(concept.getCode(), concept.getCodeSystem(), concept.getCodeSystemName(), concept.getDisplayName(),
                concept.getComment(), concept.getValueSet());
    }

    public static List<Concept> create(final List<Concept> fromConcepts)
    {
        if (fromConcepts == null)
            return null;
        final var cis = new ArrayList<Concept>();
        for (final var c : fromConcepts)
            cis.add(create(c));
        return cis;
    }

    public ConceptImpl
    {
        assert StringUtils.isNotBlank(code);
        assert StringUtils.isNotBlank(codeSystem);
    }

    @Override
    public String getCode()
    {
        return code;
    }

    @Override
    public String getCodeSystem()
    {
        return codeSystem;
    }

    @Override
    public String getCodeSystemName()
    {
        return codeSystemName;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public String getComment()
    {
        return comment;
    }

    @Override
    public ValueSet getValueSet()
    {
        return valueSet;
    }
}
