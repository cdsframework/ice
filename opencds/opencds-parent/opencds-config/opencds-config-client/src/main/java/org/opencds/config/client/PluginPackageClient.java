package org.opencds.config.client;

public interface PluginPackageClient {
    <T> T getPluginPackages(Class<T> clazz);
    
    <T> void putPluginPackages(T pluginPackages);
    
    <T> void postPluginPackage(T pluginPackage);
    
    <T> T getPluginPackage(String ppid, Class<T> clazz);
    
    <T> void putPluginPackage(String ppid, T pluginPackage);
    
    void deletePluginPackage(String ppid);
}
