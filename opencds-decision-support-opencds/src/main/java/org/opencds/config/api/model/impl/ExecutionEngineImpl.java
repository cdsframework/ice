package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opencds.config.api.model.DssOperation;
import org.opencds.config.api.model.ExecutionEngine;

public record ExecutionEngineImpl(String identifier,
                                  String adapter,
                                  String context,
                                  String knowledgeLoader,
                                  String description,
                                  Date timestamp,
                                  String userId,
                                  List<DssOperation> supportedOperations) implements ExecutionEngine
{
    public static ExecutionEngineImpl create(final String identifier, final String adapter, final String context,
            final String knowledgeLoader, final String description, final Date timestamp, final String userId,
            final List<DssOperation> supportedOperations)
    {
        return new ExecutionEngineImpl(identifier, adapter, context, knowledgeLoader, description, timestamp, userId,
                new ArrayList<>(supportedOperations));
    }

    public static ExecutionEngineImpl create(final ExecutionEngine ee)
    {
        if (ee == null)
            return null;
        if (ee instanceof final ExecutionEngineImpl executionEngineImpl)
            return executionEngineImpl;
        return create(ee.getIdentifier(), ee.getAdapter(), ee.getContext(), ee.getKnowledgeLoader(), ee.getDescription(),
                ee.getTimestamp(), ee.getUserId(), ee.getSupportedOperations());
    }

    public static List<ExecutionEngineImpl> create(final List<ExecutionEngine> ees)
    {
        if (ees == null)
            return null;
        final var eeis = new ArrayList<ExecutionEngineImpl>();
        for (final var ee : ees)
            eeis.add(create(ee));
        return eeis;
    }

    public ExecutionEngineImpl
    {
        assert StringUtils.isNotEmpty(identifier);
        assert StringUtils.isNotEmpty(adapter);
        assert StringUtils.isNotEmpty(context);
        assert StringUtils.isNotEmpty(knowledgeLoader);
        assert StringUtils.isNotEmpty(description);
        assert timestamp != null;
        assert supportedOperations != null && !supportedOperations.isEmpty();
        supportedOperations = Collections.unmodifiableList(supportedOperations);
    }

    @Override
    public String getIdentifier()
    {
        return identifier;
    }

    @Override
    public String getAdapter()
    {
        return adapter;
    }

    @Override
    public String getContext()
    {
        return context;
    }

    @Override
    public String getKnowledgeLoader()
    {
        return knowledgeLoader;
    }

    @Override
    public String getDescription()
    {
        return description;
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

    @Override
    public List<DssOperation> getSupportedOperations()
    {
        return supportedOperations;
    }
}
