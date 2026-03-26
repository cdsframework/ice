package org.opencds.config.api.model.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;

public record SemanticSignifierImpl(SSId ssId,
                                    String name,
                                    String description,
                                    String entryPoint,
                                    String exitPoint,
                                    String factListsBuilder,
                                    String resultSetBuilder,
                                    Date timestamp,
                                    String userId) implements SemanticSignifier
{
    public static SemanticSignifierImpl create(final SSId ssId, final String name, final String description,
            final String entryPoint, final String exitPoint, final String factListsBuilder, final String resultSetBuilder,
            final Date timestamp, final String userId)
    {
        return new SemanticSignifierImpl(SSIdImpl.create(ssId), name, description, entryPoint, exitPoint, factListsBuilder,
                resultSetBuilder, timestamp, userId);
    }

    public static SemanticSignifierImpl create(final SemanticSignifier ss)
    {
        if (ss == null)
            return null;
        if (ss instanceof final SemanticSignifierImpl semanticSignifierImpl)
            return semanticSignifierImpl;
        return create(ss.getSSId(), ss.getName(), ss.getDescription(), ss.getEntryPoint(), ss.getExitPoint(),
                ss.getFactListsBuilder(), ss.getResultSetBuilder(), ss.getTimestamp(), ss.getUserId());
    }

    public SemanticSignifierImpl
    {
        assert ssId != null;
        assert StringUtils.isNotBlank(name);
        assert StringUtils.isNotBlank(description);
        assert StringUtils.isNotBlank(entryPoint);
        assert StringUtils.isNotBlank(exitPoint);
        assert StringUtils.isNotBlank(factListsBuilder);
        assert timestamp != null;
    }

    @Override
    public SSId getSSId()
    {
        return ssId;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public String getEntryPoint()
    {
        return entryPoint;
    }

    @Override
    public String getExitPoint()
    {
        return exitPoint;
    }

    @Override
    public String getFactListsBuilder()
    {
        return factListsBuilder;
    }

    @Override
    public String getResultSetBuilder()
    {
        return resultSetBuilder;
    }

    @Override
    public Date getTimestamp()
    {
        return timestamp;
    }

    @Override
    public String getUserId()
    {
        return userId;
    }
}
