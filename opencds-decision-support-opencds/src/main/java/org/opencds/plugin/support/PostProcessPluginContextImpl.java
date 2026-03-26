package org.opencds.plugin.support;

import java.util.Map;

import org.opencds.plugin.api.PluginDataCache;
import org.opencds.plugin.api.PostProcessPluginContext;
import org.opencds.plugin.api.SupportingData;

public record PostProcessPluginContextImpl(Map<String, SupportingData> supportingData,
                                           PluginDataCache cache) implements PostProcessPluginContext
{
    public static PostProcessPluginContextImpl create(final Map<String, SupportingData> supportingData, final PluginDataCache cache)
    {
        return new PostProcessPluginContextImpl(supportingData, cache);
    }

    public static PostProcessPluginContextImpl createPostProcessPluginContext(final Map<String, SupportingData> supportingData,
            final PluginDataCache cache)
    {
        return create(supportingData, cache);
    }
}
