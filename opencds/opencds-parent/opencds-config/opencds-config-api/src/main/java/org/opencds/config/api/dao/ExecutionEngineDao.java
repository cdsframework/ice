package org.opencds.config.api.dao;

import java.util.List;

import org.opencds.config.api.model.ExecutionEngine;

public interface ExecutionEngineDao {

    ExecutionEngine find(String identifier);

    List<ExecutionEngine> getAll();

    void persist(ExecutionEngine ee);

    void delete(ExecutionEngine ee);

    void persist(List<ExecutionEngine> ees);

}
