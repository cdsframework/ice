package org.opencds.plugin.support;

import java.util.Map;

import org.opencds.plugin.api.PluginDataCache;
import org.opencds.plugin.api.PreProcessPluginContext;
import org.opencds.plugin.api.SupportingData;

public record PreProcessPluginContextImpl(Map<String, SupportingData> supportingData,
                                          PluginDataCache cache,
                                          Map<String, Object> globals) implements PreProcessPluginContext
{
    public static PreProcessPluginContextImpl create(final Map<String, SupportingData> supportingData, final PluginDataCache cache,
            final Map<String, Object> globals)
    {
        return new PreProcessPluginContextImpl(supportingData, cache, globals);
    }

    public static PreProcessPluginContextImpl createPreProcessPluginContext(final Map<String, SupportingData> supportingData,
            final PluginDataCache cache, final Map<String, Object> globals)
    {
        return create(supportingData, cache, globals);
    }
}
