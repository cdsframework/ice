package org.opencds.config.api.util;

import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PrePostProcessPluginId;

public record PluginIdTuple(PluginId pluginId,
                            PrePostProcessPluginId prePostProcessPluginId)
{
}
