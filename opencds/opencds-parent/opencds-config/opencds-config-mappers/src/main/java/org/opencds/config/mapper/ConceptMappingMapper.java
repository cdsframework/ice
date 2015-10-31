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

public abstract class ConceptMappingMapper {

    public static ConceptMapping internal(org.opencds.config.schema.ConceptMapping external) {
        if (external == null) {
            return null;
        }
        List<Concept> fromConceptsList = new ArrayList<>();
        for (FromConcepts fromConcepts : external.getFromConcepts()) {
            for (org.opencds.config.schema.Concept fromConcept : fromConcepts.getConcept()) {

                Concept concept = ConceptImpl.create(fromConcept.getCode(), fromConcepts.getCodeSystem(),
                        fromConcepts.getCodeSystemName(), fromConcept.getDisplayName());

                fromConceptsList.add(concept);
            }
        }
        return ConceptMappingImpl.create(ConceptImpl
                .create(external.getToConcept().getCode(), external.getToConcept().getCodeSystem(), external
                        .getToConcept().getCodeSystemName(), external.getToConcept().getDisplayName()),
                fromConceptsList);
    }

    public static org.opencds.config.schema.ConceptMapping external(ConceptMapping internal) {
        if (internal == null) {
            return null;
        }
        org.opencds.config.schema.ConceptMapping external = new org.opencds.config.schema.ConceptMapping();
        if (internal.getToConcept() != null) {
            NamespacedConcept nc = new NamespacedConcept();
            nc.setCode(internal.getToConcept().getCode());
            nc.setCodeSystem(internal.getToConcept().getCodeSystem());
            nc.setCodeSystemName(internal.getToConcept().getCodeSystemName());
            nc.setDisplayName(internal.getToConcept().getDisplayName());
            external.setToConcept(nc);
        }

        if (internal.getFromConcepts() != null && !internal.getFromConcepts().isEmpty()) {
            // grab the fromConcepts, group them by codeSystem
            // once we have our groups, we can build the Froms...
            Map<String, List<Concept>> conceptGroups = new HashMap<>();
            for (Concept intFromConcept : internal.getFromConcepts()) {
                if (!conceptGroups.containsKey(intFromConcept.getCodeSystem())) {
                    conceptGroups.put(intFromConcept.getCodeSystem(), new ArrayList<Concept>());
                }
                conceptGroups.get(intFromConcept.getCodeSystem()).add(intFromConcept);
            }

            for (Entry<String, List<Concept>> group : conceptGroups.entrySet()) {
                FromConcepts fromConcepts = null;
                for (Concept internalConcept : group.getValue()) {
                    if (fromConcepts == null) {
                        fromConcepts = new FromConcepts();
                        fromConcepts.setCodeSystem(internalConcept.getCodeSystem());
                        fromConcepts.setCodeSystemName(internalConcept.getCodeSystemName());
                        fromConcepts.setDisplayName(internalConcept.getDisplayName());
                    }
                    org.opencds.config.schema.Concept externalConcept;
                    externalConcept = ConceptMapper.external(internalConcept);
                    fromConcepts.getConcept().add(externalConcept);
                }
                external.getFromConcepts().add(fromConcepts);
            }
        }

        return external;
    }

}
