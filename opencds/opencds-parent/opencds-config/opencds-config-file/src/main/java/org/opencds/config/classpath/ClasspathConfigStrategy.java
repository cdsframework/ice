package org.opencds.config.classpath;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.config.api.ConfigData;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.KnowledgeRepositoryService;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.FileDao;
import org.opencds.config.api.dao.file.ClasspathResourceDaoImpl;
import org.opencds.config.file.dao.ConceptDeterminationMethodFileDao;
import org.opencds.config.file.dao.ExecutionEngineFileDao;
import org.opencds.config.file.dao.KnowledgeModuleFileDao;
import org.opencds.config.file.dao.PluginPackageFileDao;
import org.opencds.config.file.dao.SemanticSignifierFileDao;
import org.opencds.config.file.dao.SupportingDataFileDao;
import org.opencds.config.file.dao.support.ClasspathUtil;
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

public class ClasspathConfigStrategy extends AbstractConfigStrategy {
	private static final Logger log = LogManager.getLogger();
	private static final String CLASSPATH_SEPARATOR = "/";
	private static final String JAR_PREFIX = "opencds-knowledge-repository-data";

	public ClasspathConfigStrategy() {
		super(new HashSet<>(Arrays.asList(ConfigCapability.READ_ONCE)),
				"CLASSPATH");
	}

	@Override
    public KnowledgeRepository getKnowledgeRepository(ConfigData configData, CacheService cacheService) {
        Path path = configData.getConfigLocation();
        log.debug("Resolving configuration on path: " + path.toString());
        ClasspathUtil classpathUtil = new ClasspathUtil(JAR_PREFIX);

        ConceptDeterminationMethodServiceImpl cdmService = new ConceptDeterminationMethodServiceImpl(
                new ConceptDeterminationMethodFileDao(classpathUtil, path + CLASSPATH_SEPARATOR + CDMS), cacheService);

        ExecutionEngineServiceImpl eeService = new ExecutionEngineServiceImpl(new ExecutionEngineFileDao(classpathUtil,
                path.toString() + CLASSPATH_SEPARATOR + EXECUTION_ENGINES), cacheService);

        FileDao ppFileDao = new ClasspathResourceDaoImpl(classpathUtil, path.toString() + CLASSPATH_SEPARATOR + PLUGIN_DIR + CLASSPATH_SEPARATOR + PACKAGES);
        PluginPackageServiceImpl ppService = new PluginPackageServiceImpl(new PluginPackageFileDao(classpathUtil,
                path.toString() + CLASSPATH_SEPARATOR + PLUGIN_DIR), ppFileDao, cacheService);
        PluginDataCacheServiceImpl pdcService = new PluginDataCacheServiceImpl(cacheService, ppService.getAllPluginIds());
        
		FileDao sdFileDao = new ClasspathResourceDaoImpl(classpathUtil,
				path.toString() + CLASSPATH_SEPARATOR + SUPPORTING_DATA_DIR + CLASSPATH_SEPARATOR + PACKAGES);
        SupportingDataPackageServiceImpl sdpService = new SupportingDataPackageServiceImpl(sdFileDao, cacheService);
        SupportingDataServiceImpl sdService = new SupportingDataServiceImpl(new SupportingDataFileDao(classpathUtil,
                path.toString() + CLASSPATH_SEPARATOR + SUPPORTING_DATA_DIR), sdpService, ppService, cacheService);
        
        FileDao kpFileDao = new ClasspathResourceDaoImpl(classpathUtil, path.toString() + CLASSPATH_SEPARATOR +
                KNOWLEDGE_PACKAGE_DIR);
        KnowledgePackageServiceImpl kpService = new KnowledgePackageServiceImpl(configData, eeService, kpFileDao, cacheService);
        KnowledgeModuleServiceImpl kmService = new KnowledgeModuleServiceImpl(new KnowledgeModuleFileDao(classpathUtil,
                path.toString() + CLASSPATH_SEPARATOR + KNOWLEDGE_MODULES), kpService, sdService, cacheService);

        SemanticSignifierServiceImpl ssService = new SemanticSignifierServiceImpl(new SemanticSignifierFileDao(
                classpathUtil, path.toString() + CLASSPATH_SEPARATOR + SEMANTIC_SIGNIFIERS), cacheService);

        ConceptServiceImpl conceptService = new ConceptServiceImpl(cdmService, kmService, cacheService);

        return new KnowledgeRepositoryService(cdmService, conceptService, eeService, kmService, kpService, ppService,
                ssService, sdService, sdpService, pdcService);
    }
}
