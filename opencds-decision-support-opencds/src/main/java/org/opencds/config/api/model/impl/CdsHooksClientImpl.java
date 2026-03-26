package org.opencds.config.api.model.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.opencds.config.api.model.CdsHooksClient;
import org.opencds.config.api.model.Issuer;

public record CdsHooksClientImpl(String id,
                                 String description,
                                 List<Issuer> issuers,
                                 String tenant) implements CdsHooksClient
{
    public static CdsHooksClient create(final String id, final String description, final List<Issuer> issuers, final String tenant)
    {
        return new CdsHooksClientImpl(id, description, issuers, tenant);
    }

    public static CdsHooksClient create(final CdsHooksClient ce)
    {
        if (ce == null)
            return null;
        if (ce instanceof CdsHooksClientImpl)
            return ce;
        return create(ce.id(), ce.description(), ce.issuers().stream().map(IssuerImpl::create).toList(), ce.tenant());
    }

    public CdsHooksClientImpl
    {
        assert StringUtils.isNotBlank(id);
        assert issuers != null && !issuers.isEmpty();
        assert Objects.isNull(tenant) || StringUtils.isNotBlank(tenant);
    }
}
