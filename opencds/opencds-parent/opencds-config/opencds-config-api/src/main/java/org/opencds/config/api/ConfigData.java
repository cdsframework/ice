package org.opencds.config.api;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigData {
    private String configType;
    private String configPath;
    /**
     * Default value is 1.
     */
    private int kmThreads = 1;

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public Path getConfigLocation() {
        return Paths.get(configPath);
    }
    
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
    
    public int getKmThreads() {
        return kmThreads;
    }
    
    public void setKmThreads(int kmThreads) {
        this.kmThreads = kmThreads;
    }
}
