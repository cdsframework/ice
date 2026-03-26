package org.opencds.config.api;

import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.service.ConceptService;
import org.opencds.config.api.service.ExecutionEngineService;
import org.opencds.config.api.service.KnowledgeModuleService;
import org.opencds.config.api.service.KnowledgePackageService;
import org.opencds.config.api.service.PluginPackageService;
import org.opencds.config.api.service.SemanticSignifierService;
import org.opencds.config.api.service.SupportingDataPackageService;
import org.opencds.config.api.service.SupportingDataService;
import org.opencds.plugin.api.PluginDataCache;

public interface KnowledgeRepository
{
    ConceptService conceptService();

    ExecutionEngineService executionEngineService();

    KnowledgeModuleService knowledgeModuleService();

    KnowledgePackageService knowledgePackageService();

    SemanticSignifierService semanticSignifierService();

    SupportingDataService supportingDataService();

    SupportingDataPackageService supportingDataPackageService();

    PluginPackageService pluginPackageService();

    PluginDataCache pluginDataCache(PluginId pluginId);
}
