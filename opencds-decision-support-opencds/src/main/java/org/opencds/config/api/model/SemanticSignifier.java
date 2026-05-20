package org.opencds.config.api.model;

import java.time.LocalDate;

import org.springframework.util.StringUtils;

public record SemanticSignifier(SSId ssId,
                                String name,
                                String description,
                                LocalDate timestamp,
                                String userId)
{
    public SemanticSignifier
    {
        assert ssId != null;
        assert StringUtils.hasText(name);
        assert StringUtils.hasText(description);
        assert timestamp != null;
    }
}
