package org.opencds.plugin.api;

import java.util.Map;

public record PostProcessPluginContext(Map<String, SupportingData> supportingData,
                                       PluginDataCache cache) implements PluginContext
{
    public static PostProcessPluginContext create(final Map<String, SupportingData> supportingData, final PluginDataCache cache)
    {
        return new PostProcessPluginContext(supportingData, cache);
    }

    public static PostProcessPluginContext createPostProcessPluginContext(final Map<String, SupportingData> supportingData,
            final PluginDataCache cache)
    {
        return create(supportingData, cache);
    }
}
