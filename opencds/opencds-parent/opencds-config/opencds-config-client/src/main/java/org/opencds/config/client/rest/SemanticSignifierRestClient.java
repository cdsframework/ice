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

import org.opencds.config.client.SemanticSignifierClient;
import org.opencds.config.client.rest.util.PathUtil;

public class SemanticSignifierRestClient implements SemanticSignifierClient {
    private static final String PATH = "semanticsignifiers";

    private RestClient restClient;
    
    public SemanticSignifierRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public <T> T getSemanticSignifiers(Class<T> clazz) {
        return restClient.get(PATH, clazz);
    }
    
    @Override
    public <T> void putSemanticSignifiers(T sses) {
        restClient.put(PATH, sses);
    }

    @Override
    public <T> T getSemanticSignifier(String ssid, Class<T> clazz) {
        return restClient.get(PathUtil.buildPath(PATH, ssid), clazz);
    }

    @Override
    public <T> void postSemanticSignifier(T semanticSignifiers) {
        restClient.post(PATH, semanticSignifiers);
    }
    
    @Override
    public <T> void putSemanticSignifier(String ssid, T semanticSignifier) {
        restClient.put(PathUtil.buildPath(PATH, ssid), semanticSignifier);
    }

    @Override
    public void deleteSemanticSignifier(String ssid) {
        restClient.delete(PathUtil.buildPath(PATH, ssid));
    }

    
}
