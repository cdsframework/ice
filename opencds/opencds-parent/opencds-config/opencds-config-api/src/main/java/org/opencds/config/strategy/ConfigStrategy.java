package org.opencds.config.strategy;

import org.opencds.config.api.ConfigData;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.cache.CacheService;

/**
 * <tt>ConfigStrategy</tt> is responsible for providing services with access to
 * the underying configuration. 
 * 
 * Support likely includes injecting the appropriate DAO into the service in
 * order for the service to interact with the configuration backend.
 * 
 * @author phillip
 *
 */
public interface ConfigStrategy {

    boolean supports(String configType);

    boolean isReloadable();

    boolean isUpdatable();

    /**
     * Builds a new {@link KnowledgeRepository} that has access to the underlying configuration.
     * 
     * @param configPath
     * @return
     */
    KnowledgeRepository getKnowledgeRepository(ConfigData configData, CacheService cacheService);

}
