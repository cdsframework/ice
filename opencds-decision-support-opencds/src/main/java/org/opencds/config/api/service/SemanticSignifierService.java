package org.opencds.config.api.service;

import java.util.Map;
import java.util.stream.Collectors;

import org.opencds.config.api.dao.SemanticSignifierDao;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;

public class SemanticSignifierService
{
    private final Map<SSId, SemanticSignifier> semanticSignifierMap;

    public SemanticSignifierService(final SemanticSignifierDao dao)
    {
        semanticSignifierMap = dao.getAll()
                .stream()
                .map(ss -> Map.entry(ss.ssId(), ss))
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public SemanticSignifier find(final SSId ssId)
    {
        return semanticSignifierMap.get(ssId);
    }
}
