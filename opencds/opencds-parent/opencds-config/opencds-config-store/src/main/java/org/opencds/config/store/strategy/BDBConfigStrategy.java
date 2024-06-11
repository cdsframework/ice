/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.config.store.strategy;

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
import org.opencds.config.api.dao.file.FileDaoImpl;
import org.opencds.config.api.dao.util.FileUtil;
import org.opencds.config.api.strategy.AbstractConfigStrategy;
import org.opencds.config.api.strategy.ConfigCapability;
import org.opencds.config.service.ConceptDeterminationMethodServiceImpl;
import org.opencds.config.service.ConceptServiceImpl;
import org.opencds.config.service.ExecutionEngineServiceImpl;
import org.opencds.config.service.KnowledgeModuleServiceImpl;
import org.opencds.config.service.KnowledgePackageServiceImpl;
import org.opencds.config.service.PluginPackageServiceImpl;
import org.opencds.config.service.SemanticSignifierServiceImpl;
import org.opencds.config.service.SupportingDataPackageServiceImpl;
import org.opencds.config.service.SupportingDataServiceImpl;
import org.opencds.config.store.dao.je.ConceptDeterminationMethodJeDao;
import org.opencds.config.store.dao.je.ExecutionEngineJeDao;
import org.opencds.config.store.dao.je.KnowledgeModuleJeDao;
import org.opencds.config.store.dao.je.PluginPackageJeDao;
import org.opencds.config.store.dao.je.SemanticSignifierJeDao;
import org.opencds.config.store.dao.je.SupportingDataJeDao;
import org.opencds.config.store.je.OpenCDSConfigStore;
import org.opencds.plugin.PluginDataCache;
import org.opencds.plugin.support.PluginDataCacheImpl;

public class BDBConfigStrategy extends AbstractConfigStrategy {
    private static final String STORE_DIR = "store";

    OpenCDSConfigStore configStore;

    public BDBConfigStrategy() {
        super(new HashSet<>(Arrays.asList(ConfigCapability.UPDATE)), "STORE");
    }

    @Override
    public KnowledgeRepository getKnowledgeRepository(ConfigData configData, CacheService cacheService) {
        Path path = configData.getConfigLocation();
        File configDir = path.toFile();
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        File storeDir = Paths.get(path.toString(), STORE_DIR).toFile();
        if (!storeDir.exists()) {
            storeDir.mkdirs();
        }
        File kpDir = Paths.get(path.toString(), KNOWLEDGE_PACKAGE_DIR).toFile();
        if (!kpDir.exists()) {
            kpDir.mkdirs();
        }
        File pgDir = Paths.get(path.toString(), PLUGIN_DIR).toFile();
        if (!pgDir.exists()) {
            pgDir.mkdirs();
        }
        File sdDir = Paths.get(path.toString(), SUPPORTING_DATA_DIR).toFile();
        if (!sdDir.exists()) {
            sdDir.mkdirs();
        }
        configStore = new OpenCDSConfigStore(storeDir);

        ExecutionEngineServiceImpl eeService = new ExecutionEngineServiceImpl(new ExecutionEngineJeDao(configStore),
                cacheService);

        FileUtil fileUtil = new FileUtil();
        FileDao fileDao = new FileDaoImpl(fileUtil, kpDir);
        KnowledgePackageServiceImpl kpService = new KnowledgePackageServiceImpl(eeService, fileDao,
                cacheService);

        FileDao ppFileDao = new FileDaoImpl(fileUtil, pgDir);
        PluginPackageServiceImpl ppService = new PluginPackageServiceImpl(new PluginPackageJeDao(configStore),
                ppFileDao, cacheService);

        FileDao sdFileDao = new FileDaoImpl(fileUtil, sdDir);
        SupportingDataPackageServiceImpl sdpService = new SupportingDataPackageServiceImpl(sdFileDao, cacheService);
        SupportingDataServiceImpl sdService = new SupportingDataServiceImpl(new SupportingDataJeDao(configStore),
                sdpService, ppService, cacheService);

        KnowledgeModuleServiceImpl kmService = new KnowledgeModuleServiceImpl(new KnowledgeModuleJeDao(configStore),
                kpService, sdService, cacheService);

        ConceptDeterminationMethodServiceImpl cdmService = new ConceptDeterminationMethodServiceImpl(
                new ConceptDeterminationMethodJeDao(configStore), cacheService);

        ConceptServiceImpl conceptService = new ConceptServiceImpl(cdmService, kmService, cacheService);

        SemanticSignifierServiceImpl ssService = new SemanticSignifierServiceImpl(new SemanticSignifierJeDao(
                configStore), cacheService);

        PluginDataCache pluginDataCache = new PluginDataCacheImpl();

        return new KnowledgeRepositoryService(cdmService, conceptService, eeService, kmService, kpService, ppService,
                ssService, sdService, sdpService, cacheService, pluginDataCache);
    }

    public void shutdown() {
        if (configStore != null) {
            configStore.close();
        }
    }

}
