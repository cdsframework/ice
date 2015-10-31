package org.opencds.config.api.model;

import java.util.Date;
import java.util.List;

public interface KnowledgeModule {
    KMId getKMId();

    KMStatus getStatus();

    String getExecutionEngine();

    SSId getSSId();

    CDMId getPrimaryCDM();

    List<SecondaryCDM> getSecondaryCDMs();

    String getPackageType();

    String getPackageId();

    boolean isPreload();
    
    String getPrimaryProcess();

    List<TraitId> getTraitIds();
    
    List<PluginId> getPreProcessPluginIds();
    
    List<PluginId> getPostProcessPluginIds();

    Date getTimestamp();

    String getUserId();

}
