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
