package org.opencds.config.api.service;

import java.util.List;

import org.opencds.config.api.model.ExecutionEngine;

public interface ExecutionEngineService {
    ExecutionEngine find(String identifier);

    List<ExecutionEngine> getAll();

    void persist(ExecutionEngine ee);

    void persist(List<ExecutionEngine> internal);

    void delete(String identifier);

    <T> T getExecutionEngineInstance(ExecutionEngine engine);


}
