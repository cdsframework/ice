/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencds.config.api.model.LoadContext;
import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.Plugin;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PluginPackage;

public class PluginPackageImpl implements PluginPackage {

    private PPId identifier;
    private LoadContext loadContext;
    private String resourceName;
    private List<Plugin> plugins;
    private Date timestamp;
    private String userId;

    private PluginPackageImpl() {
    }

    public static PluginPackageImpl create(PPId identifier, LoadContext loadContext, String resourceName, List<Plugin> plugins, Date timestamp, String userId) {
        PluginPackageImpl ppi = new PluginPackageImpl();
        ppi.identifier = PPIdImpl.create(identifier);
        ppi.loadContext = loadContext;
        ppi.resourceName = resourceName;
        ppi.plugins = PluginImpl.create(plugins);
        ppi.timestamp = timestamp;
        ppi.userId = userId;
        return ppi;
    }

    public static PluginPackageImpl create(PluginPackage pp) {
        if (pp == null) {
            return null;
        }
        if (pp instanceof PluginPackageImpl) {
            return PluginPackageImpl.class.cast(pp);
        }
        return create(pp.getIdentifier(), pp.getLoadContext(), pp.getResourceName(), pp.getPlugins(),
                pp.getTimestamp(), pp.getUserId());
    }
    
    public static List<PluginPackageImpl> create(List<PluginPackage> pps) {
        if (pps == null) {
            return null;
        }
        List<PluginPackageImpl> ppis = new ArrayList<>();
        for (PluginPackage pp : pps) {
            ppis.add(create(pp));
        }
        return ppis;
    }

    @Override
    public PPId getIdentifier() {
        return identifier;
    }

    @Override
    public LoadContext getLoadContext() {
        return loadContext;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Override
    public List<Plugin> getPlugins() {
        return plugins;
    }

    @Override
    public Plugin getPlugin(PluginId pluginId) {
        if (plugins == null) {
            return null;
        }
        for (Plugin p : plugins) {
            if (p.getIdentifier().equals(pluginId)) {
                return p;
            }
        }
        return null;
    }
    
    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String getUserId() {
        return userId;
    }

}
