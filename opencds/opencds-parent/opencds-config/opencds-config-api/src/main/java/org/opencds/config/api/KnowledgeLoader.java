package org.opencds.config.api;

import java.util.Collection;

import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.service.KnowledgePackageService;

public interface KnowledgeLoader<T> {
    Collection<T> loadKnowledgePackages(KnowledgePackageService knowledgePackageService, KnowledgeModule knowledgeModule, int count);
}
