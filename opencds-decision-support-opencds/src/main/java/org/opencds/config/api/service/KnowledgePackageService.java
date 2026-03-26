package org.opencds.config.api.service;

import java.io.InputStream;
import java.util.List;

import org.opencds.config.api.model.KnowledgeModule;

public interface KnowledgePackageService
{
    void deletePackage(KnowledgeModule km);

    <KP> KP getKnowledgePackage(KnowledgeModule knowledgeModule);

    InputStream getPackageInputStream(KnowledgeModule knowledgeModule);

    void persistPackageInputStream(KnowledgeModule km, InputStream knowledgePackage);

    void preloadKnowledgePackages(List<KnowledgeModule> knowledgeModules);
}
