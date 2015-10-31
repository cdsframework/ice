package org.opencds.config.api.service;

import java.io.InputStream;
import java.util.List;

import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.Plugin;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PluginPackage;
import org.opencds.plugin.OpencdsPlugin;
import org.opencds.plugin.PluginContext;

public interface PluginPackageService {

    PluginPackage find(PPId identifier);
    
    PluginPackage find(PluginId pluginId);
    
    List<PluginPackage> getAll();

    void persist(PluginPackage pp);

    void persist(List<PluginPackage> internal);

    void delete(PPId ppId);

    InputStream getJar(PPId ppId);

    void persistJar(PPId ppId, InputStream jar);

    void deleteJar(PPId ppId);

    <PC extends PluginContext> OpencdsPlugin<PC> load(PluginId pluginId);

    List<PluginId> getAllPluginIds();

}
