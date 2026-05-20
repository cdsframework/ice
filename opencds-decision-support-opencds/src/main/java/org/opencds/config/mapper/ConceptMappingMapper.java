package org.opencds.config.mapper;

import java.util.stream.Collectors;

import org.opencds.config.api.model.Concept;
import org.opencds.config.api.model.ConceptMapping;
import org.opencds.config.schema.ConceptMapping.FromConcepts;
import org.opencds.config.schema.NamespacedConcept;
import org.springframework.util.ObjectUtils;

public abstract class ConceptMappingMapper
{
    public static ConceptMapping internal(final org.opencds.config.schema.ConceptMapping external)
    {
        if (external == null)
            return null;

        return new ConceptMapping(ConceptMapper.internal(external.getToConcept()), external.getFromConcepts()
                .stream()
                .flatMap(fromConcepts -> fromConcepts.getConcept()
                        .stream()
                        .map(fromConcept -> new Concept(fromConcept.getCode(), fromConcepts.getCodeSystem(),
                                fromConcepts.getCodeSystemName(), fromConcept.getDisplayName(), fromConcept.getComment(), null)))
                .toList());
    }

    public static org.opencds.config.schema.ConceptMapping external(final ConceptMapping internal)
    {
        if (internal == null)
            return null;

        final org.opencds.config.schema.ConceptMapping external = new org.opencds.config.schema.ConceptMapping();
        if (internal.toConcept() != null)
        {
            final NamespacedConcept nc = new NamespacedConcept();
            nc.setCode(internal.toConcept().code());
            nc.setCodeSystem(internal.toConcept().codeSystem());
            nc.setCodeSystemName(internal.toConcept().codeSystemName());
            nc.setDisplayName(internal.toConcept().displayName());
            nc.setComment(internal.toConcept().comment());
            nc.setValueSet(ValueSetMapper.external(internal.toConcept().valueSet()));
            external.setToConcept(nc);
        }

        if (!ObjectUtils.isEmpty(internal.fromConcepts()))
        {
            internal.fromConcepts().stream().collect(Collectors.groupingBy(Concept::codeSystem)).values().forEach(values ->
            {
                FromConcepts fromConcepts = null;
                for (final Concept internalConcept : values)
                {
                    if (fromConcepts == null)
                    {
                        fromConcepts = new FromConcepts();
                        fromConcepts.setCodeSystem(internalConcept.codeSystem());
                        fromConcepts.setCodeSystemName(internalConcept.codeSystemName());
                    }
                    final org.opencds.config.schema.Concept externalConcept;
                    externalConcept = ConceptMapper.external(internalConcept);
                    fromConcepts.getConcept().add(externalConcept);
                }
                external.getFromConcepts().add(fromConcepts);
            });
        }

        return external;
    }
}
