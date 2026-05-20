package org.opencds.config.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.opencds.common.utilities.XMLDateUtility;
import org.opencds.config.api.model.DssOperation;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.schema.ExecutionEngines;
import org.opencds.config.schema.OperationType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ExecutionEngineMapper
{
    public static ExecutionEngine internal(final org.opencds.config.schema.ExecutionEngine external)
    {
        final List<DssOperation> supportedOperations = external.getSupportedOperation()
                .stream()
                .flatMap(supportedOp -> Arrays.stream(DssOperation.values())
                        .filter(op -> op.getDssName().equals(supportedOp.value())))
                .toList();
        log.error("FROM MAPPER: KL= {}", external.getKnowledgeLoader());

        return new ExecutionEngine(external.getIdentifier(), external.getAdapter(), external.getContext(),
                external.getKnowledgeLoader(), external.getDescription(),
                external.getTimestamp().toGregorianCalendar().toZonedDateTime().toLocalDate(), external.getUserId(),
                supportedOperations);
    }

    public static List<ExecutionEngine> internal(final ExecutionEngines executionEngines)
    {
        if (executionEngines == null || executionEngines.getExecutionEngine() == null)
            return null;

        return executionEngines.getExecutionEngine().stream().map(ExecutionEngineMapper::internal).toList();
    }

    public static org.opencds.config.schema.ExecutionEngine external(final ExecutionEngine internal)
    {
        if (internal == null)
            return null;

        final org.opencds.config.schema.ExecutionEngine external = new org.opencds.config.schema.ExecutionEngine();
        external.setDescription(internal.description());
        external.setIdentifier(internal.identifier());
        external.setAdapter(internal.adapter());
        external.setContext(internal.context());
        external.setKnowledgeLoader(internal.knowledgeLoader());
        external.setTimestamp(XMLDateUtility.date2XMLGregorian(internal.timestamp()));
        external.setUserId(internal.userId());

        internal.supportedOperations()
                .stream()
                .filter(Objects::nonNull)
                .map(DssOperation::getDssName)
                .map(OperationType::fromValue)
                .forEach(external.getSupportedOperation()::add);

        return external;

    }

    public static ExecutionEngines external(final List<ExecutionEngine> all)
    {
        if (all == null)
            return null;

        final ExecutionEngines externalEEs = new ExecutionEngines();

        all.stream().map(ExecutionEngineMapper::external).forEach(externalEEs.getExecutionEngine()::add);

        return externalEEs;
    }
}
