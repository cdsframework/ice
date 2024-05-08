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

package org.opencds.config.client.rest;

import org.opencds.config.client.PluginPackageClient;
import org.opencds.config.client.rest.util.PathUtil;

public class PluginPackageRestClient implements PluginPackageClient {
    private static final String PATH = "pluginpackages";

    private RestClient restClient;
    
    public PluginPackageRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public <T> T getPluginPackages(Class<T> clazz) {
        return restClient.get(PATH, clazz);
    }
    
    @Override
    public <T> void putPluginPackages(T pps) {
        restClient.put(PATH, pps);
    }

    @Override
    public <T> T getPluginPackage(String ppid, Class<T> clazz) {
        return restClient.get(PathUtil.buildPath(PATH, ppid), clazz);
    }

    @Override
    public <T> void postPluginPackage(T pluginPackages) {
        restClient.post(PATH, pluginPackages);
    }
    
    @Override
    public <T> void putPluginPackage(String ppid, T pluginPackage) {
        restClient.put(PathUtil.buildPath(PATH, ppid), pluginPackage);
    }

    @Override
    public void deletePluginPackage(String ppid) {
        restClient.delete(PathUtil.buildPath(PATH, ppid));
    }

    
}
