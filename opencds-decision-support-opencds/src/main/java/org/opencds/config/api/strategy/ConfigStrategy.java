package org.opencds.config.api.strategy;

import java.net.URI;
import java.nio.file.Path;

import org.opencds.config.api.ConfigData;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.dao.ConceptDeterminationMethodDao;
import org.opencds.config.api.dao.ExecutionEngineDao;
import org.opencds.config.api.dao.FileDao;
import org.opencds.config.api.dao.KnowledgeModuleDao;
import org.opencds.config.api.dao.PluginPackageDao;
import org.opencds.config.api.dao.SemanticSignifierDao;
import org.opencds.config.api.dao.SupportingDataDao;
import org.opencds.config.api.service.ConceptDeterminationMethodService;
import org.opencds.config.api.service.ExecutionEngineService;
import org.opencds.config.api.service.KnowledgeModuleService;
import org.opencds.config.api.service.KnowledgePackageService;
import org.opencds.config.api.service.PluginPackageService;
import org.opencds.config.api.service.SemanticSignifierService;
import org.opencds.config.api.service.SupportingDataPackageService;
import org.opencds.config.api.service.SupportingDataService;
import org.opencds.config.service.ConceptServiceImpl;
import org.springframework.core.task.AsyncTaskExecutor;

public record ConfigStrategy(AsyncTaskExecutor evalPool)
{
    public static final String supportedConfigType = "SPRING_BOOT";

    public boolean supports(final String configType)
    {
        return supportedConfigType.equalsIgnoreCase(configType);
    }

    public KnowledgeRepository getKnowledgeRepository(final ConfigData configData)
    {
        final Path path = Path.of(URI.create(configData.configPath()));

        final ConceptDeterminationMethodService cdmService = new ConceptDeterminationMethodService(
                new ConceptDeterminationMethodDao(path.resolve("conceptDeterminationMethods")));

        final ExecutionEngineService eeService =
                new ExecutionEngineService(new ExecutionEngineDao(path.resolve("executionEngines.xml")));

        final PluginPackageService ppService = new PluginPackageService(new PluginPackageDao(path.resolve("plugins")));

        final SupportingDataPackageService sdpService =
                new SupportingDataPackageService(new FileDao(path.resolve("supportingData").resolve("packages")));

        final SupportingDataService sdService = new SupportingDataService(new SupportingDataDao(path.resolve("supportingData")));

        final KnowledgePackageService kpService = new KnowledgePackageService(evalPool, eeService);

        final KnowledgeModuleService kmService =
                new KnowledgeModuleService(new KnowledgeModuleDao(path.resolve("knowledgeModules.xml")));

        final SemanticSignifierService ssService =
                new SemanticSignifierService(new SemanticSignifierDao(path.resolve("semanticSignifiers.xml")));

        return new KnowledgeRepository(cdmService, new ConceptServiceImpl(cdmService, kmService), eeService, kmService, kpService,
                ppService, ssService, sdService, sdpService);
    }
}
