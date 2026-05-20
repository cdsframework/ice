package org.opencds.config.api.model;

import org.springframework.util.StringUtils;

public record Plugin(PluginId identifier,
                     String className)
{
    public Plugin
    {
        assert identifier != null;
        assert StringUtils.hasText(className);
    }
}
