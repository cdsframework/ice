package org.opencds.plugin.api;

import java.util.Map;

public interface PluginContext
{
    Map<String, SupportingData> supportingData();

    PluginDataCache cache();
}
