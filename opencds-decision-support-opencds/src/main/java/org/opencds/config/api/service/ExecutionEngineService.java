package org.opencds.config.api.service;

import java.util.List;

import org.opencds.config.api.ExecutionEngineAdapter;
import org.opencds.config.api.ExecutionEngineContext;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.model.ExecutionEngine;

public interface ExecutionEngineService
{
    ExecutionEngine find(String identifier);

    List<ExecutionEngine> getAll();

    void persist(ExecutionEngine ee);

    void persist(List<ExecutionEngine> internal);

    void delete(String identifier);

    @Deprecated(forRemoval = true)
    <T> T getExecutionEngineInstance(ExecutionEngine engine);

    <I, O, P, E extends ExecutionEngineAdapter<I, O, P>> E getExecutionEngineAdapter(ExecutionEngine engine);

    <I, O, KL extends KnowledgeLoader<I, O>> KL getKnowledgeLoader(ExecutionEngine ee);

    <I, O, C extends ExecutionEngineContext<I, O>> C createContext(ExecutionEngine ee);
}
