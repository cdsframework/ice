package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.Plugin;
import org.opencds.config.api.model.PluginId;

public class PluginImpl implements Plugin {

    private PluginId identifier;
    private String className;

    private PluginImpl() {
    }

    public static PluginImpl create(PluginId identifier, String className) {
        PluginImpl ppi = new PluginImpl();
        ppi.identifier = PluginIdImpl.create(identifier);
        ppi.className = className;
        return ppi;
    }

    public static PluginImpl create(Plugin pp) {
        if (pp == null) {
            return null;
        }
        if (pp instanceof PluginImpl) {
            return PluginImpl.class.cast(pp);
        }
        return create(pp.getIdentifier(), pp.getClassName());
    }

    public static List<Plugin> create(List<Plugin> plugins) {
        if (plugins == null) {
            return null;
        }
        List<Plugin> pis = new ArrayList<>();
        for (Plugin p : plugins) {
            pis.add(create(p));
        }
        return pis;
    }
    
    @Override
    public PluginId getIdentifier() {
        return identifier;
    }

    @Override
    public String getClassName() {
        return className;
    }

}
