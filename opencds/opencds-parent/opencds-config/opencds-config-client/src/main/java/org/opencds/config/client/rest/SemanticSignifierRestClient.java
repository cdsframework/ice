package org.opencds.config.client.rest;

import org.opencds.config.client.SemanticSignifierClient;
import org.opencds.config.client.rest.util.PathUtil;

public class SemanticSignifierRestClient implements SemanticSignifierClient {
    private static final String PATH = "semanticsignifiers";

    private RestClient restClient;
    
    public SemanticSignifierRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public <T> T getSemanticSignifiers(Class<T> clazz) {
        return restClient.get(PATH, clazz);
    }
    
    @Override
    public <T> void putSemanticSignifiers(T sses) {
        restClient.put(PATH, sses);
    }

    @Override
    public <T> T getSemanticSignifier(String ssid, Class<T> clazz) {
        return restClient.get(PathUtil.buildPath(PATH, ssid), clazz);
    }

    @Override
    public <T> void postSemanticSignifier(T semanticSignifiers) {
        restClient.post(PATH, semanticSignifiers);
    }
    
    @Override
    public <T> void putSemanticSignifier(String ssid, T semanticSignifier) {
        restClient.put(PathUtil.buildPath(PATH, ssid), semanticSignifier);
    }

    @Override
    public void deleteSemanticSignifier(String ssid) {
        restClient.delete(PathUtil.buildPath(PATH, ssid));
    }

    
}
