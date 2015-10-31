package org.opencds.config.api.dao;

import java.util.List;

import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;

public interface ConceptDeterminationMethodDao {

    ConceptDeterminationMethod find(CDMId cdmId);
    
    List<ConceptDeterminationMethod> getAll();

    void persist(ConceptDeterminationMethod cdm);

    void persist(List<ConceptDeterminationMethod> internal);
    
    void delete(ConceptDeterminationMethod cdm);

}
