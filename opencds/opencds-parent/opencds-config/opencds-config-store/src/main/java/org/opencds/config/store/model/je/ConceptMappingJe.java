package org.opencds.config.store.model.je;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.Concept;
import org.opencds.config.api.model.ConceptMapping;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class ConceptMappingJe implements ConceptMapping {

    private ConceptJe toConcept;
    private List<Concept> fromConcepts;

    private ConceptMappingJe() {
    }

    public static ConceptMappingJe create(Concept toConcept, List<Concept> fromConcepts) {
        ConceptMappingJe cmje = new ConceptMappingJe();
        cmje.toConcept = ConceptJe.create(toConcept);
        cmje.fromConcepts = ConceptJe.create(fromConcepts);
        return cmje;
    }

    public static ConceptMappingJe create(ConceptMapping cm) {
        if (cm == null) {
            return null;
        }
        if (cm instanceof ConceptMappingJe) {
            return ConceptMappingJe.class.cast(cm);
        }
        return create(cm.getToConcept(), cm.getFromConcepts());
    }

    public static List<ConceptMapping> create(List<ConceptMapping> conceptMappings) {
        if (conceptMappings == null) {
            return null;
        }
        List<ConceptMapping> cms = new ArrayList<>();
        if (conceptMappings != null) {
            for (ConceptMapping cm : conceptMappings) {
                cms.add(create(cm));
            }
        }
        return cms;
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
