package org.opencds.config.api.model;

import org.springframework.util.StringUtils;

public record KMId(String scopingEntityId,
                   String businessId,
                   String version) implements EntityIdentifier
{
    public KMId(final String eiString)
    {
        this(eiString.substring(0, eiString.indexOf("^")), eiString.substring(eiString.indexOf("^") + 1, eiString.lastIndexOf("^")),
                eiString.substring(eiString.lastIndexOf("^") + 1));
    }

    public KMId
    {
        assert StringUtils.hasText(scopingEntityId);
        assert StringUtils.hasText(businessId);
        assert StringUtils.hasText(version);
    }
}
