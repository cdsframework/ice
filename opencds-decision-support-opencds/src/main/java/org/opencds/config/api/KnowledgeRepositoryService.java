package org.opencds.config.api;

import org.opencds.common.cache.CacheRegion;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.service.CdsHooksClientService;
import org.opencds.config.api.service.ConceptDeterminationMethodService;
import org.opencds.config.api.service.ConceptService;
import org.opencds.config.api.service.ExecutionEngineService;
import org.opencds.config.api.service.KnowledgeModuleService;
import org.opencds.config.api.service.KnowledgePackageService;
import org.opencds.config.api.service.PluginPackageService;
import org.opencds.config.api.service.SemanticSignifierService;
import org.opencds.config.api.service.SupportingDataPackageService;
import org.opencds.config.api.service.SupportingDataService;
import org.opencds.plugin.api.PluginDataCache;
import org.opencds.plugin.support.PluginDataCacheImpl;

public record KnowledgeRepositoryService(ConceptDeterminationMethodService conceptDeterminationMethodService,
                                         ConceptService conceptService,
                                         ExecutionEngineService executionEngineService,
                                         KnowledgeModuleService knowledgeModuleService,
                                         KnowledgePackageService knowledgePackageService,
                                         PluginPackageService pluginPackageService,
                                         SemanticSignifierService semanticSignifierService,
                                         SupportingDataService supportingDataService,
                                         SupportingDataPackageService supportingDataPackageService,
                                         CdsHooksClientService cdsHooksClientService,
                                         CacheService cacheService) implements KnowledgeRepository
{
    private static final CacheRegion<PluginId, PluginDataCache> PLUGIN_DATA =
            CacheRegion.create(PluginId.class, PluginDataCache.class);

    @Override
    public PluginDataCache pluginDataCache(final PluginId pluginId)
    {
        final var pluginDataCache = cacheService.get(PLUGIN_DATA, pluginId);
        if (pluginDataCache == null)
            cacheService.put(PLUGIN_DATA, pluginId, new PluginDataCacheImpl());
        return cacheService.get(PLUGIN_DATA, pluginId);
    }
}
