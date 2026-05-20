package org.opencds.config.api.model;

import java.util.List;

import org.springframework.util.StringUtils;

public record PrePostProcessPluginId(String scopingEntityId,
                                     String businessId,
                                     String version,
                                     List<String> supportingDataIdentifiers)
{
    public PrePostProcessPluginId
    {
        assert StringUtils.hasText(scopingEntityId);
        assert StringUtils.hasText(businessId);
        assert StringUtils.hasText(version);
        supportingDataIdentifiers = supportingDataIdentifiers == null ? List.of() : List.copyOf(supportingDataIdentifiers);
    }
}
