package org.opencds.config.api.model;

import java.time.LocalDate;

import org.springframework.util.StringUtils;

public record SupportingData(String identifier,
                             KMId kmId,
                             String packageType,
                             String packageId,
                             PluginId loadedBy,
                             LocalDate timestamp,
                             String userId)
{
    public SupportingData
    {
        assert StringUtils.hasText(identifier);
        assert StringUtils.hasText(packageType);
        assert StringUtils.hasText(packageId);
    }
}
