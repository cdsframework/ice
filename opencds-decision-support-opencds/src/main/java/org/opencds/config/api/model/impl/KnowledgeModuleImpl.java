package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.CDSHook;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KMStatus;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.PrePostProcessPluginId;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SecondaryCDM;
import org.opencds.config.api.model.TraitId;

public record KnowledgeModuleImpl(KMId kmId,
                                  KMStatus status,
                                  CDSHook cdsHook,
                                  String executionEngine,
                                  SSId ssId,
                                  CDMId primaryCDM,
                                  List<SecondaryCDM> secondaryCDMs,
                                  String packageType,
                                  String packageId,
                                  boolean preload,
                                  String primaryProcess,
                                  List<TraitId> traitIds,
                                  List<PrePostProcessPluginId> preProcessPluginIds,
                                  List<PrePostProcessPluginId> postProcessPluginIds,
                                  Date timestamp,
                                  String userId) implements KnowledgeModule
{
    public static KnowledgeModuleImpl create(final KMId kmId, final KMStatus kmStatus, final CDSHook cdsHook,
            final String executionEngine, final SSId ssId, final CDMId primaryCDM, final List<SecondaryCDM> secondaryCDMs,
            final String packageType, final String packageId, final boolean preload, final String primaryProcess,
            final List<TraitId> traitIds, final List<PrePostProcessPluginId> preProcPlugins,
            final List<PrePostProcessPluginId> postProcPlugins, final Date timestamp, final String userId)
    {
        return new KnowledgeModuleImpl(KMIdImpl.create(kmId), kmStatus, CDSHookImpl.create(cdsHook), executionEngine,
                SSIdImpl.create(ssId), CDMIdImpl.create(primaryCDM), SecondaryCDMImpl.create(secondaryCDMs), packageType, packageId,
                preload, primaryProcess, TraitIdImpl.create(traitIds), PrePostProcessPluginIdImpl.create(preProcPlugins),
                PrePostProcessPluginIdImpl.create(postProcPlugins), timestamp, userId);
    }

    public static KnowledgeModuleImpl create(final KnowledgeModule km)
    {
        if (km == null)
            return null;
        if (km instanceof final KnowledgeModuleImpl knowledgeModuleImpl)
            return knowledgeModuleImpl;
        return create(km.getKMId(), km.getStatus(), km.getCDSHook(), km.getExecutionEngine(), km.getSSId(), km.getPrimaryCDM(),
                km.getSecondaryCDMs(), km.getPackageType(), km.getPackageId(), km.isPreload(), km.getPrimaryProcess(),
                km.getTraitIds(), km.getPreProcessPluginIds(), km.getPostProcessPluginIds(), km.getTimestamp(), km.getUserId());
    }

    public static List<KnowledgeModuleImpl> create(final List<KnowledgeModule> kms)
    {
        if (kms == null)
            return null;
        final var kmis = new ArrayList<KnowledgeModuleImpl>();
        for (final var km : kms)
            kmis.add(create(km));
        return kmis;
    }

    public KnowledgeModuleImpl
    {
        assert kmId != null;
        assert status != null;
        assert StringUtils.isNotBlank(executionEngine);
        assert ssId != null;
        assert StringUtils.isNotBlank(packageType);
        assert StringUtils.isNotBlank(packageId);
        assert timestamp != null;
        secondaryCDMs = secondaryCDMs == null ? Collections.emptyList() : Collections.unmodifiableList(secondaryCDMs);
        traitIds = traitIds == null ? Collections.emptyList() : Collections.unmodifiableList(traitIds);
        preProcessPluginIds =
                preProcessPluginIds == null ? Collections.emptyList() : Collections.unmodifiableList(preProcessPluginIds);
        postProcessPluginIds =
                postProcessPluginIds == null ? Collections.emptyList() : Collections.unmodifiableList(postProcessPluginIds);
    }

    @Override
    public KMId getKMId()
    {
        return kmId;
    }

    @Override
    public KMStatus getStatus()
    {
        return status;
    }

    @Override
    public CDSHook getCDSHook()
    {
        return cdsHook;
    }

    @Override
    public String getExecutionEngine()
    {
        return executionEngine;
    }

    @Override
    public SSId getSSId()
    {
        return ssId;
    }

    @Override
    public CDMId getPrimaryCDM()
    {
        return primaryCDM;
    }

    @Override
    public List<SecondaryCDM> getSecondaryCDMs()
    {
        return secondaryCDMs;
    }

    @Override
    public String getPackageType()
    {
        return packageType;
    }

    @Override
    public String getPackageId()
    {
        return packageId;
    }

    @Override
    public boolean isPreload()
    {
        return preload;
    }

    @Override
    public String getPrimaryProcess()
    {
        return primaryProcess;
    }

    @Override
    public List<TraitId> getTraitIds()
    {
        return traitIds;
    }

    @Override
    public List<PrePostProcessPluginId> getPreProcessPluginIds()
    {
        return preProcessPluginIds;
    }

    @Override
    public List<PrePostProcessPluginId> getPostProcessPluginIds()
    {
        return postProcessPluginIds;
    }

    @Override
    public Date getTimestamp()
    {
        return timestamp;
    }

    @Override
    public String getUserId()
    {
        return userId;
    }
}
