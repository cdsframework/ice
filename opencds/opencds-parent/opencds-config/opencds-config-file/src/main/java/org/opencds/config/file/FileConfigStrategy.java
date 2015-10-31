package org.opencds.config.file;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

import org.opencds.config.api.ConfigData;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.KnowledgeRepositoryService;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.FileDao;
import org.opencds.config.api.dao.file.ReadOnlyFileDaoImpl;
import org.opencds.config.api.dao.util.FileUtil;
import org.opencds.config.file.dao.ConceptDeterminationMethodFileDao;
import org.opencds.config.file.dao.ExecutionEngineFileDao;
import org.opencds.config.file.dao.KnowledgeModuleFileDao;
import org.opencds.config.file.dao.PluginPackageFileDao;
import org.opencds.config.file.dao.SemanticSignifierFileDao;
import org.opencds.config.file.dao.SupportingDataFileDao;
import org.opencds.config.service.ConceptDeterminationMethodServiceImpl;
import org.opencds.config.service.ConceptServiceImpl;
import org.opencds.config.service.ExecutionEngineServiceImpl;
import org.opencds.config.service.KnowledgeModuleServiceImpl;
import org.opencds.config.service.KnowledgePackageServiceImpl;
import org.opencds.config.service.PluginDataCacheServiceImpl;
import org.opencds.config.service.PluginPackageServiceImpl;
import org.opencds.config.service.SemanticSignifierServiceImpl;
import org.opencds.config.service.SupportingDataPackageServiceImpl;
import org.opencds.config.service.SupportingDataServiceImpl;
import org.opencds.config.strategy.AbstractConfigStrategy;
import org.opencds.config.strategy.ConfigCapability;

public class FileConfigStrategy extends AbstractConfigStrategy {

    public FileConfigStrategy() {
        super(new HashSet<>(Arrays.asList(ConfigCapability.RELOAD)), "SIMPLE_FILE");
    }

    @Override
    public KnowledgeRepository getKnowledgeRepository(ConfigData configData, CacheService cacheService) {
        Path path = configData.getConfigLocation();
        // TODO FIXME the dao requirements require a proper path
        FileUtil fileUtil = new FileUtil();
        File kpDir = Paths.get(path.toString(), KNOWLEDGE_PACKAGE_DIR).toFile();
        if (!kpDir.exists()) {
            kpDir.mkdirs();
        }
        FileDao kpFileDao = new ReadOnlyFileDaoImpl(fileUtil, kpDir);

        File ppDir = Paths.get(path.toString(), PLUGIN_DIR, PACKAGES).toFile();
        if (!ppDir.exists()) {
            ppDir.mkdirs();
        }
        FileDao ppFileDao = new ReadOnlyFileDaoImpl(fileUtil, ppDir);

        File sdDir = Paths.get(path.toString(), SUPPORTING_DATA_DIR, PACKAGES).toFile();
        if (!sdDir.exists()) {
            sdDir.mkdirs();
        }
        FileDao sdFileDao = new ReadOnlyFileDaoImpl(fileUtil, sdDir);

        ConceptDeterminationMethodServiceImpl cdmService = new ConceptDeterminationMethodServiceImpl(
                new ConceptDeterminationMethodFileDao(fileUtil, Paths.get(path.toString(), CDMS).toAbsolutePath()
                        .toString()), cacheService);

        ExecutionEngineServiceImpl eeService = new ExecutionEngineServiceImpl(new ExecutionEngineFileDao(fileUtil,
                Paths.get(path.toString(), EXECUTION_ENGINES).toAbsolutePath().toString()), cacheService);

        PluginPackageServiceImpl ppService = new PluginPackageServiceImpl(new PluginPackageFileDao(fileUtil, Paths
                .get(path.toString(), PLUGIN_DIR).toAbsolutePath().toString()), ppFileDao, cacheService);
        PluginDataCacheServiceImpl pdcService = new PluginDataCacheServiceImpl(cacheService, ppService.getAllPluginIds());
        
        SupportingDataPackageServiceImpl sdpService = new SupportingDataPackageServiceImpl(sdFileDao, cacheService);
        SupportingDataServiceImpl sdService = new SupportingDataServiceImpl(new SupportingDataFileDao(fileUtil, Paths
                .get(path.toString(), SUPPORTING_DATA_DIR).toAbsolutePath().toString()), sdpService, ppService,
                cacheService);
        
        KnowledgePackageServiceImpl kpService = new KnowledgePackageServiceImpl(configData, eeService, kpFileDao, cacheService);
        KnowledgeModuleServiceImpl kmService = new KnowledgeModuleServiceImpl(new KnowledgeModuleFileDao(fileUtil,
                Paths.get(path.toString(), KNOWLEDGE_MODULES).toAbsolutePath().toString()), kpService, sdService, cacheService);

        SemanticSignifierServiceImpl ssService = new SemanticSignifierServiceImpl(new SemanticSignifierFileDao(
                fileUtil, Paths.get(path.toString(), SEMANTIC_SIGNIFIERS).toAbsolutePath().toString()), cacheService);

        ConceptServiceImpl conceptService = new ConceptServiceImpl(cdmService, kmService, cacheService);

        return new KnowledgeRepositoryService(cdmService, conceptService, eeService, kmService, kpService, ppService,
                ssService, sdService, sdpService, pdcService);
    }
}
