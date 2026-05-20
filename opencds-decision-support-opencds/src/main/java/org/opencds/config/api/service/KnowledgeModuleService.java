package org.opencds.config.api.service;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.opencds.config.api.dao.KnowledgeModuleDao;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;

public class KnowledgeModuleService
{
    private final Map<KMId, KnowledgeModule> knowledgeModuleMap;

    public KnowledgeModuleService(final KnowledgeModuleDao dao)
    {
        this.knowledgeModuleMap = dao.getAll()
                .stream()
                .map(km -> Map.entry(km.kmId(), km))
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public KnowledgeModule find(final KMId kmId)
    {
        return knowledgeModuleMap.get(kmId);
    }

    public KnowledgeModule find(final String stringKmId)
    {
        return find(new KMId(stringKmId));
    }

    public KnowledgeModule find(final Predicate<? super KnowledgeModule> predicate)
    {
        return knowledgeModuleMap.values().stream().filter(predicate).findFirst().orElse(null);
    }

    public List<KnowledgeModule> getAll(final Predicate<? super KnowledgeModule> predicate)
    {
        return knowledgeModuleMap.values().stream().filter(predicate).toList();
    }

    public List<KnowledgeModule> getAll()
    {
        return List.copyOf(knowledgeModuleMap.values());
    }
}
