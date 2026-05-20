package org.opencds.config.api.model;

import org.springframework.util.StringUtils;

public record Issuer(String iss,
                     String jku,
                     String jwk,
                     AccessType accessType)
{
    public Issuer
    {
        assert StringUtils.hasText(iss);
        assert StringUtils.hasText(jku) || StringUtils.hasText(jwk);
        assert accessType != null;
    }
}
