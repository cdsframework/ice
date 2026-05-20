package org.opencds.config.api.model;

import org.springframework.util.StringUtils;

public record CDMId(String codeSystem,
                    String code,
                    String version)
{
    public CDMId
    {
        assert StringUtils.hasText(code);
    }
}
