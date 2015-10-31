package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.Concept;
import org.opencds.config.api.model.ConceptMapping;

public class ConceptMappingImpl implements ConceptMapping {

    private ConceptImpl toConcept;
    private List<Concept> fromConcepts;

    private ConceptMappingImpl() {
    }

    public static ConceptMappingImpl create(Concept toConcept, List<Concept> fromConcepts) {
        ConceptMappingImpl cmi = new ConceptMappingImpl();
        cmi.toConcept = ConceptImpl.create(toConcept);
        cmi.fromConcepts = ConceptImpl.create(fromConcepts);
        return cmi;
    }
    
    public static ConceptMappingImpl create(ConceptMapping cm) {
        if (cm == null) {
            return null;
        }
        if (cm instanceof ConceptMappingImpl) {
            return ConceptMappingImpl.class.cast(cm);
        }
        return create(cm.getToConcept(), cm.getFromConcepts());
    }
    
    public static List<ConceptMapping> create(List<ConceptMapping> conceptMappings) {
        if (conceptMappings == null) {
            return null;
        }
        List<ConceptMapping> cmis = new ArrayList<>();
        if (conceptMappings != null) {
            for (ConceptMapping cm : conceptMappings) {
                cmis.add(ConceptMappingImpl.create(cm));
            }
        }
        return cmis;
    }

    @Override
    public Concept getToConcept() {
        return toConcept;
    }

    @Override
    public List<Concept> getFromConcepts() {
        return fromConcepts;
    }

}
