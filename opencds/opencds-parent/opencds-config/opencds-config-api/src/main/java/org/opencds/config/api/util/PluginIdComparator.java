/*
 * Copyright 2020 OpenCDS.org
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

package org.opencds.config.api.util;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PrePostProcessPluginId;

public class PluginIdComparator {

    public static List<PluginIdTuple> intersect(List<PrePostProcessPluginId> prePostProcessPlugins, List<PluginId> plugins) {
        return prePostProcessPlugins.stream()
                .map(pppid ->
                        Optional.ofNullable(match(plugins, pppid))
                                .map(pluginId -> PluginIdTuple.create(pluginId, pppid))
                                .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static PluginId match(List<PluginId> pluginIds,
                                 PrePostProcessPluginId prePostProcessPluginId) {
        return pluginIds.stream().filter(pluginId -> compare(prePostProcessPluginId, pluginId))
                .findFirst()
                .orElse(null);
    }

    public static boolean compare(PrePostProcessPluginId prePostProcessPlugin, PluginId plugin) {
        return prePostProcessPlugin.getScopingEntityId().equals(plugin.getScopingEntityId())
                && prePostProcessPlugin.getBusinessId().equals(plugin.getBusinessId())
                && prePostProcessPlugin.getVersion().equals(plugin.getVersion());
    }

}
