package org.opencds.config.api.model;

public interface Plugin {
    PluginId getIdentifier();
    
    String getClassName();
}
