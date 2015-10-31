package org.opencds.config.api.model.impl;

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

public class KnowledgeModuleImpl implements KnowledgeModule {
    private KMId kmId;
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

    private KnowledgeModuleImpl() {
    }

    public static KnowledgeModuleImpl create(KMId kmId, KMStatus kmStatus, String executionEngine, SSId ssId,
            CDMId primaryCDM, List<SecondaryCDM> secondaryCDMs, String packageType, String packageId, boolean preload,
            String primaryProcess, List<TraitId> traitIds, List<PluginId> preProcessPluginIds,
            List<PluginId> postProcessPluginIds, Date timestamp, String userId) {
        KnowledgeModuleImpl kmj = new KnowledgeModuleImpl();
        kmj.kmId = KMIdImpl.create(kmId);
        kmj.status = kmStatus;
        kmj.executionEngine = executionEngine;
        kmj.ssId = SSIdImpl.create(ssId);
        kmj.primaryCDM = CDMIdImpl.create(primaryCDM);
        kmj.secondaryCDMs = SecondaryCDMImpl.create(secondaryCDMs);
        kmj.packageType = packageType;
        kmj.packageId = packageId;
        kmj.preload = preload;
        kmj.primaryProcess = primaryProcess;
        kmj.traitIds = TraitIdImpl.create(traitIds);
        kmj.preProcessPluginIds = PluginIdImpl.create(preProcessPluginIds);
        kmj.postProcessPluginIds = PluginIdImpl.create(postProcessPluginIds);
        kmj.timestamp = timestamp;
        kmj.userId = userId;
        return kmj;
    }

    public static KnowledgeModuleImpl create(KnowledgeModule km) {
        if (km == null) {
            return null;
        }
        if (km instanceof KnowledgeModuleImpl) {
            return KnowledgeModuleImpl.class.cast(km);
        }
        return create(km.getKMId(), km.getStatus(), km.getExecutionEngine(), km.getSSId(), km.getPrimaryCDM(),
                km.getSecondaryCDMs(), km.getPackageType(), km.getPackageId(), km.isPreload(), km.getPrimaryProcess(),
                km.getTraitIds(), km.getPreProcessPluginIds(), km.getPostProcessPluginIds(), km.getTimestamp(),
                km.getUserId());
    }
    
    public static List<KnowledgeModuleImpl> create(List<KnowledgeModule> kms) {
        if (kms == null) {
            return null;
        }
        List<KnowledgeModuleImpl> kmis = new ArrayList<>();
        for (KnowledgeModule km : kms) {
            kmis.add(create(km));
        }
        return kmis;
    }

    @Override
    public KMId getKMId() {
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
        return preProcessPluginIds;
    }

    @Override
    public List<PluginId> getPostProcessPluginIds() {
        return postProcessPluginIds;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("KnowledgeModule [kmId= " + kmId);
        sb.append(", status= " + status);
        sb.append(", executionEngine= " + executionEngine);
        sb.append(", ssId= " + ssId);
        sb.append(", primaryCDM= " + primaryCDM);
        if (secondaryCDMs != null) {
            sb.append(", secondaryCdms= [");
            for (SecondaryCDM scdm : secondaryCDMs) {
                sb.append(scdm + ", ");
            }
            sb.append("], ");
        }
        sb.append("packageType= " + packageType);
        sb.append("packageId= " + packageId);
        sb.append(primaryProcess + primaryProcess);
        if (traitIds != null) {
            sb.append(", traitids= [");
            for (TraitId tid : traitIds) {
                sb.append(tid + ", ");
            }
            sb.append("], ");
        }
        if (preProcessPluginIds != null) {
            sb.append(", preProcessPlugins= [");
            for (PluginId pid : preProcessPluginIds) {
                sb.append(pid + ", ");
            }
            sb.append("], ");
        }
        if (postProcessPluginIds != null) {
            sb.append(", postProcessPlugins= [");
            for (PluginId pid : postProcessPluginIds) {
                sb.append(pid + ", ");
            }
            sb.append("], ");
        }
        sb.append("timestamp= " + timestamp);
        sb.append("userId" + userId);
        sb.append("]");
        return sb.toString();
    }

}
