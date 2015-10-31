package org.opencds.config.api.dao;

import java.util.List;

import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.PluginPackage;

public interface PluginPackageDao {

    PluginPackage find(PPId ppId);
    
    List<PluginPackage> getAll();
    
    void persist(PluginPackage pluginPackage);
    
    void persist(List<PluginPackage> pluginPackages);
    
    void delete(PluginPackage pluginPackage);

}
