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

package org.opencds.config.mapper;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.Plugin;
import org.opencds.config.api.model.impl.PluginImpl;
import org.opencds.config.schema.PluginPackage.Plugins;

public class PluginMapper {

    public static Plugin internal(org.opencds.config.schema.Plugin external) {
        if (external == null) {
            return null;
        }
        return PluginImpl.create(PluginIdMapper.internal(external.getIdentifier()), external.getClassName());
    }

    public static List<Plugin> internal(Plugins external) {
        if (external == null) {
            return null;
        }
        List<Plugin> plugins = new ArrayList<>();
        for (org.opencds.config.schema.Plugin plugin : external.getPlugin()) {
            plugins.add(internal(plugin));
        }
        return plugins;
    }

    private static org.opencds.config.schema.Plugin external(Plugin internal) {
        if (internal == null) {
            return null;
        }
        org.opencds.config.schema.Plugin plugin = new org.opencds.config.schema.Plugin();
        plugin.setIdentifier(PluginIdMapper.external(internal.getIdentifier()));
        plugin.setClassName(internal.getClassName());
        return plugin;
    }

    public static Plugins external(List<Plugin> external) {
        if (external == null) {
            return null;
        }
        Plugins plugins = new Plugins();
        for (Plugin plugin : external) {
            plugins.getPlugin().add(external(plugin));
        }
        return plugins;
    }

}
