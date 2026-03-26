package org.opencds.config.api.model;

import java.util.Date;
import java.util.List;

public interface KnowledgeModule
{
    KMId getKMId();

    KMStatus getStatus();

    CDSHook getCDSHook();

    default boolean hasCdsHook()
    {
        return getCDSHook() != null;
    }

    String getExecutionEngine();

    SSId getSSId();

    CDMId getPrimaryCDM();

    List<SecondaryCDM> getSecondaryCDMs();

    String getPackageType();

    String getPackageId();

    boolean isPreload();

    String getPrimaryProcess();

    List<TraitId> getTraitIds();

    List<PrePostProcessPluginId> getPreProcessPluginIds();

    List<PrePostProcessPluginId> getPostProcessPluginIds();

    Date getTimestamp();

    String getUserId();
}
