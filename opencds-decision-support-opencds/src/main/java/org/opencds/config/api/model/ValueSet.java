package org.opencds.config.api.model;

import org.springframework.util.StringUtils;

public record ValueSet(String oid,
                       String name)
{
    public ValueSet
    {
        assert StringUtils.hasText(oid);
    }
}
