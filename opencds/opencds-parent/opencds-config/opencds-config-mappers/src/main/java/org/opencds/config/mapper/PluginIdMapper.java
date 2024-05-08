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

import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.impl.PluginIdImpl;

public class PluginIdMapper {

    public static PluginId internal(org.opencds.config.schema.PluginId external) {
        if (external == null) {
            return null;
        }
        return PluginIdImpl.create(external.getScopingEntityId(), external.getBusinessId(), external.getVersion());
    }
    
    public static List<PluginId> internal(List<org.opencds.config.schema.PluginId> plugins) {
        if (plugins == null) {
            return null;
        }
        List<PluginId> pids = new ArrayList<>();
        for (org.opencds.config.schema.PluginId pid : plugins) {
            pids.add(internal(pid));
        }
        return pids;
    }
    
    public static org.opencds.config.schema.PluginId external(PluginId internal) {
        if (internal == null) {
            return null;
        }
        org.opencds.config.schema.PluginId pid = new org.opencds.config.schema.PluginId();
        pid.setScopingEntityId(internal.getScopingEntityId());
        pid.setBusinessId(internal.getBusinessId());
        pid.setVersion(internal.getVersion());
        return pid;
    }
    
}
