package org.opencds.config.client.rest;

import org.opencds.config.client.ExecutionEngineClient;
import org.opencds.config.client.rest.util.PathUtil;

public class ExecutionEngineRestClient implements ExecutionEngineClient {
    private static final String PATH = "executionengines";

    private RestClient restClient;

    public ExecutionEngineRestClient(RestClient restClient) {
        this.restClient = restClient;
    }
    
    @Override
    public <T> T getExecutionEngines(Class<T> clazz) {
        return restClient.get(PATH, clazz);
    }
    
    @Override
    public <T> void putExecutionEngines(T t) {
        restClient.put(PATH, t);
    }

    @Override
    public <T> T getExecutionEngine(String identifier, Class<T> clazz) {
        return restClient.get(PathUtil.buildPath(PATH, identifier), clazz);
    }

    @Override
    public <T> void postExecutionEngine(T t) {
        restClient.post(PATH, t);
    }
    
    @Override
    public <T> void putExecutionEngine(String identifier, T t) {
        restClient.put(PathUtil.buildPath(PATH, identifier), t);
    }

    @Override
    public void deleteExecutionEngine(String identifier) {
        restClient.delete(PathUtil.buildPath(PATH, identifier));
    }

}
