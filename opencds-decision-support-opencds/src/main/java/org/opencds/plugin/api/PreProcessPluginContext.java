package org.opencds.plugin.api;

import java.util.Map;

public interface PreProcessPluginContext extends PluginContext
{
    Map<String, Object> globals();
}
