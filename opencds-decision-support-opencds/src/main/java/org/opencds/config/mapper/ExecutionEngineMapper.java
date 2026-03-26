package org.opencds.config.mapper;

import java.util.ArrayList;
import java.util.List;

import org.opencds.common.utilities.XMLDateUtility;
import org.opencds.config.api.model.DssOperation;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.impl.ExecutionEngineImpl;
import org.opencds.config.schema.ExecutionEngines;
import org.opencds.config.schema.OperationType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ExecutionEngineMapper
{
    public static ExecutionEngine internal(final org.opencds.config.schema.ExecutionEngine external)
    {
        final List<DssOperation> supportedOperations = new ArrayList<>();

        for (final OperationType supportedOp : external.getSupportedOperation())
        {
            for (final DssOperation op : DssOperation.values())
            {
                if (op.getDssName().equals(supportedOp.value()))
                    supportedOperations.add(op);
            }
        }
        log.error("FROM MAPPER: KL= {}", external.getKnowledgeLoader());

        return ExecutionEngineImpl.create(external.getIdentifier(), external.getAdapter(), external.getContext(),
                external.getKnowledgeLoader(), external.getDescription(), external.getTimestamp().toGregorianCalendar().getTime(),
                external.getUserId(), supportedOperations);
    }

    public static List<ExecutionEngine> internal(final ExecutionEngines executionEngines)
    {
        if (executionEngines == null || executionEngines.getExecutionEngine() == null)
            return null;
        final List<ExecutionEngine> internalEEs = new ArrayList<>();
        for (final org.opencds.config.schema.ExecutionEngine ee : executionEngines.getExecutionEngine())
            internalEEs.add(internal(ee));
        return internalEEs;
    }

    public static org.opencds.config.schema.ExecutionEngine external(final ExecutionEngine internal)
    {
        if (internal == null)
            return null;
        final org.opencds.config.schema.ExecutionEngine external = new org.opencds.config.schema.ExecutionEngine();
        external.setDescription(internal.getDescription());
        external.setIdentifier(internal.getIdentifier());
        external.setAdapter(internal.getAdapter());
        external.setContext(internal.getContext());
        external.setKnowledgeLoader(internal.getKnowledgeLoader());
        external.setTimestamp(XMLDateUtility.date2XMLGregorian(internal.getTimestamp()));
        external.setUserId(internal.getUserId());

        for (final DssOperation so : internal.getSupportedOperations())
        {
            if (so == null)
                continue;
            final OperationType op = OperationType.fromValue(so.getDssName());
            external.getSupportedOperation().add(op);
        }

        return external;

    }

    public static ExecutionEngines external(final List<ExecutionEngine> all)
    {
        if (all == null)
            return null;
        final ExecutionEngines externalEEs = new ExecutionEngines();
        for (final ExecutionEngine ee : all)
            externalEEs.getExecutionEngine().add(external(ee));
        return externalEEs;
    }
}
