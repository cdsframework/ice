package org.opencds.config.api.model.impl;

import org.apache.commons.lang3.StringUtils;
import org.opencds.config.api.model.AccessType;
import org.opencds.config.api.model.Issuer;

public record IssuerImpl(String iss,
                         String jku,
                         String jwk,
                         AccessType accessType) implements Issuer
{
    public static Issuer create(final String iss, final String jku, final String jwk, final AccessType accessType)
    {
        return new IssuerImpl(iss, jku, jwk, accessType);
    }

    public static Issuer create(final Issuer issuer)
    {
        return create(issuer.iss(), issuer.jku(), issuer.jwk(), issuer.accessType());
    }

    public IssuerImpl
    {
        assert StringUtils.isNotBlank(iss);
        assert StringUtils.isNotBlank(jku) || StringUtils.isNotBlank(jwk);
        assert accessType != null;
    }
}
