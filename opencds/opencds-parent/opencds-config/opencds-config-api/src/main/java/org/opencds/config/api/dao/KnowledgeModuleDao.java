package org.opencds.config.api.dao;

import java.util.List;

import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;

public interface KnowledgeModuleDao {

    KnowledgeModule find(KMId kmId);

    List<KnowledgeModule> getAll();

    void persist(KnowledgeModule km);

    void persist(List<KnowledgeModule> kms);
    
    void delete(KnowledgeModule km);

}
