package org.opencds.plugin.api;

import java.util.Map;

public record PreProcessPluginContext(Map<String, SupportingData> supportingData,
                                      PluginDataCache cache,
                                      Map<String, Object> globals) implements PluginContext
{
    public static PreProcessPluginContext create(final Map<String, SupportingData> supportingData, final PluginDataCache cache,
            final Map<String, Object> globals)
    {
        return new PreProcessPluginContext(supportingData, cache, globals);
    }

    public static PreProcessPluginContext createPreProcessPluginContext(final Map<String, SupportingData> supportingData,
            final PluginDataCache cache, final Map<String, Object> globals)
    {
        return create(supportingData, cache, globals);
    }
}
