package org.opencds.config.api.util;

import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PrePostProcessPluginId;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PluginIdTuple
{
    public static PluginIdTuple create(final PluginId left, final PrePostProcessPluginId right)
    {
        return new PluginIdTuple(left, right);
    }

    private final PluginId pluginId;
    private final PrePostProcessPluginId prePostProcessPluginId;

    public PluginId getLeft()
    {
        return pluginId;
    }

    public PrePostProcessPluginId getRight()
    {
        return prePostProcessPluginId;
    }
}
