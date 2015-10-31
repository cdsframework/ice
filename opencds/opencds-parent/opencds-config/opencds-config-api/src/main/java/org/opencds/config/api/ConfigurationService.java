package org.opencds.config.api;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.strategy.ConfigStrategy;

/**
 * {@link ConfigurationService} is responsible for interacting with the
 * underlying {@link ConfigStrategy} to perform the following operations:
 * <ol>
 * <li>Build a link to the configuration for the system.</li>
 * <li>Inject the required dependencies into the services this service provides.
 * </li>
 * </ol>
 * 
 * The {@link ConfigStrategy} instance is responsible for providing the DAOs
 * used by the services.
 * 
 * @author phillip
 *
 */
public class ConfigurationService {
    private static final Log log = LogFactory.getLog(ConfigurationService.class);
    private final ConfigStrategy configStrategy;
    private final CacheService cacheService;
    private final ConfigData configData;

    private KnowledgeRepository knowledgeRepository;

    public KnowledgeRepository getKnowledgeRepository() {
        return knowledgeRepository;
    }
    
    /**
     * The {@link Set} of {@link ConfigStrategy} instances specify the possible
     * configurations this instance of OpenCDS will support. The
     * <tt>configType</tt> and <tt>configPath</tt> may be injected via, e.g.,
     * Spring allowing (externalized) configuration to be specified at startup.
     * 
     * @param configStrategies
     * @param configType
     * @param configPath
     */
    public ConfigurationService(Set<ConfigStrategy> configStrategies, CacheService cacheService, ConfigData configData) {
//        initPluginSandbox();
        if (configStrategies == null || configStrategies.isEmpty()) {
            throw new IllegalArgumentException("At least one configuration strategy must be provided.");
        }
        this.cacheService = cacheService;
        this.configData = configData;
        ConfigStrategy strategy = null;
        for (ConfigStrategy configStrategy : configStrategies) {
            if (configStrategy.supports(configData.getConfigType())) {
                strategy = configStrategy;
                break;
            }
        }
        if (strategy == null) {
            throw new OpenCDSRuntimeException("Unsupported configuration type: " + configData.getConfigType());
        }
        configStrategy = strategy;
        loadConfiguration();
        knowledgeRepository.getKnowledgePackageService().preloadKnowledgePackages(knowledgeRepository.getKnowledgeModuleService().getAll());
    }
    
//    private void initPluginSandbox() {
//        Policy policy = new OpencdsSecurityPolicy();
//        Policy.setPolicy(policy);
//        System.setSecurityManager(new SecurityManager());
//        Policy.setPolicy(policy);
//    }
    
    private void loadConfiguration() {
        knowledgeRepository = configStrategy.getKnowledgeRepository(configData, cacheService);
    }
    
    public boolean reloadConfiguration() {
        if (configStrategy.isReloadable()) {
            loadConfiguration();
            log.info("Configuration reloaded");
            return true;
        }
        log.info("Configuation is not reloadable.  (Strategy: " + configStrategy + ")");
        return false;
    }

}
