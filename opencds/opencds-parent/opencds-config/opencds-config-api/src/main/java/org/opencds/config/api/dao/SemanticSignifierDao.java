package org.opencds.config.api.dao;

import java.util.List;

import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;

public interface SemanticSignifierDao {

    SemanticSignifier find(SSId ssId);

    List<SemanticSignifier> getAll();

    void persist(SemanticSignifier ss);
    
    void persist(List<SemanticSignifier> sses);

    void delete(SemanticSignifier ss);

}
