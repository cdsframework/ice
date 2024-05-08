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
