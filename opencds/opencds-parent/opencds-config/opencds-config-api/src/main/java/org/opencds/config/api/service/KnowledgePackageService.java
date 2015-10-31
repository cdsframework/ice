package org.opencds.config.api.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.opencds.config.api.model.KnowledgeModule;

public interface KnowledgePackageService {
    
    /**
     * The implementation of this method needs to know where the knowledge packages exist on the backend.
     * 
     * If the underlying implementation cannot be resolved (e.g., file not found), this method returns null;
     * 
     * @param knowledgeModule
     * @return
     * @throws IOException 
     */
    InputStream getPackageInputStream(KnowledgeModule knowledgeModule);

    void persistPackageInputStream(KnowledgeModule km, InputStream knowledgePackage);

    void deletePackage(KnowledgeModule km);
    
    /**
     * This method is used to get a cached instance of some knowledge package, of some defined type <code>T</code>.
     */
    <T> T getPackage(KnowledgeModule knowledgeModule);
    
    /**
     * This method is used to cache an instance of some knowledge package, of some defined type <code>T</code>.
     */
    <T> void putPackage(KnowledgeModule knowledgeModule, T knowledgePackage);

    /**
     * This method is used to load all knowledge packages (where preload == true) into the cache.
     */
    void preloadKnowledgePackages(List<KnowledgeModule> knowledgeModules);

    <KP> KP borrowKnowledgePackage(KnowledgeModule knowledgeModule);

    <KP> void returnKnowledgePackage(KnowledgeModule knowledgeModule, KP knowledgePackage);

}
