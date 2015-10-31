package org.opencds.config.api.service;

import java.io.InputStream;
import java.util.List;

import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;

public interface KnowledgeModuleService {
    KnowledgeModule find(KMId identifier);
    
    KnowledgeModule find(String requestedKmId);
    
    List<KnowledgeModule> getAll();
    
    void persist(KnowledgeModule km);
    
    void persist(List<KnowledgeModule> kms);
    
    void delete(KMId identifier);

    InputStream getKnowledgePackage(KMId kmId);

    void persistKnowledgePackage(KMId kmId, InputStream knowledgePackage);

    void deleteKnowledgePackage(KMId kmId);
}
