package org.opencds.config.migrate.builder;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.cache.OpencdsCache;
import org.opencds.common.xml.XmlEntity;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KMStatus;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.impl.KMIdImpl;
import org.opencds.config.api.model.impl.KnowledgeModuleImpl;
import org.opencds.config.api.model.impl.SSIdImpl;
import org.opencds.config.migrate.ConfigResourceType;
import org.opencds.config.migrate.OpencdsBaseConfig;
import org.opencds.config.migrate.cache.ConfigCacheRegion;
import org.opencds.config.migrate.model.DataModel;
import org.opencds.config.migrate.utilities.ConfigResourceUtility;
import org.opencds.config.migrate.utilities.XmlUtility;

public class KnowledgeModulesConfigBuilder {
    private static final Log log = LogFactory.getLog(KnowledgeModulesConfigBuilder.class);
    private XmlUtility xmlUtility = new XmlUtility();

    private ConfigResourceUtility configResourceUtility = new ConfigResourceUtility();

    public void loadKnowledgeModules(OpencdsBaseConfig opencdsBaseConfig, OpencdsCache cache) {
        // read knowledgeModules
        XmlEntity kmMetadataRootEntity = xmlUtility
                .unmarshalXml(configResourceUtility
                        .getResourceAsString(
                                opencdsBaseConfig,
                                ConfigResourceType.KNOWLEDGE_MODULES));

        // load km arrays
        List<XmlEntity> kmMetadataList = kmMetadataRootEntity.getChildrenWithLabel("kmMetadata");
        List<KnowledgeModule> kms = new ArrayList<>();
        for (XmlEntity kmMetadata : kmMetadataList) {
            // load kmId to dataModel map
            XmlEntity identifier = kmMetadata.getFirstChildWithLabel("identifier");
            KMId kmId = KMIdImpl.create(
                    identifier.getAttributeValue("scopingEntityId"),
                    identifier.getAttributeValue("businessId"),
                    identifier.getAttributeValue("version"));

            XmlEntity dataModelEntity = kmMetadata.getFirstChildWithLabel("dataModel");
            DataModel dataModel = DataModel.resolve(dataModelEntity.getValue());

            // load kmId to inference engine adapter map
            XmlEntity executionEngine = kmMetadata.getFirstChildWithLabel("executionEngine");
            ExecutionEngine ee = findExecutionEngineByName(cache, executionEngine.getValue());

            // load kmId to primary process name map
            XmlEntity knowledgeModulePrimaryProcessName = kmMetadata.getFirstChildWithLabel("knowledgeModulePrimaryProcessName");
            String primaryProcessName = "";
            if ((knowledgeModulePrimaryProcessName != null) && (knowledgeModulePrimaryProcessName.getValue() != null)
                    && !("".equals(knowledgeModulePrimaryProcessName.getValue()))) {
                primaryProcessName = knowledgeModulePrimaryProcessName.getValue();
            }

            List<String> packageData = findKnowledgePackage(opencdsBaseConfig, kmId);
            if (!packageData.get(2).isEmpty()) {
                List<Pair<KMId, String>> packageList = cache.get(ConfigCacheRegion.DATA, ConfigResourceType.KNOWLEDGE_PACKAGE);
                if (packageList == null) {
                    packageList = new ArrayList<>();
                }
                packageList.add(Pair.<KMId, String>of(kmId, packageData.get(2)));
                cache.put(ConfigCacheRegion.DATA, ConfigResourceType.KNOWLEDGE_PACKAGE, packageList);
            }

            kms.add(KnowledgeModuleImpl.create(
                    kmId,
                    KMStatus.APPROVED,
                    ee.getIdentifier(),
                    SSIdImpl.create(dataModel.getEntityId().getScopingEntityId(), dataModel.getEntityId().getBusinessId(), dataModel.getEntityId().getVersion()),
                    null,
                    null,
                    packageData.get(0).toUpperCase(),
                    packageData.get(1),
                    false,
                    primaryProcessName,
                    null,
                    null,
                    null,
                    new Date(),
                    System.getenv("USER")));
        }
        cache.put(ConfigCacheRegion.METADATA, ConfigResourceType.KNOWLEDGE_MODULES, kms);
    }

    private List<String> findKnowledgePackage(OpencdsBaseConfig config, KMId kmId) {
        final String packagePrefix = kmId.getScopingEntityId() + "^" + kmId.getBusinessId() + "^" + kmId.getVersion();
        File knowledgeModulesDir = Paths.get(config.getKnowledgeRepositoryLocation().getPath(), "knowledgeModules").toFile();
        String[] packages = knowledgeModulesDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(packagePrefix);
            }
        });
        List<String> kpData = new ArrayList<>();
        if (packages.length == 1) {
            String[] components = packages[0].split("\\.");
            String extension = components[components.length - 1];
            kpData.add(extension);
            kpData.add(packages[0]);
            kpData.add(new File(knowledgeModulesDir, packages[0]).getAbsolutePath());
        } else {
            log.error("Too many files matching prefix or prefix not found: Found " + packages.length
                    + " matching file(s).");
            kpData.add("");
            kpData.add("");
            kpData.add("");
        }
        return kpData;
    }

    private ExecutionEngine findExecutionEngineByName(OpencdsCache cache, String value) {
        ExecutionEngine execEng = null;
        List<ExecutionEngine> engines = cache.get(ConfigCacheRegion.METADATA, ConfigResourceType.EXECUTION_ENGINES);
        if (engines != null) {
            for (ExecutionEngine ee : engines) {
                if (ee.getIdentifier().equals(value)) {
                    execEng = ee;
                    break;
                }
            }
        }
        return execEng;
    }

}
