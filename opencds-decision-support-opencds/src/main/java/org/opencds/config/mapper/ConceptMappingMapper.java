package org.opencds.config.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.opencds.config.api.model.Concept;
import org.opencds.config.api.model.ConceptMapping;
import org.opencds.config.api.model.impl.ConceptImpl;
import org.opencds.config.api.model.impl.ConceptMappingImpl;
import org.opencds.config.schema.ConceptMapping.FromConcepts;
import org.opencds.config.schema.NamespacedConcept;

public abstract class ConceptMappingMapper
{
    public static ConceptMapping internal(final org.opencds.config.schema.ConceptMapping external)
    {
        if (external == null)
            return null;
        final List<Concept> fromConceptsList = new ArrayList<>();
        for (final FromConcepts fromConcepts : external.getFromConcepts())
        {
            for (final org.opencds.config.schema.Concept fromConcept : fromConcepts.getConcept())
            {
                final Concept concept =
                        ConceptImpl.create(fromConcept.getCode(), fromConcepts.getCodeSystem(), fromConcepts.getCodeSystemName(),
                                fromConcept.getDisplayName(), fromConcept.getComment(), null);

                fromConceptsList.add(concept);
            }
        }
        return ConceptMappingImpl.create(ConceptMapper.internal(external.getToConcept()), fromConceptsList);
    }

    public static org.opencds.config.schema.ConceptMapping external(final ConceptMapping internal)
    {
        if (internal == null)
            return null;
        final org.opencds.config.schema.ConceptMapping external = new org.opencds.config.schema.ConceptMapping();
        if (internal.getToConcept() != null)
        {
            final NamespacedConcept nc = new NamespacedConcept();
            nc.setCode(internal.getToConcept().getCode());
            nc.setCodeSystem(internal.getToConcept().getCodeSystem());
            nc.setCodeSystemName(internal.getToConcept().getCodeSystemName());
            nc.setDisplayName(internal.getToConcept().getDisplayName());
            nc.setComment(internal.getToConcept().getComment());
            nc.setValueSet(ValueSetMapper.external(internal.getToConcept().getValueSet()));
            external.setToConcept(nc);
        }

        if (internal.getFromConcepts() != null && !internal.getFromConcepts().isEmpty())
        {
            final Map<String, List<Concept>> conceptGroups = new HashMap<>();
            for (final Concept intFromConcept : internal.getFromConcepts())
            {
                if (!conceptGroups.containsKey(intFromConcept.getCodeSystem()))
                    conceptGroups.put(intFromConcept.getCodeSystem(), new ArrayList<>());
                conceptGroups.get(intFromConcept.getCodeSystem()).add(intFromConcept);
            }

            for (final Entry<String, List<Concept>> group : conceptGroups.entrySet())
            {
                FromConcepts fromConcepts = null;
                for (final Concept internalConcept : group.getValue())
                {
                    if (fromConcepts == null)
                    {
                        fromConcepts = new FromConcepts();
                        fromConcepts.setCodeSystem(internalConcept.getCodeSystem());
                        fromConcepts.setCodeSystemName(internalConcept.getCodeSystemName());
                    }
                    final org.opencds.config.schema.Concept externalConcept;
                    externalConcept = ConceptMapper.external(internalConcept);
                    fromConcepts.getConcept().add(externalConcept);
                }
                external.getFromConcepts().add(fromConcepts);
            }
        }

        return external;
    }
}
