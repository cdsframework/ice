package org.opencds.config.api.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public record ExecutionEngine(String identifier,
                              String adapter,
                              String context,
                              String knowledgeLoader,
                              String description,
                              LocalDate timestamp,
                              String userId,
                              List<DssOperation> supportedOperations)
{
    public ExecutionEngine
    {
        assert StringUtils.hasText(identifier);
        assert StringUtils.hasText(adapter);
        assert StringUtils.hasText(context);
        assert StringUtils.hasText(knowledgeLoader);
        assert StringUtils.hasText(description);
        assert timestamp != null;
        assert !ObjectUtils.isEmpty(supportedOperations);
        supportedOperations = List.copyOf(supportedOperations);
    }
}
