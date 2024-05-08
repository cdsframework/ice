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

import org.opencds.config.client.ExecutionEngineClient;
import org.opencds.config.client.rest.util.PathUtil;

public class ExecutionEngineRestClient implements ExecutionEngineClient {
    private static final String PATH = "executionengines";

    private RestClient restClient;

    public ExecutionEngineRestClient(RestClient restClient) {
        this.restClient = restClient;
    }
    
    @Override
    public <T> T getExecutionEngines(Class<T> clazz) {
        return restClient.get(PATH, clazz);
    }
    
    @Override
    public <T> void putExecutionEngines(T t) {
        restClient.put(PATH, t);
    }

    @Override
    public <T> T getExecutionEngine(String identifier, Class<T> clazz) {
        return restClient.get(PathUtil.buildPath(PATH, identifier), clazz);
    }

    @Override
    public <T> void postExecutionEngine(T t) {
        restClient.post(PATH, t);
    }
    
    @Override
    public <T> void putExecutionEngine(String identifier, T t) {
        restClient.put(PathUtil.buildPath(PATH, identifier), t);
    }

    @Override
    public void deleteExecutionEngine(String identifier) {
        restClient.delete(PathUtil.buildPath(PATH, identifier));
    }

}
