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

package org.opencds.config.store.model.je;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.CDSHook;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KMStatus;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.PrePostProcessPluginId;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SecondaryCDM;
import org.opencds.config.api.model.TraitId;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity(version=1)
public class KnowledgeModuleJe implements KnowledgeModule, ConfigEntity<KMIdJe> {
    @PrimaryKey
    private KMIdJe kmId;
    private KMStatus status;
    private CDSHook cdsHook;
    private String executionEngine;
    private SSId ssId;
    private CDMId primaryCDM;
    private List<SecondaryCDM> secondaryCDMs;
    private String packageType;
    private String packageId;
    private boolean preload;
    private String primaryProcess;
    private List<TraitId> traitIds;
    private List<PrePostProcessPluginId> preProcessPluginIds;
    private List<PrePostProcessPluginId> postProcessPluginIds;
    private Date timestamp;
    private String userId;

    private KnowledgeModuleJe() {
    }

    public static KnowledgeModuleJe create(KMId kmId, KMStatus kmStatus, CDSHook cdsHook, String executionEngine, SSId ssId,
            CDMId primaryCDM, List<SecondaryCDM> secondaryCDMs, String packageType, String packageId, boolean preload,
            String primaryProcess, List<TraitId> traitIds, List<PrePostProcessPluginId> preProcessPluginIds,
            List<PrePostProcessPluginId> postProcessPluginIds, Date timestamp, String userId) {
        KnowledgeModuleJe kmj = new KnowledgeModuleJe();
        kmj.kmId = KMIdJe.create(kmId);
        kmj.status = kmStatus;
        kmj.cdsHook = CDSHookJe.create(cdsHook);
        kmj.executionEngine = executionEngine;
        kmj.ssId = SSIdJe.create(ssId);
        kmj.primaryCDM = CDMIdJe.create(primaryCDM);
        kmj.secondaryCDMs = SecondaryCDMJe.create(secondaryCDMs);
        kmj.packageType = packageType;
        kmj.packageId = packageId;
        kmj.preload = preload;
        kmj.primaryProcess = primaryProcess;
        kmj.traitIds = TraitIdJe.create(traitIds);
        kmj.preProcessPluginIds = PrePostProcessPluginIdJe.create(preProcessPluginIds);
        kmj.postProcessPluginIds = PrePostProcessPluginIdJe.create(postProcessPluginIds);
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
        return create(km.getKMId(), km.getStatus(), km.getCDSHook(), km.getExecutionEngine(), km.getSSId(), km.getPrimaryCDM(),
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
    public CDSHook getCDSHook() {
    	return cdsHook;
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
    public List<PrePostProcessPluginId> getPreProcessPluginIds() {
        if (preProcessPluginIds == null) {
            return null;
        }
        return Collections.unmodifiableList(preProcessPluginIds);
    }

    @Override
    public List<PrePostProcessPluginId> getPostProcessPluginIds() {
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
