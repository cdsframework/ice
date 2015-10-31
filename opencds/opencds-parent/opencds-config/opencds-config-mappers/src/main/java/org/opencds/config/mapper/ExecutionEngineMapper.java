package org.opencds.config.mapper;

import java.util.ArrayList;
import java.util.List;

import org.opencds.common.utilities.XMLDateUtility;
import org.opencds.config.api.model.DssOperation;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.impl.ExecutionEngineImpl;
import org.opencds.config.schema.ExecutionEngines;
import org.opencds.config.schema.OperationType;

public abstract class ExecutionEngineMapper {

    public static ExecutionEngine internal(org.opencds.config.schema.ExecutionEngine external) {

        List<DssOperation> supportedOperations = new ArrayList<>();

        for (OperationType supportedOp : external.getSupportedOperation()) {
            for (DssOperation op : DssOperation.values()) {
                if (op.getDssName().equals(supportedOp.value())) {
                    supportedOperations.add(op);
                }
            }
        }

        return ExecutionEngineImpl.create(external.getIdentifier(), external.getDescription(), external.getTimestamp()
                .toGregorianCalendar().getTime(), external.getUserId(), supportedOperations);
    }

    public static List<ExecutionEngine> internal(ExecutionEngines executionEngines) {
        if (executionEngines == null || executionEngines.getExecutionEngine() == null) {
            return null;
        }
        List<ExecutionEngine> internalEEs = new ArrayList<>();
        for (org.opencds.config.schema.ExecutionEngine ee : executionEngines.getExecutionEngine()) {
            internalEEs.add(internal(ee));
        }
        return internalEEs;
    }

    public static org.opencds.config.schema.ExecutionEngine external(ExecutionEngine internal) {
        if (internal == null) {
            return null;
        }
        org.opencds.config.schema.ExecutionEngine external = new org.opencds.config.schema.ExecutionEngine();
        external.setDescription(internal.getDescription());
        external.setIdentifier(internal.getIdentifier());
        external.setTimestamp(XMLDateUtility.date2XMLGregorian(internal.getTimestamp()));
        external.setUserId(internal.getUserId());

        for (DssOperation so : internal.getSupportedOperations()) {
            if (so == null) {
                continue;
            }
            OperationType op = OperationType.fromValue(so.getDssName());
            external.getSupportedOperation().add(op);
        }

        return external;

    }

    public static ExecutionEngines external(List<ExecutionEngine> all) {
        if (all == null) {
            return null;
        }
        ExecutionEngines externalEEs = new ExecutionEngines();
        for (ExecutionEngine ee : all) {
            externalEEs.getExecutionEngine().add(external(ee));
        }
        return externalEEs;
    }

}
