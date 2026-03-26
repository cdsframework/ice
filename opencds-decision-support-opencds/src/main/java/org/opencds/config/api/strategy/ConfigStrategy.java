package org.opencds.config.api.strategy;

import org.opencds.config.api.ConfigData;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.cache.CacheService;

public interface ConfigStrategy
{
    boolean supports(String configType);

    boolean isReloadable();

    KnowledgeRepository getKnowledgeRepository(ConfigData configData, CacheService cacheService);
}
