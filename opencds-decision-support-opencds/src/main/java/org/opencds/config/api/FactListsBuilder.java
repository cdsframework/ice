package org.opencds.config.api;

import java.util.List;
import java.util.Map;

import org.opencds.config.api.model.KnowledgeModule;

public interface FactListsBuilder
{
    Map<Class<?>, List<?>> buildFactLists(KnowledgeRepository knowledgeRepository, KnowledgeModule knowledgeModule, Object payload,
            java.util.Date evalTime);
}
