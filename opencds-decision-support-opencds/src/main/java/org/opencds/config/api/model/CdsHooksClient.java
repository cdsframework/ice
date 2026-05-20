package org.opencds.config.api.model;

import java.util.List;
import java.util.Objects;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public record CdsHooksClient(String id,
                             String description,
                             List<Issuer> issuers,
                             String tenant)
{
    public CdsHooksClient
    {
        assert StringUtils.hasText(id);
        assert !ObjectUtils.isEmpty(issuers);
        assert Objects.isNull(tenant) || StringUtils.hasText(tenant);
    }
}
