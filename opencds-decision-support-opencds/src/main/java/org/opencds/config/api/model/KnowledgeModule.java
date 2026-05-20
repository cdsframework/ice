package org.opencds.config.api.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.util.StringUtils;

public record KnowledgeModule(KMId kmId,
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
                              LocalDate timestamp,
                              String userId)
{
    public KnowledgeModule
    {
        assert kmId != null;
        assert status != null;
        assert StringUtils.hasText(executionEngine);
        assert ssId != null;
        assert StringUtils.hasText(packageType);
        assert StringUtils.hasText(packageId);
        assert timestamp != null;
        secondaryCDMs = secondaryCDMs == null ? List.of() : List.copyOf(secondaryCDMs);
        traitIds = traitIds == null ? List.of() : List.copyOf(traitIds);
        preProcessPluginIds = preProcessPluginIds == null ? List.of() : List.copyOf(preProcessPluginIds);
        postProcessPluginIds = postProcessPluginIds == null ? List.of() : List.copyOf(postProcessPluginIds);
    }
}
