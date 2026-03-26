package org.opencds.config.api;

import java.util.Set;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.utilities.ClassUtil;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.strategy.ConfigStrategy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigurationService
{
    private final ConfigStrategy configStrategy;
    private final Class<? extends CacheService> cacheServiceClass;
    private final ConfigData configData;

    @Getter
    private KnowledgeRepository knowledgeRepository;

    public ConfigurationService(final Set<ConfigStrategy> configStrategies, final Class<? extends CacheService> cacheServiceClass,
            final ConfigData configData)
    {
        if (configStrategies == null || configStrategies.isEmpty())
            throw new IllegalArgumentException("At least one configuration strategy must be provided.");
        this.cacheServiceClass = cacheServiceClass;
        this.configData = configData;
        ConfigStrategy strategy = null;
        for (final ConfigStrategy configStrategy : configStrategies)
        {
            if (configStrategy.supports(configData.configType()))
            {
                strategy = configStrategy;
                break;
            }
        }
        if (strategy == null)
            throw new OpenCDSRuntimeException("Unsupported configuration type: " + configData.configType());
        configStrategy = strategy;
        loadConfiguration();
        log.info("Configuration loaded.");
    }

    private void loadConfiguration()
    {
        final var cacheService = ClassUtil.newInstance(cacheServiceClass);
        final var knowledgeRepository = configStrategy.getKnowledgeRepository(configData, cacheService);
        knowledgeRepository.knowledgePackageService()
                .preloadKnowledgePackages(knowledgeRepository.knowledgeModuleService().getAll());
        this.knowledgeRepository = knowledgeRepository;
    }
}
