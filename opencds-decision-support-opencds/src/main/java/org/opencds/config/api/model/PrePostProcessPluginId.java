package org.opencds.config.api.model;

import java.util.List;

public interface PrePostProcessPluginId
{
    String getScopingEntityId();

    String getBusinessId();

    String getVersion();

    List<String> getSupportingDataIdentifiers();
}
