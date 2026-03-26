package org.opencds.config.api.service;

import java.util.List;
import java.util.function.Predicate;

import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;

public interface KnowledgeModuleService
{
    KnowledgeModule find(KMId identifier);

    KnowledgeModule find(String requestedKmId);

    KnowledgeModule find(Predicate<? super KnowledgeModule> predicate);

    List<KnowledgeModule> getAll();

    List<KnowledgeModule> getAll(Predicate<? super KnowledgeModule> predicate);

    void persist(KnowledgeModule km);

    void persist(List<KnowledgeModule> kms);

    void delete(KMId identifier);
}
