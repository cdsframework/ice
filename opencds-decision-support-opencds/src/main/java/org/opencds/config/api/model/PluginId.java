package org.opencds.config.api.model;

import org.springframework.util.StringUtils;

public record PluginId(String scopingEntityId,
                       String businessId,
                       String version) implements EntityIdentifier
{
    public PluginId
    {
        assert StringUtils.hasText(scopingEntityId);
        assert StringUtils.hasText(businessId);
        assert StringUtils.hasText(version);
    }
}
