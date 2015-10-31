package org.opencds.config.store.model.je;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KMStatus;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SecondaryCDM;
import org.opencds.config.api.model.TraitId;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class KnowledgeModuleJe implements KnowledgeModule, ConfigEntity<KMIdJe> {
    @PrimaryKey
    private KMIdJe kmId;
    private KMStatus status;
    private String executionEngine;
    private SSId ssId;
    private CDMId primaryCDM;
    private List<SecondaryCDM> secondaryCDMs;
    private String packageType;
    private String packageId;
    private boolean preload;
    private String primaryProcess;
    private List<TraitId> traitIds;
    private List<PluginId> preProcessPluginIds;
    private List<PluginId> postProcessPluginIds;
    private Date timestamp;
    private String userId;

    private KnowledgeModuleJe() {
    }

    public static KnowledgeModuleJe create(KMId kmId, KMStatus kmStatus, String executionEngine, SSId ssId,
            CDMId primaryCDM, List<SecondaryCDM> secondaryCDMs, String packageType, String packageId, boolean preload,
            String primaryProcess, List<TraitId> traitIds, List<PluginId> preProcessPluginIds,
            List<PluginId> postProcessPluginIds, Date timestamp, String userId) {
        KnowledgeModuleJe kmj = new KnowledgeModuleJe();
        kmj.kmId = KMIdJe.create(kmId);
        kmj.status = kmStatus;
        kmj.executionEngine = executionEngine;
        kmj.ssId = SSIdJe.create(ssId);
        kmj.primaryCDM = CDMIdJe.create(primaryCDM);
        kmj.secondaryCDMs = SecondaryCDMJe.create(secondaryCDMs);
        kmj.packageType = packageType;
        kmj.packageId = packageId;
        kmj.preload = preload;
        kmj.primaryProcess = primaryProcess;
        kmj.traitIds = TraitIdJe.create(traitIds);
        kmj.preProcessPluginIds = PluginIdJe.create(preProcessPluginIds);
        kmj.postProcessPluginIds = PluginIdJe.create(postProcessPluginIds);
        kmj.timestamp = timestamp;
        kmj.userId = userId;
        return kmj;
    }

    public static KnowledgeModuleJe create(KnowledgeModule km) {
        if (km == null) {
            return null;
        }
        if (km instanceof KnowledgeModuleJe) {
            return KnowledgeModuleJe.class.cast(km);
        }
        return create(km.getKMId(), km.getStatus(), km.getExecutionEngine(), km.getSSId(), km.getPrimaryCDM(),
                km.getSecondaryCDMs(), km.getPackageType(), km.getPackageId(), km.isPreload(), km.getPrimaryProcess(),
                km.getTraitIds(), km.getPreProcessPluginIds(), km.getPostProcessPluginIds(), km.getTimestamp(),
                km.getUserId());
    }

    public static List<KnowledgeModuleJe> create(List<KnowledgeModule> kms) {
        if (kms == null) {
            return null;
        }
        List<KnowledgeModuleJe> kmjs = new ArrayList<>();
        for (KnowledgeModule km : kms) {
            kmjs.add(create(km));
        }
        return kmjs;
    }

    @Override
    public KMIdJe getPrimaryKey() {
        return getKMId();
    }

    @Override
    public KMIdJe getKMId() {
        return kmId;
    }

    @Override
    public KMStatus getStatus() {
        return status;
    }

    @Override
    public String getExecutionEngine() {
        return executionEngine;
    }

    @Override
    public SSId getSSId() {
        return ssId;
    }

    @Override
    public CDMId getPrimaryCDM() {
        return primaryCDM;
    }

    @Override
    public List<SecondaryCDM> getSecondaryCDMs() {
        if (secondaryCDMs == null) {
            return null;
        }
        return Collections.unmodifiableList(secondaryCDMs);
    }

    @Override
    public String getPackageType() {
        return packageType;
    }

    @Override
    public String getPackageId() {
        return packageId;
    }

    @Override
    public boolean isPreload() {
        return preload;
    }

    @Override
    public String getPrimaryProcess() {
        return primaryProcess;
    }

    @Override
    public List<TraitId> getTraitIds() {
        if (traitIds == null) {
            return null;
        }
        return Collections.unmodifiableList(traitIds);
    }

    @Override
    public List<PluginId> getPreProcessPluginIds() {
        if (preProcessPluginIds == null) {
            return null;
        }
        return Collections.unmodifiableList(preProcessPluginIds);
    }

    @Override
    public List<PluginId> getPostProcessPluginIds() {
        if (postProcessPluginIds == null) {
            return null;
        }
        return Collections.unmodifiableList(postProcessPluginIds);
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String getUserId() {
        return userId;
    }

}
