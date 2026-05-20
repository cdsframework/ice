package org.opencds.config.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.opencds.config.api.model.PluginId;
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

public record KnowledgeRepository(ConceptDeterminationMethodService conceptDeterminationMethodService,
                                  ConceptService conceptService,
                                  ExecutionEngineService executionEngineService,
                                  KnowledgeModuleService knowledgeModuleService,
                                  KnowledgePackageService knowledgePackageService,
                                  PluginPackageService pluginPackageService,
                                  SemanticSignifierService semanticSignifierService,
                                  SupportingDataService supportingDataService,
                                  SupportingDataPackageService supportingDataPackageService)
{
    private static final Map<PluginId, PluginDataCache> pluginDataMap = new ConcurrentHashMap<>();

    public org.opencds.plugin.api.PluginDataCache pluginDataCache(final PluginId pluginId)
    {
        return pluginDataMap.computeIfAbsent(pluginId, _ -> new PluginDataCache());
    }
}
