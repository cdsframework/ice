package org.opencds.config.api.model;

import org.springframework.util.StringUtils;

public record Concept(String code,
                      String codeSystem,
                      String codeSystemName,
                      String displayName,
                      String comment,
                      ValueSet valueSet)
{
    public Concept
    {
        assert StringUtils.hasText(code);
        assert StringUtils.hasText(codeSystem);
    }
}
