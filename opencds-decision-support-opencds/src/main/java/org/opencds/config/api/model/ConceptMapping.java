package org.opencds.config.api.model;

import java.util.List;

import org.springframework.util.ObjectUtils;

public record ConceptMapping(Concept toConcept,
                             List<Concept> fromConcepts)
{
    public ConceptMapping
    {
        assert toConcept != null;
        assert !ObjectUtils.isEmpty(fromConcepts);
    }
}
