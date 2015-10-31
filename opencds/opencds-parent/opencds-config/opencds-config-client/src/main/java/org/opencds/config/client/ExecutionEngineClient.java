package org.opencds.config.client;

public interface ExecutionEngineClient {
    <T> T getExecutionEngines(Class<T> clazz);
    
    <T> void putExecutionEngines(T t);
    
    <T> void postExecutionEngine(T t);
    
    <T> T getExecutionEngine(String identifier, Class<T> clazz);
    
    <T> void putExecutionEngine(String identifier, T t);
    
    void deleteExecutionEngine(String identifier);
}
