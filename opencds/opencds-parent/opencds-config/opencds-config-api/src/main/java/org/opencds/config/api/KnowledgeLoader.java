package org.opencds.config.api;

import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.service.KnowledgePackageService;

public interface KnowledgeLoader<T> {
    T loadKnowledgePackage(KnowledgePackageService knowledgePackageService, KnowledgeModule knowledgeModule);
}
