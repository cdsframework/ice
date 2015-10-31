package org.opencds.config.api.service;

import java.util.List;

import org.opencds.common.interfaces.ResultSetBuilder;
import org.opencds.config.api.FactListsBuilder;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;

public interface SemanticSignifierService {
    SemanticSignifier find(SSId ssId);
    
    List<SemanticSignifier> getAll();
    
    void persist(SemanticSignifier ss);
    
    void persist(List<SemanticSignifier> sses);
    
    void delete(SSId ssId);
    
    FactListsBuilder getFactListsBuilder(SSId ssId);
    
    <T> ResultSetBuilder<T> getResultSetBuilder(SSId ssId);
}
