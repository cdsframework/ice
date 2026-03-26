package org.opencds.config.api.service;

import java.util.List;

import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PluginPackage;
import org.opencds.plugin.api.OpencdsPlugin;
import org.opencds.plugin.api.PluginContext;

public interface PluginPackageService
{
    PluginPackage find(PPId identifier);

    PluginPackage find(PluginId pluginId);

    List<PluginPackage> getAll();

    void persist(PluginPackage pp);

    void persist(List<PluginPackage> internal);

    void delete(PPId ppId);

    <PC extends PluginContext, OP extends OpencdsPlugin<PC>> OP load(PluginId pluginId);

    List<PluginId> getAllPluginIds();
}
