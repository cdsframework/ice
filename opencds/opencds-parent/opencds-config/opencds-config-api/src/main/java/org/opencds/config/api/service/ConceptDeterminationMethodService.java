package org.opencds.config.api.service;

import java.util.List;
import java.util.Map;

import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.SecondaryCDM;
import org.opencds.config.api.model.SupportMethod;

public interface ConceptDeterminationMethodService {
    ConceptDeterminationMethod find(CDMId cdmId);
    
    List<ConceptDeterminationMethod> getAll();
    
    void persist(ConceptDeterminationMethod cdm);
    
    void persist(List<ConceptDeterminationMethod> internal);
    
    void delete(CDMId cdm);

    Map<ConceptDeterminationMethod, SupportMethod> find(List<SecondaryCDM> secondaryCDMs);
}
