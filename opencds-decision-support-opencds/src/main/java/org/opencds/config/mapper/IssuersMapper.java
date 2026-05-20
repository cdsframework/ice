package org.opencds.config.mapper;

import java.util.List;

import org.opencds.config.api.model.AccessType;
import org.opencds.config.api.model.Issuer;
import org.opencds.config.schema.Issuers;

public class IssuersMapper
{
    public static List<Issuer> internal(final Issuers issuers)
    {
        if (issuers == null)
            return null;

        return issuers.getIssuer().stream().map(IssuersMapper::internal).toList();
    }

    private static Issuer internal(final org.opencds.config.schema.Issuer issuer)
    {
        if (issuer == null)
            throw new IllegalArgumentException("issuer must not be null");

        return new Issuer(issuer.getIss(), issuer.getJku(), issuer.getJwk(), AccessType.valueOf(issuer.getAccessType().value()));
    }

    public static Issuers external(final List<Issuer> issuers)
    {
        if (issuers == null)
            return null;

        final var issuer = new Issuers();
        issuers.stream().map(IssuersMapper::external).forEach(issuer.getIssuer()::add);
        return issuer;
    }

    public static org.opencds.config.schema.Issuer external(final Issuer issuer)
    {
        if (issuer == null)
            throw new IllegalArgumentException("issuer must not be null");

        final var iss = new org.opencds.config.schema.Issuer();
        iss.setIss(issuer.iss());
        iss.setJku(issuer.jku());
        iss.setJwk(issuer.jwk());
        iss.setAccessType(org.opencds.config.schema.AccessType.valueOf(issuer.accessType().name()));
        return iss;
    }
}
