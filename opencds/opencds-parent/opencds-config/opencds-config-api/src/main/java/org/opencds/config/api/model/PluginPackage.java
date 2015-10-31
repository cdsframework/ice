package org.opencds.config.api.model;

import java.util.Date;
import java.util.List;

public interface PluginPackage {
    
    PPId getIdentifier();
    
    LoadContext getLoadContext();
    
    String getResourceName();
    
    List<Plugin> getPlugins();
    
    Plugin getPlugin(PluginId pluginId);
    
    Date getTimestamp();
    
    String getUserId();

}
