package org.opencds.config.api.model;

import java.util.List;

public interface ConceptMapping {
    Concept getToConcept();
    
    List<Concept> getFromConcepts();
}
