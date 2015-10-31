package org.opencds.config.api;

import org.opencds.config.api.service.ConceptDeterminationMethodService;
import org.opencds.config.api.service.ConceptService;
import org.opencds.config.api.service.ExecutionEngineService;
import org.opencds.config.api.service.KnowledgeModuleService;
import org.opencds.config.api.service.KnowledgePackageService;
import org.opencds.config.api.service.PluginDataCacheService;
import org.opencds.config.api.service.PluginPackageService;
import org.opencds.config.api.service.SemanticSignifierService;
import org.opencds.config.api.service.SupportingDataPackageService;
import org.opencds.config.api.service.SupportingDataService;

public class KnowledgeRepositoryService implements KnowledgeRepository {

    private final ConceptDeterminationMethodService conceptDeterminationMethodService;
    private final ConceptService conceptService;
    private final ExecutionEngineService executionEngineService;
    private final KnowledgeModuleService knowledgeModuleService;
    private final KnowledgePackageService knowledgePackageService;
    private final PluginPackageService pluginPackageService;
    private final SemanticSignifierService semanticSignifierService;
    private final SupportingDataService supportingDataService;
    private final SupportingDataPackageService supportingDataPackageService;
    private final PluginDataCacheService pluginDataCacheService;

    public KnowledgeRepositoryService(ConceptDeterminationMethodService conceptDeterminationMethodService,
            ConceptService conceptService, ExecutionEngineService executionEngineService,
            KnowledgeModuleService knowledgeModuleService, KnowledgePackageService knowledgePackageService,
            PluginPackageService pluginPackageService, SemanticSignifierService semanticSignifierService,
            SupportingDataService supportingDataService, SupportingDataPackageService supportingDataPackageService,
            PluginDataCacheService pluginDataCacheService) {
        this.conceptDeterminationMethodService = conceptDeterminationMethodService;
        this.conceptService = conceptService;
        this.executionEngineService = executionEngineService;
        this.knowledgeModuleService = knowledgeModuleService;
        this.knowledgePackageService = knowledgePackageService;
        this.pluginPackageService = pluginPackageService;
        this.semanticSignifierService = semanticSignifierService;
        this.supportingDataService = supportingDataService;
        this.supportingDataPackageService = supportingDataPackageService;
        this.pluginDataCacheService = pluginDataCacheService;
    }

    @Override
    public ConceptDeterminationMethodService getConceptDeterminationMethodService() {
        return conceptDeterminationMethodService;
    }

    @Override
    public ConceptService getConceptService() {
        return conceptService;
    }

    @Override
    public ExecutionEngineService getExecutionEngineService() {
        return executionEngineService;
    }

    @Override
    public KnowledgeModuleService getKnowledgeModuleService() {
        return knowledgeModuleService;
    }

    @Override
    public KnowledgePackageService getKnowledgePackageService() {
        return knowledgePackageService;
    }

    @Override
    public PluginPackageService getPluginPackageService() {
        return pluginPackageService;
    }

    @Override
    public SemanticSignifierService getSemanticSignifierService() {
        return semanticSignifierService;
    }

    @Override
    public SupportingDataService getSupportingDataService() {
        return supportingDataService;
    }

    @Override
    public SupportingDataPackageService getSupportingDataPackageService() {
        return supportingDataPackageService;
    }
    
    @Override
    public PluginDataCacheService getPluginDataCacheService() {
        return pluginDataCacheService;
    }

}
