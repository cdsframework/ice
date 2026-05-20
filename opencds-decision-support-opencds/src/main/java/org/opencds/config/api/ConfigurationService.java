package org.opencds.config.api;

import java.util.Set;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.strategy.ConfigStrategy;
import org.springframework.util.ObjectUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ConfigurationService
{
    private final KnowledgeRepository knowledgeRepository;

    public ConfigurationService(final Set<ConfigStrategy> configStrategies, final ConfigData configData)
    {
        if (ObjectUtils.isEmpty(configStrategies))
            throw new IllegalArgumentException("At least one configuration strategy must be provided.");

        final ConfigStrategy configStrategy = configStrategies.stream().filter(cs -> cs.supports(configData.configType()))
                .findFirst()
                .orElseThrow(() -> new OpenCDSRuntimeException("Unsupported configuration type: " + configData.configType()));

        final var knowledgeRepository = configStrategy.getKnowledgeRepository(configData);
        knowledgeRepository.knowledgePackageService()
                .preloadKnowledgePackages(knowledgeRepository.knowledgeModuleService().getAll());
        this.knowledgeRepository = knowledgeRepository;

        log.info("Configuration loaded.");
    }
}
