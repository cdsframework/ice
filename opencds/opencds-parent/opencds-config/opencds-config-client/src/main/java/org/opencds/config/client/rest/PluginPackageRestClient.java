package org.opencds.config.client.rest;

import org.opencds.config.client.PluginPackageClient;
import org.opencds.config.client.rest.util.PathUtil;

public class PluginPackageRestClient implements PluginPackageClient {
    private static final String PATH = "pluginpackages";

    private RestClient restClient;
    
    public PluginPackageRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public <T> T getPluginPackages(Class<T> clazz) {
        return restClient.get(PATH, clazz);
    }
    
    @Override
    public <T> void putPluginPackages(T pps) {
        restClient.put(PATH, pps);
    }

    @Override
    public <T> T getPluginPackage(String ppid, Class<T> clazz) {
        return restClient.get(PathUtil.buildPath(PATH, ppid), clazz);
    }

    @Override
    public <T> void postPluginPackage(T pluginPackages) {
        restClient.post(PATH, pluginPackages);
    }
    
    @Override
    public <T> void putPluginPackage(String ppid, T pluginPackage) {
        restClient.put(PathUtil.buildPath(PATH, ppid), pluginPackage);
    }

    @Override
    public void deletePluginPackage(String ppid) {
        restClient.delete(PathUtil.buildPath(PATH, ppid));
    }

    
}
